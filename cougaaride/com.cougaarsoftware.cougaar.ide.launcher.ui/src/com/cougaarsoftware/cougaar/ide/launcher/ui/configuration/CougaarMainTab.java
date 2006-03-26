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

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.internal.debug.ui.launcher.SharedJavaMainTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

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
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		comp.setLayout(topLayout);
		comp.setFont(font);
		createProjectEditor(comp);
		createVerticalSpacer(comp, 1);
		fSearchExternalJarsCheckButton = createCheckButton(parent, LauncherMessages.JavaMainTab_E_xt__jars_6); 
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		fSearchExternalJarsCheckButton.setLayoutData(gd);
		fSearchExternalJarsCheckButton.addSelectionListener(getDefaultListener());
		fConsiderInheritedMainButton = createCheckButton(parent, LauncherMessages.JavaMainTab_22); 
		gd = new GridData();
		gd.horizontalSpan = 2;
		fConsiderInheritedMainButton.setLayoutData(gd);
		fConsiderInheritedMainButton.addSelectionListener(getDefaultListener());
		fStopInMainCheckButton = createCheckButton(parent, LauncherMessages.JavaMainTab_St_op_in_main_1); 
		gd = new GridData();
		fStopInMainCheckButton.setLayoutData(gd);
		fStopInMainCheckButton.addSelectionListener(getDefaultListener());
		createMainTypeEditor(comp, LauncherMessages.JavaMainTab_Main_cla_ss__4, new Button[] {fSearchExternalJarsCheckButton, fConsiderInheritedMainButton, fStopInMainCheckButton});
		

	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		IJavaElement javaElement = getContext();
		if (javaElement != null) {
			initializeJavaProject(javaElement, config);
		}//end if 
		else {
			config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);
		}//end else
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
		// TODO Auto-generated method stub
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return "Cougaar Launcher";
	}
}
