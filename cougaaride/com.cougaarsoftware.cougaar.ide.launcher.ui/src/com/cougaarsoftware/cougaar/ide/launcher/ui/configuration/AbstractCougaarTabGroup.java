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


import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import com.cougaarsoftware.cougaar.ide.launcher.core.constants.ICougaarLaunchConfigurationConstants;


/**
 * DOCUMENT ME!
 *
 * @author mabrams
 */
public abstract class AbstractCougaarTabGroup
    extends AbstractLaunchConfigurationTabGroup {
    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        super.performApply(configuration);

        ILaunchConfigurationTab[] tabs = getTabs();

        configuration.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_CONFIGURATION_ERROR,
            false);

        for (int i = 0; i < tabs.length; i++) {
            if (!tabs[i].isValid(configuration)) {
                configuration.setAttribute(ICougaarLaunchConfigurationConstants.ATTR_CONFIGURATION_ERROR,
                    true);
                break;
            }
        }
    }
}
