/*
 * Cougaar IDE
 * 
 * Copyright (C) 2003, Cougaar Software, Inc. <tcarrico@cougaarsoftware.com>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.cougaarsoftware.cougaar.ide.launcher.ui.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.JavaDebugImages;
import org.eclipse.jdt.internal.debug.ui.actions.ControlAccessibleListener;
import org.eclipse.jdt.internal.debug.ui.launcher.JavaLaunchConfigurationTab;
import org.eclipse.jdt.internal.debug.ui.launcher.NameValuePairDialog;
import org.eclipse.jdt.internal.debug.ui.launcher.WorkingDirectoryBlock;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;
import com.cougaarsoftware.cougaar.ide.launcher.core.constants.ICougaarLaunchConfigurationConstants;
import com.cougaarsoftware.cougaar.ide.launcher.ui.LauncherUIMessages;

/**
 * A launch configuration tab that displays and edits program arguments, VM
 * arguments, cougaar node name to launch, society xml file to use, and working
 * directory launch configuration attributes.
 * 
 * @author mabrams
 */
public class CougaarXMLParametersTab extends JavaLaunchConfigurationTab {

    protected static final String COUGAAR_NODE_NAME = "-Dorg.cougaar.node.name";

    protected static final String COUGAAR_SOCIETY_NAME = "-Dorg.cougaar.society.file";

    protected static final String COUGAAR_CONFIG_PATH = "-Dorg.cougaar.config.path";

    // Society arguments widgets
    protected Label fSocietyXMLFileLabel;

    protected Text fSocietyXMLFileText;

    /** The last launch config this tab was initialized from */
    protected ILaunchConfiguration fLaunchConfiguration;

    protected Table fParametersTable;

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private class CougaarParamsTabListener extends SelectionAdapter implements
            ModifyListener {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         */
        public void modifyText(ModifyEvent e) {
            updateLaunchConfigurationDialog();
            setDirty(true);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();
            if (source == fParametersTable) {
                setParametersButtonsEnableState();
            } else if (source == fParametersAddButton) {
                handleParametersAddButtonSelected();
            } else if (source == fParametersEditButton) {
                handleParametersEditButtonSelected();
            } else if (source == fParametersRemoveButton) {
                handleParametersRemoveButtonSelected();
            }
        }

    }

    private CougaarParamsTabListener fListener = new CougaarParamsTabListener();

    // Working directory
    protected WorkingDirectoryBlock fWorkingDirectoryBlock;

    private NodeSelectionComboBlock fNodeBlock;

    private String tabName;

    private Button fParametersEditButton;

    private Button fParametersRemoveButton;

    private Button fParametersAddButton;

    private Button fLoadXMLArgumentsButton;

    private Button fLoadDefaultArgumentsButton;

    private String prevSocietyPath;

