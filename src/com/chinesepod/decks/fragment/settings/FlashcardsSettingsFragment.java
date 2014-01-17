package com.chinesepod.decks.fragment.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

import com.chinesepod.decks.CPDecksSettingsActivity;
import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.logic.CPDecksAccount;

public class FlashcardsSettingsFragment extends PreferenceFragment {

	private CheckBoxPreference mFlashcardShowTargetPref;
	private CheckBoxPreference mFlashcardShowTargetPhoneticsPref;
	private CheckBoxPreference mFlashcardShowSourcePref;
	private CheckBoxPreference mFlashcardShowImagePref;
	private CheckBoxPreference mFlashcardShowAudioPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.flashcards_preferences);

		final CPDecksAccount account = CPDecksAccount.getInstance();

		mFlashcardShowTargetPref = (CheckBoxPreference)findPreference("flashcards_show_target");
		mFlashcardShowTargetPref.setChecked(account.getFlashCardsShowTarget());
		mFlashcardShowTargetPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				account.setFlashCardsShowTarget((Boolean) newValue);
				return true;
			}
		});

		mFlashcardShowTargetPhoneticsPref = (CheckBoxPreference)findPreference("flashcards_show_target_phonetics");
		mFlashcardShowTargetPhoneticsPref.setChecked(account.getFlashCardsShowTargetPhonetics());
		mFlashcardShowTargetPhoneticsPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				account.setFlashCardsShowTargetPhonetics((Boolean) newValue);
				return true;
			}
		});

		mFlashcardShowSourcePref = (CheckBoxPreference)findPreference("flashcards_show_source");
		mFlashcardShowSourcePref.setChecked(account.getFlashCardsShowSource());
		mFlashcardShowSourcePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				account.setFlashCardsShowSource((Boolean) newValue);
				return true;
			}
		});
		
		mFlashcardShowImagePref = (CheckBoxPreference)findPreference("flashcards_show_image");
		mFlashcardShowImagePref.setChecked(account.getFlashCardsShowImage());
		mFlashcardShowImagePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				account.setFlashCardsShowImage((Boolean) newValue);
				return true;
			}
		});

		mFlashcardShowAudioPref = (CheckBoxPreference)findPreference("flashcards_show_audio");
		mFlashcardShowAudioPref.setChecked(account.getFlashCardsShowAudio());
		mFlashcardShowAudioPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				account.setFlashCardsShowAudio((Boolean) newValue);
				return true;
			}
		});

		ListPreference timeIntervalList = ((ListPreference) findPreference("flascards_time_interval"));
		timeIntervalList.setValue(account.getFlashCardsTimeInterval() + "");
		timeIntervalList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object val) {
				account.setFlashCardsTimeInterval(Integer.valueOf((String) val));
				return true;
			}
		});

		ListPreference deckEndedBehaviorList = ((ListPreference) findPreference("end_of_deck_behavior"));
		deckEndedBehaviorList.setValue(account.getDeckEndedBehavior());
		deckEndedBehaviorList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object val) {
				account.setDeckEndedBehavior((String) val);
				return true;
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
