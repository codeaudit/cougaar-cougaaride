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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.crimson.tree.TextNode;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
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
 * @author mabrams
 */
public class CougaarXMLParametersTab extends CougaarINIParametersTab {

    protected Text fSocietyNameText;

    protected static final String COUGAAR_SOCIETY_NAME = "-Dorg.cougaar.society.file";

    protected Button fArgumentsFromFileButton;

    protected Button fBrowseForSocietyXMLButton;

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
        fNameLabel.setText(LauncherUIMessages
                .getString("cougaarlauncher.argumenttab.namelabel.text")); //$NON-NLS-1$
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

        Composite societyXML = new Composite(comp, SWT.NULL);
        societyXML.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        societyXML.setLayout(layout);

        fNameLabel = new Label(societyXML, SWT.NONE);
        fNameLabel.setText(LauncherUIMessages
                .getString("CougaarXMLParametersTab.(cougaar_society_name)_1")); //$NON-NLS-1$
        fNameLabel.setFont(font);

        fSocietyNameText = new Text(societyXML, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        fSocietyNameText.setLayoutData(gd);
        fSocietyNameText.setFont(font);
        fSocietyNameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent evt) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        });

        fBrowseForSocietyXMLButton = new Button(societyXML, SWT.PUSH);
        fBrowseForSocietyXMLButton.setText(LauncherUIMessages
                .getString("CougaarXMLParametersTab.SocietyBrowse"));
        gd = new GridData(GridData.CENTER);
        fBrowseForSocietyXMLButton.setLayoutData(gd);
        fBrowseForSocietyXMLButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                browseForInstallDir();

            }

        });

        Label blank = new Label(nameComp, SWT.NONE);
        blank.setText(EMPTY_STRING);
        Label hint = new Label(nameComp, SWT.NONE);
        hint.setText(LauncherUIMessages
                .getString("CougaarINIParametersTab.(cougaar_node_name)_1")); //$NON-NLS-1$
        gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        hint.setLayoutData(gd);
        hint.setFont(font);
        createVerticalSpacer(comp, 1);

        fArgumentsDefaultButton = new Button(comp, SWT.CHECK);

        fArgumentsDefaultButton.setText(LauncherUIMessages
                .getString("CougaarEnvironmentTab.Use_defau&lt_arguments_1")); //$NON-NLS-1$
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 2;
        fArgumentsDefaultButton.setLayoutData(gd);
        fArgumentsDefaultButton.setFont(font);

        fArgumentsDefaultButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                handleArgumentsDefaultButtonSelected();
            }
        });

        fArgumentsFromFileButton = new Button(comp, SWT.CHECK);

        fArgumentsFromFileButton.setText(LauncherUIMessages
                .getString("CougaarEnvironmentTab.Use_file&lt_arguments_1")); //$NON-NLS-1$
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 2;
        fArgumentsFromFileButton.setLayoutData(gd);
        fArgumentsFromFileButton.setFont(font);

        fArgumentsFromFileButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                handleArgumentsFromFileButtonSelected();
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
        parameterLabel.setText(LauncherUIMessages
                .getString("cougaarlauncher.argumenttab.parameterslabel.text")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalSpan = 2;
        parameterLabel.setLayoutData(gd);
        parameterLabel.setFont(font);

        fVMParametersTable = new Table(parametersComp, SWT.BORDER | SWT.MULTI);
        fVMParametersTable
                .setData(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS);
        TableLayout tableLayout = new TableLayout();
        fVMParametersTable.setLayout(tableLayout);
        fVMParametersTable.setFont(font);
        gd = new GridData(GridData.FILL_BOTH);
        fVMParametersTable.setLayoutData(gd);
        TableColumn column1 = new TableColumn(this.fVMParametersTable, SWT.NONE);
        column1
                .setText(LauncherUIMessages
                        .getString("cougaarlauncher.argumenttab.parameterscolumn.name.text")); //$NON-NLS-1$
        TableColumn column2 = new TableColumn(this.fVMParametersTable, SWT.NONE);
        column2
                .setText(LauncherUIMessages
                        .getString("cougaarlauncher.argumenttab.parameterscolumn.value.text")); //$NON-NLS-1$
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

    }

    /**
     * DOCUMENT ME!
     * 
     * @param configuration
     */
    protected void setDefaultCougaarParameters(
            ILaunchConfigurationWorkingCopy configuration) {
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
                    pair[1] = CougaarXMLParameters
                            .getString(pair[0])
                            .replaceAll(
                                    ICougaarConstants.COUGAAR_INSTALL_PATH_STRING,
                                    cip);
                }

                map.put(pair[0], pair[1]);
            }
        }

        configuration
                .setAttribute(
                        ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
                        map);
    }

    protected void setCougaarSocietyFileName() {
        TableItem societyNameItem = getTableItemForName(COUGAAR_SOCIETY_NAME);
        if (societyNameItem == null) {
            societyNameItem = new TableItem(this.fVMParametersTable, SWT.NONE);
        }
        String societyFile = fSocietyNameText.getText();
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

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        // if (isDirty()) {
        configuration.setAttribute(
                ICougaarLaunchConfigurationConstants.ATTR_NODE_NAME, fNameText
                        .getText());
        configuration.setAttribute(
                ICougaarLaunchConfigurationConstants.ATTR_SOCIETY_NAME,
                fSocietyNameText.getText());
        configuration.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
                setProgramArguments());

        setCougaarNodeName();

        setCougaarSocietyFileName();
        configuration
                .setAttribute(
                        ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
                        getMapFromParametersTable());
        configuration
                .setAttribute(
                        ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_DEFAULT_PARAMETERS,
                        fArgumentsDefaultButton.getSelection());

        configuration
                .setAttribute(
                        ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_LOAD_PARAMS_FROM_XML,
                        fArgumentsDefaultButton.getSelection());
        fWorkingDirectoryBlock.performApply(configuration);
        setDirty(false);

        // }
    }

    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
     */
    public void initializeFrom(ILaunchConfiguration config) {
        boolean useDefault = true;
        boolean useSocietyPrams = false;
        try {
            useDefault = config
                    .getAttribute(
                            ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_DEFAULT_PARAMETERS,
                            true);
            useSocietyPrams = config
                    .getAttribute(
                            ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_LOAD_PARAMS_FROM_XML,
                            false);
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
		fArgumentsFromFileButton.setSelection(useSocietyPrams);
        try {
            fNameText
                    .setText(config
                            .getAttribute(
                                    ICougaarLaunchConfigurationConstants.ATTR_NODE_NAME,
                                    LauncherUIMessages
                                            .getString("cougaarlauncher.argumenttab.name.defaultvalue"))); //$NON-NLS-1$
            fSocietyNameText
                    .setText(config
                            .getAttribute(
                                    ICougaarLaunchConfigurationConstants.ATTR_SOCIETY_NAME,
                                    LauncherUIMessages
                                            .getString("cougaarlauncher.argumenttab.name.defaultvalue"))); //$NON-NLS-1$
        } catch (CoreException ce) {
            fNameText
                    .setText(LauncherUIMessages
                            .getString("cougaarlauncher.argumenttab.name.defaultvalue")); //$NON-NLS-1$
        }

        updateParametersFromConfig(config);
        fVMParametersTable.setEnabled(!useDefault);
        setDirty(false);
    }

    /**
     * The load paramets from file button has been toggled
     */
    protected void handleArgumentsFromFileButtonSelected() {
        String locationName = fSocietyNameText.getText();
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
                fArgumentsFromFileButton.setSelection(false);
            } else {
                String nodeName = fNameText.getText();
                if (nodeName != null && !nodeName.equals("")) {

                    setDirty(true);
                    boolean loadFromFile = fArgumentsFromFileButton
                            .getSelection();
                    fArgumentsFromFileButton.setSelection(loadFromFile);

                    if (loadFromFile) {
                        clearTableItems();
                        displaySocietyXMLParameters(file, nodeName);
                    }

                    fVMParametersTable.setEnabled(true);

                    fArgumentsDefaultButton.setSelection(false);
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
                    fArgumentsFromFileButton.setSelection(false);
                }
            }
        } else {
            fArgumentsFromFileButton.setSelection(false);
            MessageDialog
                    .openError(
                            getShell(),
                            LauncherUIMessages
                                    .getString("CougaarEnvironmentTab.NoSocietyFileTitle"),
                            LauncherUIMessages
                                    .getString("CougaarEnvironmentTab.NoSocietyFileMessage"));
        }

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
        fArgumentsFromFileButton.setSelection(false);
        setParametersButtonsEnableState();
        updateLaunchConfigurationDialog();

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
                    System.out.println(paramList.size());
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
                String[] nameValuePair = value.split("=");
                if ((defaultVersion != null) && !defaultVersion.equals("")) {
                    String cip = "";
                    cip = CougaarPlugin.getCougaarBaseLocation(defaultVersion);
                    if ((cip != null) && !cip.equals("")) {
                        nameValuePair[1] = nameValuePair[1].replaceAll(
                                ICougaarConstants.COUGAAR_INSTALL_PATH_STRING,
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
                    this.fVMParametersTable
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
                paramList = getNodeVMParams(nList, nodeName);
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
                                if (childNode instanceof TextNode) {
                                    paramList.add(childNode.getNodeValue()
                                            .trim());
                                }
                            }
                        }
                    }
                }
            }
        }
        return paramList;
    }

    private void browseForInstallDir() {
        FileDialog dialog = new FileDialog(getShell());

        String newPath = dialog.open();
        if (newPath != null) {
            fSocietyNameText.setText(newPath);
        }
    }

}
