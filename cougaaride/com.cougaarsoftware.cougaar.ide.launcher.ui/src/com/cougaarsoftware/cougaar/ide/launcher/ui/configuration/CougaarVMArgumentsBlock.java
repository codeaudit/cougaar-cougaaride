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


import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.internal.debug.ui.launcher.VMArgumentsBlock;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;


/**
 * DOCUMENT ME!
 *
 * @author mabrams
 */
public class CougaarVMArgumentsBlock extends VMArgumentsBlock {
    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        String vmArgs;
        try {
            vmArgs = createCougaarVMArgs(configuration);
            configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                vmArgs);
        } catch (CoreException e) {
            JDIDebugUIPlugin.log(e);
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param configuration
     * @param useDefault DOCUMENT ME!
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration,
        boolean useDefault) {
        if (useDefault) {
            String vmArgs;
            try {
                vmArgs = createCougaarVMArgs(configuration);
                configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                    vmArgs);
            } catch (CoreException e) {
                JDIDebugUIPlugin.log(e);
            }
        } else {
            configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                fVMArgumentsText.getText());
        }
    }


    /**
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Font font = parent.getFont();

        Composite comp = new Composite(parent, SWT.NONE);
        setControl(comp);
        GridLayout topLayout = new GridLayout();
        topLayout.marginHeight = 0;
        topLayout.marginWidth = 0;
        comp.setLayout(topLayout);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        comp.setLayoutData(gd);

        Label fVMArgumentsLabel = new Label(comp, SWT.NONE);
        fVMArgumentsLabel.setText(LauncherMessages.getString(
                "JavaArgumentsTab.VM_ar&guments__6")); //$NON-NLS-1$
        fVMArgumentsLabel.setFont(font);

        fVMArgumentsText = new Text(comp,
                SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 40;
        gd.widthHint = 100;
        fVMArgumentsText.setLayoutData(gd);
        fVMArgumentsText.setFont(font);
        fVMArgumentsText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    updateLaunchConfigurationDialog();
                }
            });
    }


    private String createCougaarVMArgs(ILaunchConfigurationWorkingCopy config)
        throws CoreException {
        String cougaarHome = config.getAttribute(ICougaarConstants.ATTR_COUGAAR_HOME_DIR,
                "");
        String cougaarVMArgs = ICougaarConstants.COUGAAR_INSTALL_PATH
            + cougaarHome + " ";

        cougaarVMArgs += (ICougaarConstants.COUGAAR_CONFIG_PATH + ".;"
        + cougaarHome + "/common/configs;" + cougaarHome + "/configs/glmtrans ");
        cougaarVMArgs += (ICougaarConstants.COUGAAR_LOGGING_CONFIG
        + "log.properties ");

        cougaarVMArgs += (ICougaarConstants.COUGAAR_SYSTEM_PATH
        + config.getAttribute(ICougaarConstants.COUGAAR_SYSTEM_PATH, "")
        + "/sys ");
        cougaarVMArgs += (ICougaarConstants.COUGAAR_CORE_SERVLET_ENABLE
        + "true ");
        cougaarVMArgs += (ICougaarConstants.COUGAAR_LIB_SCAN_RANGE + "100 ");
        cougaarVMArgs += (ICougaarConstants.COUGAAR_HTTP_PORT + "8800 ");
        cougaarVMArgs += (ICougaarConstants.COUGAAR_HTTPS_PORT + "-1 ");
        cougaarVMArgs += (ICougaarConstants.COUGAAR_CLIENT_AUTH + "true ");
        cougaarVMArgs += (ICougaarConstants.COUGAAR_XBOOT_CLASSPATH
        + cougaarHome + "/lib/javaiopatch.jar ");
        cougaarVMArgs += (ICougaarConstants.COUGAAR_CLASSPATH + cougaarHome
        + "/lib/bootstrap.jar ");
        return cougaarVMArgs;
    }
}
