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

import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;


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
     * @param descriptor DOCUMENT ME!
     */
    public CougaarPlugin(IPluginDescriptor descriptor) {
        super(descriptor);
        plugin = this;
        try {
            resourceBundle = ResourceBundle.getBundle(
                    "com.ibm.lab.soln.resources.ResourcesPluginResources");
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
    }

    /**
     * Method Stub.  Does nothing specific, but supports immediate integration
     * of  plug-in into the running Workbench.
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
        if (getDefault() == null) {
            return "com.cougaarsoftware.cougaar.ide.core.CougaarPlugin";
        }

        return getDefault().getDescriptor().getUniqueIdentifier();
    }


    /**
     * Logs the specified status with this plug-in's log.
     *
     * @param status status to log
     */
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }


    /**
     * Logs an internal error with the specified throwable
     *
     * @param e the exception to be logged
     */
    public static void log(Throwable e) {
        log(new Status(IStatus.ERROR, getUniqueIdentifier(), 0,
                "Internal Error", e)); //$NON-NLS-1$
    }


    /**
     * Logs an internal error with the specified message.
     *
     * @param message the error message to log
     */
    public static void logError(String message) {
        logError(message, null);
    }


    /**
     * Logs a throwable with the specified message.
     *
     * @param message Message to log
     * @param throwable Throwable to log
     */
    public static void logError(String message, Throwable throwable) {
        log(new Status(IStatus.ERROR, getUniqueIdentifier(), 0, message,
                throwable));
    }


    /**
     * Returns the workspace instance using the platform  implementation of the
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
     * @param key DOCUMENT ME!
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
     * @param aProject DOCUMENT ME!
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
     * @param version DOCUMENT ME!
     * @param cougaarInstallPath DOCUMENT ME!
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
     * Untested! method to update the classpath container when the cougaar
     * install for the project changes.
     *
     * @param project
     *
     * @throws CoreException
     */
    public static void updateClasspathContainer(IJavaProject project)
        throws CoreException {
        if (!isCougaarProject(project.getProject())) {
            return;
        }

        IPath path = new Path(IResourceIDs.CLASSPATH_CONTAINER_ID);

        //TODO get install location for the project
        String version = CougaarPlugin.getDefault().getPreferenceStore()
                                      .getString(CougaarPlugin.DEFAULT_COUGAAR_PREFERENCE);
        String installPrefix = getCougaarBaseLocation(version);

        //had an elaborate mechanism to reuse the existing one if it existed; but javaCore doesn't update if i modify the existing one
        IClasspathContainer container = new CougaarClasspathContainer(installPrefix);

        IJavaProject[] javaProjects = new IJavaProject[] { project };
        IClasspathContainer[] containers = new IClasspathContainer[] { container };

        JavaCore.setClasspathContainer(path, javaProjects, containers, null);
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
     * Initializes a preference store with default preference values  for this
     * plug-in.
     *
     * @param store DOCUMENT ME!
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
     * @param preference DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param project DOCUMENT ME!
     */
    public static void savePreference(String preference, String value,
        IProject project) {
        if (!JavaProject.hasJavaNature(project)) {
            return; // ignore
        }


        File prefFile = project.getPluginWorkingLocation(JavaCore.getPlugin()
                                                                 .getDescriptor())
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
        } catch (IOException e) { // problems saving preference store - quietly ignore
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
     * @param project DOCUMENT ME!
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getCougaarPreference(IProject project, String key) {
        if (!JavaProject.hasJavaNature(project)) {
            return null;
        }

        Properties props = new Properties();

        //		File prefFile = getProject().getLocation().append(PREF_FILENAME).toFile();
        IPath projectMetaLocation = project.getPluginWorkingLocation(JavaCore.getPlugin()
                                                                             .getDescriptor());
        if (projectMetaLocation != null) {
            File prefFile = projectMetaLocation.append(PREF_FILENAME).toFile();
            if (prefFile.exists()) { // load preferences from file
                InputStream in = null;
                try {
                    in = new BufferedInputStream(new FileInputStream(prefFile));
                    props.load(in);
                    return props.getProperty(key);

                } catch (IOException e) { // problems loading preference store - quietly ignore
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
}
