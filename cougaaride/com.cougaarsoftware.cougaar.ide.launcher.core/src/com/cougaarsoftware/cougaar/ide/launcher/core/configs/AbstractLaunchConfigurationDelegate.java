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


package com.cougaarsoftware.cougaar.ide.launcher.core.configs;


import java.io.File;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.cougaarsoftware.cougaar.ide.launcher.core.constants.ICougaarLaunchConfigurationConstants;


/**
 * Abstract launch configuration delegate for launching Cougaar applications
 *
 * @author mabrams
 */
public abstract class AbstractLaunchConfigurationDelegate
	extends AbstractJavaLaunchConfigurationDelegate
	implements ICougaarLaunchConfigurationDelegate {
	/**
	 * @see ILaunchConfigurationDelegate#launch(ILaunchConfiguration, String,
	 *      ILaunch, IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode,
		ILaunch launch, IProgressMonitor monitor)
		throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		monitor.beginTask(MessageFormat.format(LaunchingMessages.getString(
					"JavaLocalApplicationLaunchConfigurationDelegate.Launching_{0}..._1"),
				new String[] { configuration.getName() }), 3); //$NON-NLS-1$
		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		monitor.subTask(LaunchingMessages.getString(
				"JavaLocalApplicationLaunchConfigurationDelegate.Verifying_launch_attributes..._1")); //$NON-NLS-1$

		String mainTypeName = verifyMainTypeName(configuration);

		IVMInstall vm = verifyVMInstall(configuration);

		IVMRunner runner = vm.getVMRunner(mode);
		if (runner == null) {
			if (mode == ILaunchManager.DEBUG_MODE) {
				abort(MessageFormat.format(LaunchingMessages.getString(
							"JavaLocalApplicationLaunchConfigurationDelegate.JRE_{0}_does_not_support_debug_mode._1"),
						new String[] { vm.getName() }), null,
					IJavaLaunchConfigurationConstants.ERR_VM_RUNNER_DOES_NOT_EXIST); //$NON-NLS-1$
			} else {
				abort(MessageFormat.format(LaunchingMessages.getString(
							"JavaLocalApplicationLaunchConfigurationDelegate.JRE_{0}_does_not_support_run_mode._2"),
						new String[] { vm.getName() }), null,
					IJavaLaunchConfigurationConstants.ERR_VM_RUNNER_DOES_NOT_EXIST); //$NON-NLS-1$
			}
		}

		File workingDir = verifyWorkingDirectory(configuration);
		String workingDirName = null;
		if (workingDir != null) {
			workingDirName = workingDir.getAbsolutePath();
		}

		// Program & VM args
		String pgmArgs = getProgramArguments(configuration);
		String vmArgs = getVMArguments(configuration);
		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);

		// VM-specific attributes
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);

		// Classpath
		String[] classpath = getClasspath(configuration);

		// Create VM config
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName,
				classpath);
		runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
		runConfig.setVMArguments(execArgs.getVMArgumentsArray());
		runConfig.setWorkingDirectory(workingDirName);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);

		// Bootpath
		String[] bootpath = getBootpath(configuration);
		runConfig.setBootClassPath(bootpath);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		// stop in main
		prepareStopInMain(configuration);

		// done the verification phase
		monitor.worked(1);

		monitor.subTask(LaunchingMessages.getString(
				"JavaLocalApplicationLaunchConfigurationDelegate.Creating_source_locator..._2")); //$NON-NLS-1$
		// set the default source locator if required
		setDefaultSourceLocator(launch, configuration);
		monitor.worked(1);

		// Launch the configuration - 1 unit of work
		runner.run(runConfig, launch, monitor);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		monitor.done();
	}


	/**
	 * Returns the program arguments specified by the given launch
	 * configuration, as a string. The returned string is empty if no program
	 * arguments are specified.
	 *
	 * @param configuration launch configuration
	 *
	 * @return the program arguments specified by the given  launch
	 *         configuration, possibly an empty string
	 *
	 * @exception CoreException if unable to retrieve the attribute
	 */
	public String getVMArguments(ILaunchConfiguration configuration)
		throws CoreException {
		String ret = "";
		Map vmParameters = configuration.getAttribute(ICougaarLaunchConfigurationConstants.ATTR_COUGAAR_VM_PARAMETERS,
				new HashMap());
		if (vmParameters.size() != 0) {
			Iterator iterator = vmParameters.keySet().iterator();
			while (iterator.hasNext()) {
				String next = (String) iterator.next();
				if (next.equals("-classpath")) {
					ret += (next + " " + (String) vmParameters.get(next) + " ");
				} else if (next.indexOf("-Xbootclasspath") > -1) {
					ret += ((String) vmParameters.get(next) + " ");
				} else if (next.indexOf("-X") > -1) {
					ret += ((String) vmParameters.get(next) + " ");
				} else {
					ret += (next + "=" + (String) vmParameters.get(next) + " ");
				}
			}
		}


		return ret.trim();
	}
}