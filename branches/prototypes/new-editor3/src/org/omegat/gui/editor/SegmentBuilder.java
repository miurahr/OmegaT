/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2009 Alex Buloichik
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **************************************************************************/

package org.omegat.gui.editor;

import java.text.DecimalFormat;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.omegat.core.data.SourceTextEntry;
import org.omegat.util.OConsts;
import org.omegat.util.Preferences;
import org.omegat.util.gui.Styles;
import org.omegat.util.gui.UIThreadsUtil;

/**
 * Class for store information about displayed segment, and for show segment in
 * editor.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class SegmentBuilder {

    /** Attributes for show text. */
    protected static final AttributeSet ATTR_SOURCE = Styles.GREEN;
    protected static final AttributeSet ATTR_SEGMENT_MARK = Styles.BOLD;
    protected static final AttributeSet ATTR_TRANS_TRANSLATED = Styles.TRANSLATED;
    protected static final AttributeSet ATTR_TRANS_UNTRANSLATED = Styles.UNTRANSLATED;
    protected static final AttributeSet ATTR_ACTIVE = new SimpleAttributeSet();
    public static final String SEGMENT_MARK_ATTRIBUTE = "SEGMENT_MARK_ATTRIBUTE";
    public static final String SEGMENT_SPELL_CHECK = "SEGMENT_SPELL_CHECK";
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0000");

    final SourceTextEntry ste;
    final int segmentNumberInProject;

    private final Document3 doc;
    private final EditorController3 controller;
    private final EditorSettings settings;

    protected int activeTranslationBeginOffset, activeTranslationEndOffset;

    protected Position beginPosP1, endPosM1;

    /**
     * true if beginSpellCheck/endSpellCheck is P/M mode, i.e. smaller of real
     * text - used for inactive text
     * 
     * false if M/P mode, i.e. bigger of real text - used for active text
     */
    protected boolean spellPM;
    protected Position beginSpellCheckPM1, endSpellCheckPM1;

    protected int offset;

    public SegmentBuilder(final EditorController3 controller,
            final Document3 doc, final EditorSettings settings,
            final SourceTextEntry ste, final int segmentNumberInProject) {
        this.controller = controller;
        this.doc = doc;
        this.settings = settings;
        this.ste = ste;
        this.segmentNumberInProject = segmentNumberInProject;
    }

    /**
     * Create element for one segment.
     * 
     * @param doc
     *            document
     * @return OmElementSegment
     */
    protected void createSegmentElement(final boolean isActive) {
        UIThreadsUtil.mustBeSwingThread();

        beginSpellCheckPM1 = null;
        endSpellCheckPM1 = null;

        doc.trustedChangesInProgress = true;
        try {
            int beginOffset, endOffset;
            try {
                if (beginPosP1 != null && endPosM1 != null) {
                    // remove old segment
                    beginOffset = beginPosP1.getOffset() - 1;
                    endOffset = endPosM1.getOffset() + 1;
                    doc.remove(beginOffset, endOffset - beginOffset);
                    offset = beginOffset;
                } else {
                    offset = doc.getLength();
                }

                boolean translationExists = ste.getTranslation() != null
                        && ste.getTranslation().length() > 0;

                boolean needToCheckSpelling = false;
                beginOffset = offset;
                if (isActive) {
                    /** Create for active segment. */
                    addInactiveSegPart(ste.getSrcText(), ATTR_SOURCE);
                    setAttributes(beginOffset, offset, true);

                    String activeText;
                    if (translationExists) {
                        // translation exist
                        activeText = ste.getTranslation();
                        if (settings.isAutoSpellChecking()) {
                            // spell it
                            needToCheckSpelling = true;
                            doc.controller.spellCheckerThread.addForCheck(ste
                                    .getTranslation());
                        }
                    } else if (!Preferences
                            .isPreference(Preferences.DONT_INSERT_SOURCE_TEXT)) {
                        // need to insert source text on empty translation
                        activeText = ste.getSrcText();
                        if (settings.isAutoSpellChecking()) {
                            // spell it
                            needToCheckSpelling = true;
                            doc.controller.spellCheckerThread.addForCheck(ste
                                    .getSrcText());
                        }
                    } else {
                        // empty text on non-exist translation
                        activeText = "";
                    }

                    int prevOffset = offset;
                    addActiveSegPart(activeText, ATTR_ACTIVE);
                    setAttributes(prevOffset, offset, false);

                    if (needToCheckSpelling) {
                        beginSpellCheckPM1 = doc
                                .createPosition(activeTranslationBeginOffset - 1);
                        endSpellCheckPM1 = doc
                                .createPosition(activeTranslationEndOffset + 1);
                        spellPM = false;
                    }
                } else {
                    /** Create for inactive segment. */
                    if (settings.isDisplaySegmentSources()) {
                        addInactiveSegPart(ste.getSrcText(), ATTR_SOURCE);
                        setAttributes(beginOffset, offset, true);
                    }
                    if (translationExists) {
                        // translation exist
                        if (settings.isAutoSpellChecking()) {
                            // spell it
                            needToCheckSpelling = true;
                            doc.controller.spellCheckerThread.addForCheck(ste
                                    .getTranslation());
                        }
                        int prevOffset = offset;
                        addInactiveSegPart(ste.getTranslation(), settings
                                .getTranslatedAttributeSet());
                        setAttributes(prevOffset, offset, false);

                        if (needToCheckSpelling) {
                            beginSpellCheckPM1 = doc
                                    .createPosition(prevOffset + 1);
                            endSpellCheckPM1 = doc.createPosition(offset - 1);
                            spellPM = true;
                        }
                    } else if (!settings.isDisplaySegmentSources()) {
                        int prevOffset = offset;
                        addInactiveSegPart(ste.getSrcText(), settings
                                .getUntranslatedAttributeSet());
                        setAttributes(prevOffset, offset, false);
                    }
                }
                insert("\n", null);
                endOffset = offset;

                beginPosP1 = doc.createPosition(beginOffset + 1);
                endPosM1 = doc.createPosition(endOffset - 1);
                doc.activeTranslationBeginM1 = doc
                        .createPosition(activeTranslationBeginOffset - 1);
                doc.activeTranslationEndP1 = doc
                        .createPosition(activeTranslationEndOffset + 1);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            doc.trustedChangesInProgress = false;
        }
    }

    /**
     * Get segment's start position.
     * 
     * @return start position
     */
    public int getStartPosition() {
        return beginPosP1.getOffset() - 1;
    }

    /**
     * Set attributes for created paragraphs for better RTL support.
     * 
     * @param begin
     *            paragraphs begin
     * @param end
     *            paragraphs end
     * @param isSource
     *            is source segment part
     */
    private void setAttributes(int begin, int end, boolean isSource) {
        if (isSource) {
            if (controller.currentOrientation == Document3.ORIENTATION.DIFFER) {
                boolean rtl = controller.sourceLangIsRTL;
                SimpleAttributeSet a = new SimpleAttributeSet();
                a.addAttribute(StyleConstants.Alignment,
                        rtl ? StyleConstants.ALIGN_RIGHT
                                : StyleConstants.ALIGN_LEFT);
                doc.setParagraphAttributes(begin, end - begin, a, true);
            }
        }
    }

    public boolean isInsideSegment(int location) {
        return beginPosP1.getOffset() - 1 <= location
                && location < endPosM1.getOffset() + 1;
    }

    public boolean isInsideSpellCheckable(int location) {
        if (beginSpellCheckPM1 == null || endSpellCheckPM1 == null) {
            return false;
        }
        int b = beginSpellCheckPM1.getOffset();
        int e = endSpellCheckPM1.getOffset();
        if (spellPM) {
            b--;
            e++;
        } else {
            b++;
            e--;
        }
        return b <= location && location < e;
    }

    /**
     * Add inactive segment part, without segment begin/end marks.
     * 
     * @param parent
     *            parent element
     * @param text
     *            segment part text
     * @param attrs
     *            attributes
     * @param langIsRTL
     * @return true if language is RTL
     */
    private void addInactiveSegPart(String text, AttributeSet attrs)
            throws BadLocationException {
        String data = text + "\n";
        insert(data, attrs);
    }

    /**
     * Add active segment part, with segment begin/end marks.
     * 
     * @param parent
     *            parent element
     * @param text
     *            segment part text
     * @param attrs
     *            attributes
     * @param markBeg
     *            text of begin mark
     * @param markEnd
     *            text of end mark
     * @param langIsRTL
     *            true if language is RTL
     * @return segment part element
     */
    private void addActiveSegPart(String text, AttributeSet attrs)
            throws BadLocationException {

        StringBuilder smTextB = new StringBuilder();
        // place space on the left of begin segment mark for RTL text
        smTextB.append(OConsts.segmentStartString.trim().replace("0000",
                NUMBER_FORMAT.format(segmentNumberInProject)));
        insert(smTextB.toString(), ATTR_SEGMENT_MARK);
        insert(" ", null);

        activeTranslationBeginOffset = offset;
        insert(text, attrs);
        activeTranslationEndOffset = offset;

        StringBuilder smTextE = new StringBuilder();
        // place space on the left of end segment mark for LTR text
        insert(" ", null);
        smTextE.append(OConsts.segmentEndString.trim());
        insert(smTextE.toString(), ATTR_SEGMENT_MARK);
        insert("\n", null);
    }

    private void insert(String text, AttributeSet attrs)
            throws BadLocationException {
        doc.insertString(offset, text, attrs);
        offset += text.length();
    }
}
