package com.chinesepod.decks.fragment.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksApplication;

public class SearchSettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.search_preferences);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CPDecksApplication.getContext());
		Boolean searchByLessonTitle = prefs.getBoolean("search_by_lesson_title", true);
		Boolean searchByLessonId = prefs.getBoolean("search_by_lesson_id", true);
		Boolean searchByLessonTags = prefs.getBoolean("search_by_lesson_tags", true);
		Boolean searchByLessonIntroduction = prefs.getBoolean("search_by_lesson_introduction", false);
		Boolean searchByLessonDialogueVocab = prefs.getBoolean("search_by_lesson_data", false);

		if ((searchByLessonTitle || searchByLessonId || searchByLessonTags || searchByLessonIntroduction || searchByLessonDialogueVocab) == false) {
			Editor editor = prefs.edit();
			editor.putBoolean("search_by_lesson_title", true);
			editor.commit();
		}
		super.onPause();
	}

}
