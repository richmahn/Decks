/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Word of the Day activity
 */
package com.chinesepod.decks;

import android.os.Bundle;

public class CPDecksWordOfTheDayActivity extends CPDecksActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_word_of_the_day;
	}
}
