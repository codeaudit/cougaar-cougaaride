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


package com.cougaarsoftware.cougaar.ide.core.constants;


/**
 * DOCUMENT ME!
 *
 * @author mabrams
 */
public interface ICougaarConstants {
    /** Cougaar Constant */
    public static final String COUGAAR_CONFIG_PATH = "-Dorg.cougaar.config.path=";
    /** Cougaar Constant */
    public static final String ATTR_CONFIG_HOME_DIR = "org.cougaar.configDir";
    /** Cougaar Constant */
    public static final String COUGAAR_INSTALL_PATH = "-Dorg.cougaar.install.path=";
    /** Cougaar Constant */
    public static final String COUGAAR_LOGGING_CONFIG = "-Dorg.cougaar.core.logging.config.filename=";
    /** Cougaar Constant */
    public static final String COUGAAR_SYSTEM_PATH = "-Dorg.cougaar.system.path=";
    /** Cougaar Constant */
    public static final String COUGAAR_CORE_SERVLET_ENABLE = "-Dorg.cougaar.core.servlet.enable=";
    /** Cougaar Constant */
    public static final String COUGAAR_LIB_SCAN_RANGE = "-Dorg.cougaar.lib.web.scanRange=";
    /** Cougaar Constant */
    public static final String COUGAAR_HTTP_PORT = "-Dorg.cougaar.lib.web.http.port=";
    /** Cougaar Constant */
    public static final String COUGAAR_HTTPS_PORT = "-Dorg.cougaar.lib.web.https.port=";
    /** Cougaar Constant */
    public static final String COUGAAR_CLIENT_AUTH = "-Dorg.cougaar.lib.web.https.clientAuth=";
    /** Cougaar Constant */
    public static final String COUGAAR_XBOOT_CLASSPATH = "-Xbootclasspath/p:";
    /** Cougaar Constant */
    public static final String COUGAAR_CLASSPATH = "-classpath ";
    /** Cougaar Constant */
    public static final String COUGAAR_PROGRAM_ARGUMENTS = "org.cougaar.core.node.Node -c -n ";
    /** Cougaar Constant */
    public static final String RELATIVE_WORKING_DIR = "./configs/test/workflow";
    /** Cougaar Constant */
    public static final String ATTR_COUGAAR_HOME_DIR = "org.cougaar.homeDir";
    /** Cougaar Constant */
    public static final String ATTR_NODE_NAME = "org.cougaar.nodeName";
    /** Cougaar Constant */
    public static final String COUGAAR_MAIN_CLASS = "org.cougaar.bootstrap.Bootstrapper";
}
