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


package com.cougaarsoftware.cougaar.ide.ui.wizards;


import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cougaarsoftware.cougaar.ide.ui.CougaarUIMessages;


/**
 * DOCUMENT ME!
 *
 * @author mabrams
 */
public class CougaarCapabilityConfigurationPage extends NewElementWizardPage {
    private static final String PAGE_NAME = "CougaarCapabilityConfigurationPage"; //$NON-NLS-1$
    /** DOCUMENT ME! */
    public Text cougaarDirText;
    private Button cougaarDirButton;
    private Label cougaarDirLabel;
    protected String configDirLabelText;
//    private IJavaProject fJavaProject;

    /**
     * DOCUMENT ME!
     */
    public CougaarCapabilityConfigurationPage() {
        super(PAGE_NAME);
//        fJavaProject = null;
        setTitle(CougaarUIMessages.getString("CougaarCapabilityPageTitle"));
        configDirLabelText = "Cougaar Install Path";

//        IStatusChangeListener listener = new IStatusChangeListener() {
//                public void statusChanged(IStatus status) {
//                    updateStatus(status);
//                }
//            };
    }

    /**
     * Initializes the page with the project and default classpaths.
     * 
     * <p>
     * The default classpath entries must correspond the the given project.
     * </p>
     * 
     * <p>
     * The caller of this method is responsible for creating the underlying
     * project. The page will create the output, source and library folders if
     * required.
     * </p>
     * 
     * <p>
     * The project does not have to exist at the time of initialization, but
     * must exist when executing the runnable obtained by
     * <code>getRunnable()</code>.
     * </p>
     *
     * @param jproject The Java project.
     * @param defaultOutputLocation The default classpath entries
     *        or<code>null</code> to let the page choose the default
     * @param defaultEntries The folder to be taken as the default output path
     *        or<code>null</code> to let the page choose the default
     * @param defaultsOverrideExistingClasspath DOCUMENT ME!
     */
    public void init(IJavaProject jproject, IPath defaultOutputLocation,
        IClasspathEntry[] defaultEntries,
        boolean defaultsOverrideExistingClasspath) {
        if (!defaultsOverrideExistingClasspath && jproject.exists()
            && jproject.getProject().getFile(".classpath").exists()) { //$NON-NLS-1$
            defaultOutputLocation = null;
            defaultEntries = null;
        }

//        fJavaProject = jproject;

    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NONE);

        int nColumns = 2;

        GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        createCougaarControls(composite, nColumns);
        setControl(composite);


        //        WorkbenchHelp.setHelp(composite,
        //            IJavaHelpContextIds.NEW_INTERFACE_WIZARD_PAGE);
    }


    /**
     * DOCUMENT ME!
     *
     * @param comp DOCUMENT ME!
     * @param nColumns DOCUMENT ME!
     */
    protected void createCougaarControls(Composite comp, int nColumns) {
        GridLayout topLayout = new GridLayout();
        comp.setLayout(topLayout);
        GridData gd;

        createVerticalSpacer(comp, 1);
        Composite cougaarComposite = new Composite(comp, SWT.NONE);
        GridLayout cougaarLayout = new GridLayout();
        cougaarLayout.numColumns = nColumns;
        cougaarLayout.marginHeight = 0;
        cougaarLayout.marginWidth = 0;
        cougaarComposite.setLayout(cougaarLayout);
        gd = new GridData(GridData.FILL_HORIZONTAL);

        cougaarComposite.setLayoutData(gd);
        cougaarDirLabel = new Label(cougaarComposite, SWT.NONE);

        cougaarDirLabel.setText(configDirLabelText);
        gd = new GridData();
        gd.horizontalSpan = 2;
        cougaarDirLabel.setLayoutData(gd);

        cougaarDirText = new Text(cougaarComposite, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);

        cougaarDirText.setLayoutData(gd);
        cougaarDirText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                    //                     updateLaunchConfigurationDialog();
                }
            });


        cougaarDirButton = createPushButton(cougaarComposite,
                CougaarUIMessages.getString("CougaarCapabilityPage.Directory_1"));

        cougaarDirButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {
                    cougaarHomeDirButtonSelected();
                }
            });
    }


    /**
     * Method homedirButtonSelected.
     */
    private void cougaarHomeDirButtonSelected() {
        File f = new File(cougaarDirText.getText());

        if (!f.exists()) {
            f = null;
        }

        File d = getDirectory(f, getShell());

        if (d == null) {
            return;
        }

        cougaarDirText.setText(d.getAbsolutePath());
    }


    /**
     * Helper that opens the directory chooser dialog.
     *
     * @param startingDirectory Description of the Parameter
     * @param shell Description of the Parameter
     *
     * @return The directory value
     */
    public File getDirectory(File startingDirectory, Shell shell) {
        DirectoryDialog fileDialog = new DirectoryDialog(shell, SWT.OPEN);

        if (startingDirectory != null) {
            fileDialog.setFilterPath(startingDirectory.getPath());
        }

        String dir = fileDialog.open();

        if (dir != null) {
            dir = dir.trim();
            if (dir.length() > 0) {
                return new File(dir);
            }
        }

        return null;
    }


    /**
     * Creates and returns a new push button with the given label and/or image.
     *
     * @param parent parent control
     * @param label button label or <code>null</code>
     *
     * @return a new push button
     */
    public Button createPushButton(Composite parent, String label) {
        Button button = new Button(parent, SWT.PUSH);
        button.setFont(parent.getFont());

        if (label != null) {
            button.setText(label);
        }

        GridData gd = new GridData();
        button.setLayoutData(gd);
        SWTUtil.setButtonDimensionHint(button);
        return button;
    }


    /**
     * Create some empty space.
     *
     * @param comp DOCUMENT ME!
     * @param colSpan DOCUMENT ME!
     */
    protected void createVerticalSpacer(Composite comp, int colSpan) {
        Label label = new Label(comp, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = colSpan;
        label.setLayoutData(gd);
    }
}
