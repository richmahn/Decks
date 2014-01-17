package com.chinesepod.decks.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;

import com.chinesepod.decks.fragment.FlashcardFragment;
import com.chinesepod.decks.fragment.ImageFragment;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksImage;
import com.chinesepod.decks.logic.CPDecksVocabulary;

public class CPDecksImagePagerAdapter extends CPodFragmentPagerAdapter {
	List<Fragment> pages = null;
	private Map<Integer, Fragment> mPageReferenceMap;
	private ArrayList<CPDecksImage> mImageList;

	public CPDecksImagePagerAdapter(FragmentManager fm, ArrayList<CPDecksImage> imageList) {
		super(fm);
		mImageList = imageList;
		mPageReferenceMap = new HashMap<Integer, Fragment>();
	}

	@Override
	public int getCount() {
		return mImageList.size();
	}

	@Override
	public void startUpdate(View container) {
		super.startUpdate(container);
	}

	@Override
	public Fragment getItem(int position) {
		ImageFragment fr = new ImageFragment();
		fr.setImage(mImageList.get(position));
		return fr;
	}

	public FlashcardFragment getFragment(int position) {
		return (FlashcardFragment) mPageReferenceMap.get(position);
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
