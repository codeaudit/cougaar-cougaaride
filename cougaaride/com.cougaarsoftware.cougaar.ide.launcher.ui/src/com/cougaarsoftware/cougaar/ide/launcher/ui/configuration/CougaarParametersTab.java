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


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.JavaDebugImages;
import org.eclipse.jdt.internal.debug.ui.launcher.JavaLaunchConfigurationTab;
import org.eclipse.jdt.internal.debug.ui.launcher.WorkingDirectoryBlock;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;
import com.cougaarsoftware.cougaar.ide.launcher.core.constants.ICougaarLaunchConfigurationConstants;
import com.cougaarsoftware.cougaar.ide.launcher.ui.LauncherUIMessages;
import com.cougaarsoftware.cougaar.ide.launcher.ui.util.NameValuePairDialog;


/**
 * DOCUMENT ME!
 *
 * @author Matt Abrams
 *
 * @see JavaLaunchConfigurationTab
 */
public class CougaarParametersTab extends JavaLaunchConfigurationTab {
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private Label fNameLabel;
    private Text fNameText;
    private Table fVMParametersTable;
    private Button fParametersAddButton;
    private Button fParametersRemoveButton;
    private Button fParametersEditButton;
    protected Button fArgumentsDefaultButton;
    /** The last launch config this tab was initialized from */
    protected ILaunchConfiguration fLaunchConfiguration;

    //	Working directory
    protected WorkingDirectoryBlock fWorkingDirectoryBlock;

