package com.cougaarsoftware.cougaar.ide.launcher.ui.configuration;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

/**
 * @author mabrams
 */
public class CougaarXMLTabGroup extends AbstractCougaarTabGroup {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new CougaarMainTab(), new CougaarXMLParametersTab(),
				new JavaJRETab(), new JavaClasspathTab(), new CommonTab()
			};
		setTabs(tabs);
	}
}