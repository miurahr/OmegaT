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

public class SegmentBuilder {

    /** Attributes for show text. */
    protected static final AttributeSet ATTR_SOURCE = Styles.GREEN;
    protected static final AttributeSet ATTR_SEGMENT_MARK;
    protected static final AttributeSet ATTR_TRANS_TRANSLATED = Styles.TRANSLATED;
    protected static final AttributeSet ATTR_TRANS_UNTRANSLATED = Styles.UNTRANSLATED;
    protected static final AttributeSet ATTR_NONE = new SimpleAttributeSet();
    public static final String SEGMENT_MARK_ATTRIBUTE="SEGMENT_MARK_ATTRIBUTE";
    
    static {
        SimpleAttributeSet a=new SimpleAttributeSet();
        a.addAttribute(SEGMENT_MARK_ATTRIBUTE, SEGMENT_MARK_ATTRIBUTE);
        ATTR_SEGMENT_MARK=a;
    }

    final SourceTextEntry ste;
    final int segmentNumberInProject;
    boolean needToCheckSpelling;

    private final Document3 doc;
    private final EditorController3 controller;
    private final EditorSettings settings;

    private final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0000");

    protected int activeTranslationBeginOffset, activeTranslationEndOffset;
    protected Position beginPosP1, endPosM1;

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
        
        doc.trustedChangesInProgress=true;
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
                            // doc.controller.spellCheckerThread.addForCheck(ste
                            // .getTranslation());
                        }
                    } else if (!Preferences
                            .isPreference(Preferences.DONT_INSERT_SOURCE_TEXT)) {
                        // need to insert source text on empty translation
                        activeText = ste.getSrcText();
                        if (settings.isAutoSpellChecking()) {
                            // spell it
                            needToCheckSpelling = true;
                            // doc.controller.spellCheckerThread.addForCheck(ste
                            // .getSrcText());
                        }
                    } else {
                        // empty text on non-exist translation
                        activeText = "";
                    }

                    int prevOffset=offset;
                    addActiveSegPart(activeText, ATTR_NONE);
                    setAttributes(prevOffset, offset, false);
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
                            // doc.controller.spellCheckerThread.addForCheck(ste
                            // .getTranslation());
                        }
                        int prevOffset=offset;
                        addInactiveSegPart(ste.getTranslation(), settings
                                .getTranslatedAttributeSet());
                        setAttributes(prevOffset, offset, false);
                    } else if (!settings.isDisplaySegmentSources()) {
                        int prevOffset=offset;
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
            doc.trustedChangesInProgress=false;
        }
    }
    
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
        insert(" ",null);

        activeTranslationBeginOffset = offset;
        insert(text, attrs);
        activeTranslationEndOffset = offset;

        StringBuilder smTextE = new StringBuilder();
        // place space on the left of end segment mark for LTR text
        insert(" ",null);
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
