package com.chinesepod.decks.settings;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chinesepod.decks.CPDecksSettingsActivity;

public class UsernamePreference extends EditTextPreference implements OnPreferenceChangeListener {

	public UsernamePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public UsernamePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UsernamePreference(Context context) {
		super(context);
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		return false;
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		getPreferenceManager().setSharedPreferencesName("account");
		final String username = getSharedPreferences().getString("username", "Unnamed");
		View v = super.onCreateView(parent);
		setSummary(username);
		setOnPreferenceChangeListener(this);
		final EditText te = getEditText();
		TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (te.getText().toString().contains(" ")) {
					te.setText("");
				}
			}

		};
		te.addTextChangedListener(fieldValidatorTextWatcher);
		getEditText().setSingleLine();
		getEditText().setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						((CPDecksSettingsActivity) getContext()).changeUsername(getEditText().getText().toString());
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
			((CPDecksSettingsActivity) getContext()).changeUsername(getEditText().getText().toString());
		}
	}

}
