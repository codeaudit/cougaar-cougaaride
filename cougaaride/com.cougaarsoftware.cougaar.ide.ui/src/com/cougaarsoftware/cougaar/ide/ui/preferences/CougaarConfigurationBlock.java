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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.ICougaarInstall;
import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUI;
import com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor;
import com.cougaarsoftware.cougaar.ide.ui.widgets.CougaarInstallSelectionWidget;
import com.cougaarsoftware.cougaar.ide.ui.widgets.ICougaarInstallSelectionChangeListener;


/**
 * DOCUMENT ME!
 *
 * @author Matt Abrams
 */
public class CougaarConfigurationBlock extends PropertyPage
    implements IAddCougaarDialogRequestor,
        ICougaarInstallSelectionChangeListener {
    private Combo fCougaarCombo;
    private String cougaarVersion = "";
    private IStatusChangeListener fStatus;
    private IAddCougaarDialogRequestor requestor;
    private IProject project;

    /**
     * Creates a new CougaarConfigurationBlock object.
     *
     * @param status DOCUMENT ME!
     * @param req DOCUMENT ME!
     * @param proj DOCUMENT ME!
     */
    public CougaarConfigurationBlock(IStatusChangeListener status,
        IAddCougaarDialogRequestor req, IProject proj) {
        fStatus = status;
        requestor = req;
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
        return new CougaarInstallSelectionWidget(parent, SWT.NULL, this, project);
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

        for (int i = 0; i < count; i++) {
            String item = fCougaarCombo.getItem(i);
            if (item.equals(selectedVersion)) {
                fCougaarCombo.select(i);
                break;
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
            String cougaar = fCougaarCombo.getItem(i);
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
        setValues(cougaar.getId());
        if (requestor != null) {
            requestor.cougaarAdded(cougaar);
        }
    }


    /* (non-Javadoc)
     * @see com.cougaarsoftware.cougaar.ide.ui.widgets.ICougaarInstallSelectionChangeListener#handleCougaarInstallSelected(java.lang.String)
     */
    public void handleCougaarInstallSelected(String version) {
        String[] versions = getCougaarVersions();
        fCougaarCombo.setItems(versions);

    }
}
