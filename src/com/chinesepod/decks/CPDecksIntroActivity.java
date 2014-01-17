/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 */
package com.chinesepod.decks;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import com.chinesepod.decks.adapter.CPDecksIntroFragmentAdapter;

public class CPDecksIntroActivity extends FragmentActivity {
	public static final String PREF_DONT_SHOW_INTRO = "com.chinesepod.decks:dontShowIntro"; // SharedPreference to stop showing intro screen
	
    CPDecksIntroFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        mAdapter = new CPDecksIntroFragmentAdapter(this, getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        
        findViewById(R.id.StartButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
    }
    
    @Override
    public void finish(){
		SharedPreferences prefs = getSharedPreferences(getApplication().getPackageName(), 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PREF_DONT_SHOW_INTRO, true);
		editor.commit();
		
		super.finish();
    }
}
