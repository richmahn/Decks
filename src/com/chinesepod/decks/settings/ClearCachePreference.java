package com.chinesepod.decks.settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.utility.db.CPDecksDBHelper;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class ClearCachePreference extends DialogPreference {
	private Context mContext;
	
	public ClearCachePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPersistent(false);
	}

	@Override
	protected void onBindDialogView(View view) {
		// TODO Auto-generated method stub
		super.onBindDialogView(view);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			CPDecksDBHelper.clearCachedApiData();
			UrlImageViewHelper.cleanup(mContext, 1);
		}
		super.onDialogClosed(positiveResult);
	}
}
