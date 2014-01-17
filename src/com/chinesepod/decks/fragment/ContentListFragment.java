package com.chinesepod.decks.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.chinesepod.decks.R;
import com.chinesepod.decks.utility.CPDecksContentManager;
import com.chinesepod.decks.adapter.CPDecksContentListAdapter;
import com.chinesepod.decks.logic.CPDecksContentCategory;
import com.chinesepod.decks.logic.CPodExpandableObject;

public class ContentListFragment extends ListFragment {
//	private CPDecksContentListAdapter adapter;
//
//	protected static final String IS_SENTENCE_OPEN = "is_sentence_open";
//	protected static final String SELECTED_SENTENCE = "selected_sentence";
//	protected boolean isSentenceOpen;
//	protected int mSelectedSentence;
//
//	private CPDecksContentCategory mContentCategory;
//
//	public ContentListFragment() {
//		super();
//	}
//
//	public void onStart() {
//		super.onStart();
//		updateView();
//	}
//
//	public void updateData() {
//		if (getActivity() != null && mContentCategory != null) {
//			if (! mContentCategory.getContentIdList().isEmpty() ) {
//				adapter = new CPDecksContentListAdapter(getActivity(), mContentCategory.getContentIdList());
//				setListAdapter(adapter);
//			}
//			updateView();
//		}
//	}
//
//	public void updateView() {
//		if (getView() == null) {
//			return;
//		}
//		onViewCreated(null, null);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		mContentCategory = CPDecksContentManager.getCurrentContentCategory();
//
//		if( mContentCategory == null ){
//			getActivity().finish();
//		}
//		
//		updateData();
//		getActivity().setTitle(mContentCategory.getCategoryName());
//		super.onCreateView(inflater, container, savedInstanceState);
//		return inflater.inflate(R.layout.content_list, null);
//	}
//
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id) {
//		// Unclickable
//	}
}
