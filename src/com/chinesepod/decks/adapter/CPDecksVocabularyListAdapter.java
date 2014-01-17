package com.chinesepod.decks.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.CPDecksListActivity;
import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;

public class CPDecksVocabularyListAdapter extends BaseAdapter implements ExpandableListAdapter {
	private LayoutInflater mInflater;
	private ArrayList<CPDecksVocabulary> wordList;
	final private Context mContext;
	Typeface mFont;
	private ListView mListView;
	private CPDecksDeck mDeck;

	public CPDecksVocabularyListAdapter(Context context, ArrayList<CPDecksVocabulary> wordList, CPDecksDeck deck, ListView lv) {
		mContext = context;
		this.wordList = wordList;
		mInflater = LayoutInflater.from(context);
		mListView = lv;
		mDeck = deck;
	}

	public int getCount() {
		return wordList.size();
	}

	public Object getItem(int position) {
		return wordList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final CPDecksVocabulary word = wordList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_vocabulary_word, null);

			holder.txtWordOrigin = (TextView) convertView.findViewById(R.id.wordSource);
			holder.txtWordPinyin = (TextView) convertView.findViewById(R.id.wordPinyin);
			holder.txtWordTranslation = (TextView) convertView.findViewById(R.id.wordTranslation);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtWordOrigin.setVisibility(View.VISIBLE);
		holder.txtWordPinyin.setVisibility(View.VISIBLE);
		holder.txtWordTranslation.setVisibility(View.VISIBLE);

		holder.txtWordOrigin.setText((position+1)+". "+word.getSource());

		holder.txtWordPinyin.setText(word.getTargetPhonetics());

		holder.txtWordPinyin.setTypeface(mFont);

		holder.txtWordTranslation.setText(word.getTarget());

		return convertView;
	}

	static class ViewHolder {
		TextView txtWordOrigin;
		TextView txtWordPinyin;
		TextView txtWordTranslation;

	}

	class onWordPlayButtonClickListener implements OnClickListener {
		private CPDecksVocabulary mWord;

		public onWordPlayButtonClickListener(CPDecksVocabulary word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
//			((CPDecksListActivity) mContext).playSound(mWord.getAudio(), v);
		}
	};

	public void refreshUI(ArrayList<CPDecksVocabulary> wordList) {
		this.wordList = wordList;
		this.notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.list_vocabulary_word_buttons, null);
		final CPDecksVocabulary word = wordList.get(groupPosition);

		final Button removeTermButton = (Button)v.findViewById(R.id.removeTermButton);
		if( mDeck.getVocabulary().contains(word) ){
			removeTermButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					new RemoveDeckTermTask(mContext, word, mDeck, removeTermButton).execute();
				}
			});
		}
		else {
			removeTermButton.setText(R.string.removed_term);
			removeTermButton.setEnabled(false);
			removeTermButton.setClickable(false);
		}
		
//		if( CPDecksApplication.getDecksWithoutVocabulary(word).size() > 0 ){
//			v.findViewById(R.id.moveTermButton).setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					try {
//						final ArrayList<Deck> decks = CPDecksApplication.getDecksWithoutVocabulary(word);
//						if( decks.size() < 1 ){
//							Toast.makeText(mContext, "All decks already contain this vocabulary", Toast.LENGTH_SHORT).show();
//							return;
//						}
//						ArrayAdapter<Deck> myAdapter = new ArrayAdapter<Deck>(mContext, android.R.layout.simple_list_item_1, decks);
//						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//						builder.setTitle(R.string.choose_deck);
//						builder.setAdapter(myAdapter, new android.content.DialogInterface.OnClickListener() {
//							public void onClick(final DialogInterface dialog, final int position) {
//								new MoveDeckTermTask(mContext, wordList, groupPosition, mDeck, decks.get(position), CPDecksVocabularyListAdapter.this, mListView).execute();
//							}
//						});
//						builder.create().show();
//
//					} catch (Exception x) {
//						Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
//					}
//				}
//			});
//			if( mDeck.getId() == 0 ){
//				((Button)v.findViewById(R.id.moveTermButton)).setText(R.string.copy_term);
//			}
//		}
//		else {
//			v.findViewById(R.id.moveTermButton).setVisibility(View.GONE);
//		}

		Button btnWordPlay = (Button) v.findViewById(R.id.playWordButton);
		final CPDecksVocabulary vocabulary = (CPDecksVocabulary) getGroup(groupPosition);

