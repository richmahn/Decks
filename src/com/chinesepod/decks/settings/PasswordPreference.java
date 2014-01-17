package com.chinesepod.decks.settings;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksSettingsActivity;

public class PasswordPreference extends DialogPreference {

	public PasswordPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPersistent(false);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		getPreferenceManager().setSharedPreferencesName("account");

		View v = super.onCreateView(parent);
		return v;
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setPositiveButton("Change", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText oldPasswordET = (EditText) getDialog().findViewById(R.id.dialog_old_password);
				EditText confirmPasswordET = (EditText) getDialog().findViewById(R.id.dialog_confirm_password);
				EditText newPasswordET = (EditText) getDialog().findViewById(R.id.dialog_new_password);
				String oldPassword = oldPasswordET.getText().toString();
				String newPassword = newPasswordET.getText().toString();
				String confirmPassword = confirmPasswordET.getText().toString();
				if (!newPassword.equals(confirmPassword)) {

					Toast.makeText(getContext(), "Confirm password doesn't match", Toast.LENGTH_LONG).show();
				} else {
					((CPDecksSettingsActivity) getContext()).changePassword(oldPassword, newPassword);
				}
			}
		});
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
		}
		super.onDialogClosed(positiveResult);
	}

}
