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
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;

import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;

/**
 * DOCUMENT ME!
 * 
 * @author mabrams
 */
public class CougaarMainTab extends JavaMainTab {

  /** DOCUMENT ME! */
  public static String projectName = "";

  /**
   * DOCUMENT ME!
   * 
   * @param parent
   *          DOCUMENT ME!
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

    Label fProjLabel = new Label(projComp, SWT.NONE);
    fProjLabel.setText(LauncherMessages.JavaMainTab__Project__2);

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
        LauncherMessages.JavaMainTab__Browse_3, null); //$NON-NLS-1$
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

    Label fMainLabel = new Label(mainComp, SWT.NONE);
    fMainLabel.setText(LauncherMessages.JavaMainTab_Main_cla_ss__4);
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
        LauncherMessages.JavaMainTab_Searc_h_5, null); //$NON-NLS-1$
    fSearchButton.addSelectionListener(new SelectionAdapter() {

      public void widgetSelected(SelectionEvent evt) {
        handleSearchButtonSelected();
      }
    });

    fSearchExternalJarsCheckButton = new Button(mainComp, SWT.CHECK);
    fSearchExternalJarsCheckButton
        .setText(LauncherMessages.JavaMainTab_E_xt__jars_6);
    fSearchExternalJarsCheckButton.setFont(font);
    fSearchExternalJarsCheckButton.addSelectionListener(new SelectionAdapter() {

      public void widgetSelected(SelectionEvent evt) {
        updateLaunchConfigurationDialog();
      }
    });

    fConsiderInheritedMainButton = createCheckButton(mainComp,
        LauncherMessages.JavaMainTab_22);
    gd = new GridData();
    gd.horizontalSpan = 2;
    fConsiderInheritedMainButton.setLayoutData(gd);
    fConsiderInheritedMainButton.addSelectionListener(fListener);

    fStopInMainCheckButton = new Button(comp, SWT.CHECK);
    fStopInMainCheckButton
        .setText(LauncherMessages.JavaMainTab_St_op_in_main_1);
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

  }

  /**
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
   */
  public void setDefaults(ILaunchConfigurationWorkingCopy config) {
    super.setDefaults(config);
    config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
        ICougaarConstants.COUGAAR_MAIN_CLASS);
    config.setAttribute(JavaMainTab.ATTR_INCLUDE_EXTERNAL_JARS, true);
  }

  /**
   * DOCUMENT ME!
   * 
   * @param config
   *          DOCUMENT ME!
   */
  public void initializeFrom(ILaunchConfiguration config) {
    super.initializeFrom(config);
  }

  /**
   * A listener which handles widget change events for the controls in this tab.
   */
  private class WidgetListener implements ModifyListener, SelectionListener {

    public void modifyText(ModifyEvent e) {
      updateLaunchConfigurationDialog();
    }

    public void widgetSelected(SelectionEvent e) {
      Object source = e.getSource();
      if (source == fProjButton) {
        handleProjectButtonSelected();
      } else if (source == fSearchButton) {
        handleSearchButtonSelected();
      } else {
        updateLaunchConfigurationDialog();
      }
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  private WidgetListener fListener = new WidgetListener();
}
