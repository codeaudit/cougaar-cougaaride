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


import org.eclipse.jface.text.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class XMLDoubleClickStrategy implements ITextDoubleClickStrategy {
    protected ITextViewer fText;

    /**
     * DOCUMENT ME!
     *
     * @param part DOCUMENT ME!
     */
    public void doubleClicked(ITextViewer part) {
        int pos = part.getSelectedRange().x;

        if (pos < 0) {
            return;
        }

        fText = part;

        if (!selectComment(pos)) {
            selectWord(pos);
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @param caretPos DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected boolean selectComment(int caretPos) {
        IDocument doc = fText.getDocument();
        int startPos;
        int endPos;

        try {
            int pos = caretPos;
            char c = ' ';

            while (pos >= 0) {
                c = doc.getChar(pos);
                if (c == '\\') {
                    pos -= 2;
                    continue;
                }

                if ((c == Character.LINE_SEPARATOR) || (c == '\"')) {
                    break;
                }

                --pos;
            }

            if (c != '\"') {
                return false;
            }

            startPos = pos;

            pos = caretPos;
            int length = doc.getLength();
            c = ' ';

            while (pos < length) {
                c = doc.getChar(pos);
                if ((c == Character.LINE_SEPARATOR) || (c == '\"')) {
                    break;
                }

                ++pos;
            }

            if (c != '\"') {
                return false;
            }

            endPos = pos;

            int offset = startPos + 1;
            int len = endPos - offset;
            fText.setSelectedRange(offset, len);
            return true;
        } catch (BadLocationException x) {
        }

        return false;
    }


    /**
     * DOCUMENT ME!
     *
     * @param caretPos DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected boolean selectWord(int caretPos) {
        IDocument doc = fText.getDocument();
        int startPos;
        int endPos;

        try {
            int pos = caretPos;
            char c;

            while (pos >= 0) {
                c = doc.getChar(pos);
                if (!Character.isJavaIdentifierPart(c)) {
                    break;
                }

                --pos;
            }

            startPos = pos;

            pos = caretPos;
            int length = doc.getLength();

            while (pos < length) {
                c = doc.getChar(pos);
                if (!Character.isJavaIdentifierPart(c)) {
                    break;
                }

                ++pos;
            }

            endPos = pos;
            selectRange(startPos, endPos);
            return true;

        } catch (BadLocationException x) {
        }

        return false;
    }


    private void selectRange(int startPos, int stopPos) {
        int offset = startPos + 1;
        int length = stopPos - offset;
        fText.setSelectedRange(offset, length);
    }
}
