package com.chinesepod.decks.settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import com.chinesepod.decks.utility.CPDecksUtility;

public class ClearAudioPreference extends DialogPreference {
	private Context mContext;
	
	public ClearAudioPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPersistent(false);
		mContext = context;
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
//			CPDecksUtility.removeAllAudioFiles();
		}
		super.onDialogClosed(positiveResult);
	}
}
