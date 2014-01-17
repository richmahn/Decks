package com.chinesepod.decks.settings;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.chinesepod.decks.CPDecksSettingsActivity;

public class BioPreference extends EditTextPreference {

	public BioPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BioPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BioPreference(Context context) {
		super(context);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		getPreferenceManager().setSharedPreferencesName("account");

		final String name = getSharedPreferences().getString("bio", "");
		View v = super.onCreateView(parent);
		setSummary(name);

		return v;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			((CPDecksSettingsActivity) getContext()).changeBio(getEditText().getText().toString());
		}
	}

}
