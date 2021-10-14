/*
 * ExtractFields plugin for Pentaho PTH-04
 * Copyright (C) 2021  Fail-Safe IT Solutions Oy
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *  
 *  
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *  
 * If you modify this Program, or any covered work, by linking or combining it 
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Fail-Safe IT Solutions Oy without any 
 * additional modifications.
 *  
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *  
 * Origin of the software must be attributed to Fail-Safe IT Solutions Oy. 
 * Any modified versions must be marked as "Modified version of" The Program.
 *  
 * Names of the licensors and authors may not be used for publicity purposes.
 *  
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *  
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *  
 */
package fi.failsafe.lms.pth04;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@Step(
		  id = "FieldExtract",
		  name = "FieldExtract.Name",
		  description = "FieldExtract.TooltipDesc",
		  image = "fi/failsafe/lms/pth04/resources/fieldextract.svg",
		  categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Transform",
		  i18nPackageName = "fi.failsafe.lms.pth04",
		  documentationUrl = "FieldExtract.DocumentationURL",
		  casesUrl = "FieldExtract.CasesURL",
		  forumUrl = "FieldExtract.ForumURL"
		  )
		@InjectionSupported( localizationPrefix = "FieldExtractMeta.Injection." )
public class FieldExtractMeta extends BaseStepMeta implements StepMetaInterface {

	  private static final Class<?> PKG = FieldExtractMeta.class; // for i18n purposes

	  @Injection( name = "OUTPUT_FIELD" )
	  private String outputField;
	  
	  @Injection( name = "INPUT_FIELD" )
	  private String inputField;
	  
	  @Injection( name = "EXTRACTION_RULE" )
	  private String extractionRule;

	  public FieldExtractMeta() {
	    super();
	  }

	  public StepDialogInterface getDialog( Shell shell, StepMetaInterface meta, TransMeta transMeta, String name ) {
	    return new FieldExtractDialog( shell, meta, transMeta, name );
	  }


	  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
	      Trans disp ) {
	    return new FieldExtract( stepMeta, stepDataInterface, cnr, transMeta, disp );
	  }

	  public StepDataInterface getStepData() {
	    return new FieldExtractData();
	  }

	  public void setDefault() {
	    setOutputField( "extracted_json_field" );
	    setInputField( "raw_data" );
	    setExtractionRule( BaseMessages.getString( PKG, "FEX.DefaultRule" ) );
	  }


	  public String getOutputField() {
	    return outputField;
	  }
	  
	  public String getInputField() {
		    return inputField;
		  }
	  
	  public String getExtractionRule() {
		    return extractionRule;
		  }
	  
	  

	  public void setOutputField( String outputField ) {
	    this.outputField = outputField;
	  }
	  
	  public void setInputField( String inputField ) {
		    this.inputField = inputField;
		  }
		  
	  public void setExtractionRule( String extractionRule ) {
		    this.extractionRule = extractionRule;
		  }


	  public Object clone() {
	    Object retval = super.clone();
	    return retval;
	  }

	  public String getXML() throws KettleValueException {
	    StringBuilder xml = new StringBuilder();

	    // fields to serialize
	    xml.append( XMLHandler.addTagValue( "outputfield", outputField ) );
	    xml.append( XMLHandler.addTagValue( "inputfield", inputField ) );
	    xml.append( XMLHandler.addTagValue( "extreactionrule", extractionRule ) );
	    return xml.toString();
	  }


	  public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {
	    try {
	      setOutputField( XMLHandler.getNodeValue( XMLHandler.getSubNode( stepnode, "outputfield" ) ) );
	      setInputField( XMLHandler.getNodeValue( XMLHandler.getSubNode( stepnode, "inputfield" ) ) );
	      setExtractionRule( XMLHandler.getNodeValue( XMLHandler.getSubNode( stepnode, "extreactionrule" ) ) );
	    } catch ( Exception e ) {
	      throw new KettleXMLException( "FieldExtract plugin unable to read step info from XML node", e );
	    }
	  }


	  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
	      throws KettleException {
	    try {
	      rep.saveStepAttribute( id_transformation, id_step, "outputfield", outputField ); //$NON-NLS-1$
	      rep.saveStepAttribute( id_transformation, id_step, "inputfield", inputField ); //$NON-NLS-1$
	      rep.saveStepAttribute( id_transformation, id_step, "extreactionrule", extractionRule ); //$NON-NLS-1$
	    } catch ( Exception e ) {
	      throw new KettleException( "Unable to save step into repository: " + id_step, e );
	    }
	  }

	  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
	      throws KettleException {
	    try {
	      outputField  = rep.getStepAttributeString( id_step, "outputfield" ); //$NON-NLS-1$
	      inputField  = rep.getStepAttributeString( id_step, "inputfield" ); //$NON-NLS-1$
	      extractionRule  = rep.getStepAttributeString( id_step, "extreactionrule" ); //$NON-NLS-1$
	    } catch ( Exception e ) {
	      throw new KettleException( "Unable to load step from repository", e );
	    }
	  }

	  public void getFields( RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
	      VariableSpace space, Repository repository, IMetaStore metaStore ) throws KettleStepException {

	    ValueMetaInterface v = new ValueMetaString( outputField );
	    v.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
	    v.setOrigin( name );
	    
	    /*
	    ValueMetaInterface inputFv = new ValueMetaString( inputField );
	    inputFv.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
	    inputFv.setOrigin( name );
	    
	    ValueMetaInterface extreactionRv = new ValueMetaString( extractionRule );
	    extreactionRv.setTrimType( ValueMetaInterface.TRIM_TYPE_BOTH );
	    extreactionRv.setOrigin( name );
		*/

	    inputRowMeta.addValueMeta( v );
	    //inputRowMeta.addValueMeta( inputFv );
	    //inputRowMeta.addValueMeta( extreactionRv );
	  }


	  public void check( List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
	      String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository,
	      IMetaStore metaStore ) {
	    CheckResult cr;

	    // See if there are input streams leading to this step!
	    if ( input != null && input.length > 0 ) {
	      cr = new CheckResult( CheckResult.TYPE_RESULT_OK,
	        BaseMessages.getString( PKG, "FEX.CheckResult.ReceivingRows.OK" ), stepMeta );
	      remarks.add( cr );
	    } else {
	      cr = new CheckResult( CheckResult.TYPE_RESULT_ERROR,
	        BaseMessages.getString( PKG, "FEX.CheckResult.ReceivingRows.ERROR" ), stepMeta );
	      remarks.add( cr );
	    }
	  }
	}
