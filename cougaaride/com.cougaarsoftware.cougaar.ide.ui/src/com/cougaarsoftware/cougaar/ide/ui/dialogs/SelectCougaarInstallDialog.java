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


package com.cougaarsoftware.cougaar.ide.ui.dialogs;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.dialogs.StatusDialog;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.cougaarsoftware.cougaar.ide.ui.widgets.CougaarInstallSelectionWidget;


/**
 * Dialog to select a cougaar install, with the ability to create a new one
 *
 * @author soster
 */
public class SelectCougaarInstallDialog extends StatusDialog {
    private IStatus[] fStatus;
    private CougaarInstallSelectionWidget control;
    private String installVersion;

    /**
     * Constructor
     *
     * @param parent the parent Shell
     */
    public SelectCougaarInstallDialog(Shell parent) {
        super(parent);
        fStatus = new IStatus[5];
        for (int i = 0; i < fStatus.length; i++) {
            fStatus[i] = new StatusInfo();
        }
    }

    /**
     * create the add dialog
     *
     * @param ancestor ancestor composite
     *
     * @return the control
     */
    protected Control createDialogArea(Composite ancestor) {
        this.control = new CougaarInstallSelectionWidget(ancestor, SWT.NONE);
        return this.control;
    }


    /**
     * lets the user know if the current input is valid
     */
    protected void updateStatusLine() {
        IStatus max = null;
        for (int i = 0; i < fStatus.length; i++) {
            IStatus curr = fStatus[i];
            if (curr.matches(IStatus.ERROR)) {
                updateStatus(curr);
                super.updateButtonsEnableState(curr);
                return;
            }

            if ((max == null) || (curr.getSeverity() > max.getSeverity())) {
                max = curr;
            }
        }

        updateStatus(max);
    }


    /**
     * DOCUMENT ME!
     *
     * @return
     */
    public String getValue() {
        return installVersion;
    }


    /**
     * DOCUMENT ME!
     */
    protected void okPressed() {
        installVersion = this.control.getSelectedCougaarInstall();
        super.okPressed();
    }
}
