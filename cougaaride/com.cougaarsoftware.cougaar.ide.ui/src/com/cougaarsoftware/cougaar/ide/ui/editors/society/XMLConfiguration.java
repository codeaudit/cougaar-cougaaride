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


/**
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials  are made available under the
 * terms of the Common Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/cpl-v10.html
 * Contributors: IBM Corporation - initial API and implementation
 */
package com.cougaarsoftware.cougaar.ide.ui.editors.society;


import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class XMLConfiguration extends SourceViewerConfiguration {
    private XMLDoubleClickStrategy doubleClickStrategy;
    private XMLTagScanner tagScanner;
    private XMLScanner scanner;
    private ColorManager colorManager;

    /**
     * Creates a new XMLConfiguration object.
     *
     * @param colorManager DOCUMENT ME!
     */
    public XMLConfiguration(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sourceViewer DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] {
            IDocument.DEFAULT_CONTENT_TYPE, XMLPartitionScanner.XML_COMMENT,
            XMLPartitionScanner.XML_TAG
        };
    }


    /**
     * DOCUMENT ME!
     *
     * @param sourceViewer DOCUMENT ME!
     * @param contentType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ITextDoubleClickStrategy getDoubleClickStrategy(
        ISourceViewer sourceViewer, String contentType) {
        if (doubleClickStrategy == null) {
            doubleClickStrategy = new XMLDoubleClickStrategy();
        }

        return doubleClickStrategy;
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected XMLScanner getXMLScanner() {
        if (scanner == null) {
            scanner = new XMLScanner(colorManager);
            scanner.setDefaultReturnToken(new Token(
                    new TextAttribute(colorManager.getColor(
                            IXMLColorConstants.DEFAULT))));
        }

        return scanner;
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected XMLTagScanner getXMLTagScanner() {
        if (tagScanner == null) {
            tagScanner = new XMLTagScanner(colorManager);
            tagScanner.setDefaultReturnToken(new Token(
                    new TextAttribute(colorManager.getColor(
                            IXMLColorConstants.TAG))));
        }

        return tagScanner;
    }


    /**
     * DOCUMENT ME!
     *
     * @param sourceViewer DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public IPresentationReconciler getPresentationReconciler(
        ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLTagScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_TAG);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_TAG);

        dr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(
                    colorManager.getColor(IXMLColorConstants.XML_COMMENT)));
        reconciler.setDamager(ndr, XMLPartitionScanner.XML_COMMENT);
        reconciler.setRepairer(ndr, XMLPartitionScanner.XML_COMMENT);

        return reconciler;
    }
}
