/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Help Activity for Settings
 */
package com.chinesepod.decks;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;

import com.chinesepod.decks.R;
import com.chinesepod.decks.utility.CPDecksUtility;

public class CPDecksSettingsHelpActivity extends CPDecksActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final WebView webView = new WebView(this);
		WebSettings webSettings = webView.getSettings();
		if (CPDecksUtility.isTablet((Activity) this)) {
			webSettings.setTextSize(WebSettings.TextSize.LARGER);
		}
		webView.setVerticalScrollBarEnabled(false);

		((ScrollView) findViewById(R.id.instructionsScrollView)).addView(webView);

//		webView.loadDataWithBaseURL(null, getString(R.string.instructions_text), "text/html", "UTF-8", "about:blank");
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.settings_help;
	}
}
