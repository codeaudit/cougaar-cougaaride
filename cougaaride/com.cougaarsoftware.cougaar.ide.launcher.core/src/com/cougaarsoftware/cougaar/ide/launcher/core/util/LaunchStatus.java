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


package com.cougaarsoftware.cougaar.ide.launcher.core.util;


import com.cougaarsoftware.cougaar.ide.launcher.core.CougaarLauncherMessages;


/**
 * DOCUMENT ME!
 *
 * @author mabrams To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class LaunchStatus {
    /** DOCUMENT ME! */
    public static final LaunchStatus CONFIGURE_ERROR = new LaunchStatus(CougaarLauncherMessages
            .getString("LaunchStatusconfigure_error_3"));
    /** DOCUMENT ME! */
    public static final LaunchStatus NOT_RUNNING = new LaunchStatus(CougaarLauncherMessages
            .getString("LaunchStatusnot_running_2"));
    /** DOCUMENT ME! */
    public static final LaunchStatus RUNNING = new LaunchStatus(CougaarLauncherMessages
            .getString("LaunchStatusrunning_1"));
    private String name;

    /**
     * Creates a new LaunchStatus object.
     *
     * @param name DOCUMENT ME!
     */
    public LaunchStatus(String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return
     */
    public String getName() {
        return name;
    }


    /**
     * DOCUMENT ME!
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
}
