/*
 * <copyright> Copyright 2000-2004 Cougaar Software, Inc. All Rights Reserved
 * </copyright>
 */
package com.cougaarsoftware.cougaar.ide.launcher.ui.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.cougaarsoftware.cougaar.ide.launcher.ui.LauncherUIMessages;

/**
 * A composite that displays the list of node names contained in a Society XML
 * file
 * 
 * @author mabrams
 * 
 * @version $Revision: 1.1 $
 */
public class NodeSelectionComboBlock implements ISelectionProvider {

    /**
     * This block's control
     */
    private Composite fControl;

    /**
     * The title used for the Node Name block
     */
    private String fTitle = null;

    /**
     * The main control
     */
    private Combo fCombo;

    /**
     * Node Names being displayed
     */
    private List fNodeNames = new ArrayList();

    /**
     * label for nod enames
     */
    private Label fNodeNameLabel;

    /**
     * Selection listeners (checked JRE changes)
     */
    private ListenerList fSelectionListeners = new ListenerList();

    /**
     * Previous selection
     */
    private ISelection fPrevSelection = new StructuredSelection();

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        fSelectionListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        String nodeName = getNodeName();
        if (nodeName == null) {
            return new StructuredSelection();
        }
        return new StructuredSelection(nodeName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
        fSelectionListeners.remove(listener);
    }

    /**
     * Returns this block's control
     * 
     * @return control
     */
    public Control getControl() {
        return fControl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            if (!selection.equals(fPrevSelection)) {
                fPrevSelection = selection;
                if (selection.isEmpty()) {
                    fCombo.setText("");
                    fCombo.select(-1);
                    fCombo.setItems(new String[] {});
                } else {
                    Object name = ((IStructuredSelection) selection)
                            .getFirstElement();
                    int index = fNodeNames.indexOf(name);
                    if (index >= 0) {
                        fCombo.select(index);
                    }
                }
                fireSelectionChanged();
            }
        }
    }

    /**
     * Fire current selection
     */
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
     * Creates this block's control in the given control.
     * 
     * @param ancestor
     *                 containing control
     */
    public void createControl(Composite ancestor) {
        Font font = ancestor.getFont();
        Composite comp = new Composite(ancestor, SWT.NONE);
        GridLayout layout = new GridLayout();
        comp.setLayout(new GridLayout());

        comp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        fControl = comp;
        comp.setFont(font);

        Group group = new Group(comp, SWT.NO_TRIM);
        layout = new GridLayout();
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        group.setFont(font);

        GridData data;

        fNodeNameLabel = new Label(group, SWT.NONE);
        fNodeNameLabel.setText(LauncherUIMessages
                .getString("CougaarXMLParametersTab.NodeName"));
        data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        fNodeNameLabel.setLayoutData(data);

        fCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
        fCombo.setFont(font);
        data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        fCombo.setLayoutData(data);
        fCombo.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                fireSelectionChanged();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

    }

    /**
     * Sets the Node names to be displayed in this block
     * 
     * @param nodeNames
     *                 node names to be displayed
     */
    protected void setNodeNames(List nodeNames) {
        fNodeNames.clear();
        if (nodeNames != null) {
            fNodeNames.addAll(nodeNames);

            //sort by name
            Collections.sort(fNodeNames, new Comparator() {

                public int compare(Object o1, Object o2) {
                    String left = (String) o1;
                    String right = (String) o2;
                    return left.compareToIgnoreCase(right);
                }

                public boolean equals(Object obj) {
                    return obj == this;
                }
            });

            // now make array of names
            String[] names = new String[fNodeNames.size()];
            Iterator iter = fNodeNames.iterator();
            int i = 0;
            while (iter.hasNext()) {
                String name = (String) iter.next();
                names[i] = name;
                i++;
            }
            fCombo.setItems(names);
        } else {
            fCombo.setItems(new String[1]);
        }
    }

    public String getNodeName() {
        int index = fCombo.getSelectionIndex();
        if (index >= 0) {
            return (String) fNodeNames.get(index);
        }
        return null;
    }

    public void setNodeSelection(String nodeName) {
        for (int i = 0; i < fCombo.getItemCount(); i++) {
            String name = fCombo.getItem(i);
            if (name.equals(nodeName)) {
                fCombo.select(i);
            }
        }
    }

}
