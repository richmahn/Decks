/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity for a user to see how accurate their voice is with a native speaker.
 */
package com.chinesepod.decks;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;

public class CPDecksAccuracyActivity extends CPDecksActivity {

	public static final int RESULT_SPEECH = 3;
	public static final String LINGUISTIC_OBJECT = "linguistic_object";
	public CPDecksVocabulary mVocabulary;
	public SpeechRecognizer sr;
	public static String TAG = "CPodComparerecordingsActivity";
	public TextView mYourRecordedText;
	public Button mRecordButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		if (CPDecksUtility.isTv()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else if (CPDecksUtility.isTablet(this)) {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				if (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				}
			} else {
				if (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				}
			}
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}
		
		mVocabulary = (CPDecksVocabulary)getIntent().getSerializableExtra(LINGUISTIC_OBJECT);

		
		final Button btnPlaySentenceButton = (Button)findViewById(R.id.playButton);
		final TextView originalSentence = (TextView)findViewById(R.id.originalSentence);
		final TextView originalSentenceLabel = (TextView)findViewById(R.id.originalSentenceLabel);
		final TextView originalSentenceEdit = (TextView)findViewById(R.id.originalSentenceEdit);
		final EditText originalSentenceEditor = (EditText)findViewById(R.id.originalSentenceEditor);