//		if (!HttpConnectionHelper.isProperFileUrl(term.getAudio().getAudioUrl())) {
//			btnWordPlay.setVisibility(View.GONE);
//		} else {
//			btnWordPlay.setVisibility(View.VISIBLE);
//			if (CPDecksUtility.isAnotationAudioFileDownloaded(term)) {
//				btnWordPlay.setBackgroundResource(R.drawable.sound_downloaded);
//			} else {
//				btnWordPlay.setBackgroundResource(R.drawable.sound);
//			}
//
//			btnWordPlay.setOnClickListener(new onWordPlayButtonClickListener(term));
//		}
		
//		Button btnGlossary = (Button) v.findViewById(R.id.glossaryButton);
//		btnGlossary.setOnClickListener(new onGlossaryButtonClickListener(term));
		
//		if (term.getId() != 0) {
//			if (CPDecksUtility.isAudioRecorded(term)) {
//				v.findViewById(R.id.playRecordSentenceButton).setBackgroundResource(R.drawable.play_record_green_dot);
//			} else {
//				v.findViewById(R.id.playRecordSentenceButton).setBackgroundResource(R.drawable.play_record);
//			}
//			v.findViewById(R.id.recordSentenceButton).setOnClickListener(new onSentenceRecordButtonClickListener(term, v.findViewById(R.id.playRecordSentenceButton)));
//			v.findViewById(R.id.playRecordSentenceButton).setOnClickListener(new onSentencePlayRecordedButtonClickListener(term));
//		} else {
//			v.findViewById(R.id.recordSentenceButton).setVisibility(View.GONE);
//			v.findViewById(R.id.playRecordSentenceButton).setVisibility(View.GONE);
//		}
//		
//		if (CPDecksUtility.canRecognizeAudio(mContext)) {
//			v.findViewById(R.id.accuracyButton).setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(mContext, CPDecksAccuracyActivity.class);
//					intent.putExtra(CPDecksAccuracyActivity.LINGUISTIC_OBJECT, vocabulary);
//					((Activity) mContext).startActivity(intent);
//				}
//			});
//		} else {
//			v.findViewById(R.id.accuracyButton).setVisibility(View.GONE);
//		}
//
//		if (term.getV3id() != null && ! vocabulary.getV3id().isEmpty() && (CPDecksApplication.getCurrentLesson() == null || CPDecksApplication.getCurrentLesson().getV3id() != vocabulary.getV3id())) {
//			v.findViewById(R.id.lessonButton).setVisibility(View.VISIBLE);
//			v.findViewById(R.id.lessonButton).setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
////					CPDecksApplication.setCurrentLesson(CPDecksApplication.getLesson(term.getV3id()));
////					Intent i = new Intent(mContext, CPodLessonActivity.class);
////					mContext.startActivity(i);
//				}
//			});
//		} else {
//			v.findViewById(R.id.lessonButton).setVisibility(View.GONE);
//		}

		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		return 0;
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return wordList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return wordList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		return getView(groupPosition, convertView, parent);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {

	}

	@Override
	public void onGroupExpanded(int groupPosition) {

	}

	class onGlossaryButtonClickListener implements OnClickListener {
		private CPDecksVocabulary mWord;

		public onGlossaryButtonClickListener(CPDecksVocabulary word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
//			Intent i = new Intent(mContext, CPodGlossarySearchActivity.class);
//			i.putExtra("query", mWord.getPreferredSource());
//			mContext.startActivity(i);
		}
	};

	class onSentenceRecordButtonClickListener implements OnClickListener {

		private CPDecksVocabulary mTerm;
		private View mPlayRecordSentenceButton;

		public onSentenceRecordButtonClickListener(CPDecksVocabulary vocabulary, View playRecordSentenceButton) {
			mTerm = vocabulary;
			mPlayRecordSentenceButton = playRecordSentenceButton;
		}

		@Override
		public void onClick(View v) {
//			((CPDecksListActivity)mContext).recordSound(mTerm, v, mPlayRecordSentenceButton);
		}
	}

	class onSentencePlayRecordedButtonClickListener implements OnClickListener {

		private CPDecksVocabulary mTerm;

		public onSentencePlayRecordedButtonClickListener(CPDecksVocabulary vocabulary) {
			mTerm = vocabulary;
		}

		@Override
		public void onClick(View v) {
//			((CPDecksListActivity)mContext).playRecordedSound(mTerm, v);
		}
	}
}