    public CougaarXMLParametersTab() {
        fWorkingDirectoryBlock = new WorkingDirectoryBlock();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Font font = parent.getFont();
        Composite comp = new Composite(parent, parent.getStyle());
        setControl(comp);
        GridLayout layout = new GridLayout(1, true);
        comp.setLayout(layout);
        comp.setFont(font);

        Group group = new Group(comp, SWT.NONE);
        group.setFont(font);
        layout = new GridLayout(4, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        String controlName = (LauncherUIMessages
                .getString("CougaarXMLParametersTab.SocietySelection")); //$NON-NLS-1$
        group.setText(controlName);

        fSocietyXMLFileLabel = new Label(group, SWT.SINGLE);
        fSocietyXMLFileLabel.setText(LauncherUIMessages
                .getString("CougaarXMLParametersTab.SocietyFile"));
        GridData gd = new GridData(SWT.LEFT);
        fSocietyXMLFileLabel.setLayoutData(gd);

        fSocietyXMLFileText = new Text(group, SWT.SINGLE | SWT.WRAP
                | SWT.BORDER);
        fSocietyXMLFileText.setEditable(false);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        fSocietyXMLFileText.setLayoutData(gd);
        fSocietyXMLFileText.setFont(font);
        fSocietyXMLFileText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent evt) {
                updateLaunchConfigurationDialog();
            }
        });
        ControlAccessibleListener.addListener(fSocietyXMLFileText, group
                .getText());

        String buttonLabel = LauncherUIMessages
                .getString("CougaarXMLParametersTab.SocietyBrowse"); //$NON-NLS-1$
        Button browseSocietyButton = createPushButton(group, buttonLabel, null);
        browseSocietyButton.setLayoutData(new GridData(SWT.END, SWT.CENTER,
                false, false));
        browseSocietyButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell());
                String newPath = dialog.open();
                if (newPath != null) {
                    fSocietyXMLFileText.setText(newPath);
                }
                populateNodeNameList();
            }
        });

        fNodeBlock = new NodeSelectionComboBlock();
        fNodeBlock.createControl(group);
        fNodeBlock.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                updateSelectedNode((String) ((StructuredSelection) event
                        .getSelection()).getFirstElement());
                updateLaunchConfigurationDialog();
            }
        });
        Control control = fNodeBlock.getControl();
        gd = new GridData(SWT.FILL, SWT.NONE, true, true);
        control.setLayoutData(gd);

        createVerticalSpacer(comp, 1);

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
        parameterLabel.setText(LauncherUIMessages
                .getString("cougaarlauncher.argumenttab.parameterslabel.text")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalSpan = 2;
        parameterLabel.setLayoutData(gd);
        parameterLabel.setFont(font);

        fParametersTable = new Table(parametersComp, SWT.BORDER | SWT.SINGLE
                | SWT.V_SCROLL);
        fParametersTable
                .setData(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS);
        TableLayout tableLayout = new TableLayout();
        fParametersTable.setLayout(tableLayout);
        gd = new GridData(GridData.FILL_BOTH);
        fParametersTable.setLayoutData(gd);
        TableColumn column1 = new TableColumn(this.fParametersTable, SWT.NONE);
        column1
                .setText(LauncherUIMessages
                        .getString("cougaarlauncher.argumenttab.parameterscolumn.name.text")); //$NON-NLS-1$
        TableColumn column2 = new TableColumn(this.fParametersTable, SWT.NONE);
        column2
                .setText(LauncherUIMessages
                        .getString("cougaarlauncher.argumenttab.parameterscolumn.value.text")); //$NON-NLS-1$
        tableLayout.addColumnData(new ColumnWeightData(100));
        tableLayout.addColumnData(new ColumnWeightData(100));
        fParametersTable.setHeaderVisible(true);
        fParametersTable.setLinesVisible(true);
        fParametersTable.addSelectionListener(fListener);
        fParametersTable.addMouseListener(new MouseAdapter() {

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

        fParametersAddButton = createPushButton(
                envButtonComp,
                LauncherUIMessages
                        .getString("cougaarlauncher.argumenttab.parameters.button.add.text"),
                null); //$NON-NLS-1$
        fParametersAddButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                handleParametersAddButtonSelected();
            }
        });

        fParametersEditButton = createPushButton(
                envButtonComp,
                LauncherUIMessages
                        .getString("cougaarlauncher.argumenttab.parameters.button.edit.text"),
                null); //$NON-NLS-1$
        fParametersEditButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                handleParametersEditButtonSelected();
            }
        });

        fParametersRemoveButton = createPushButton(
                envButtonComp,
                LauncherUIMessages
                        .getString("cougaarlauncher.argumenttab.parameters.button.remove.text"),
                null); //$NON-NLS-1$
        fParametersRemoveButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                handleParametersRemoveButtonSelected();
            }
        });

        fLoadDefaultArgumentsButton = createPushButton(
                envButtonComp,
                LauncherUIMessages
                        .getString("CougaarXMLParametersTab.LoadDefaultArguments"),
                null);

        fLoadDefaultArgumentsButton
                .addSelectionListener(new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent evt) {
                        displayDefaultCougaarParameters();
                    }
                });

        fLoadXMLArgumentsButton = createPushButton(envButtonComp,
                LauncherUIMessages
                        .getString("CougaarXMLParametersTab.LoadXMLArguments"),
                null);
        fLoadXMLArgumentsButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                handleLoadArgumentsFromFileButtonSelected();
            }
        });

        createVerticalSpacer(comp, 1);
        fWorkingDirectoryBlock.createControl(comp);
    }

    /**
     * @param firstElement
     */
    protected void updateSelectedNode(String nodeName) {
        TableItem nodeNameItem = getTableItemForName(COUGAAR_NODE_NAME);
        if (nodeNameItem == null) {
            nodeNameItem = new TableItem(this.fParametersTable, SWT.NONE);
        }
        String[] nameValuePair = new String[] { COUGAAR_NODE_NAME, nodeName};
        nodeNameItem.setText(nameValuePair);
        setDirty(true);
    }

    private void handleParametersAddButtonSelected() {
        NameValuePairDialog dialog = new NameValuePairDialog(
                getShell(),
                LauncherUIMessages
                        .getString("cougaarlauncher.argumenttab.parameters.dialog.add.title"), //$NON-NLS-1$
                new String[] {
                        LauncherUIMessages
                                .getString("cougaarlauncher.argumenttab.parameters.dialog.add.name.text"),
                        LauncherUIMessages
                                .getString("cougaarlauncher.argumenttab.parameters.dialog.add.value.text")}, //$NON-NLS-1$ //$NON-NLS-2$
                new String[] { EMPTY_STRING, EMPTY_STRING});
        openNewParameterDialog(dialog, null);
        setParametersButtonsEnableState();
    }

    private void handleParametersEditButtonSelected() {
        TableItem selectedItem = this.fParametersTable.getSelection()[0];
        String name = selectedItem.getText(0);
        String value = selectedItem.getText(1);
        NameValuePairDialog dialog = new NameValuePairDialog(
                getShell(),
                LauncherUIMessages
                        .getString("cougaarlauncher.argumenttab.parameters.dialog.edit.title"), //$NON-NLS-1$
                new String[] {
                        LauncherUIMessages
                                .getString("cougaarlauncher.argumenttab.parameters.dialog.edit.name.text"),
                        LauncherUIMessages
                                .getString("cougaarlauncher.argumenttab.parameters.dialog.edit.value.text")}, //$NON-NLS-1$ //$NON-NLS-2$
                new String[] { name, value});
        openNewParameterDialog(dialog, selectedItem);
    }

    private void handleParametersRemoveButtonSelected() {
        int[] selectedIndices = this.fParametersTable.getSelectionIndices();
        this.fParametersTable.remove(selectedIndices);
        setParametersButtonsEnableState();
        updateLaunchConfigurationDialog();
    }

    /**
     * Set the enabled state of the three environment variable-related buttons
     * based on the selection in the Table widget.
     */
    private void setParametersButtonsEnableState() {
        int selectCount = this.fParametersTable.getSelectionIndices().length;
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
     * Helper method that indicates whether the specified parameter name is
     * already present in the parameters table.
     * 
     * @param candidateName
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected TableItem getTableItemForName(String candidateName) {
        if (fParametersTable != null) {
            TableItem[] items = this.fParametersTable.getItems();
            for (int i = 0; i < items.length; i++) {
                String name = items[i].getText(0);
                if (name.equals(candidateName)) {
                    return items[i];
                }
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        HashMap map = new HashMap();
        Enumeration keys = CougaarXMLParameters.getKeys();
        IJavaProject project = getJavaProject(configuration);
        if (project != null) {
            String defaultVersion = CougaarPlugin.getCougaarPreference(project
                    .getProject(), ICougaarConstants.COUGAAR_VERSION);
            String cip = CougaarPlugin.getCougaarBaseLocation(defaultVersion);
            while (keys.hasMoreElements()) {
                String[] pair = new String[2];
                pair[0] = (String) keys.nextElement();

                if ((cip != null) && !cip.equals("")) {                   
                    pair[1] = replaceCIP(cip, CougaarXMLParameters
                            .getString(pair[0]));
                }

                map.put(pair[0], pair[1]);
            }
        }
        configuration
                .setAttribute(
                        ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
                        map);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
     */
    public void initializeFrom(ILaunchConfiguration config) {
        setParametersButtonsEnableState();
        fWorkingDirectoryBlock.initializeFrom(config);
        setLaunchConfiguration(config);
        try {
            fSocietyXMLFileText
                    .setText(config
                            .getAttribute(
                                    ICougaarLaunchConfigurationConstants.ATTR_SOCIETY_NAME,
                                    LauncherUIMessages
                                            .getString("cougaarlauncher.argumenttab.name.defaultvalue"))); //$NON-NLS-1$
            String nodeName = config
                    .getAttribute(
                            ICougaarLaunchConfigurationConstants.ATTR_NODE_NAME,
                            LauncherUIMessages
                                    .getString(LauncherUIMessages
                                            .getString("cougaarlauncher.argumenttab.name.defaultvalue")));
            if (nodeName != null && !nodeName.equals("")) {
                populateNodeNameList();
                fNodeBlock.setNodeSelection(nodeName);
            }
        } catch (CoreException ce) {
            MessageDialog.openError(getShell(), "ERROR", ce.getMessage());
        }
        updateParametersFromConfig(config);
        setDirty(false);

    }

    protected void updateParametersFromConfig(ILaunchConfiguration config) {
        Map envVars = null;
        try {
            if (config != null) {
                envVars = config
                        .getAttribute(
                                ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
                                (Map) null);
            }

            updateTable(envVars, this.fParametersTable);
            setParametersButtonsEnableState();
        } catch (CoreException ce) {
            JDIDebugUIPlugin.log(ce);
        }
    }

    protected void updateTable(Map map, Table tableWidget) {
        tableWidget.removeAll();
        if (map == null) {
            return;
        }

        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = (String) map.get(key);
            TableItem tableItem = new TableItem(tableWidget, SWT.NONE);
            tableItem.setText(new String[] { key, value});
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(
                ICougaarLaunchConfigurationConstants.ATTR_NODE_NAME, fNodeBlock
                        .getNodeName());
        configuration.setAttribute(
                ICougaarLaunchConfigurationConstants.ATTR_SOCIETY_NAME,
                fSocietyXMLFileText.getText());
        configuration.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
                LauncherUIMessages.getString("cougaarLauncher.node.argument"));
        setCougaarNodeName();
        setCougaarSocietyFileName();
        updateCougaarConfigPath();
        configuration
                .setAttribute(
                        ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
                        getMapFromParametersTable());

        fWorkingDirectoryBlock.performApply(configuration);
        setDirty(false);
    }

    protected Map getMapFromParametersTable() {
        TableItem[] items = fParametersTable.getItems();

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

    protected void setCougaarNodeName() {
        TableItem nodeNameItem = getTableItemForName(COUGAAR_NODE_NAME);
        if (nodeNameItem == null) {
            nodeNameItem = new TableItem(this.fParametersTable, SWT.NONE);
        }

        String[] nameValuePair = new String[] { COUGAAR_NODE_NAME,
                fNodeBlock.getNodeName()};
        nodeNameItem.setText(nameValuePair);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     */
    public String getName() {
        if (tabName == null) {
            tabName = LauncherUIMessages
                    .getString("CougaarXMLParametersTab.Name");
        }
        return tabName;
    }

    protected void populateNodeNameList() {
        String locationName = fSocietyXMLFileText.getText();
        if (locationName != null && !locationName.equals("")) {
            File file = new File(locationName);
            if (file.exists()) {
                List nodeNameList = getNodeNameList(file);
                fNodeBlock.setNodeNames(nodeNameList);
            }
        }
        setDirty(true);
    }

    protected List getNodeNameList(File societyXMLFile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        List nodeNameList = null;
        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(societyXMLFile);
            NodeList list = doc.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeName().equalsIgnoreCase("society") || node.getNodeName().equalsIgnoreCase("host")) {
                    nodeNameList = getNodeNameList(node.getChildNodes());
                } 
            }
        } catch (ParserConfigurationException e1) {
            MessageDialog.openError(getShell(), "ERROR", e1.getMessage());
        } catch (SAXException e) {
            MessageDialog.openError(getShell(), "ERROR", e.getMessage());
        } catch (IOException e) {
            MessageDialog.openError(getShell(), "ERROR", e.getMessage());
        }
        return nodeNameList;
    }

    protected List getNodeNameList(NodeList nodeList) {
        List nodeNameList = null;
        if (nodeList != null) {
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node childNode = nodeList.item(j);
                if (childNode.getNodeName().equalsIgnoreCase("host")) {
                    NodeList nList = childNode.getChildNodes();
                    if (nodeNameList == null) {
                        nodeNameList = getNodeNameList(nList);
                    } else {
                        List tmpList = getNodeNameList(nList);
                        if (tmpList != null) {
                            nodeNameList.addAll(getNodeNameList(nList));
                        }
                    }
                } else if (childNode.getNodeName().equalsIgnoreCase("node")) {
                    NamedNodeMap nnMap = childNode.getAttributes();
                    Node nameNode = nnMap.getNamedItem("name");
                    if (nodeNameList == null) {
                        nodeNameList = new ArrayList();
                    }
                    nodeNameList.add(nameNode.getNodeValue());
                }
            }
        }
        return nodeNameList;
    }

    /**
     * Show the specified dialog and update the parameter table based on its
     * results.
     * 
     * @param updateItem
     *            the item to update, or <code>null</code> if adding a new
     *            item
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
                tableItem = new TableItem(this.fParametersTable, SWT.NONE);
            }
        }
        tableItem.setText(nameValuePair);
        this.fParametersTable.setSelection(new TableItem[] { tableItem});
        updateLaunchConfigurationDialog();
    }

    public ILaunchConfiguration getLaunchConfiguration() {
        return fLaunchConfiguration;
    }

    public void setLaunchConfiguration(ILaunchConfiguration launchConfiguration) {
        fLaunchConfiguration = launchConfiguration;
    }

    protected void displayDefaultCougaarParameters() {
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
        Enumeration keys = CougaarINIParameters.getKeys();
        while (keys.hasMoreElements()) {
            String[] nameValuePair = new String[2];
            nameValuePair[0] = (String) keys.nextElement();
            if ((defaultVersion != null) && !defaultVersion.equals("")) {
                String cip = "";
                cip = CougaarPlugin.getCougaarBaseLocation(defaultVersion);
                if ((cip != null) && !cip.equals("")) {
                    nameValuePair[1] = replaceCIP(cip, CougaarXMLParameters.getString(
                            nameValuePair[0]));                                 
                }

                map.put(nameValuePair[0], nameValuePair[1]);

                TableItem tableItem = null;
                if (tableItem == null) {
                    tableItem = getTableItemForName(nameValuePair[0]);
                    if (tableItem == null) {
                        tableItem = new TableItem(this.fParametersTable,
                                SWT.NONE);
                    }
                }

                tableItem.setText(nameValuePair);
                this.fParametersTable
                        .setSelection(new TableItem[] { tableItem});
            }
        }

        setDirty(true);
        performApply(wc);
    }

    /**
     * Replaces the COUGAAR_INSTALL_PATH variable with the actual value for
     * the CIP.
     * 
     * @param cip
     * @param original string
     * @return the original string with the actual CIP
     */
    private String replaceCIP(String cip, String originalString) {
        String pattern = "[\\$%]?\\{?"
            + ICougaarConstants.COUGAAR_INSTALL_PATH_STRING
            + "\\}?[%]?";                 
        String tmpString = originalString.replaceAll(pattern, cip);  
        return tmpString;
    }

    protected void handleLoadArgumentsFromFileButtonSelected() {
        String locationName = fSocietyXMLFileText.getText();
        if (locationName != null && !locationName.equals("")) {

            File file = null;
            file = new File(locationName);
            if (!file.exists()) {
                MessageDialog
                        .openError(
                                getShell(),
                                LauncherUIMessages
                                        .getString("CougaarEnvironmentTab.SocietyFileDoesNotExistTitle"),
                                LauncherUIMessages
                                        .getString("CougaarEnvironmentTab.SocietyFileDoesNotExistMessage"));
            } else {
                String nodeName = fNodeBlock.getNodeName();
                if (nodeName != null && !nodeName.equals("")) {

                    setDirty(true);
                    this.fParametersTable.removeAll();
                    displaySocietyXMLParameters(file, nodeName);
                    setParametersButtonsEnableState();
                    updateLaunchConfigurationDialog();
                } else {
                    MessageDialog
                            .openError(
                                    getShell(),
                                    LauncherUIMessages
                                            .getString("CougaarEnvironmentTab.NoNodeSelectedTitle"),
                                    LauncherUIMessages
                                            .getString("CougaarEnvironmentTab.NoNodeSelectedMessage"));
                }
            }
        } else {
            MessageDialog
                    .openError(
                            getShell(),
                            LauncherUIMessages
                                    .getString("CougaarEnvironmentTab.NoSocietyFileTitle"),
                            LauncherUIMessages
                                    .getString("CougaarEnvironmentTab.NoSocietyFileMessage"));
        }
    }

    protected void displaySocietyXMLParameters(File societyXMLFile,
            String nodeName) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        List paramList = null;
        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(societyXMLFile);
            NodeList list = doc.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeName().equalsIgnoreCase("society")) {
                    NodeList nodeList = node.getChildNodes();
                    paramList = getNodeVMParams(nodeList, nodeName);
                }

            }
        } catch (ParserConfigurationException e1) {
            MessageDialog.openError(getShell(), "ERROR", e1.getMessage());
        } catch (SAXException e) {
            MessageDialog.openError(getShell(), "ERROR", e.getMessage());
        } catch (IOException e) {
            MessageDialog.openError(getShell(), "ERROR", e.getMessage());
        }

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
        if (paramList != null) {
            Iterator iter = paramList.iterator();
            while (iter.hasNext()) {
                String value = (String) iter.next();
                String[] nameValuePair = new String[2];
                nameValuePair = value.split("=");
                if (nameValuePair.length < 2) {
                    String tmp = nameValuePair[0];
                    nameValuePair = new String[] {tmp, tmp};
                }
                if ((defaultVersion != null) && !defaultVersion.equals("")) {
                    String cip = "";
                    cip = CougaarPlugin.getCougaarBaseLocation(defaultVersion);                   
                    if ((cip != null) && !cip.equals("")) {                        
                        nameValuePair[1] = replaceCIP(cip, nameValuePair[1]);
                    }

                    map.put(nameValuePair[0], nameValuePair[1]);

                    TableItem tableItem = null;
                    if (tableItem == null) {
                        tableItem = getTableItemForName(nameValuePair[0]);
                        if (tableItem == null) {
                            tableItem = new TableItem(this.fParametersTable,
                                    SWT.NONE);
                        }
                    }

                    tableItem.setText(nameValuePair);
                    this.fParametersTable
                            .setSelection(new TableItem[] { tableItem});
                }

            }
        }

        setDirty(true);
        performApply(wc);
    }

    /**
     * @param nodeList
     */
    private List getNodeVMParams(NodeList nodeList, String nodeName) {
        List paramList = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equalsIgnoreCase("host")) {                
                NodeList nList = node.getChildNodes();
                if (paramList == null) {
                    paramList = getNodeVMParams(nList, nodeName); 
                } else {
                    List tmpList = getNodeVMParams(nList, nodeName); 
                    if (tmpList != null) {
                        paramList.addAll(tmpList);
                    }
                }
            } else if (node.getNodeName().equalsIgnoreCase("node")) {
                NamedNodeMap nnMap = node.getAttributes();
                Node nameNode = nnMap.getNamedItem("name");
                if (nameNode.getNodeValue().equalsIgnoreCase(nodeName)) {
                    NodeList pList = node.getChildNodes();
                    for (int j = 0; j < pList.getLength(); j++) {
                        Node param = pList.item(j);
                        if (param.getNodeName()
                                .equalsIgnoreCase("vm_parameter")) {
                            String pValue = param.getNodeValue();
                            if (pValue != null) {
                                paramList.add(pValue.trim());
                            } else {
                                Node childNode = param.getFirstChild();

                                paramList.add(childNode.getNodeValue().trim());

                            }
                        }
                    }
                }
            }
        }
        return paramList;
    }

    /**
     * Return the IJavaProject corresponding to the project name in the project
     * name text field, or null if the text does not match a project name.
     * 
     */
    protected IJavaProject getJavaProject(ILaunchConfigurationWorkingCopy config) {
        String projectName = "";
        try {
            projectName = config.getAttribute(
                    IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
                    EMPTY_STRING);
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // String projectName = fProjText.getText().trim();
        if (projectName.length() < 1) {
            return null;
        }

        return getJavaModel().getJavaProject(projectName);

    }

    protected IJavaModel getJavaModel() {
        return JavaCore.create(getWorkspaceRoot());
    }

    protected IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    protected void setCougaarSocietyFileName() {
        TableItem societyNameItem = getTableItemForName(COUGAAR_SOCIETY_NAME);
        if (societyNameItem == null) {
            societyNameItem = new TableItem(this.fParametersTable, SWT.NONE);
        }
        String societyFile = fSocietyXMLFileText.getText();
        if (societyFile.indexOf("/") > 0) {
            societyFile = societyFile
                    .substring(societyFile.lastIndexOf("/") + 1);
        } else {
            societyFile = societyFile
                    .substring(societyFile.lastIndexOf("\\") + 1);
        }
        String[] nameValuePair = new String[] { COUGAAR_SOCIETY_NAME,
                societyFile};
        societyNameItem.setText(nameValuePair);
    }

    protected void updateCougaarConfigPath() {
        TableItem configPath = getTableItemForName(COUGAAR_CONFIG_PATH);
        if (configPath == null) {
            configPath = new TableItem(this.fParametersTable, SWT.NONE);
        }
        String societyPath = fSocietyXMLFileText.getText();
        if (societyPath.indexOf("/") > 0) {
            societyPath = societyPath
                    .substring(0, societyPath.lastIndexOf("/"));
        } else if (societyPath.indexOf("\\") > 0) {
            societyPath = societyPath.substring(0, societyPath
                    .lastIndexOf("\\"));
        }
        String oldPath = configPath.getText(1);
        if (prevSocietyPath != null && !prevSocietyPath.equals("")
                && !prevSocietyPath.equals(";")) {
            StringBuffer sb = new StringBuffer(oldPath.length());
            int start = 0;
            int end = 0;
            while ((end = oldPath.indexOf(prevSocietyPath, start)) != -1) {
                sb.append(oldPath.substring(start, end)).append("");
                start = end + prevSocietyPath.length();
            }
            sb.append(oldPath.substring(start));
            oldPath = sb.toString();
        }
        prevSocietyPath = societyPath;
        if (societyPath != null && !societyPath.equals("")) {
            String newPath = oldPath + ";" + societyPath;
            if (!newPath.equals(";")) {
                String[] nameValuePair = new String[] { COUGAAR_CONFIG_PATH,
                        newPath};
                configPath.setText(nameValuePair);
            }
        }
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(ILaunchConfiguration)
     */
    public boolean isValid(ILaunchConfiguration config) {
        return fWorkingDirectoryBlock.isValid(config);
    }

    /**
     * @see ILaunchConfigurationTab#getImage()
     */
    public Image getImage() {
        return JavaDebugImages.get(JavaDebugImages.IMG_VIEW_ARGUMENTS_TAB);
    }
    
}