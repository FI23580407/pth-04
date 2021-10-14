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

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;


public class FieldExtract extends BaseStep implements StepInterface {
	private static final Class<?> PKG = FieldExtractMeta.class; // for i18n purposes

	// JNA Object here, be careful with that sharp c-shaped razor
	LogNorm logNorm = null;	

	public FieldExtract( StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis ) {
		super( s, stepDataInterface, c, t, dis );
	}

	public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
		FieldExtractMeta meta = (FieldExtractMeta) smi;
		FieldExtractData data = (FieldExtractData) sdi;
		if ( !super.init( meta, data ) ) {
			return false;
		}

		return true;
	}

	public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
		FieldExtractMeta meta = (FieldExtractMeta) smi;
		FieldExtractData data = (FieldExtractData) sdi;

		Object[] r = getRow();

		if ( r == null ) {
			setOutputDone();
			return false;
		}

		if ( first ) {
			first = false;
			data.outputRowMeta = (RowMetaInterface) getInputRowMeta().clone();
			meta.getFields( data.outputRowMeta, getStepname(), null, null, this, null, null );


			data.outputFieldIndex = data.outputRowMeta.indexOfValue( meta.getOutputField() );
			if ( data.outputFieldIndex < 0 ) {
				log.logError( BaseMessages.getString( PKG, "FieldExtract.Error.NoOutputField" ) );
				setErrors( 1L );
				setOutputDone();
				return false;
			}

			// initialize normalizer
			logNorm = new LogNorm(meta.getExtractionRule());			
		}

		// inputField
		String inputField = data.outputRowMeta.getString(r, meta.getInputField(), "");
		String normalized = logNorm.Normalize(inputField);	

		// output extracted
		Object[] outputRow = RowDataUtil.resizeArray( r, data.outputRowMeta.size() );
		outputRow[data.outputFieldIndex] = normalized;

		putRow( data.outputRowMeta, outputRow );

		if ( checkFeedback( getLinesRead() ) ) {
			logBasic( BaseMessages.getString( PKG, "FieldExtract.Linenr", getLinesRead() ) );
		}


		return true;
	}

	public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {
		// Free JNA Object
		logNorm.Destroy();
		
		FieldExtractMeta meta = (FieldExtractMeta) smi;
		FieldExtractData data = (FieldExtractData) sdi;
		super.dispose( meta, data );
	}


}
