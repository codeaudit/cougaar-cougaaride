/*
 * Cougaar IDE
 *
 * Copyright (C) 2003, Cougaar Software, Inc. <tcarrico@cougaarsoftware.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */


package com.cougaarsoftware.cougaar.ide.ui.wizards;


import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.preferences.PreferencesMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.ICougaarInstall;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUI;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUIMessages;
import com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor;
import com.cougaarsoftware.cougaar.ide.ui.preferences.CougaarPreferencePage;
import com.cougaarsoftware.cougaar.ide.ui.preferences.CougaarPreferencesMessages;


/**
 * DOCUMENT ME!
 *
 * @author mabrams
 */
public class CougaarCapabilityConfigurationPage extends WizardPage implements IAddCougaarDialogRequestor {
    private static final String PAGE_NAME = "CougaarCapabilityConfigurationPage"; //$NON-NLS-1$
    private Combo fCougaarCombo;
    private String cougaarVersion = "";
    private NewCougaarProjectWizard cougaarProjectWizard;
    private Button fAddCougaarInstall;
	private Control control;

    /**
     * DOCUMENT ME!
     */
    public CougaarCapabilityConfigurationPage(NewCougaarProjectWizard ncpw) {
        super(PAGE_NAME);
		cougaarProjectWizard = ncpw;
        setTitle(CougaarUIMessages.getString("CougaarCapabilityPageTitle"));
        setDescription(CougaarUIMessages.getString(
                "CougaarCapabilityPage.description"));


    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
		Composite topComp = new Composite(parent, SWT.NONE);
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 3;
		topLayout.marginWidth = 0;
		topLayout.marginHeight = 0;
		topComp.setLayout(topLayout);
		String[] cougaarNames = getCougaarVersions();
		if (cougaarNames != null) {
			if (cougaarNames.length > 0) {
				Label cougaarSelectionLabel = new Label(topComp, SWT.NONE);
				cougaarSelectionLabel.setText(CougaarPreferencesMessages
					.getString("CougaarConfigurationBlock.cougaarVersion"));
				cougaarSelectionLabel.setLayoutData(new GridData());

				fCougaarCombo = new Combo(topComp, SWT.READ_ONLY);
				fCougaarCombo.setItems(cougaarNames);

				fCougaarCombo.setLayoutData(new GridData(
						GridData.HORIZONTAL_ALIGN_FILL));
				fCougaarCombo.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent evt) {
							handleCougaarComboBoxModified();
						}
					});
			}
		}

		fAddCougaarInstall = new Button(topComp, SWT.NONE);
		fAddCougaarInstall.setText("Add");
		fAddCougaarInstall.setLayoutData(new GridData());
		fAddCougaarInstall.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					handleAddButtonSelected();
				}
			});


		DialogField.createEmptySpace(topComp, 2);


	   control = topComp;
		setControl(topComp);

    }
    
	private void handleAddButtonSelected() {
		 String id = "com.cougaarsoftware.cougaar.ide.ui.preferences.CougaarPreferencePage";

		 CougaarPreferencePage page = new CougaarPreferencePage();
		 showPreferencePage(id, page);
	 }
	 
	private boolean showPreferencePage(String id, IPreferencePage page) {
			final IPreferenceNode targetNode = new PreferenceNode(id, page);

			PreferenceManager manager = new PreferenceManager();
			manager.addToRoot(targetNode);

			final PreferenceDialog dialog = new PreferenceDialog(control.getShell(),
					manager);
			final boolean[] result = new boolean[] { false };
			BusyIndicator.showWhile(control.getDisplay(),
				new Runnable() {
					public void run() {
						dialog.create();
						dialog.setMessage(targetNode.getLabelText());
						result[0] = (dialog.open() == PreferenceDialog.OK);
					}
				});
			return result[0];
		}




    /**
     * DOCUMENT ME!
     *
     * @return
     */
    private String[] getCougaarVersions() {
        Map versions = CougaarUI.getCougaarLocations();
        String[] ret = new String[versions.size()];
        Iterator iter = versions.keySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            String version = (String) iter.next();
            ret[count++] = version;
        }

        return ret;
    }


    /**
     * DOCUMENT ME!
     */
    protected void handleCougaarComboBoxModified() {
        cougaarVersion = fCougaarCombo.getText();
        cougaarProjectWizard.setCougaarVersion(cougaarVersion);
//		
    }
    

	private IStatus updateCIPStatus(String cipVersion) {
		StatusInfo status = new StatusInfo();

		if (cipVersion.length() > 0) {
			return status;
		} else {
			status.setWarning(PreferencesMessages.getString(
					"Error Setting Cougaar Install Path Setting")); //$NON-NLS-1$
			return status;
		}
	}

	/* (non-Javadoc)
	 * @see com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor#isDuplicateName(java.lang.String)
	 */
	public boolean isDuplicateName(String name) {
		for (int i = 0; i < fCougaarCombo.getItemCount(); i++) {
		String cougaar = (String) fCougaarCombo.getItem(i);
		if (cougaar.equals(name)) {
			return true;
		}
	}

	return false;
	}

	/* (non-Javadoc)
	 * @see com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor#cougaarAdded(com.cougaarsoftware.cougaar.ide.core.ICougaarInstall)
	 */
	public void cougaarAdded(ICougaarInstall cougaar) {
		System.err.println("Cougaar Install Added");
		
	}
}
