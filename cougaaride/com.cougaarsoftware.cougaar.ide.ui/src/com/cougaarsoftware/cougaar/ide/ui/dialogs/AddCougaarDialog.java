/*
 * Cougaar IDE
 *
 * Copyright (C) 2003, Cougaar Software, Inc. <tcarrico@cougaarsoftware.com>
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.cougaarsoftware.cougaar.ide.ui.dialogs;


import java.io.File;

import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.dialogs.StatusDialog;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cougaarsoftware.cougaar.ide.core.CougaarInstall;
import com.cougaarsoftware.cougaar.ide.core.ICougaarInstall;
import com.cougaarsoftware.cougaar.ide.ui.CougaarInstallBlock;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUI;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUIMessages;
import com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor;


/**
 * Dialog to add a new cougaar install
 *
 * @author mabrams
 */
public class AddCougaarDialog extends StatusDialog {
    private IAddCougaarDialogRequestor fRequestor;
    private ICougaarInstall fEditedCougaar;
    private CougaarInstallBlock fCougaarInstallBlock;
    private StringButtonDialogField fCougaarRoot;
    private StringDialogField fCougaarName;
    private IStatus[] fStatus;

    /**
     * Constructor
     *
     * @param requestor the requestor (so we can notify them of changes)
     * @param parent the parent Shell
     * @param cougaarInstall the cougaarInstall being edited (can be NULL)
     */
    public AddCougaarDialog(IAddCougaarDialogRequestor requestor, Shell parent,
        ICougaarInstall cougaarInstall) {
        super(parent);
        fRequestor = requestor;
        fStatus = new IStatus[5];
        for (int i = 0; i < fStatus.length; i++) {
            fStatus[i] = new StatusInfo();
        }

        fEditedCougaar = cougaarInstall;
    }

    /**
     * create the add dialog
     *
     * @param ancestor ancestor composite
     *
     * @return the control
     */
    protected Control createDialogArea(Composite ancestor) {
        Font font = ancestor.getFont();
        initializeDialogUnits(ancestor);
        createDialogFields();
        Composite parent = new Composite(ancestor, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);
        parent.setFont(font);

        fCougaarName.doFillIntoGrid(parent, 3);

        fCougaarRoot.doFillIntoGrid(parent, 3);

        Label l = new Label(parent, SWT.NONE);
        l.setText(CougaarUIMessages.getString(
                "AddCougaarDialog.Cougaar_system_libraries__1")); //$NON-NLS-1$
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        l.setLayoutData(gd);
        l.setFont(font);

        fCougaarInstallBlock = new CougaarInstallBlock(this);
        Control block = fCougaarInstallBlock.createControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        block.setLayoutData(gd);

        Text t = fCougaarRoot.getTextControl(parent);
        gd = (GridData) t.getLayoutData();
        gd.grabExcessHorizontalSpace = true;
        gd.widthHint = convertWidthInCharsToPixels(50);

        initializeFields();

        return parent;
    }


    private void initializeFields() {
        if (fEditedCougaar == null) {
            fCougaarName.setText(""); //$NON-NLS-1$
            fCougaarRoot.setText(""); //$NON-NLS-1$
            fCougaarInstallBlock.initializeFrom(null);
        } else {
            fCougaarName.setText(fEditedCougaar.getId());
            fCougaarRoot.setText(fEditedCougaar.getInstallLocation()
                                               .getAbsolutePath());
            fCougaarInstallBlock.initializeFrom(fEditedCougaar);
        }
    }


    /**
     * create the dialog fields
     */
    protected void createDialogFields() {
        fCougaarName = new StringDialogField();
        fCougaarName.setLabelText(CougaarUIMessages.getString(
                "addCougaarDialog.cougaarName")); //$NON-NLS-1$
        fCougaarName.setDialogFieldListener(new IDialogFieldListener() {
                public void dialogFieldChanged(DialogField field) {
                    setCougaarNameStatus(validateCougaarName());
                    updateStatusLine();
                }
            });

        fCougaarRoot = new StringButtonDialogField(new IStringButtonAdapter() {
                    public void changeControlPressed(DialogField field) {
                        browseForInstallDir();
                    }
                });
        fCougaarRoot.setLabelText(CougaarUIMessages.getString(
                "addCougaarDialog.cougaarHome")); //$NON-NLS-1$
        fCougaarRoot.setButtonLabel(CougaarUIMessages.getString(
                "addVMDialog.browse1")); //$NON-NLS-1$
        fCougaarRoot.setDialogFieldListener(new IDialogFieldListener() {
                public void dialogFieldChanged(DialogField field) {
                    setCougaarLocationStatus(validateCougaarLocation());
                    updateStatusLine();
                }
            });
    }


