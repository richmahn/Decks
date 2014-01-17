package com.chinesepod.decks.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.chinesepod.decks.CPDecksActivity;
import com.chinesepod.decks.CPDecksAccuracyActivity;
import com.chinesepod.decks.CPDecksSampleSentencesActivity;
import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksSentence;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;

public class CPDecksSampleSentenceAdapter extends BaseExpandableListAdapter {

	private LayoutInflater mInflater;
	private ArrayList<CPDecksSentence> mSentences;
	private Context mContext;
	private String mTerm;

	public CPDecksSampleSentenceAdapter(Context context, ArrayList<CPDecksSentence> sentences, String term) {
		this(context, sentences);
		mTerm = term;
	}

	public CPDecksSampleSentenceAdapter(Context context, ArrayList<CPDecksSentence> sentences) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mSentences = sentences;
	}

	public int getCount() {
		return mSentences.size();
	}

	public Object getItem(int position) {
		return mSentences.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_glossary_sentence, null);

			holder = new ViewHolder();
			holder.txtSentenceOrigin = (TextView) convertView.findViewById(R.id.sentenceSource);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final CPDecksSentence sentence = mSentences.get(position);
		// holder.txtSentenceOrigin.setText(sentence.getTarget());
		if (mTerm != null) {
			holder.txtSentenceOrigin.setText(Html.fromHtml(sentence.getTarget().replaceAll(mTerm, "<font color=\"red\">" + mTerm + "</font>")));
		} else {
			holder.txtSentenceOrigin.setText(sentence.getTarget());
		}
		return convertView;
	}

	class onSentencePlayButtonClickListener implements OnClickListener {

		private CPDecksSentence mSentence;

		public onSentencePlayButtonClickListener(CPDecksSentence sentence) {
			mSentence = sentence;
		}

		@Override
		public void onClick(View v) {
			((CPDecksActivity) mContext).playSound(mSentence.getTargetAudio(), v, R.drawable.sound_red, R.drawable.sound_downloaded);

		}
	}

	class onSentenceDrilldownButtonClickListener implements OnClickListener {

		private int position;

		public onSentenceDrilldownButtonClickListener(int pos) {
			position = pos;
		}

		@Override
		public void onClick(View v) {
			if (mContext instanceof CPDecksSampleSentencesActivity) {
				((CPDecksSampleSentencesActivity) mContext).drillDownSentence(position);
			}
		}
	}

	class onTranslationButtonClickListener implements OnClickListener {

		private CPDecksSentence mSentence;
		private TextView mTranslationTV;
		private TextView mPhoneticsTV;

		public onTranslationButtonClickListener(CPDecksSentence sentence, TextView textView, TextView textView2) {
			mSentence = sentence;
			mTranslationTV = textView;
			mPhoneticsTV = textView2;
		}

		@Override
		public void onClick(View v) {
			if (mSentence.isTranslationShown()) {
				mTranslationTV.setVisibility(View.GONE);
				mPhoneticsTV.setVisibility(View.GONE);
				mSentence.setTranslationShown(false);
			} else {
				mTranslationTV.setVisibility(View.VISIBLE);
				mTranslationTV.setText(mSentence.getSource());
				if (!mSentence.getTargetPhonetics().isEmpty()) {
					mPhoneticsTV.setVisibility(View.VISIBLE);
					mPhoneticsTV.setText(mSentence.getTargetPhonetics());
				}
				mSentence.setTranslationShown(true);
			}

		}
	}

	static class ViewHolder {
		TextView txtSentenceOrigin;

	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mSentences.get(groupPosition);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.list_dialog_element_buttons, null);
		final CPDecksSentence sentence = mSentences.get(groupPosition);
		TextView translationTV = (TextView) v.findViewById(R.id.translation);
		TextView phoneticsTV = (TextView) v.findViewById(R.id.phonetics);
		Button btnPlaySentenceButton = (Button) v.findViewById(R.id.playSentenceButton);
		if (sentence.isTranslationShown()) {
			if (!sentence.getTargetPhonetics().isEmpty()) {
				phoneticsTV.setVisibility(View.VISIBLE);
				phoneticsTV.setText(sentence.getTargetPhonetics());
			}
			translationTV.setVisibility(View.VISIBLE);
			translationTV.setText(sentence.getSource());
		} else {
			translationTV.setVisibility(View.GONE);
		}

		if (!HttpConnectionHelper.isProperFileUrl(sentence.getTargetAudio().getAudioUrl())) {
			btnPlaySentenceButton.setVisibility(View.GONE);
		} else {
			btnPlaySentenceButton.setVisibility(View.VISIBLE);
			btnPlaySentenceButton.setOnClickListener(new onSentencePlayButtonClickListener(sentence));
			if (CPDecksUtility.isAnotationAudioFileDownloaded(sentence)) {
				btnPlaySentenceButton.setBackgroundResource(R.drawable.sound_downloaded);
			} else {
				btnPlaySentenceButton.setBackgroundResource(R.drawable.sound);
			}
		}

		btnPlaySentenceButton.setOnClickListener(new onSentencePlayButtonClickListener(sentence));
		v.findViewById(R.id.drilldownSentenceButton).setOnClickListener(new onSentenceDrilldownButtonClickListener(groupPosition));
		v.findViewById(R.id.toggleTranslationButton).setOnClickListener(new onTranslationButtonClickListener(sentence, translationTV, phoneticsTV));

		if (sentence.getCpodVocabId() != 0) {
			if (CPDecksUtility.isAudioRecorded(sentence)) {
				v.findViewById(R.id.playRecordSentenceButton).setBackgroundResource(R.drawable.play_record_green_dot);
			} else {
				v.findViewById(R.id.playRecordSentenceButton).setBackgroundResource(R.drawable.play_record_disabled);
			}
			v.findViewById(R.id.recordSentenceButton).setOnClickListener(new onSentenceRecordButtonClickListener(sentence, v.findViewById(R.id.playRecordSentenceButton)));
			v.findViewById(R.id.playRecordSentenceButton).setOnClickListener(new onSentencePlayRecordedButtonClickListener(sentence));
		} else {
			v.findViewById(R.id.recordSentenceButton).setVisibility(View.GONE);
			v.findViewById(R.id.playRecordSentenceButton).setVisibility(View.GONE);
		}

		if (CPDecksUtility.canRecognizeAudio(mContext)) {
			v.findViewById(R.id.accuracyButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, CPDecksAccuracyActivity.class);
					intent.putExtra(CPDecksAccuracyActivity.LINGUISTIC_OBJECT, sentence);
					((Activity) mContext).startActivity(intent);
				}
			});
		} else {
			v.findViewById(R.id.accuracyButton).setVisibility(View.GONE);
		}

		if (sentence.getV3id() != null && ! sentence.getV3id().isEmpty()) {
			v.findViewById(R.id.referenceLessonButton).setVisibility(View.VISIBLE);
			v.findViewById(R.id.referenceLessonButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://chinesepod.com/lesson-redirect?v3id="+sentence.getV3id()));
					mContext.startActivity(i);
				}

			});
		} else {
			v.findViewById(R.id.referenceLessonButton).setVisibility(View.GONE);
		}

		return v;
	}

	class onSentenceRecordButtonClickListener implements OnClickListener {

		private CPDecksSentence mSentence;
		private View listenRecordedButton;

		public onSentenceRecordButtonClickListener(CPDecksSentence sentence, View listenRecordedButton) {
			mSentence = sentence;
			this.listenRecordedButton = listenRecordedButton;
		}

		@Override
		public void onClick(View recordButton) {
			((CPDecksActivity) mContext).recordSound(mSentence, recordButton, listenRecordedButton);

		}
	}

	class onSentencePlayRecordedButtonClickListener implements OnClickListener {

		private CPDecksSentence mSentence;

		public onSentencePlayRecordedButtonClickListener(CPDecksSentence sentence) {
			mSentence = sentence;
		}

		@Override
		public void onClick(View v) {
			((CPDecksActivity) mContext).playRecordedSound(mSentence, v);

		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mSentences.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mSentences.size();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		return getView(groupPosition, convertView, parent);
	}

	public void refreshUI(ArrayList<CPDecksSentence> sentences) {
		this.mSentences = sentences;
		this.notifyDataSetChanged();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
