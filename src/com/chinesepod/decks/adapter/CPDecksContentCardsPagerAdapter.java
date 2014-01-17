package com.chinesepod.decks.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;

import com.chinesepod.decks.fragment.ContentCardFragment;
import com.chinesepod.decks.logic.CPDecksContent;
import com.chinesepod.decks.logic.CPDecksContentCategory;
import com.chinesepod.decks.utility.CPDecksContentManager;

public class CPDecksContentCardsPagerAdapter extends CPodFragmentPagerAdapter {

	private CPDecksContentCategory mContentCategory;
	List<Fragment> pages = null;
	private Map<Integer, Fragment> mPageReferenceMap;
	private ArrayList<Long> mContentIdList;

	public CPDecksContentCardsPagerAdapter(FragmentManager fm, CPDecksContentCategory contentCategory) {
		super(fm);
		mContentCategory = contentCategory;
		mContentIdList = mContentCategory.getContentIdList();
		mPageReferenceMap = new HashMap<Integer, Fragment>();
	}

	@Override
	public int getCount() {
		return mContentIdList.size();
	}

	@Override
	public void startUpdate(View container) {
		super.startUpdate(container);
	}

	@Override
	public Fragment getItem(int position) {

		ContentCardFragment fr = new ContentCardFragment();
		fr.setContent(CPDecksContentManager.getContent(mContentIdList.get(position)));
		fr.setContentCategory(mContentCategory);
		return fr;
	}

	public ContentCardFragment getFragment(int position) {
		return (ContentCardFragment) mPageReferenceMap.get(position);
	}

	public void destroyItem(View container, int position, Object object) {
		super.destroyItem(container, position, object);
		mPageReferenceMap.remove(position);
	}

	@Override
	public void linkItem(int position, Fragment fragment) {
		mPageReferenceMap.put(position, fragment);
	}
}
