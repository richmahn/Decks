package com.chinesepod.decks.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinesepod.decks.CPDecksActivity;
import com.chinesepod.decks.CPDecksAccuracyActivity;
import com.chinesepod.decks.CPDecksSampleSentencesActivity;
import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;

public class CPodWordAdapter extends BaseAdapter implements ExpandableListAdapter {
	private LayoutInflater mInflater;
	private int count;
	private ArrayList<CPDecksVocabulary> wordList;
	final private Context mContext;

	public CPodWordAdapter(Context context, ArrayList<CPDecksVocabulary> wordList) {
		mContext = context;
		this.wordList = wordList;
		count = wordList.size();
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return count;
	}

	public Object getItem(int position) {
		return wordList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final CPDecksVocabulary word = wordList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_sentence_word, null);

			holder.headerRow = (RelativeLayout) convertView.findViewById(R.id.headerRow);
			holder.txtWordHeader = (TextView) convertView.findViewById(R.id.wordHeader);
			holder.wordRow = (LinearLayout) convertView.findViewById(R.id.wordRow);
			holder.txtWordOrigin = (TextView) convertView.findViewById(R.id.wordSource);
			holder.txtWordPinyin = (TextView) convertView.findViewById(R.id.wordPinyin);
			holder.txtWordTranslation = (TextView) convertView.findViewById(R.id.wordTranslation);
			holder.btnSaveAll = (Button) convertView.findViewById(R.id.saveAllButton);
			holder.btnDummy = (Button) convertView.findViewById(R.id.dummyButton);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.headerRow.setVisibility(View.GONE);
		holder.wordRow.setVisibility(View.VISIBLE);
		convertView.setBackgroundResource(R.drawable.list_selector_light);
		holder.txtWordOrigin.setText((position+1)+". "+word.getTarget());
		holder.txtWordPinyin.setText(word.getTargetPhonetics());
		holder.txtWordTranslation.setText(word.getSource());

