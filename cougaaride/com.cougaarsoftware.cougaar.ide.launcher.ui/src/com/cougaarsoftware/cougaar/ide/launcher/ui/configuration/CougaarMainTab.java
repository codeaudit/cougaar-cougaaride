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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.internal.debug.ui.launcher.MainMethodSearchEngine;
import org.eclipse.jdt.internal.debug.ui.launcher.SharedJavaMainTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;

/**
 * DOCUMENT ME!
 * 
 * @author mabrams
 */
public class CougaarMainTab extends SharedJavaMainTab {

	/** DOCUMENT ME! */
	public static String projectName = "";

	// UI widgets
	private Button fSearchExternalJarsCheckButton;

	private Button fConsiderInheritedMainButton;

	private Button fStopInMainCheckButton;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param parent
	 *            DOCUMENT ME!
	 */
	public void createControl(Composite parent) {

		Font font = parent.getFont();
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
				IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		comp.setLayout(topLayout);
		comp.setFont(font);
		createProjectEditor(comp);
		createVerticalSpacer(comp, 1);
		fSearchExternalJarsCheckButton = createCheckButton(parent,
				LauncherMessages.JavaMainTab_E_xt__jars_6);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		fSearchExternalJarsCheckButton.setLayoutData(gd);
		fSearchExternalJarsCheckButton
				.addSelectionListener(getDefaultListener());
		fConsiderInheritedMainButton = createCheckButton(parent,
				LauncherMessages.JavaMainTab_22);
		gd = new GridData();
		gd.horizontalSpan = 2;
		fConsiderInheritedMainButton.setLayoutData(gd);
		fConsiderInheritedMainButton.addSelectionListener(getDefaultListener());
		fStopInMainCheckButton = createCheckButton(parent,
				LauncherMessages.JavaMainTab_St_op_in_main_1);
		gd = new GridData();
		fStopInMainCheckButton.setLayoutData(gd);
		fStopInMainCheckButton.addSelectionListener(getDefaultListener());
		createMainTypeEditor(comp, LauncherMessages.JavaMainTab_Main_cla_ss__4,
				new Button[] { fSearchExternalJarsCheckButton,
						fConsiderInheritedMainButton, fStopInMainCheckButton });

	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		IJavaElement javaElement = getContext();
		if (javaElement != null) {
			initializeJavaProject(javaElement, config);
		}// end if
		else {
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					EMPTY_STRING);
		}// end else
		initializeMainTypeAndName(javaElement, config);

		config.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				ICougaarConstants.COUGAAR_MAIN_CLASS);
		config.setAttribute(JavaMainTab.ATTR_INCLUDE_EXTERNAL_JARS, true);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param config
	 *            DOCUMENT ME!
	 */
	public void initializeFrom(ILaunchConfiguration config) {
		super.initializeFrom(config);
		updateMainTypeFromConfig(config);
	}

	protected void handleSearchButtonSelected() {
		IJavaProject project = getJavaProject();
		IJavaElement[] elements = null;
		if ((project == null) || !project.exists()) {
			IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace()
					.getRoot());
			if (model != null) {
				try {
					elements = model.getJavaProjects();
				}// end try
				catch (JavaModelException e) {
					JDIDebugUIPlugin.log(e);
				}
			}// end if
		}// end if
		else {
			elements = new IJavaElement[] { project };
		}// end else
		if (elements == null) {
			elements = new IJavaElement[] {};
		}// end if
		int constraints = IJavaSearchScope.SOURCES;
		if (fSearchExternalJarsCheckButton.getSelection()) {
			constraints |= IJavaSearchScope.APPLICATION_LIBRARIES;
			constraints |= IJavaSearchScope.SYSTEM_LIBRARIES;
		}// end if
		IJavaSearchScope searchScope = SearchEngine.createJavaSearchScope(
				elements, constraints);
		MainMethodSearchEngine engine = new MainMethodSearchEngine();
		IType[] types = null;
		try {
			types = engine.searchMainMethods(getLaunchConfigurationDialog(),
					searchScope, fConsiderInheritedMainButton.getSelection());
		}// end try
		catch (InvocationTargetException e) {
			setErrorMessage(e.getMessage());
			return;
		}// end catch
		catch (InterruptedException e) {
			setErrorMessage(e.getMessage());
			return;
		}// end catch
		SelectionDialog dialog = null;
		try {
			dialog = JavaUI.createTypeDialog(getShell(),
					getLaunchConfigurationDialog(), SearchEngine
							.createJavaSearchScope(types),
					IJavaElementSearchConstants.CONSIDER_CLASSES, false, "**"); //$NON-NLS-1$
		} catch (JavaModelException e) {
			setErrorMessage(e.getMessage());
			return;
		}// end catch
		dialog.setTitle(LauncherMessages.JavaMainTab_Choose_Main_Type_11);
		dialog
				.setMessage(LauncherMessages.JavaMainTab_Choose_a_main__type_to_launch__12);
		if (dialog.open() == Window.CANCEL) {
			return;
		}// end if
		Object[] results = dialog.getResult();
		IType type = (IType) results[0];
		if (type != null) {
			fMainText.setText(type.getFullyQualifiedName());
			fProjText.setText(type.getJavaProject().getElementName());
		}// end if
	}

	public void performApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText
						.getText().trim());
		config.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				fMainText.getText().trim());
		mapResources(config);

		// attribute added in 2.1, so null must be used instead of false for
		// backwards compatibility
		if (fStopInMainCheckButton.getSelection()) {
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, true);
		}// end if
		else {
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN,
					(String) null);
		}// end else
	}

	public String getName() {
		return "Cougaar Launcher";
	}
}
