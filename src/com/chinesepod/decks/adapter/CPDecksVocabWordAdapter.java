package com.chinesepod.decks.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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

import com.chinesepod.decks.CPDecksActivity;
import com.chinesepod.decks.CPDecksListActivity;
import com.chinesepod.decks.CPDecksSampleSentencesActivity;
import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksAccuracyActivity;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;

public class CPDecksVocabWordAdapter extends BaseAdapter implements ExpandableListAdapter {
	private LayoutInflater mInflater;
	private ArrayList<CPDecksVocabulary> wordList;
	final private Context mContext;
	Typeface mFont;
	private CPDecksDeck mDeck;

	public CPDecksVocabWordAdapter(Context context, ArrayList<CPDecksVocabulary> wordList, CPDecksDeck deck, ListView lv) {
		mContext = context;
		this.wordList = wordList;
		mInflater = LayoutInflater.from(context);
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

			holder.txtWordTarget = (TextView) convertView.findViewById(R.id.wordTarget);
			holder.txtWordTargetPhonetics = (TextView) convertView.findViewById(R.id.wordTargetPhonetics);
			holder.txtWordSource = (TextView) convertView.findViewById(R.id.wordSource);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtWordTarget.setVisibility(View.VISIBLE);
		holder.txtWordTargetPhonetics.setVisibility(View.VISIBLE);
		holder.txtWordSource.setVisibility(View.VISIBLE);

		holder.txtWordTarget.setText((position+1)+". "+word.getTarget());

		holder.txtWordTargetPhonetics.setText(word.getTargetPhonetics());

		holder.txtWordTargetPhonetics.setTypeface(mFont);

		holder.txtWordSource.setText(word.getSource());

		return convertView;
	}

	static class ViewHolder {
		TextView txtWordTarget;
		TextView txtWordTargetPhonetics;
		TextView txtWordSource;

	}

	class onWordPlayButtonClickListener implements OnClickListener {
		private CPDecksVocabulary mWord;

		public onWordPlayButtonClickListener(CPDecksVocabulary word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
			((CPDecksListActivity) mContext).playSound(mWord.getTargetAudio(), v, R.drawable.sound_red, R.drawable.sound_downloaded);
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
		removeTermButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean result = CPDecksVocabularyManager.removeVocabularyFromDeck(word, mDeck);
				if( result ){
					Toast.makeText(mContext, "Successfully removed from "+mDeck.getTitle(), Toast.LENGTH_SHORT).show();
					if( mContext instanceof CPDecksActivity ){
						((CPDecksActivity)mContext).refreshPage();
					}
				}
				else {
					Toast.makeText(mContext, "Unable to remove. Please try again.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		v.findViewById(R.id.copyTermButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mContext instanceof CPDecksActivity ){
					((CPDecksActivity)mContext).copyTermToAnotherDeck(word);
				}
			}
		});
		v.findViewById(R.id.moveTermButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mContext instanceof CPDecksActivity ){
					((CPDecksActivity)mContext).moveTermToAnotherDeck(word, mDeck);
				}
			}
		});
		if( mDeck.getId() == 0 ){
			((Button)v.findViewById(R.id.copyTermButton)).setText(R.string.copy_term);
		}

		Button btnWordPlay = (Button) v.findViewById(R.id.playWordButton);
		final CPDecksVocabulary term = (CPDecksVocabulary) getGroup(groupPosition);

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

		if (term.getV3id() != null && ! term.getV3id().isEmpty() ) {
			v.findViewById(R.id.referenceLessonButton).setVisibility(View.VISIBLE);
			v.findViewById(R.id.referenceLessonButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://chinesepod.com/lesson-redirect?v3id="+term.getV3id()));
					mContext.startActivity(i);
				}
			});
		} else {
			v.findViewById(R.id.referenceLessonButton).setVisibility(View.GONE);
		}

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

	class onSampleSentencesButtonClickListener implements OnClickListener {
		private CPDecksVocabulary mWord;

		public onSampleSentencesButtonClickListener(CPDecksVocabulary word) {
			mWord = word;
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(mContext, CPDecksSampleSentencesActivity.class);
			i.putExtra(CPDecksSampleSentencesActivity.VOCABULARY_ID, mWord.getId());
			mContext.startActivity(i);
		}
	};

	class onSentenceRecordButtonClickListener implements OnClickListener {

		private CPDecksVocabulary mTerm;
		private View mPlayRecordSentenceButton;

		public onSentenceRecordButtonClickListener(CPDecksVocabulary term, View playRecordSentenceButton) {
			mTerm = term;
			mPlayRecordSentenceButton = playRecordSentenceButton;
		}

		@Override
		public void onClick(View v) {
			((CPDecksListActivity)mContext).recordSound(mTerm, v, mPlayRecordSentenceButton);

		}
	}

	class onSentencePlayRecordedButtonClickListener implements OnClickListener {

		private CPDecksVocabulary mTerm;

		public onSentencePlayRecordedButtonClickListener(CPDecksVocabulary term) {
			mTerm = term;
		}

		@Override
		public void onClick(View v) {
			((CPDecksListActivity)mContext).playRecordedSound(mTerm, v);
		}
	}
}