    /**
     * Creates a new CougaarParametersTab object.
     */
    public CougaarParametersTab() {
        fWorkingDirectoryBlock = createWorkingDirBlock();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected WorkingDirectoryBlock createWorkingDirBlock() {
        return new WorkingDirectoryBlock();
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Font font = parent.getFont();

        Composite comp = new Composite(parent, SWT.NONE);
        setControl(comp);
        GridLayout topLayout = new GridLayout();
        comp.setLayout(topLayout);
        GridData gd;

        createVerticalSpacer(comp);

        Composite nameComp = new Composite(comp, SWT.NONE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        nameComp.setLayoutData(gd);
        GridLayout nameLayout = new GridLayout();
        nameLayout.marginHeight = 0;
        nameLayout.marginWidth = 0;
        nameLayout.numColumns = 1;
        nameComp.setLayout(nameLayout);

        fNameLabel = new Label(nameComp, SWT.NONE);
        fNameLabel.setText(LauncherUIMessages.getString(
                "cougaarlauncher.argumenttab.namelabel.text")); //$NON-NLS-1$
        fNameLabel.setFont(font);

        fNameText = new Text(nameComp, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fNameText.setLayoutData(gd);
        fNameText.setFont(font);
        fNameText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    setDirty(true);
                    updateLaunchConfigurationDialog();
                }
            });

        Label blank = new Label(nameComp, SWT.NONE);
        blank.setText(EMPTY_STRING);
        Label hint = new Label(nameComp, SWT.NONE);
        hint.setText(LauncherUIMessages.getString(
                "CougaarParametersTab.(cougaar_node_name)_1")); //$NON-NLS-1$
        gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        hint.setLayoutData(gd);
        hint.setFont(font);
        createVerticalSpacer(comp, 1);

        fArgumentsDefaultButton = new Button(comp, SWT.CHECK);

        fArgumentsDefaultButton.setText(LauncherUIMessages.getString(
                "CougaarEnvironmentTab.Use_defau&lt_arguments_1")); //$NON-NLS-1$
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 2;
        fArgumentsDefaultButton.setLayoutData(gd);
        fArgumentsDefaultButton.setFont(font);

        fArgumentsDefaultButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    handleArgumentsDefaultButtonSelected();
                }
            });

        createVerticalSpacer(comp);

        Composite parametersComp = new Composite(comp, SWT.NONE);
        gd = new GridData(GridData.FILL_BOTH);
        parametersComp.setLayoutData(gd);
        GridLayout parametersLayout = new GridLayout();
        parametersLayout.numColumns = 2;
        parametersLayout.marginHeight = 0;
        parametersLayout.marginWidth = 0;
        parametersComp.setLayout(parametersLayout);
        parametersComp.setFont(font);

        Label parameterLabel = new Label(parametersComp, SWT.NONE);
        parameterLabel.setText(LauncherUIMessages.getString(
                "cougaarlauncher.argumenttab.parameterslabel.text")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalSpan = 2;
        parameterLabel.setLayoutData(gd);
        parameterLabel.setFont(font);

        fVMParametersTable = new Table(parametersComp, SWT.BORDER | SWT.MULTI);
        fVMParametersTable.setData(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS);
        TableLayout tableLayout = new TableLayout();
        fVMParametersTable.setLayout(tableLayout);
        fVMParametersTable.setFont(font);
        gd = new GridData(GridData.FILL_BOTH);
        fVMParametersTable.setLayoutData(gd);
        TableColumn column1 = new TableColumn(this.fVMParametersTable, SWT.NONE);
        column1.setText(LauncherUIMessages.getString(
                "cougaarlauncher.argumenttab.parameterscolumn.name.text")); //$NON-NLS-1$
        TableColumn column2 = new TableColumn(this.fVMParametersTable, SWT.NONE);
        column2.setText(LauncherUIMessages.getString(
                "cougaarlauncher.argumenttab.parameterscolumn.value.text")); //$NON-NLS-1$
        tableLayout.addColumnData(new ColumnWeightData(100));
        tableLayout.addColumnData(new ColumnWeightData(100));
        fVMParametersTable.setHeaderVisible(true);
        fVMParametersTable.setLinesVisible(true);
        fVMParametersTable.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    setParametersButtonsEnableState();
                }
            });
        fVMParametersTable.addMouseListener(new MouseAdapter() {
                public void mouseDoubleClick(MouseEvent e) {
                    setParametersButtonsEnableState();
                    if (fParametersEditButton.isEnabled()) {
                        handleParametersEditButtonSelected();
                    }
                }
            });

        Composite envButtonComp = new Composite(parametersComp, SWT.NONE);
        GridLayout envButtonLayout = new GridLayout();
        envButtonLayout.marginHeight = 0;
        envButtonLayout.marginWidth = 0;
        envButtonComp.setLayout(envButtonLayout);
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                | GridData.HORIZONTAL_ALIGN_FILL);
        envButtonComp.setLayoutData(gd);
        envButtonComp.setFont(font);

        createVerticalSpacer(comp, 1);

        fWorkingDirectoryBlock.createControl(comp);


        fParametersAddButton = createPushButton(envButtonComp,
                LauncherUIMessages.getString(
                    "cougaarlauncher.argumenttab.parameters.button.add.text"),
                null); //$NON-NLS-1$
        fParametersAddButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    handleParametersAddButtonSelected();
                }
            });

        fParametersEditButton = createPushButton(envButtonComp,
                LauncherUIMessages.getString(
                    "cougaarlauncher.argumenttab.parameters.button.edit.text"),
                null); //$NON-NLS-1$
        fParametersEditButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    handleParametersEditButtonSelected();
                }
            });

        fParametersRemoveButton = createPushButton(envButtonComp,
                LauncherUIMessages.getString(
                    "cougaarlauncher.argumenttab.parameters.button.remove.text"),
                null); //$NON-NLS-1$
        fParametersRemoveButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    handleParametersRemoveButtonSelected();
                }
            });
    }


    /**
     * The default classpath button has been toggled
     */
    protected void handleArgumentsDefaultButtonSelected() {
        setDirty(true);
        boolean useDefault = fArgumentsDefaultButton.getSelection();
        fArgumentsDefaultButton.setSelection(useDefault);

        if (useDefault) {
            displayDefaultCougaarParameters();
        }

        fVMParametersTable.setEnabled(!useDefault);

        updateLaunchConfigurationDialog();

    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(ILaunchConfiguration)
     */
    public boolean isValid(ILaunchConfiguration config) {
        return fWorkingDirectoryBlock.isValid(config);
    }


    private void handleParametersAddButtonSelected() {
    	//TODO: 2.1 -> 3.0 this constructor takes a boolean in 3.0 
        NameValuePairDialog dialog = new NameValuePairDialog(getShell(),
                LauncherUIMessages.getString(
                    "cougaarlauncher.argumenttab.parameters.dialog.add.title"), //$NON-NLS-1$
                new String[] {
                    LauncherUIMessages.getString(
                        "cougaarlauncher.argumenttab.parameters.dialog.add.name.text"),
                    LauncherUIMessages.getString(
                        "cougaarlauncher.argumenttab.parameters.dialog.add.value.text")
                }, //$NON-NLS-1$ //$NON-NLS-2$
                new String[] { EMPTY_STRING, EMPTY_STRING }, true);
        openNewParameterDialog(dialog, null);
        setParametersButtonsEnableState();
    }


    private void handleParametersEditButtonSelected() {
        TableItem selectedItem = this.fVMParametersTable.getSelection()[0];
        String name = selectedItem.getText(0);
        String value = selectedItem.getText(1);
		//TODO: 2.1 -> 3.0 this constructor takes a boolean in 3.0
        NameValuePairDialog dialog = new NameValuePairDialog(getShell(),
                LauncherUIMessages.getString(
                    "cougaarlauncher.argumenttab.parameters.dialog.edit.title"), //$NON-NLS-1$
                new String[] {
                    LauncherUIMessages.getString(
                        "cougaarlauncher.argumenttab.parameters.dialog.edit.name.text"),
                    LauncherUIMessages.getString(
                        "cougaarlauncher.argumenttab.parameters.dialog.edit.value.text")
                }, //$NON-NLS-1$ //$NON-NLS-2$
                new String[] { name, value }, true);
        openNewParameterDialog(dialog, selectedItem);
    }


    private void handleParametersRemoveButtonSelected() {
        int[] selectedIndices = this.fVMParametersTable.getSelectionIndices();
        this.fVMParametersTable.remove(selectedIndices);
        setParametersButtonsEnableState();
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setLaunchConfigurationDialog(ILaunchConfigurationDialog)
     */
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        super.setLaunchConfigurationDialog(dialog);
        fWorkingDirectoryBlock.setLaunchConfigurationDialog(dialog);
    }


    /**
     * Set the enabled state of the three environment variable-related buttons
     * based on the selection in the Table widget.
     */
    private void setParametersButtonsEnableState() {
        int selectCount = this.fVMParametersTable.getSelectionIndices().length;
        if (selectCount < 1) {
            fParametersEditButton.setEnabled(false);
            fParametersRemoveButton.setEnabled(false);
        } else {
            fParametersRemoveButton.setEnabled(true);
            if (selectCount == 1) {
                fParametersEditButton.setEnabled(true);
            } else {
                fParametersEditButton.setEnabled(false);
            }
        }

        fParametersAddButton.setEnabled(true);
    }


    /**
     * Show the specified dialog and update the parameter table based on its
     * results.
     *
     * @param dialog DOCUMENT ME!
     * @param updateItem the item to update, or <code>null</code> if adding a
     *        new item
     */
    private void openNewParameterDialog(NameValuePairDialog dialog,
        TableItem updateItem) {
        if (dialog.open() != Window.OK) {
            return;
        }

        String[] nameValuePair = dialog.getNameValuePair();
        TableItem tableItem = updateItem;
        if (tableItem == null) {
            tableItem = getTableItemForName(nameValuePair[0]);
            if (tableItem == null) {
                tableItem = new TableItem(this.fVMParametersTable, SWT.NONE);
            }
        }

        tableItem.setText(nameValuePair);
        this.fVMParametersTable.setSelection(new TableItem[] { tableItem });
        setDirty(true);
        updateLaunchConfigurationDialog();
    }


    /**
     * Helper method that indicates whether the specified parameter name is
     * already present  in the parameters table.
     *
     * @param candidateName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private TableItem getTableItemForName(String candidateName) {
        TableItem[] items = this.fVMParametersTable.getItems();
        for (int i = 0; i < items.length; i++) {
            String name = items[i].getText(0);
            if (name.equals(candidateName)) {
                return items[i];
            }
        }

        return null;
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        // if (isDirty()) {
        configuration.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_NODE_NAME,
            fNameText.getText());
        configuration.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
            getMapFromParametersTable());
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
            setProgramArguments());
        configuration.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_DEFAULT_PARAMETERS,
            fArgumentsDefaultButton.getSelection());
        fWorkingDirectoryBlock.performApply(configuration);
        setDirty(false);

        // }
    }


    /**
     * DOCUMENT ME!
     *
     * @return
     */
    private String setProgramArguments() {
        return LauncherUIMessages.getString("cougaarLauncher.node.argument");
        //+ " -n " + "\"" + fNameText.getText() + "\"";
    }


    private Map getMapFromParametersTable() {
        TableItem[] items = fVMParametersTable.getItems();
        if (items.length == 0) {
            return null;
        }

        Map map = new HashMap(items.length);
        for (int i = 0; i < items.length; i++) {
            TableItem item = items[i];
            String key = item.getText(0);
            String value = item.getText(1);
            map.put(key, value);
        }

        return map;
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        setDefaultCougaarParameters(configuration);
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
            (String) null);
    }


    /**
     * DOCUMENT ME!
     *
     * @param configuration
     */
    private void setDefaultCougaarParameters(
        ILaunchConfigurationWorkingCopy configuration) {
        HashMap map = new HashMap();
        Enumeration keys = CougaarParameters.getKeys();
        IJavaProject project = getJavaProject(configuration);
        if (project != null) {
            String defaultVersion = CougaarPlugin.getCougaarPreference(project
                    .getProject(), ICougaarConstants.COUGAAR_VERSION);
            while (keys.hasMoreElements()) {
                String[] pair = new String[2];
                pair[0] = (String) keys.nextElement();
                String cip = "";
                cip = CougaarPlugin.getCougaarBaseLocation(defaultVersion);
                if ((cip != null) && !cip.equals("")) {
                    pair[1] = CougaarParameters.getString(pair[0]).replaceAll(ICougaarConstants.COUGAAR_INSTALL_PATH_STRING,
                            cip);
                }

                map.put(pair[0], pair[1]);
            }
        }

        configuration.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
            map);
    }


    private void displayDefaultCougaarParameters() {
        ILaunchConfiguration config = getLaunchConfiguration();
        ILaunchConfigurationWorkingCopy wc = null;
        try {
            if (config.isWorkingCopy()) {
                wc = (ILaunchConfigurationWorkingCopy) config;
            } else {
                wc = config.getWorkingCopy();
            }
        } catch (CoreException e) {
            JDIDebugUIPlugin.log(e);
        }

        IJavaProject javaProject = getJavaProject(wc);

        IProject project = null;
        String defaultVersion = "";
        if (javaProject != null) {
            project = javaProject.getProject();
            defaultVersion = CougaarPlugin.getCougaarPreference(project
                    .getProject(), ICougaarConstants.COUGAAR_VERSION);
        }

        HashMap map = new HashMap();
        Enumeration keys = CougaarParameters.getKeys();
        while (keys.hasMoreElements()) {
            String[] nameValuePair = new String[2];
            nameValuePair[0] = (String) keys.nextElement();
            if ((defaultVersion != null) && !defaultVersion.equals("")) {
                String cip = "";
                cip = CougaarPlugin.getCougaarBaseLocation(defaultVersion);
                if ((cip != null) && !cip.equals("")) {
                    nameValuePair[1] = CougaarParameters.getString(nameValuePair[0])
                                                        .replaceAll(ICougaarConstants.COUGAAR_INSTALL_PATH_STRING,
					cip);
                }


                map.put(nameValuePair[0], nameValuePair[1]);

                TableItem tableItem = null;
                if (tableItem == null) {
                    tableItem = getTableItemForName(nameValuePair[0]);
                    if (tableItem == null) {
                        tableItem = new TableItem(this.fVMParametersTable,
                                SWT.NONE);
                    }
                }

                tableItem.setText(nameValuePair);
                this.fVMParametersTable.setSelection(new TableItem[] { tableItem });
            }
        }

        setDirty(true);
        performApply(wc);
    }


    private void updateParametersFromConfig(ILaunchConfiguration config) {
        Map envVars = null;
        try {
            if (config != null) {
                envVars = config.getAttribute(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
                        (Map) null);
            }

            updateTable(envVars, this.fVMParametersTable);
            setParametersButtonsEnableState();
        } catch (CoreException ce) {
            JDIDebugUIPlugin.log(ce);
        }
    }


    private void updateTable(Map map, Table tableWidget) {
        tableWidget.removeAll();
        if (map == null) {
            return;
        }

        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = (String) map.get(key);
            TableItem tableItem = new TableItem(tableWidget, SWT.NONE);
            tableItem.setText(new String[] { key, value });
        }
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
     */
    public void initializeFrom(ILaunchConfiguration config) {
        boolean useDefault = true;
        try {
            useDefault = config.getAttribute(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_DEFAULT_PARAMETERS,
                    true);
            fWorkingDirectoryBlock.initializeFrom(config);
        } catch (CoreException e) {
            JDIDebugUIPlugin.log(e);
        }

        if (config == getLaunchConfiguration()) {
            if (!useDefault && !fArgumentsDefaultButton.getSelection()) {
                setDirty(false);
                return;
            }
        }

        setLaunchConfiguration(config);
        fArgumentsDefaultButton.setSelection(useDefault);

        try {
            fNameText.setText(config.getAttribute(
                    ICougaarLaunchConfigurationConstants.ATTR_NODE_NAME,
                    LauncherUIMessages.getString(
                        "cougaarlauncher.argumenttab.name.defaultvalue"))); //$NON-NLS-1$
        } catch (CoreException ce) {
            fNameText.setText(LauncherUIMessages.getString(
                    "cougaarlauncher.argumenttab.name.defaultvalue")); //$NON-NLS-1$
        }

        updateParametersFromConfig(config);
        fVMParametersTable.setEnabled(!useDefault);
        setDirty(false);
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
     * @return
     */
    public ILaunchConfiguration getLaunchConfiguration() {
        return fLaunchConfiguration;
    }


    /**
     * DOCUMENT ME!
     *
     * @param launchConfiguration
     */
    public void setLaunchConfiguration(ILaunchConfiguration launchConfiguration) {
        fLaunchConfiguration = launchConfiguration;
    }


    /**
     * Create some empty space
     *
     * @param comp DOCUMENT ME!
     */
    private void createVerticalSpacer(Composite comp) {
        new Label(comp, SWT.NONE);
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     */
    public String getName() {
        return LauncherUIMessages.getString("cougaarlauncher.argumenttab.name"); //$NON-NLS-1$
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
     */
    public Image getImage() {
        return JavaDebugImages.get(JavaDebugImages.IMG_VIEW_ARGUMENTS_TAB);
    }
}
