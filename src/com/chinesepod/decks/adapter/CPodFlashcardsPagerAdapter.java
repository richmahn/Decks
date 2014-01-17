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
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;

public class CPodFlashcardsPagerAdapter extends CPodFragmentPagerAdapter {

	private CPDecksDeck mDeck;
	List<Fragment> pages = null;
	private Map<Integer, Fragment> mPageReferenceMap;
	private ArrayList<CPDecksVocabulary> mWordsList;

	public CPodFlashcardsPagerAdapter(FragmentManager fm, CPDecksDeck deck) {
		super(fm);
		mDeck = deck;
		mWordsList = mDeck.getVocabulary();
		mPageReferenceMap = new HashMap<Integer, Fragment>();
		Collections.shuffle(mWordsList);
	}

	@Override
	public int getCount() {
		return mWordsList.size();
	}

	@Override
	public void startUpdate(View container) {
		super.startUpdate(container);
	}

	@Override
	public Fragment getItem(int position) {

		FlashcardFragment fr = new FlashcardFragment();
		fr.setWord(mWordsList.get(position));
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
