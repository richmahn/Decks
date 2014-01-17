/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity for showing the history of translations
 */
package com.chinesepod.decks;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.chinesepod.decks.logic.CPDecksTranslation;

/**
 * This class handles the history of past translations.
 */
public class History {
    private static final String HISTORY = "history";

    /**
     * Sort the translations by timestamp.
     */
    private static final Comparator<HistoryRecord> MOST_RECENT_COMPARATOR
             = new Comparator<HistoryRecord>() {

        public int compare(HistoryRecord object1, HistoryRecord object2) {
            return (int) (object2.when - object1.when);
        }
    };

    /**
     * Sort the translations by destination language and then by input.
     */
    private static final Comparator<HistoryRecord> LANGUAGE_COMPARATOR
            = new Comparator<HistoryRecord>() {

        public int compare(HistoryRecord object1, HistoryRecord object2) {
            int result = object1.mTranslation.getTargetLanguage().getLongName().compareTo(object2.mTranslation.getTargetLanguage().getLongName());
            if (result == 0) {
                result = object1.mTranslation.getSource().compareTo(object2.mTranslation.getSource());
            }
            return result;
        }
    };

    private List<HistoryRecord> mHistoryRecords = Lists.newArrayList();
    
    public History(SharedPreferences prefs) {
        mHistoryRecords = restoreHistory(prefs);
    }

    public static List<HistoryRecord> restoreHistory(SharedPreferences prefs) {
        List<HistoryRecord> result = Lists.newArrayList();
        Map<String, ?> allKeys = prefs.getAll();
        for (String key : allKeys.keySet()) {
            if (key.startsWith(HISTORY)) {
                String value = (String) allKeys.get(key);
                result.add(HistoryRecord.decode(value));
            }
        }

        return result;
    }

    public static void addHistoryRecord(Context context, CPDecksTranslation translation) {
        HistoryRecord hr = new HistoryRecord(translation, System.currentTimeMillis());
        
        // Find an empty key to add this history record
        SharedPreferences prefs = CPDecksApplication.getPrefs(context);
        int i = 0;
        while (true) {
            String key = HISTORY + "-" + i; 
            if (!prefs.contains(key)) {
                Editor edit = prefs.edit();
                edit.putString(key, hr.encode());
                log("Committing " + key + " " + hr.encode());
                edit.commit();
                return;
            } else {
                i++;
            }
        }
    }
    
    private static void log(String s) {
        Log.d("History", s);
    }
    
    
    public List<HistoryRecord> getHistoryRecordsMostRecentFirst() {
        Collections.sort(mHistoryRecords, MOST_RECENT_COMPARATOR);
        return mHistoryRecords;
    }
    
    public List<HistoryRecord> getHistoryRecordsByLanguages() {
        Collections.sort(mHistoryRecords, LANGUAGE_COMPARATOR);
        return mHistoryRecords;
    }

    public List<HistoryRecord> getHistoryRecords(Comparator<HistoryRecord> comparator) {
        if (comparator != null) {
            Collections.sort(mHistoryRecords, comparator);
        }
        return mHistoryRecords;
    }

    public void clear(Context context) {
        int size = mHistoryRecords.size();
        mHistoryRecords = Lists.newArrayList();
        Editor edit = CPDecksApplication.getPrefs(context).edit();
        for (int i = 0; i < size; i++) {
            String key = HISTORY + "-" + i;
            log("Removing key " + key);
            edit.remove(key);
        }
        edit.commit();
    }
}
