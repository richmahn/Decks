package com.chinesepod.decks.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinesepod.decks.fragment.FlashcardFragment;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;

import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;

public class CPDecksFlashcardsPagerAdapter extends CPDecksFragmentPagerAdapter {

	private CPDecksDeck mDeck;
	List<Fragment> pages = null;
	private Map<Integer, Fragment> mPageReferenceMap;
	private ArrayList<CPDecksVocabulary> mVocabularyList;

	public CPDecksFlashcardsPagerAdapter(FragmentManager fm, CPDecksDeck deck) {
		super(fm);
		mDeck = deck;
		mVocabularyList = mDeck.getVocabulary();
		mPageReferenceMap = new HashMap<Integer, Fragment>();
		Collections.shuffle(mVocabularyList);
	}

	@Override
	public int getCount() {
		return mVocabularyList.size();
	}

	@Override
	public void startUpdate(View container) {
		super.startUpdate(container);
	}

	@Override
	public Fragment getItem(int position) {

		FlashcardFragment fr = new FlashcardFragment();
		fr.setWord(mVocabularyList.get(position));
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
