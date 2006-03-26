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


package com.cougaarsoftware.cougaar.ide.ui.preferences;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cougaarsoftware.cougaar.ide.core.CougaarInstall;
import com.cougaarsoftware.cougaar.ide.core.CougaarInstallComparer;
import com.cougaarsoftware.cougaar.ide.core.CougaarPlugin;
import com.cougaarsoftware.cougaar.ide.core.ICougaarInstall;
import com.cougaarsoftware.cougaar.ide.ui.CougaarUI;
import com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor;
import com.cougaarsoftware.cougaar.ide.ui.ICougaarInstallChangeListener;
import com.cougaarsoftware.cougaar.ide.ui.ListContentProvider;
import com.cougaarsoftware.cougaar.ide.ui.dialogs.AddCougaarDialog;


/**
 * Cougaar Preference page for adding new cougaar installs
 *
 * @author mabrams
 */
public class CougaarPreferencePage extends PreferencePage
    implements IWorkbenchPreferencePage, IAddCougaarDialogRequestor,
        ISelectionProvider {
    private CheckboxTableViewer fCougaarList;
    private Button fAddButton;
    private Button fRemoveButton;
    private Button fEditButton;

    //    private Button fSearchButton;
    private List fCougaarInstalls;
    private ListenerList fSelectionListeners = new ListenerList();
    private IAddCougaarDialogRequestor requestor;
    private ISelection fPrevSelection = new StructuredSelection();

    //    private ICougaarInstallChangeListener removedListener;
    /**
     * Creates a new CougaarPreferencePage object.
     */
    public CougaarPreferencePage() {
        super();

        setTitle(CougaarPreferencesMessages.getString(
                "CougaarPreferencePage.Installed_CougaarVersions_1"));

    }


    /**
     * Creates a new CougaarPreferencePage object.
     *
     * @param req DOCUMENT ME!
     */
    public CougaarPreferencePage(IAddCougaarDialogRequestor req) {
        super();
        this.requestor = req;
        setTitle(CougaarPreferencesMessages.getString(
                "CougaarPreferencePage.Installed_CougaarVersions_1"));
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite ancestor) {
        Font font = ancestor.getFont();
        initializeDialogUnits(ancestor);

        noDefaultAndApplyButton();

        Composite parent = new Composite(ancestor, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        parent.setLayout(layout);

        GridData data;

        Label tableLabel = new Label(parent, SWT.NONE);
        tableLabel.setText(CougaarPreferencesMessages.getString(
                "CougaarPreferencePage.Installed_&Cougaars__1")); //$NON-NLS-1$
        data = new GridData();
        data.horizontalSpan = 2;
        tableLabel.setLayoutData(data);
        tableLabel.setFont(font);

        Table table = new Table(parent,
                SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);

        data = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(data);
        table.setFont(font);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableLayout tableLayout = new TableLayout();
        table.setLayout(tableLayout);


        TableColumn column2 = new TableColumn(table, SWT.NULL);
        column2.setText(CougaarPreferencesMessages.getString(
                "cougaarPreferencePage.cougaarName")); //$NON-NLS-1$

        TableColumn column3 = new TableColumn(table, SWT.NULL);
        column3.setText(CougaarPreferencesMessages.getString(
                "cougaarPreferencePage.cougaarLocation")); //$NON-NLS-1$

        fCougaarList = new CheckboxTableViewer(table);
        fCougaarList.setComparer(new CougaarInstallComparer());

        fCougaarList.setSorter(new ViewerSorter() {
                public int compare(Viewer viewer, Object e1, Object e2) {
                    if ((e1 instanceof ICougaarInstall)
                        && (e2 instanceof ICougaarInstall)) {
                        ICougaarInstall left = (ICougaarInstall) e1;
                        ICougaarInstall right = (ICougaarInstall) e2;
                        String leftType = left.getId();
                        String rightType = right.getId();
                        int res = leftType.compareToIgnoreCase(rightType);
                        if (res != 0) {
                            return res;
                        }

                        return left.getId().compareToIgnoreCase(right.getId());
                    }

                    return super.compare(viewer, e1, e2);
                }


                public boolean isSorterProperty(Object element, String property) {
                    return true;
                }
            });

        fCougaarList.setLabelProvider(new CougaarLabelProvider());
        fCougaarList.setContentProvider(new ListContentProvider(fCougaarList,
                Collections.EMPTY_LIST));

        fCougaarList.addSelectionChangedListener(new ISelectionChangedListener() {
                public void selectionChanged(SelectionChangedEvent evt) {
                    enableButtons();
                }
            });


        fCougaarList.addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick(DoubleClickEvent e) {
                    editCougaar();
                }
            });

        fCougaarList.addCheckStateListener(new ICheckStateListener() {
                public void checkStateChanged(CheckStateChangedEvent event) {
                    if (event.getChecked()) {
                        setCheckedCougaar((ICougaarInstall) event.getElement());
                    } else {
                        setCheckedCougaar(null);
                    }
                }
            });
        table.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent event) {
                    if ((event.character == SWT.DEL) && (event.stateMask == 0)) {
                        removeCougaarInstalls();
                    }
                }
            });

        Composite buttons = new Composite(parent, SWT.NULL);
        buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        buttons.setLayout(layout);

        fAddButton = new Button(buttons, SWT.PUSH);
        setButtonLayoutData(fAddButton);
        fAddButton.setFont(font);
        fAddButton.setText(CougaarPreferencesMessages.getString(
                "cougaarPreferencePage.add")); //$NON-NLS-1$
        fAddButton.addListener(SWT.Selection,
            new Listener() {
                public void handleEvent(Event evt) {
                    addCougaar();
                }
            });

        fEditButton = new Button(buttons, SWT.PUSH);
        setButtonLayoutData(fEditButton);
        fEditButton.setFont(font);
        fEditButton.setText(CougaarPreferencesMessages.getString(
                "cougaarPreferencePage.edit")); //$NON-NLS-1$
        fEditButton.addListener(SWT.Selection,
            new Listener() {
                public void handleEvent(Event evt) {
                    editCougaar();
                }
            });

        fRemoveButton = new Button(buttons, SWT.PUSH);
        fRemoveButton.setFont(font);
        setButtonLayoutData(fRemoveButton);
        fRemoveButton.setText(CougaarPreferencesMessages.getString(
                "cougaarPreferencePage.remove")); //$NON-NLS-1$
        fRemoveButton.addListener(SWT.Selection,
            new Listener() {
                public void handleEvent(Event evt) {
                    removeCougaarInstalls();
                }
            });

        //not implemented yet so commenting out for  now
        //        fSearchButton = new Button(buttons, SWT.PUSH);
        //        fSearchButton.setFont(font);       
        //        setButtonLayoutData(fSearchButton);
        //        fSearchButton.setText(CougaarPreferencesMessages.getString(
        //                "CougaarPreferencePage.&Search..._1")); //$NON-NLS-1$
        //        fSearchButton.addListener(SWT.Selection,
        //            new Listener() {
        //                public void handleEvent(Event evt) {
        //                    search();
        //                }
        //            });
        configureTableResizing(parent, buttons, table, column2, column3);

        populateCougaarList();
        enableButtons();


        return parent;
    }


    /**
     * Correctly resizes the table so no phantom columns appear
     *
     * @param parent DOCUMENT ME!
     * @param buttons DOCUMENT ME!
     * @param table DOCUMENT ME!
     * @param column1 DOCUMENT ME!
     * @param column2 DOCUMENT ME!
     */
    protected void configureTableResizing(final Composite parent,
        final Composite buttons, final Table table, final TableColumn column1,
        final TableColumn column2) {
        parent.addControlListener(new ControlAdapter() {
                public void controlResized(ControlEvent e) {
                    Rectangle area = parent.getClientArea();
                    Point preferredSize = table.computeSize(SWT.DEFAULT,
                            SWT.DEFAULT);
                    int width = area.width - (2 * table.getBorderWidth());
                    if (preferredSize.y > area.height) {
                        // Subtract the scrollbar width from the total column width
                        // if a vertical scrollbar will be required
                        Point vBarSize = table.getVerticalBar().getSize();
                        width -= vBarSize.x;
                    }

                    width -= buttons.getSize().x;
                    Point oldSize = table.getSize();
                    if (oldSize.x > width) {
                        // table is getting smaller so make the columns
                        // smaller first and then resize the table to
                        // match the client area width
                        column1.setWidth(width / 2);
                        column2.setWidth(width / 2);

                        table.setSize(width, area.height);
                    } else {
                        // table is getting bigger so make the table
                        // bigger first and then make the columns wider
                        // to match the client area width
                        table.setSize(width, area.height);
                        column1.setWidth(width / 2);
                        column2.setWidth(width / 2);

                    }
                }
            });
    }


    private void populateCougaarList() {
        Map map = CougaarPlugin.getAllCougaarLocations();
        fCougaarInstalls = new ArrayList();
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String version = (String) keys.next();
            String location = (String) map.get(version);
            fCougaarInstalls.add(new CougaarInstall(version, location));
        }

        fCougaarList.setInput(fCougaarInstalls);
        setDefaultCougaarSelection();

    }


    private void addCougaar() {
        AddCougaarDialog dialog = new AddCougaarDialog(this, getShell(), null);
        dialog.setTitle(CougaarPreferencesMessages.getString(
                "cougaarPreferencePage.addCougaar.title")); //$NON-NLS-1$
        if (dialog.open() != Window.OK) {
            return;
        }

        fCougaarList.refresh();
    }


    private void removeCougaarInstalls() {
        IStructuredSelection selection = (IStructuredSelection) fCougaarList
            .getSelection();
        Iterator elements = selection.iterator();
        while (elements.hasNext()) {
            Object o = elements.next();

            if (o instanceof CougaarInstall) {
                CougaarInstall cInstall = (CougaarInstall) o;

                CougaarUI.setCougaarInstallPathLocation(cInstall.getId(), "");
                this.cougaarRemoved(cInstall);
            }

            fCougaarInstalls.remove(o);
        }


        fCougaarList.refresh();
    }


    private void enableButtons() {
        fAddButton.setEnabled(fCougaarInstalls.size() > -1);
        int selectionCount = ((IStructuredSelection) fCougaarList.getSelection())
            .size();
        fEditButton.setEnabled(selectionCount == 1);
        fRemoveButton.setEnabled((selectionCount > 0)
            && (selectionCount < fCougaarList.getTable().getItemCount()));
    }


    private void setDefaultCougaarSelection() {
        for (int i = 0; i < fCougaarInstalls.size(); i++) {
            ICougaarInstall cougaar = (ICougaarInstall) fCougaarInstalls.get(i);
            if (CougaarPlugin.isDefaultCougaarVersion(cougaar.getId())) {
                fCougaarList.setChecked(cougaar, true);
            } else {
                fCougaarList.setChecked(cougaar, false);
            }
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param cougaar DOCUMENT ME!
     */
    public void setCheckedCougaar(ICougaarInstall cougaar) {
        if (cougaar == null) {
            setSelection(new StructuredSelection());
        } else {
            setSelection(new StructuredSelection(cougaar));
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param selection DOCUMENT ME!
     */
    public void setSelection(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            if (!selection.equals(fPrevSelection)) {
                fPrevSelection = selection;
                Object cougaar = ((IStructuredSelection) selection)
                    .getFirstElement();
                fCougaarList.setCheckedElements(new Object[] { cougaar });
                fCougaarList.reveal(cougaar);
                ICougaarInstall cInstall = (ICougaarInstall) cougaar;
                CougaarPlugin.setDefaultCougaarVersion(cInstall.getId());
                fireSelectionChanged();
            }
        }
    }


    private void fireSelectionChanged() {
        SelectionChangedEvent event = new SelectionChangedEvent(this,
                getSelection());
        Object[] listeners = fSelectionListeners.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
            listener.selectionChanged(event);
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ISelection getSelection() {
        return new StructuredSelection(fCougaarList.getCheckedElements());
    }


    private void editCougaar() {
        IStructuredSelection selection = (IStructuredSelection) fCougaarList
            .getSelection();

        // assume it's length one, otherwise this will not be called
        ICougaarInstall cougaarInstall = (ICougaarInstall) selection
            .getFirstElement();
        if (cougaarInstall == null) {
            // not element selected, must have double clicked on the check box
            return;
        }

        AddCougaarDialog dialog = new AddCougaarDialog(this, getShell(),
                cougaarInstall);
        dialog.setTitle(CougaarPreferencesMessages.getString(
                "CougaarPreferencePage.editVersion.title"));
        if (dialog.open() != Window.OK) {
            return;
        }

        fCougaarList.refresh(cougaarInstall);
    }


    /* (non-Javadoc)
     * @see com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor#isDuplicateName(java.lang.String)
     */
    public boolean isDuplicateName(String name) {
        for (int i = 0; i < fCougaarInstalls.size(); i++) {
            ICougaarInstall cougaar = (ICougaarInstall) fCougaarInstalls.get(i);
            if (cougaar.getId().equals(name)) {
                return true;
            }
        }

        return false;
    }


    /* (non-Javadoc)
     * @see com.cougaarsoftware.cougaar.ide.ui.IAddCougaarDialogRequestor#cougaarAdded(com.cougaarsoftware.cougaar.ide.core.ICougaarInstall)
     */
    public void cougaarAdded(ICougaarInstall cougaar) {
        fCougaarInstalls.add(cougaar);
        fCougaarList.refresh();
        if (requestor != null) {
            requestor.cougaarAdded(cougaar);
        }

        if (fCougaarInstalls.size() == 1) {
            CougaarPlugin.setDefaultCougaarVersion(cougaar.getId());
            setDefaultCougaarSelection();
        }
    }


    /**
     * Notify cougaar removed listeners if a cougaar installation was removed
     *
     * @param cougaar the cougaar installation that was removed
     */
    public void cougaarRemoved(ICougaarInstall cougaar) {
        Object[] listeners = fSelectionListeners.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            Object o = listeners[i];
            if (o instanceof ICougaarInstallChangeListener) {
                ICougaarInstallChangeListener removedListener = (ICougaarInstallChangeListener) o;
                removedListener.cougaarRemoved(cougaar);
            }
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        fSelectionListeners.add(listener);

    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(
        ISelectionChangedListener listener) {
        fSelectionListeners.remove(listener);

    }
}
