package com.chinesepod.decks.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.chinesepod.decks.CPDecksSampleSentencesActivity;
import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksActivity;
import com.chinesepod.decks.CPDecksAccuracyActivity;
import com.chinesepod.decks.logic.CPDecksContent;
import com.chinesepod.decks.utility.CPDecksContentManager;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class CPDecksContentListAdapter extends BaseAdapter implements ExpandableListAdapter {
	private LayoutInflater mInflater;
	private int count;
	private ArrayList<Long> mContentIdList;
	final private Context mContext;
	private int mSelectedItemPosition;

	public CPDecksContentListAdapter(Context context, ArrayList<Long> contentIdList) {
		mContext = context;
		mContentIdList = contentIdList;
		count = mContentIdList.size();
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return count;
	}

	public CPDecksContent getItem(int position) {
		return CPDecksContentManager.getContent(mContentIdList.get(position));
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final CPDecksContent word = CPDecksContentManager.getContent(mContentIdList.get(position));
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.content_item, null);

			holder.txtFirstRow = (TextView) convertView.findViewById(R.id.firstRow);
			holder.txtSecondRow = (TextView) convertView.findViewById(R.id.secondRow);
			holder.txtThirdRow = (TextView) convertView.findViewById(R.id.thirdRow);
			holder.txtRightColumn = (TextView) convertView.findViewById(R.id.rightColumn);
			holder.imgContentIcon = (ImageView) convertView.findViewById(R.id.contentIcon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		convertView.setBackgroundResource(R.drawable.list_selector_light);
		holder.txtFirstRow.setText(word.getTarget());
		holder.txtSecondRow.setText(word.getTargetPhonetics());
		holder.txtThirdRow.setText(word.getSource());
		
		holder.txtRightColumn.setText(word.getPublishedDate());
		
		int resId = 0;
		switch(word.getTypeId()){
		case CPDecksContentManager.CONTENT_TYPE_PHRASE:
			resId = R.drawable.phrases_content;
			break;
		case CPDecksContentManager.CONTENT_TYPE_WORDLIST:
			resId = R.drawable.lists_content;
			break;
		case CPDecksContentManager.CONTENT_TYPE_WOTD:
		default:
			resId = R.drawable.word_of_the_day_content;
		}
		
		if( word.getImageUrl() != null && ! word.getImageUrl().isEmpty() ){
			UrlImageViewHelper.setUrlDrawable(holder.imgContentIcon, word.getImageUrl(), resId);
		}
		else {
			holder.imgContentIcon.setImageResource(resId);
		}
		
		return convertView;
	}

	class onWordPlayButtonClickListener implements OnClickListener {
		private CPDecksContent mWord;

		public onWordPlayButtonClickListener(CPDecksContent word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
			v.setBackgroundResource(R.drawable.sound_red);
			((CPDecksActivity) mContext).playSound(mWord, v, R.drawable.sound_red, R.drawable.sound_downloaded);
		}
	};

	class onSaveButtonClickListener implements OnClickListener {
		private CPDecksContent mWord;

		public onSaveButtonClickListener(CPDecksContent word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
			if( mContext instanceof CPDecksActivity ){
				((CPDecksActivity) mContext).saveTermToDeck(mWord);
			}
		}
	};

	class onSampleSentencesButtonClickListener implements OnClickListener {
		private CPDecksContent mWord;

		public onSampleSentencesButtonClickListener(CPDecksContent word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(mContext, CPDecksSampleSentencesActivity.class);
			i.putExtra(CPDecksSampleSentencesActivity.CONTENT_ID, mWord.getAppDecksContentId());
			mContext.startActivity(i);
		}
	};

	static class ViewHolder {
		TextView txtFirstRow;
		TextView txtSecondRow;
		TextView txtThirdRow;
		TextView txtRightColumn;
		ImageView imgContentIcon;
	}

	public void refreshUI(ArrayList<Long> contentIdList) {
		mContentIdList = contentIdList;
		count = contentIdList.size();
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

	class onSentenceRecordButtonClickListener implements OnClickListener {

		private CPDecksContent mTerm;
		private View mPlayRecordSentenceButton;

		public onSentenceRecordButtonClickListener(CPDecksContent term, View playRecordSentenceButton) {
			mTerm = term;
			mPlayRecordSentenceButton = playRecordSentenceButton;
		}

		@Override
		public void onClick(View v) {
			((CPDecksActivity) mContext).recordSound(mTerm, v, mPlayRecordSentenceButton);

		}
	}

	class onSentencePlayRecordedButtonClickListener implements OnClickListener {

		private CPDecksContent mTerm;

		public onSentencePlayRecordedButtonClickListener(CPDecksContent term) {
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
		final CPDecksContent term = (CPDecksContent) getGroup(groupPosition);

		if( CPDecksVocabularyManager.getDecksWithAppDecksContentId(term.getId()).size() > 0){
			btnSave.setText(R.string.saved);
			btnSave.setBackgroundResource(R.drawable.saved);
		}
		btnSave.setEnabled(true);
		btnSave.setClickable(true);
		btnSave.setOnClickListener(new onSaveButtonClickListener(term));
		
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
		
		Button btnSampleSentences = (Button) v.findViewById(R.id.sampleSentencesButton);
		btnSampleSentences.setOnClickListener(new onSampleSentencesButtonClickListener(term));
		
		if (term.getId() != 0) {
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

		v.findViewById(R.id.referenceLessonButton).setVisibility(View.GONE);
		
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (((CPDecksContent) getGroup(groupPosition)).isHeader()) {
			return 0;
		}
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
		return CPDecksContentManager.getContent(mContentIdList.get(groupPosition));
	}

	@Override
	public int getGroupCount() {
		return count;
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

	public void setSelectedItemPosition(int position) {
		mSelectedItemPosition = position;
	}

	public int getSelectedItemPosition() {
		return mSelectedItemPosition;
	}
}
