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


package com.cougaarsoftware.cougaar.ide.ui.widgets;


import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Label;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.ICougaarInstall;
import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUI;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUIMessages;
import com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor;
import com.cougaarsoftware.cougaar.ide.ui.ICougaarInstallChangeListener;
import com.cougaarsoftware.cougaar.ide.ui.preferences.CougaarPreferencePage;
import com.cougaarsoftware.cougaar.ide.ui.preferences.CougaarPreferencesMessages;


/**
 * DOCUMENT ME!
 *
 * @author soster
 */
public class CougaarInstallSelectionWidget extends Composite
    implements IAddCougaarDialogRequestor, ICougaarInstallChangeListener {
    private Button fAddCougaarInstall;
    private Combo fCougaarCombo;
    private ICougaarInstallSelectionChangeListener listener;
    private IProject project;

    /**
     * DOCUMENT ME!
     *
     * @param parent
     * @param style
     * @param listener DOCUMENT ME!
     * @param proj DOCUMENT ME!
     */
    public CougaarInstallSelectionWidget(Composite parent, int style,
        ICougaarInstallSelectionChangeListener listener, IProject proj) {
        super(parent, style);
        this.listener = listener;
        this.project = proj;
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 3;
        topLayout.marginWidth = 0;
        topLayout.marginHeight = 0;
        this.setLayout(topLayout);
        String[] cougaarNames = getCougaarVersions();

        Label cougaarSelectionLabel = new Label(this, SWT.NONE);
        cougaarSelectionLabel.setText(CougaarPreferencesMessages.getString(
                "CougaarConfigurationBlock.cougaarVersion"));
        cougaarSelectionLabel.setLayoutData(new GridData());

        fCougaarCombo = new Combo(this, SWT.READ_ONLY);
        fCougaarCombo.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    handleCougaarComboBoxModified();
                }
            });
        if (cougaarNames.length > 0) {
            fCougaarCombo.setItems(cougaarNames);
        }


        fCougaarCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        fAddCougaarInstall = new Button(this, SWT.NONE);
        fAddCougaarInstall.setText(CougaarUIMessages.getString(
                "CougaarCapabilityConfigurationPage.newInstallButton"));
        fAddCougaarInstall.setLayoutData(new GridData());
        fAddCougaarInstall.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    handleAddButtonSelected();
                }
            });

        DialogField.createEmptySpace(this, 2);

        String defaultVersion = CougaarPlugin.getCougaarPreference(project,
                ICougaarConstants.COUGAAR_VERSION);

        setValues(defaultVersion);
    }

    /**
     * called when the user makes a selection from the combo box
     */
    protected void handleCougaarComboBoxModified() {
        String version = fCougaarCombo.getText();
        listener.handleCougaarInstallSelected(version);
    }


    private void handleAddButtonSelected() {
        String id = "com.cougaarsoftware.cougaar.ide.ui.preferences.CougaarPreferencePage";

        CougaarPreferencePage page = new CougaarPreferencePage(this);
        showPreferencePage(id, page);
        fCougaarCombo.update();
    }


    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isDuplicateName(String name) {
        return false;
    }


    /**
     * DOCUMENT ME!
     *
     * @param cougaar DOCUMENT ME!
     */
    public void cougaarAdded(ICougaarInstall cougaar) {
        String[] versions = getCougaarVersions();
        fCougaarCombo.setItems(versions);
        setValues(cougaar.getId());
    }


    /**
     * DOCUMENT ME!
     *
     * @param removed DOCUMENT ME!
     */
    public void cougaarRemoved(ICougaarInstall removed) {
        String[] versions = getCougaarVersions();
        fCougaarCombo.setItems(versions);
    }


    /**
     * get the cougaar versions
     *
     * @return an array of currently configured cougaar versions
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
     *
     * @param selectedVersion DOCUMENT ME!
     */
    protected void setValues(String selectedVersion) {
        int count = fCougaarCombo.getItemCount();

        for (int i = 0; i < count; i++) {
            String item = fCougaarCombo.getItem(i);
            if (item.equals(selectedVersion)) {
                fCougaarCombo.select(i);
                break;
            }
        }
    }


    private boolean showPreferencePage(String id, IPreferencePage page) {
        final IPreferenceNode targetNode = new PreferenceNode(id, page);

        PreferenceManager manager = new PreferenceManager();
        manager.addToRoot(targetNode);

        final PreferenceDialog dialog = new PreferenceDialog(this.getShell(),
                manager);
        final boolean[] result = new boolean[] { false };
        BusyIndicator.showWhile(this.getDisplay(),
            new Runnable() {
                public void run() {
                    dialog.create();
                    dialog.setMessage(targetNode.getLabelText());
                    result[0] = (dialog.open() == Window.OK);
                }
            });
        return result[0];
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSelectedCougaarInstall() {
        String version = fCougaarCombo.getText();
        return version;

    }
}