    private IStatus validateCougaarLocation() {
        String locationName = fCougaarRoot.getText();
        IStatus s = null;
        File file = null;
        if (locationName.length() == 0) { //$NON-NLS-1$
            s = new StatusInfo(IStatus.ERROR,
                    CougaarUIMessages.getString(
                        "addCougaarDialog.enterLocation")); //$NON-NLS-1$
        } else {
            file = new File(locationName);
            if (!file.exists()) {
                s = new StatusInfo(IStatus.ERROR,
                        CougaarUIMessages.getString(
                            "addCougaarDialog.locationNotExists")); //$NON-NLS-1$
            } else {
                s = new StatusInfo();
            }
        }

        if (s.isOK()) {
            fCougaarInstallBlock.setHomeDirectory(file);
        } else {
            fCougaarInstallBlock.setHomeDirectory(null);
        }

        fCougaarInstallBlock.update();

        return s;
    }


    private IStatus validateCougaarName() {
        StatusInfo status = new StatusInfo();
        String name = fCougaarName.getText();
        if ((name == null) || (name.trim().length() == 0)) {
            status.setError(CougaarUIMessages.getString(
                    "addCougaarDialog.enterName")); //$NON-NLS-1$
        } else {
            if (fRequestor.isDuplicateName(name)
                && ((fEditedCougaar == null)
                || !name.equals(fEditedCougaar.getId()))) {
                status.setError(CougaarUIMessages.getString(
                        "adCoguaarMDialog.duplicateName")); //$NON-NLS-1$
            } else {
                IStatus s = ResourcesPlugin.getWorkspace().validateName(name,
                        IResource.FILE);
                if (!s.isOK()) {
                    status.setError(MessageFormat.format(
                            CougaarUIMessages.getString(
                                "AddCougaarDialog.Cougaar_name_must_be_a_valid_file_name__{0}_1"),
                            new String[] { s.getMessage() })); //$NON-NLS-1$
                }
            }
        }

        return status;
    }


    private void setCougaarNameStatus(IStatus status) {
        fStatus[0] = status;
    }


    private void setCougaarLocationStatus(IStatus status) {
        fStatus[1] = status;
    }


    /**
     * DOCUMENT ME!
     *
     * @param cougaar DOCUMENT ME!
     */
    protected void setFieldValuesToVM(ICougaarInstall cougaar) {
        cougaar.setInstallLocation(new File(fCougaarRoot.getText())
            .getAbsoluteFile());
        cougaar.setId(fCougaarName.getText());

        fCougaarInstallBlock.performApply(cougaar);
    }


    private void doOkPressed() {
        if (fEditedCougaar == null) {
            String path = fCougaarInstallBlock.getHomeDirectory()
                                              .getAbsolutePath().replace('\\',
                    '/');
            ICougaarInstall cougaar = new CougaarInstall(fCougaarName.getText(),
                    path);
            setFieldValuesToVM(cougaar);
            CougaarUI.setCougaarInstallPathLocation(cougaar.getId(),
                cougaar.getInstallLocation().getAbsolutePath());
            fRequestor.cougaarAdded(cougaar);
        } else {
            setFieldValuesToVM(fEditedCougaar);
            String path = fEditedCougaar.getInstallLocation().getAbsolutePath()
                                        .replace('\\', '/');
            CougaarUI.setCougaarInstallPathLocation(fEditedCougaar.getId(), path);
        }
    }


    /**
     * called when user tries to add a new cougaar install
     */
    protected void okPressed() {
        doOkPressed();
        super.okPressed();
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


    private void browseForInstallDir() {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setFilterPath(fCougaarRoot.getText());
        dialog.setMessage(CougaarUIMessages.getString(
                "addCougaarDialog.pickCougaarRootDialog.message")); //$NON-NLS-1$
        String newPath = dialog.open();
        if (newPath != null) {
            fCougaarRoot.setText(newPath);
        }
    }
}