		return convertView;
	}

	class onWordPlayButtonClickListener implements OnClickListener {
		private CPDecksVocabulary mWord;

		public onWordPlayButtonClickListener(CPDecksVocabulary word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
			v.setBackgroundResource(R.drawable.sound_red);
			((CPDecksActivity) mContext).playSound(mWord, v, R.drawable.sound_red, R.drawable.sound_downloaded);
		}
	};

	class onSaveButtonClickListener implements OnClickListener {
		private CPDecksVocabulary mWord;

		public onSaveButtonClickListener(CPDecksVocabulary word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
			if( mContext instanceof CPDecksActivity ){
				((CPDecksActivity) mContext).saveTermToDeck(mWord);
			}
		}
	};

	class onGlossaryButtonClickListener implements OnClickListener {
		private CPDecksVocabulary mWord;

		public onGlossaryButtonClickListener(CPDecksVocabulary word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(mContext, CPDecksSampleSentencesActivity.class);
			i.putExtra(CPDecksSampleSentencesActivity.CPOD_VOCABULARY, mWord);
			mContext.startActivity(i);
		}
	};

	static class ViewHolder {
		RelativeLayout headerRow;
		TextView txtWordHeader;
		LinearLayout wordRow;
		TextView txtWordOrigin;
		TextView txtWordPinyin;
		TextView txtWordTranslation;
		Button btnSaveAll; 
		Button btnDummy; // This dummy button prevents item of being clickable
	}

	public void refreshUI(ArrayList<CPDecksVocabulary> wordList) {
		this.wordList = wordList;
		count = wordList.size();
		this.notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	class onSentenceRecordButtonClickListener implements OnClickListener {

		private CPDecksVocabulary mTerm;
		private View mPlayRecordSentenceButton;

		public onSentenceRecordButtonClickListener(CPDecksVocabulary term, View playRecordSentenceButton) {
			mTerm = term;
			mPlayRecordSentenceButton = playRecordSentenceButton;
		}

		@Override
		public void onClick(View v) {
			((CPDecksActivity) mContext).recordSound(mTerm, v, mPlayRecordSentenceButton);

		}
	}

	class onSentencePlayRecordedButtonClickListener implements OnClickListener {

		private CPDecksVocabulary mTerm;

		public onSentencePlayRecordedButtonClickListener(CPDecksVocabulary term) {
			mTerm = term;
		}

		@Override
		public void onClick(View v) {
			((CPDecksActivity) mContext).playRecordedSound(mTerm, v);

		}
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.list_sentence_word_buttons, null);
		Button btnSave = (Button) v.findViewById(R.id.saveButton);
		Button btnWordPlay = (Button) v.findViewById(R.id.playWordButton);
		final CPDecksVocabulary term = (CPDecksVocabulary) getGroup(groupPosition);
		if (term.getCpodVocabId() < 1 ) {
			btnSave.setVisibility(View.GONE);
		} else {
			btnSave.setVisibility(View.VISIBLE);
			if( CPDecksVocabularyManager.getDecksWithVocabulary(term).size() > 0){
				btnSave.setText(R.string.saved);
				btnSave.setBackgroundResource(R.drawable.saved);
			}
			if (CPDecksVocabularyManager.getDecksWithoutVocabulary(term).size() < 1) {
				btnSave.setEnabled(false);
				btnSave.setClickable(false);
			} else {
				btnSave.setEnabled(true);
				btnSave.setClickable(true);
				btnSave.setOnClickListener(new onSaveButtonClickListener(term));
			}
		}
		if (!HttpConnectionHelper.isProperFileUrl(term.getTargetAudio().getAudioUrl())) {
			btnWordPlay.setVisibility(View.GONE);
		} else {
			btnWordPlay.setVisibility(View.VISIBLE);
			if (CPDecksUtility.isAnotationAudioFileDownloaded(term)) {
				btnWordPlay.setBackgroundResource(R.drawable.sound_downloaded);
			} else {
				btnWordPlay.setBackgroundResource(R.drawable.sound);
			}

			btnWordPlay.setOnClickListener(new onWordPlayButtonClickListener(term));
		}
		
		Button btnGlossary = (Button) v.findViewById(R.id.sampleSentencesButton);
		btnGlossary.setOnClickListener(new onGlossaryButtonClickListener(term));
		
		if (term.getCpodVocabId() != 0) {
			if (CPDecksUtility.isAudioRecorded(term)) {
				v.findViewById(R.id.playRecordSentenceButton).setBackgroundResource(R.drawable.play_record_green_dot);
			} else {
				v.findViewById(R.id.playRecordSentenceButton).setBackgroundResource(R.drawable.play_record_disabled);
			}
			v.findViewById(R.id.recordSentenceButton).setOnClickListener(new onSentenceRecordButtonClickListener(term, v.findViewById(R.id.playRecordSentenceButton)));
			v.findViewById(R.id.playRecordSentenceButton).setOnClickListener(new onSentencePlayRecordedButtonClickListener(term));
		} else {
			v.findViewById(R.id.recordSentenceButton).setVisibility(View.GONE);
			v.findViewById(R.id.playRecordSentenceButton).setVisibility(View.GONE);
		}
		
		if (CPDecksUtility.canRecognizeAudio(mContext)) {
			v.findViewById(R.id.accuracyButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, CPDecksAccuracyActivity.class);
					intent.putExtra(CPDecksAccuracyActivity.LINGUISTIC_OBJECT, term);
					((Activity) mContext).startActivity(intent);
				}
			});
		} else {
			v.findViewById(R.id.accuracyButton).setVisibility(View.GONE);
		}

		if (term.getV3id() != null && ! term.getV3id().isEmpty() ) {
			v.findViewById(R.id.referenceLessonButton).setVisibility(View.VISIBLE);
			v.findViewById(R.id.referenceLessonButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
		} else {
			v.findViewById(R.id.referenceLessonButton).setVisibility(View.GONE);
		}
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (((CPDecksVocabulary) getGroup(groupPosition)).isHeader()) {
			return 0;
		}
		return 1;
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return wordList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return count;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		return getView(groupPosition, convertView, parent);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		// TODO Auto-generated method stub

	}
}
