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
package com.cougaarsoftware.cougaar.ide.launcher.ui.configuration;


import java.io.File;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;
import com.cougaarsoftware.cougaar.ide.launcher.core.constants.ICougaarLaunchConfigurationConstants;
import com.cougaarsoftware.cougaar.ide.launcher.ui.LauncherUIMessages;


/**
 * DOCUMENT ME!
 *
 * @author mabrams
 */
public class CougaarMainTab extends JavaMainTab {
    /** DOCUMENT ME! */
    public static String cougaarInstallPath = "";

    //	Project UI widgets
    protected Label fCougaarLabel;
    protected Text fCougaarText;
    protected Button fCougaarButton;

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     */
    public void createControl(Composite parent) {
        Font font = parent.getFont();

        Composite comp = new Composite(parent, SWT.NONE);
        setControl(comp);
        WorkbenchHelp.setHelp(getControl(),
            IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
        GridLayout topLayout = new GridLayout();
        comp.setLayout(topLayout);
        GridData gd;

        createVerticalSpacer(comp, 1);

        Composite projComp = new Composite(comp, SWT.NONE);
        GridLayout projLayout = new GridLayout();
        projLayout.numColumns = 2;
        projLayout.marginHeight = 0;
        projLayout.marginWidth = 0;
        projComp.setLayout(projLayout);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        projComp.setLayoutData(gd);
        projComp.setFont(font);

        fProjLabel = new Label(projComp, SWT.NONE);
        fProjLabel.setText(LauncherMessages.getString("JavaMainTab.&Project__2")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalSpan = 2;
        fProjLabel.setLayoutData(gd);
        fProjLabel.setFont(font);

        fProjText = new Text(projComp, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fProjText.setLayoutData(gd);
        fProjText.setFont(font);
        fProjText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    updateLaunchConfigurationDialog();
                }
            });

        fProjButton = createPushButton(projComp,
                LauncherMessages.getString("JavaMainTab.&Browse_3"), null); //$NON-NLS-1$
        fProjButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    handleProjectButtonSelected();
                }
            });

        createVerticalSpacer(comp, 1);

        Composite mainComp = new Composite(comp, SWT.NONE);
        GridLayout mainLayout = new GridLayout();
        mainLayout.numColumns = 2;
        mainLayout.marginHeight = 0;
        mainLayout.marginWidth = 0;
        mainComp.setLayout(mainLayout);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        mainComp.setLayoutData(gd);
        mainComp.setFont(font);

        fMainLabel = new Label(mainComp, SWT.NONE);
        fMainLabel.setText(LauncherMessages.getString(
                "JavaMainTab.Main_cla&ss__4")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalSpan = 2;
        fMainLabel.setLayoutData(gd);
        fMainLabel.setFont(font);

        fMainText = new Text(mainComp, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fMainText.setLayoutData(gd);
        fMainText.setFont(font);
        fMainText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    updateLaunchConfigurationDialog();
                }
            });

        fSearchButton = createPushButton(mainComp,
                LauncherMessages.getString("JavaMainTab.Searc&h_5"), null); //$NON-NLS-1$
        fSearchButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    handleSearchButtonSelected();
                }
            });

        fSearchExternalJarsCheckButton = new Button(mainComp, SWT.CHECK);
        fSearchExternalJarsCheckButton.setText(LauncherMessages.getString(
                "JavaMainTab.E&xt._jars_6")); //$NON-NLS-1$
        fSearchExternalJarsCheckButton.setFont(font);
        fSearchExternalJarsCheckButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    updateLaunchConfigurationDialog();
                }
            });

        fStopInMainCheckButton = new Button(comp, SWT.CHECK);
        fStopInMainCheckButton.setText(LauncherMessages.getString(
                "JavaMainTab.St&op_in_main_1")); //$NON-NLS-1$
        fStopInMainCheckButton.setFont(font);
        fStopInMainCheckButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    updateLaunchConfigurationDialog();
                }
            });

        createVerticalSpacer(comp, 1);

        Composite cougaarComp = new Composite(comp, SWT.NONE);
        GridLayout cougaarLayout = new GridLayout();
        cougaarLayout.numColumns = 2;
        cougaarLayout.marginHeight = 0;
        cougaarLayout.marginWidth = 0;
        cougaarComp.setLayout(cougaarLayout);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        cougaarComp.setLayoutData(gd);
        cougaarComp.setFont(font);

        fCougaarLabel = new Label(projComp, SWT.NONE);
        fCougaarLabel.setText(LauncherUIMessages.getString(
                "CougaarMainTab.&Cougaar__1")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalSpan = 2;
        fCougaarLabel.setLayoutData(gd);
        fCougaarLabel.setFont(font);

        fCougaarText = new Text(projComp, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fCougaarText.setLayoutData(gd);
        fCougaarText.setFont(font);
        fCougaarText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    updateLaunchConfigurationDialog();
                }
            });

        fCougaarButton = createPushButton(projComp,
                LauncherMessages.getString("JavaMainTab.&Browse_3"), null); //$NON-NLS-1$
        fCougaarButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    handleCougaarButtonSelected();
                }
            });
    }


    /**
     * DOCUMENT ME!
     */
    protected void handleCougaarButtonSelected() {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setMessage(LauncherMessages.getString(
                "JavaArgumentsTab.Select_a_&working_directory_for_the_launch_configuration__7")); //$NON-NLS-1$
        String currentWorkingDir = fCougaarText.getText();
        if (!currentWorkingDir.trim().equals("")) { //$NON-NLS-1$
            File path = new File(currentWorkingDir);
            if (path.exists()) {
                dialog.setFilterPath(currentWorkingDir);
            }
        }

        String selectedDirectory = dialog.open();
        if (selectedDirectory != null) {
            fCougaarText.setText(selectedDirectory);
        }
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy config) {
        super.setDefaults(config);
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
            ICougaarConstants.COUGAAR_MAIN_CLASS);
        config.setAttribute(JavaMainTab.ATTR_INCLUDE_EXTERNAL_JARS, true);
        IJavaProject javaProject = getJavaProject(config);
        if (javaProject != null) {
            config.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_HOME_DIR,
                CougaarPlugin.getCougaarPreference(javaProject.getProject(),
                    "COUGAAR_VERSION"));
        }
    }


    /**
     * Return the IJavaProject corresponding to the project name in the project
     * name text field, or null if the text does not match a project name.
     *
     * @param config DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected IJavaProject getJavaProject(
        ILaunchConfigurationWorkingCopy config) {
        if (fProjText != null) {
            String projectName = "";
            try {
                projectName = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
                        EMPTY_STRING);
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //        String projectName = fProjText.getText().trim();
            if (projectName.length() < 1) {
                return null;
            }

            return getJavaModel().getJavaProject(projectName);
        } else {
            return null;
        }
    }


    /**
     * Convenience method to get the workspace root.
     *
     * @return DOCUMENT ME!
     */
    private IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }


    /**
     * Convenience method to get access to the java model.
     *
     * @return DOCUMENT ME!
     */
    private IJavaModel getJavaModel() {
        return JavaCore.create(getWorkspaceRoot());
    }


    /**
     * DOCUMENT ME!
     *
     * @param config DOCUMENT ME!
     */
    public void initializeFrom(ILaunchConfiguration config) {
        super.initializeFrom(config);
        try {
            fCougaarText.setText(config.getAttribute(
                    ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_HOME_DIR,
                    ""));
        } catch (CoreException e) {
            JDIDebugUIPlugin.log(e);
        }
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy config) {
        super.performApply(config);
        config.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_HOME_DIR,
            fCougaarText.getText());
        CougaarMainTab.cougaarInstallPath = fCougaarText.getText();
    }
}
