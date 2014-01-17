package com.chinesepod.decks.settings;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;

import com.chinesepod.decks.CPDecksSettingsActivity;

public class FullnamePreference extends EditTextPreference {

	public FullnamePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FullnamePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FullnamePreference(Context context) {
		super(context);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		getPreferenceManager().setSharedPreferencesName("account");

		final String name = getSharedPreferences().getString("name", "Unnamed");
		View v = super.onCreateView(parent);
		setSummary(name);
		getEditText().setSingleLine();
		getEditText().setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						((CPDecksSettingsActivity) getContext()).changeFullname(getEditText().getText().toString());
						getDialog().dismiss();
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});
		return v;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			((CPDecksSettingsActivity) getContext()).changeFullname(getEditText().getText().toString());
		}
	}

}
