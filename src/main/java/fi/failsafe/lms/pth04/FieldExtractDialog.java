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

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.widget.LabelText;
import org.pentaho.di.ui.core.widget.StyledTextComp;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;

public class FieldExtractDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = FieldExtractMeta.class; // for i18n purposes  

	private FieldExtractMeta meta;

	private LabelText wOutputFieldName, wInputFieldName;
	private StyledTextComp wRuleTextWin;

	public FieldExtractDialog( Shell parent, Object in, TransMeta transMeta, String sname ) {
		super( parent, (BaseStepMeta) in, transMeta, sname );
		meta = (FieldExtractMeta) in;
	}

	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
		props.setLook( shell );
		setShellImage( shell, meta );

		changed = meta.hasChanged();

		ModifyListener lsMod = new ModifyListener() {
			public void modifyText( ModifyEvent e ) {
				meta.setChanged();
			}
		};

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		shell.setLayout( formLayout );
		shell.setText( BaseMessages.getString( PKG, "FEX.Shell.Title" ) );
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		wlStepname = new Label( shell, SWT.RIGHT );
		wlStepname.setText( BaseMessages.getString( PKG, "System.Label.StepName" ) );
		props.setLook( wlStepname );
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment( 0, 0 );
		fdlStepname.right = new FormAttachment( middle, -margin );
		fdlStepname.top = new FormAttachment( 0, margin );
		wlStepname.setLayoutData( fdlStepname );



		wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
		wStepname.setText( stepname );
		props.setLook( wStepname );
		wStepname.addModifyListener( lsMod );
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment( middle, 0 );
		fdStepname.top = new FormAttachment( 0, margin );
		fdStepname.right = new FormAttachment( 100, 0 );
		wStepname.setLayoutData( fdStepname );

		wInputFieldName = new LabelText( shell, BaseMessages.getString( PKG, "FEX.InputFieldName.Label" ), null );
		props.setLook( wInputFieldName );
		wInputFieldName.addModifyListener( lsMod );
		FormData fdInputValName = new FormData();
		fdInputValName.left = new FormAttachment( 0, 0 );
		fdInputValName.right = new FormAttachment( 100, 0 );
		fdInputValName.top = new FormAttachment( wStepname, margin );
		wInputFieldName.setLayoutData( fdInputValName );

		wOutputFieldName = new LabelText( shell, BaseMessages.getString( PKG, "FEX.OutputFieldName.Label" ), null );
		props.setLook( wOutputFieldName );
		wOutputFieldName.addModifyListener( lsMod );
		FormData fdValName = new FormData();
		fdValName.left = new FormAttachment( 0, 0 );
		fdValName.right = new FormAttachment( 100, 0 );
		fdValName.top = new FormAttachment( wInputFieldName, margin );
		wOutputFieldName.setLayoutData( fdValName );

		// FEX label line
		Label wlScript = new Label( shell, SWT.NONE );
		wlScript.setText( BaseMessages.getString( PKG, "FEX.Rule.Label" ) );
		props.setLook( wlScript );
		FormData fdlScript = new FormData();
		fdlScript.left = new FormAttachment( 0, 0 );
		fdlScript.top = new FormAttachment( wOutputFieldName, margin );
		wlScript.setLayoutData( fdlScript );

		// rule editor
		wRuleTextWin =
				new StyledTextComp( transMeta, shell, SWT.MULTI
						| SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, "" );
		//wRuleTextWin.setText( BaseMessages.getString( PKG, "FEX.DefaultRule" ) );
		props.setLook( wRuleTextWin, Props.WIDGET_STYLE_FIXED );
		wRuleTextWin.addModifyListener( lsMod );
		FormData fdRuleScript = new FormData();
		fdRuleScript.top = new FormAttachment( wlScript, margin );
		fdRuleScript.left = new FormAttachment( 0, 0 );
		fdRuleScript.right = new FormAttachment( 100, -10 );
		fdRuleScript.bottom = new FormAttachment( 100, -60 );
		wRuleTextWin.setLayoutData( fdRuleScript );

		// OK and cancel buttons
		wOK = new Button( shell, SWT.PUSH );
		wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
		wCancel = new Button( shell, SWT.PUSH );
		wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
		setButtonPositions( new Button[] { wOK, wCancel }, margin, wRuleTextWin );

		lsCancel = new Listener() {
			public void handleEvent( Event e ) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent( Event e ) {
				ok();
			}
		};
		wCancel.addListener( SWT.Selection, lsCancel );
		wOK.addListener( SWT.Selection, lsOK );

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected( SelectionEvent e ) {
				ok();
			}
		};
		wStepname.addSelectionListener( lsDef );
		wOutputFieldName.addSelectionListener( lsDef );
		wInputFieldName.addSelectionListener ( lsDef );
		//		SelectionAdapter lsVar = VariableButtonListenerFactory.getSelectionAdapter(shell, wRuleTextWin);
		//	    wScript.addKeyListener(TextVar.getControlSpaceKeyListener(wRuleTextWin, lsVar));

		shell.addShellListener( new ShellAdapter() {
			public void shellClosed( ShellEvent e ) {
				cancel();
			}
		} );

		setSize();

		populateDialog();

		meta.setChanged( changed );

		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}

		return stepname;
	}

	private void populateDialog() {
		wStepname.selectAll();
		wOutputFieldName.setText( meta.getOutputField() );
		wInputFieldName.setText( meta.getInputField());
		wRuleTextWin.setText( meta.getExtractionRule());

	}

	private void cancel() {

		stepname = null;
		meta.setChanged( changed );
		dispose();
	}

	private void ok() {
		stepname = wStepname.getText();
		meta.setOutputField( wOutputFieldName.getText() );
		meta.setInputField( wInputFieldName.getText() );
		meta.setExtractionRule( wRuleTextWin.getText() );
		dispose();
	}
}
