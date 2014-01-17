package com.chinesepod.decks;

import static com.chinesepod.decks.Languages.Language;

import com.chinesepod.decks.logic.CPDecksTranslation;

/**
 * This class describes one entry in the history
 */
public class HistoryRecord {
    private static final String SEPARATOR = "@";

	public CPDecksTranslation mTranslation;
    public long when;

    public HistoryRecord(CPDecksTranslation translation, long when) {
        super();
        this.mTranslation = translation;
        this.when = when;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        try {
            HistoryRecord other = (HistoryRecord) o;
            return other.mTranslation.getSourceLanguage().equals(mTranslation.getSourceLanguage()) && other.mTranslation.getTargetLanguage().equals(mTranslation.getTargetLanguage()) &&
                    other.mTranslation.getSource().equals(mTranslation.getSource()) && other.mTranslation.getTarget().equals(mTranslation.getTarget());
        } catch(Exception ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return mTranslation.getSource().hashCode() ^ mTranslation.getTarget().hashCode() ^ mTranslation.getSource().hashCode() ^ mTranslation.getTarget().hashCode();
    }

    public String encode() {
        return mTranslation.getSourceLanguage().name() + SEPARATOR + mTranslation.getTargetLanguage().name() + SEPARATOR + mTranslation.getSource()
                + SEPARATOR + mTranslation.getTarget() + SEPARATOR + new Long(when);
    }
    
    @Override
    public String toString() {
        return encode();
    }
    
    public static HistoryRecord decode(String s) {
        Object[] o = s.split(SEPARATOR);
        int i = 0;
        Language from = Language.valueOf((String) o[i++]);
        Language to = Language.valueOf((String) o[i++]);
        String input = (String) o[i++];
        String output = (String) o[i++];
        CPDecksTranslation translation = new CPDecksTranslation();
        translation.setSourceLanguage(from);
        translation.setTargetLanguage(to);
        translation.setSource(input);
        translation.setTarget(output);
        Long when = Long.valueOf((String) o[i++]);
        HistoryRecord result = new HistoryRecord(translation, when);
        return result;
    }
    
}

