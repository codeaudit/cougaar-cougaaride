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

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

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
		fNameLabel.setText(LauncherUIMessages.getString("cougaarlauncher.argumenttab.namelabel.text")); //$NON-NLS-1$
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

		fNameLabel = new Label(nameComp, SWT.NONE);
		fNameLabel.setText(LauncherUIMessages.getString("CougaarXMLParametersTab.(cougaar_society_name)_1")); //$NON-NLS-1$
		fNameLabel.setFont(font);

		fSocietyNameText = new Text(nameComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fSocietyNameText.setLayoutData(gd);
		fSocietyNameText.setFont(font);
		fSocietyNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});

		Label blank = new Label(nameComp, SWT.NONE);
		blank.setText(EMPTY_STRING);
		Label hint = new Label(nameComp, SWT.NONE);
		hint.setText(LauncherUIMessages.getString("CougaarINIParametersTab.(cougaar_node_name)_1")); //$NON-NLS-1$
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		hint.setLayoutData(gd);
		hint.setFont(font);
		createVerticalSpacer(comp, 1);

		fArgumentsDefaultButton = new Button(comp, SWT.CHECK);

		fArgumentsDefaultButton.setText(LauncherUIMessages.getString("CougaarEnvironmentTab.Use_defau&lt_arguments_1")); //$NON-NLS-1$
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
		parameterLabel.setText(LauncherUIMessages.getString("cougaarlauncher.argumenttab.parameterslabel.text")); //$NON-NLS-1$
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
		column1.setText(LauncherUIMessages.getString("cougaarlauncher.argumenttab.parameterscolumn.name.text")); //$NON-NLS-1$
		TableColumn column2 = new TableColumn(this.fVMParametersTable, SWT.NONE);
		column2.setText(LauncherUIMessages.getString("cougaarlauncher.argumenttab.parameterscolumn.value.text")); //$NON-NLS-1$
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
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
		envButtonComp.setLayoutData(gd);
		envButtonComp.setFont(font);

		createVerticalSpacer(comp, 1);

		fWorkingDirectoryBlock.createControl(comp);

		fParametersAddButton = createPushButton(envButtonComp, LauncherUIMessages.getString("cougaarlauncher.argumenttab.parameters.button.add.text"), null); //$NON-NLS-1$
		fParametersAddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleParametersAddButtonSelected();
			}
		});

		fParametersEditButton = createPushButton(envButtonComp, LauncherUIMessages.getString("cougaarlauncher.argumenttab.parameters.button.edit.text"), null); //$NON-NLS-1$
		fParametersEditButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleParametersEditButtonSelected();
			}
		});

		fParametersRemoveButton = createPushButton(envButtonComp, LauncherUIMessages.getString("cougaarlauncher.argumenttab.parameters.button.remove.text"), null); //$NON-NLS-1$
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
	protected void setDefaultCougaarParameters(ILaunchConfigurationWorkingCopy configuration) {
		HashMap map = new HashMap();
		Enumeration keys = CougaarXMLParameters.getKeys();
		IJavaProject project = getJavaProject(configuration);
		if (project != null) {
			String defaultVersion =
				CougaarPlugin.getCougaarPreference(project.getProject(), ICougaarConstants.COUGAAR_VERSION);
			String cip = CougaarPlugin.getCougaarBaseLocation(defaultVersion);
			while (keys.hasMoreElements()) {
				String[] pair = new String[2];
				pair[0] = (String) keys.nextElement();

				if ((cip != null) && !cip.equals("")) {
					pair[1] =
						CougaarXMLParameters.getString(pair[0]).replaceAll(
							ICougaarConstants.COUGAAR_INSTALL_PATH_STRING,
							cip);
				}

				map.put(pair[0], pair[1]);
			}
		}

		configuration.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS, map);
	}

	protected void setCougaarSocietyFileName() {
		TableItem societyNameItem = getTableItemForName(COUGAAR_SOCIETY_NAME);
		if (societyNameItem == null) {
			societyNameItem = new TableItem(this.fVMParametersTable, SWT.NONE);
		}

		String[] nameValuePair = new String[] { COUGAAR_SOCIETY_NAME, fSocietyNameText.getText()};
		societyNameItem.setText(nameValuePair);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// if (isDirty()) {
		configuration.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_NODE_NAME, fNameText.getText());
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, setProgramArguments());
		setCougaarNodeName();
		setCougaarSocietyFileName();
		configuration.setAttribute(
			ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
			getMapFromParametersTable());
		configuration.setAttribute(
			ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_DEFAULT_PARAMETERS,
			fArgumentsDefaultButton.getSelection());
		fWorkingDirectoryBlock.performApply(configuration);
		setDirty(false);

		// }
	}
}