		if( mVocabulary != null ){
			if( mVocabulary.getType() == CPDecksVocabulary.TYPE_WORD ){
				originalSentenceLabel.setText(R.string.compare_recordings_original_word);
			}

			originalSentence.setText(mVocabulary.toString());
			originalSentenceEditor.setText(mVocabulary.getTarget());
			
			if (!HttpConnectionHelper.isProperFileUrl(mVocabulary.getTargetAudio().getAudioUrl())) {
				btnPlaySentenceButton.setVisibility(View.GONE);
			} else {
				btnPlaySentenceButton.setVisibility(View.VISIBLE);
				btnPlaySentenceButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						playSound(mVocabulary.getTargetAudio(), v, R.drawable.sound_red, R.drawable.sound_downloaded);
					}
				});
				if (CPDecksUtility.isAnotationAudioFileDownloaded(mVocabulary)) {
					btnPlaySentenceButton.setBackgroundResource(R.drawable.sound_downloaded);
				} else {
					btnPlaySentenceButton.setBackgroundResource(R.drawable.sound);
				}
			}
			originalSentenceEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					originalSentenceLabel.setText("Text to compare:");
					originalSentence.setVisibility(View.GONE);
					originalSentenceEdit.setVisibility(View.GONE);
					originalSentenceEditor.setVisibility(View.VISIBLE);
					btnPlaySentenceButton.setVisibility(View.INVISIBLE);
				}
			});
		}
		else {
			originalSentenceLabel.setText("Text to compare:");
			originalSentence.setVisibility(View.GONE);
			originalSentenceEdit.setVisibility(View.GONE);
			originalSentenceEditor.setVisibility(View.VISIBLE);
			originalSentenceEditor.setText("你好�?");
			btnPlaySentenceButton.setVisibility(View.INVISIBLE);
		}

		mRecordButton = (Button)findViewById(R.id.compareButton);
		mRecordButton.setOnClickListener(new OnRecordClickListener());

	    sr = SpeechRecognizer.createSpeechRecognizer(this);       
	    sr.setRecognitionListener(new listener());        

		mYourRecordedText = (TextView)findViewById(R.id.yourRecording);
	
	}

	class listener implements RecognitionListener {
	    public void onReadyForSpeech(Bundle params)
	    {
	             Log.d(TAG, "onReadyForSpeech");
	    }
	    public void onBeginningOfSpeech()
	    {
	             Log.d(TAG, "onBeginningOfSpeech");
	    }
	    public void onRmsChanged(float rmsdB)
	    {
	             Log.d(TAG, "onRmsChanged");
	    }
	    public void onBufferReceived(byte[] buffer)
	    {
	        Log.d(TAG, "onBufferReceived");
	    }
	    public void onEndOfSpeech()
	    {
	             Log.d(TAG, "onEndofSpeech");
	    }
	    public void onError(int error)
	    {
	    	switch(error){
	    	case SpeechRecognizer.ERROR_NETWORK:
	    	case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
				Toast.makeText(CPDecksAccuracyActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
				break;
	    	case SpeechRecognizer.ERROR_NO_MATCH:
				Toast.makeText(CPDecksAccuracyActivity.this, "No voice was picked up. Please speak louder and make sure your microphone is working.", Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(CPDecksAccuracyActivity.this, "An error occured. Please try again.", Toast.LENGTH_SHORT).show();
	    	}
			mRecordButton.setEnabled(true);
	    }
	    public void onResults(Bundle results)                   
	    {
			mRecordButton.setEnabled(true);

			ArrayList<String> text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			String recordedText = text.get(0);
			
			EditText editor = (EditText)findViewById(R.id.originalSentenceEditor);
			String textToCompare = editor.getText().toString();

			String origString = textToCompare;
			String origStringForAccuracy = origString.replaceAll("[^\\p{L}\\p{Space}]", "").replaceAll("[他她它]", "他").toLowerCase();

			String recordedTextForAccuracy = recordedText.replaceAll("[^\\p{L}\\p{Space}]", "").replaceAll("["+CPDecksUtility.CHINESE_PRONOUNS+"]", "他").toLowerCase();
			int accuracy = CPDecksUtility.calculateStringsAccuracy(origStringForAccuracy, recordedTextForAccuracy);
			
			recordedText = CPDecksUtility.makeStringsSimilarForComparison(origString, recordedText);
			mYourRecordedText.setText(recordedText);

			((TextView) findViewById(R.id.accuracyRate)).setText(getString(R.string.compare_recordings_accuracy) + " " + accuracy + "%");
	    }
	    public void onPartialResults(Bundle partialResults)
	    {
	             Log.d(TAG, "onPartialResults");
	    }
	    public void onEvent(int eventType, Bundle params)
	    {
	             Log.d(TAG, "onEvent " + eventType);
	    }
	}

	class OnRecordClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication().getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh");
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "zh");

			EditText editor = (EditText)findViewById(R.id.originalSentenceEditor);
			String textToCompare = editor.getText().toString();
			
			if( textToCompare == null || textToCompare.isEmpty() ){
				return;
			}
			
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, textToCompare);
			intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true);

			mRecordButton.setEnabled(false);
			new AnimateRecordButtonTask().execute();
			
			try {
	             sr.startListening(intent);
			} catch (ActivityNotFoundException a) {
				Toast t = Toast.makeText(CPDecksAccuracyActivity.this, "Oops! Your device doesn't support Speech to Text", Toast.LENGTH_SHORT);
				t.show();
				mRecordButton.setEnabled(true);
			}
		}
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_accuracy;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	class AnimateRecordButtonTask extends AsyncTask<Void, Integer, Void> {
		public String mProgressString;

		@Override
		protected void onPreExecute() {
			mRecordButton.setText(R.string.recording);
			mRecordButton.setEnabled(false);
			mProgressString = "....";
			findViewById(R.id.recordingProgressBar).setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mRecordButton.setEnabled(false);
			while( ! mRecordButton.isEnabled() && ! isCancelled() ){
				publishProgress(0);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			publishProgress(1);
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			int progress = 0;
			if( values != null && values.length > 0 ){
				progress = values[0];
			}
			if( progress != 1 ){
				String newProgressString = mProgressString.replaceFirst(" ",".");
				if( newProgressString.equals(mProgressString) ){
					newProgressString = mProgressString.replaceAll(".", " ");
				}
				mProgressString = newProgressString;
				mRecordButton.setText(getString(R.string.recording)+mProgressString);
			}
			else {
				mRecordButton.setText(R.string.record_again);
				mRecordButton.setEnabled(true);
				findViewById(R.id.recordingProgressBar).setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();

		if( sr != null ){
			sr.stopListening();
			sr.cancel();
			sr.destroy();
		}
	}

}