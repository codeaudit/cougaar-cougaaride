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

package com.cougaarsoftware.cougaar.ide.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.cougaarsoftware.cougaar.ide.core.constants.ICougaarConstants;

/**
 * The main Cougaar Plugin.
 */
public class CougaarPlugin extends AbstractUIPlugin {
	//Reference to the shared instance, returned when requested.
	private static CougaarPlugin plugin;
	/** DOCUMENT ME! */
	public static final String DEFAULT_COUGAAR_VERSION = "";
	/** DOCUMENT ME! */
	public static final String DEFAULT_COUGAAR_PREFERENCE = "default_cougaar_preference";
	/** Name of file containing custom project preferences */
	public static final String PREF_FILENAME = ".cougaarprefs";

	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 * 
	 * @param descriptor
	 *            DOCUMENT ME!
	 */
	public CougaarPlugin(IPluginDescriptor descriptor) {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle
					.getBundle("com.cougaarsoftware.cougaar.ide.core.CougaarPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Method Stub. Does nothing specific, but supports immediate integration of
	 * plug-in into the running Workbench.
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		// All work is done in the startup() method
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return DOCUMENT ME!
	 */
	public static CougaarPlugin getDefault() {
		return plugin;
	}

	/**
	 * Convenience method which returns the unique identifier of this plugin.
	 * 
	 * @return The unique indentifier value
	 */
	public static String getUniqueIdentifier() {
		return "com.cougaarsoftware.cougaar.ide.core.CougaarPlugin";
	}

	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status
	 *            status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Logs an internal error with the specified throwable
	 * 
	 * @param e
	 *            the exception to be logged
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getUniqueIdentifier(), 0,
				"Internal Error", e)); //$NON-NLS-1$
	}

	/**
	 * Logs an internal error with the specified message.
	 * 
	 * @param message
	 *            the error message to log
	 */
	public static void logError(String message) {
		logError(message, null);
	}

	/**
	 * Logs a throwable with the specified message.
	 * 
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public static void logError(String message, Throwable throwable) {
		log(new Status(IStatus.ERROR, getUniqueIdentifier(), 0, message,
				throwable));
	}

	/**
	 * Returns the workspace instance using the platform implementation of the
	 * Resources plugin. (names happen to be the same).
	 * 
	 * @return DOCUMENT ME!
	 */
	public static IWorkspace getWorkspace() {
		return org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 * 
	 * @param key
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 * 
	 * @return DOCUMENT ME!
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param aProject
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static boolean isCougaarProject(IProject aProject) {
		try {
			return aProject.hasNature(IResourceIDs.COUGAAR_NATURE_ID);
		} catch (CoreException e) {
		}

		return false;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param version
	 *            DOCUMENT ME!
	 * @param cougaarInstallPath
	 *            DOCUMENT ME!
	 */
	public static void setCougaarInstallPathLocation(String version,
			String cougaarInstallPath) {
		CougaarLocations.setCougaarLocation(version, cougaarInstallPath);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param version
	 * 
	 * @return
	 */
	public static String getCougaarBaseLocation(String version) {
		return CougaarLocations.getCougaarBaseLocation(version);
	}

	/**
	 * Updates the classpath container when the cougaar install for the project
	 * changes, and performs a full rebuild.
	 * 
	 * @param jproject
	 *            the IJavaProject
	 * @param monitor
	 *            DOCUMENT ME!
	 * 
	 * @throws CoreException
	 */
	public static void updateClasspathContainer(IJavaProject jproject,
			IProgressMonitor monitor) throws CoreException {
		IProject project = jproject.getProject();
		if (!isCougaarProject(project)) {
			return;
		}

		IPath path = new Path(IResourceIDs.CLASSPATH_CONTAINER_ID);

		String version = CougaarPlugin.getCougaarPreference(project,
				ICougaarConstants.COUGAAR_VERSION);
		String installPrefix = getCougaarBaseLocation(version);

		//had an elaborate mechanism to reuse the existing one if it existed;
		// but javaCore doesn't update if i modify the existing one
		IClasspathContainer container = new CougaarClasspathContainer(
				installPrefix);

		IJavaProject[] javaProjects = new IJavaProject[]{jproject};
		IClasspathContainer[] containers = new IClasspathContainer[]{container};
		SubProgressMonitor subMonitor = null;
		if (monitor != null) {
			subMonitor = new SubProgressMonitor(monitor, 1);
			subMonitor.setTaskName("Setting classpath");
		}

		JavaCore.setClasspathContainer(path, javaProjects, containers,
				subMonitor);

		subMonitor = null;
		if (monitor != null) {
			subMonitor = new SubProgressMonitor(monitor, 1);
			subMonitor.setTaskName("Performing full build");
		}

		project.build(IncrementalProjectBuilder.FULL_BUILD, subMonitor);
	}

	/**
	 * Convert an existing javaproject to a cougaar project. Does nothing if its
	 * already a cougaar project; aborts if no cougaar version preference is set
	 * for the project.
	 * 
	 * @param jproject
	 *            java project to convert
	 * @param monitor
	 *            progress monitor
	 * 
	 * @throws CoreException
	 */
	public static void convertToCougaarProject(IJavaProject jproject,
			IProgressMonitor monitor) throws CoreException {
		IProject project = jproject.getProject();
		if (isCougaarProject(project)) {
			return;
		}

		//abort if no cougaar version is found
		String version = getCougaarPreference(project,
				ICougaarConstants.COUGAAR_VERSION);
		if (version == null) {
			throw new CoreException(null);
		}

		SubProgressMonitor subMonitor = null;
		if (monitor != null) {
			subMonitor = new SubProgressMonitor(monitor, 4);
			subMonitor.setTaskName("Converting to Cougaar project");
		}

		//add cougaarNature
		addCougaarNature(project, subMonitor);

		//remove jars in existing CP that are under selected install path
		String baseLocation = getCougaarBaseLocation(version);
		IPath basepath = new Path(baseLocation);
		basepath = JavaProject.canonicalizedPath(basepath);
		IClasspathEntry[] entries = jproject.getRawClasspath();

		IClasspathEntry[] newentries;
		ArrayList keptEntries = new ArrayList();

		//look for the entry already in the classpath
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			IPath path = JavaProject.canonicalizedPath(entry.getPath());
			if (!basepath.isPrefixOf(path)) {
				keptEntries.add(entry);
			}
		}

		newentries = new IClasspathEntry[keptEntries.size()];
		System.arraycopy(keptEntries.toArray(), 0, newentries, 0, keptEntries
				.size());

		//make sure we didn't screw up the classpath
		IJavaModelStatus validation = JavaConventions.validateClasspath(
				jproject, newentries, jproject.getOutputLocation());
		if (!validation.isOK()) {
			throw new CoreException(validation);
		}

		//save the CP (same, but with cougaar jars stripped)
		jproject.setRawClasspath(newentries, subMonitor);

		//add the cougaar classpath container
		addCougaarClasspathContainer(jproject, subMonitor);

		//finally populate the cougaar CP container and rebuild
		updateClasspathContainer(jproject, subMonitor);
	}

	/**
	 * Add the nature to the project.
	 * 
	 * @param project
	 *            project to add nature to
	 * @param monitor
	 *            progress monitor
	 * 
	 * @return true for success
	 * 
	 * @throws CoreException
	 */
	public static boolean addCougaarNature(IProject project,
			IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = IResourceIDs.COUGAAR_NATURE_ID;
		description.setNatureIds(newNatures);

		SubProgressMonitor subMonitor = null;
		if (monitor != null) {
			subMonitor = new SubProgressMonitor(monitor, 1);
			subMonitor.setTaskName("Adding Cougaar Nature");
		}

		project.setDescription(description, subMonitor);

		return true;
	}

	/**
	 * Add classpath container for the cougaar project.
	 * 
	 * @param javaProject
	 *            the project
	 * @param monitor
	 *            progress monitor
	 * 
	 * @throws CoreException
	 */
	public static void addCougaarClasspathContainer(IJavaProject javaProject,
			IProgressMonitor monitor) throws CoreException {
		IPath path = new Path(IResourceIDs.CLASSPATH_CONTAINER_ID);
		IClasspathEntry conEntry = JavaCore.newContainerEntry(path, false);

		IClasspathEntry[] entries = javaProject.getRawClasspath();
		IClasspathEntry[] newentries;
		int index = entries.length;

		//look for the entry already in the classpath
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].equals(conEntry)) {
				index = i;
				break;
			}
		}

		//if we didnt find an existing entry
		if (index == entries.length) {
			newentries = new IClasspathEntry[entries.length + 1];
			System.arraycopy(entries, 0, newentries, 0, entries.length);
			newentries[newentries.length - 1] = conEntry;
		} else {
			newentries = entries;
		}

		IJavaModelStatus validation = JavaConventions.validateClasspath(
				javaProject, newentries, javaProject.getOutputLocation());
		if (!validation.isOK()) {
			throw new CoreException(validation);
		}

		SubProgressMonitor subMonitor = null;
		if (monitor != null) {
			subMonitor = new SubProgressMonitor(monitor, 1);
			subMonitor.setTaskName("Configuring Cougaar classpath container");
		}

		javaProject.setRawClasspath(newentries, subMonitor);

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static Map getAllCougaarLocations() {
		return CougaarLocations.getAllCougaarLocations();
	}

	/**
	 * Initializes a preference store with default preference values for this
	 * plug-in.
	 * 
	 * @param store
	 *            DOCUMENT ME!
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(DEFAULT_COUGAAR_PREFERENCE, DEFAULT_COUGAAR_VERSION);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void savePluginSettings() {
		super.savePluginPreferences();

	}

	/**
	 * Save project custom preferences
	 * 
	 * @param preference
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 * @param project
	 *            DOCUMENT ME!
	 */
	public static void savePreference(String preference, String value,
			IProject project) {
		if (!JavaProject.hasJavaNature(project)) {
			return; // ignore
		}

		File prefFile = project.getWorkingLocation(getUniqueIdentifier())
				.append(PREF_FILENAME).toFile();

		// write file, overwriting an existing one
		OutputStream out = null;
		Properties props = new Properties();
		props.setProperty(preference, value);
		try {
			// do it as carefully as we know how so that we don't lose/mangle
			// the setting in times of stress
			out = new BufferedOutputStream(new FileOutputStream(prefFile));
			props.store(out, null);
		} catch (IOException e) { // problems saving preference store - quietly
								  // ignore
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) { // ignore problems with close
				}
			}
		}
	}

	/**
	 * Returns the project custom preference pool. Project preferences may
	 * include custom encoding.
	 * 
	 * @param project
	 *            DOCUMENT ME!
	 * @param key
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static String getCougaarPreference(IProject project, String key) {
		if (!JavaProject.hasJavaNature(project)) {
			return null;
		}

		Properties props = new Properties();

		//		File prefFile =
		// getProject().getLocation().append(PREF_FILENAME).toFile();
		IPath projectMetaLocation = project
				.getWorkingLocation(getUniqueIdentifier());
		if (projectMetaLocation != null) {
			File prefFile = projectMetaLocation.append(PREF_FILENAME).toFile();
			if (prefFile.exists()) { // load preferences from file
				InputStream in = null;
				try {
					in = new BufferedInputStream(new FileInputStream(prefFile));
					props.load(in);
					return props.getProperty(key);

				} catch (IOException e) { // problems loading preference store -
										  // quietly ignore
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) { // ignore problems with close
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param string
	 */
	public static void log(String string) {
		// TODO Auto-generated method stub
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param version
	 * 
	 * @return
	 */
	public static boolean isDefaultCougaarVersion(String version) {
		return CougaarLocations.isDefaultVersion(version);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param version
	 *            DOCUMENT ME!
	 */
	public static void setDefaultCougaarVersion(String version) {
		CougaarLocations.setDefaultVersion(version);
	}
}