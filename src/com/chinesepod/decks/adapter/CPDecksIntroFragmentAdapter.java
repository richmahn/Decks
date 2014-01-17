package com.chinesepod.decks.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chinesepod.decks.R;
import com.chinesepod.decks.fragment.CPDecksIntroFragment;
import com.viewpagerindicator.IconPagerAdapter;

public class CPDecksIntroFragmentAdapter extends FragmentPagerAdapter {
    private int mCount;
	private Context mContext;

    public CPDecksIntroFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mCount = mContext.getResources().getStringArray(R.array.IntroImages).length;
    }

    @Override
    public Fragment getItem(int position) {
    	CPDecksIntroFragment fragment = new CPDecksIntroFragment();
        fragment.setPosition(position);
        
        return fragment;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return "Welcome "+(position+1);
    }
}