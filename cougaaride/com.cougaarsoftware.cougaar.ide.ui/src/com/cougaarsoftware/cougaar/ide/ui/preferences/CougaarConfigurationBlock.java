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


package com.cougaarsoftware.cougaar.ide.ui.preferences;


import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.preferences.PreferencesMessages;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
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
import org.eclipse.ui.dialogs.PropertyPage;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.ICougaarInstall;
import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUI;
import com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor;


/**
 * DOCUMENT ME!
 *
 * @author Matt Abrams
 */
public class CougaarConfigurationBlock extends PropertyPage
    implements IAddCougaarDialogRequestor {
    private Combo fCougaarCombo;
    private String cougaarVersion = "";
    private IStatusChangeListener fStatus;
    private Button fAddCougaarInstall;
    private IProject project;
    
    private Control control;

    /**
     * Creates a new CougaarConfigurationBlock object.
     *
     * @param status DOCUMENT ME!
     * @param proj DOCUMENT ME!
     */
    public CougaarConfigurationBlock(IStatusChangeListener status, IProject proj) {
        fStatus = status;        
        project = proj;
    }

    /**
     * DOCUMENT ME!
     *
     * @return
     */
    public String getCougaarVersion() {
        return cougaarVersion;
    }


    /**
     * DOCUMENT ME!
     *
     * @param cougaarInstallPath
     */
    public void setCougaarVersion(String cougaarInstallPath) {
        this.cougaarVersion = cougaarInstallPath;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#createContents(org.eclipse.swt.widgets.Composite)
     */
    public Control createContents(Composite parent) {
        Composite topComp = new Composite(parent, SWT.NONE);
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 3;
        topLayout.marginWidth = 0;
        topLayout.marginHeight = 0;
        topComp.setLayout(topLayout);
        String[] cougaarNames = getCougaarVersions();
        Label cougaarSelectionLabel = new Label(topComp, SWT.NONE);
        cougaarSelectionLabel.setText(CougaarPreferencesMessages.getString(
                "CougaarConfigurationBlock.cougaarVersion"));
        cougaarSelectionLabel.setLayoutData(new GridData());

        fCougaarCombo = new Combo(topComp, SWT.READ_ONLY);
        fCougaarCombo.setItems(cougaarNames);

        fCougaarCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        fCougaarCombo.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    handleCougaarComboBoxModified();
                }
            });


        fAddCougaarInstall = new Button(topComp, SWT.NONE);
        fAddCougaarInstall.setText("Add");
        fAddCougaarInstall.setLayoutData(new GridData());
        fAddCougaarInstall.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    handleAddButtonSelected();
                }
            });


        DialogField.createEmptySpace(topComp, 2);
        String defaultVersion = CougaarPlugin.getCougaarPreference(project,
                ICougaarConstants.COUGAAR_VERSION);

        setValues(defaultVersion);
        control = topComp;
        return topComp;
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
        IStatus status = updateCIPStatus(cougaarVersion);
        fStatus.statusChanged(status);
    }


    private void handleAddButtonSelected() {
        String id = "com.cougaarsoftware.cougaar.ide.ui.preferences.CougaarPreferencePage";

        CougaarPreferencePage page = new CougaarPreferencePage(this);
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


    private void setValues(String selectedVersion) {
      
        int count = fCougaarCombo.getItemCount();
        if (count == 1) {
        	fCougaarCombo.select(1);        	
        } else {
        	for (int i = 0; i < count; i++) {
            	String item = fCougaarCombo.getItem(i);
            	if (item.equals(selectedVersion)) {
                	fCougaarCombo.select(i);
                	break;
            	}
        	}
        }
    }


    /**
     * perform defaults
     */
    public void performDefaults() {
        String defaultVersion = CougaarPlugin.getCougaarPreference(project,
                ICougaarConstants.COUGAAR_VERSION);
        setValues(defaultVersion);
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
       String[] versions = getCougaarVersions();
       fCougaarCombo.setItems(versions);
    }
}
