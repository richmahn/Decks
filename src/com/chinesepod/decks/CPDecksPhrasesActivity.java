/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Phrases Activity
 */
package com.chinesepod.decks;

import android.os.Bundle;

public class CPDecksPhrasesActivity extends CPDecksActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_phrases;
	}
}
