package com.chinesepod.decks.fragment;

import java.io.File;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.chinesepod.decks.CPDecksActivity;
import com.chinesepod.decks.CPDecksContentCardsActivity;
import com.chinesepod.decks.CPDecksSampleSentencesActivity;
import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksCanPlaySound;
import com.chinesepod.decks.logic.CPDecksContent;
import com.chinesepod.decks.logic.CPDecksContentCategory;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class ContentCardFragment extends Fragment {

	private static final String CONTENT = "content";

	CPDecksContent mContent = null;

	private TextView mSourceText;
	private TextView mTargetText;
	private TextView mTargetPhoneticsText;
	private Button mSampleSentencesButton;
	
	private CPDecksContentCategory mContentCategory;

	private Button mSaveButton;

	private ImageView mImage;

	public void setContent(CPDecksContent content) {
		mContent = content;
	}

	public void setContentCategory(CPDecksContentCategory contentCategory) {
		mContentCategory = contentCategory;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mContent == null && savedInstanceState != null ) {
			mContent = (CPDecksContent) savedInstanceState.getSerializable(CONTENT);
		}

		if (mContent == null) {
			getActivity().finish();
			return null;
		}

		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.content_card, null);
		
		mSourceText = (TextView) v.findViewById(R.id.wordSource);
		mTargetPhoneticsText = (TextView) v.findViewById(R.id.wordTargetPhonetics);
		mTargetText = (TextView) v.findViewById(R.id.wordTarget);
		
		String source = mContent.getSource();
		String target = mContent.getTarget();
		String targetPhonetics = mContent.getTargetPhonetics();

		mSourceText.setText(source);
		mTargetText.setText(target);
		mTargetPhoneticsText.setText(targetPhonetics);

		mSampleSentencesButton = (Button) v.findViewById(R.id.sampleSentencesButton);
		mSaveButton = (Button) v.findViewById(R.id.saveButton);

		mSaveButton.setOnClickListener(new onSaveButtonClickListener());
		
		mImage = (ImageView)v.findViewById(R.id.wordImage);
		if (HttpConnectionHelper.isProperUrl(mContent.getImageUrl())) {
			mImage.setVisibility(View.VISIBLE);
			UrlImageViewHelper.setUrlDrawable(mImage, mContent.getImageUrl(), R.drawable.default_image);
		} else {
			mImage.setVisibility(View.INVISIBLE);
		}
		
		Button playButton = (Button) v.findViewById(R.id.wordPlayButton);

		if (HttpConnectionHelper.isProperUrl(mContent.getTargetAudio().getAudioUrl())) {
			playButton.setVisibility(View.VISIBLE);
			
			if( new File(mContent.getTargetAudio().getAudioFile()).exists() ){
				playButton.setBackgroundResource(R.drawable.sound_downloaded);
			}
			
			playButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((CPDecksCanPlaySound) getActivity()).playSound(mContent.getTargetAudio(), v, R.drawable.sound_red, R.drawable.sound_downloaded);
				}
			});
		} else {
			playButton.setVisibility(View.INVISIBLE);
		}

		mSampleSentencesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), CPDecksSampleSentencesActivity.class);
				i.putExtra(CPDecksSampleSentencesActivity.CONTENT_ID, mContent.getAppDecksContentId());
				getActivity().startActivity(i);
			}
		});

		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(CONTENT, mContent);
		super.onSaveInstanceState(outState);
	}

	class onSaveButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if( getActivity() instanceof CPDecksActivity ){
				((CPDecksActivity) getActivity()).saveTermToDeck(mContent);
			}
		}
	};

}
