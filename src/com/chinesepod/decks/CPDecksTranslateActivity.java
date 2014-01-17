/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity for Translation of a word or sentence.
 */

package com.chinesepod.decks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.chinesepod.decks.Languages.Language;
import com.chinesepod.decks.adapter.CPDecksImagePagerAdapter;
import com.chinesepod.decks.logic.CPDecksError;
import com.chinesepod.decks.logic.CPDecksResponse;
import com.chinesepod.decks.logic.CPDecksTranslation;
import com.chinesepod.decks.utility.CPDecksTranslationManager;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.net.NetworkUtilityModel;

import android.provider.ContactsContract;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CPDecksTranslateActivity extends CPDecksActivity implements OnClickListener, OnInitListener, CPDecksCanPlaySound, OnPageChangeListener {
    public boolean mIsRetrieving;
	public ProgressDialog mRetrievingDialog;

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private static final int CHECK_4_TTS_CODE = 1235;
    private EditText mSourceEditTextView;
    private TextView mSourceTextView;
    private TextView mTargetTextView;
    private TextView mTargetPhoneticView;
	private View mSourceLanguageBar;
	private View mTargetLanguageBar;
    private Button mSourceLanguageSelectButton;
    private TextView mSourceLanguageTextView;
    private TextView mTargetLanguageTextView;
    private Button mTargetLanguageSelectButton;
    private ImageView mTranslateButton;
    private ImageView mSwapLanguagesButton;
	private String mSpeechRecognitionResult;
    private CPDecksTranslation mTranslation;

    // true if changing a language should automatically trigger a translation
    private boolean mDoTranslate = true;

    // Dialog id's
    private static final int LANGUAGE_DIALOG_ID = 1;

    // Saved preferences
    private static final String SOURCE_LOCALE = "source_locale";
    private static final String TARGET_LOCALE = "target_locale";
    private static final String SOURCE_TEXT = "source_text";
    private static final String TARGET_TEXT = "target_text";
    
    // Default language pair if no saved preferences are found
    private static final String DEFAULT_SOURCE_LOCALE = Language.ENGLISH.getShortName();
    private static final String DEFAULT_TARGET_LOCALE = Language.CHINESE.getShortName();
	private static final int PICK_CONTACT = 0;

    private Button mLatestButton;

    private OnClickListener mClickListener = new OnClickListener() {
        public void onClick(View v) {
            mLatestButton = (Button) v;
            showDialog(LANGUAGE_DIALOG_ID);
        }
    };
	private Button mShowGoogleImageResultsButton;
	private MenuItem mSaveMenuItem;
	private MenuItem mSampleSentencesMenuItem;
	private View mClearSourceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.translate);
//        new RetrieveTranslationTask(Language.ENGLISH, Language.CHINESE, "hello world").execute();
        
        mSwapLanguagesButton = (ImageView) findViewById(R.id.swapLanguagesButton);
        mSourceLanguageSelectButton = (Button) findViewById(R.id.sourceLanguageButton);
        mTargetLanguageSelectButton = (Button) findViewById(R.id.targetLanguageButton);

        mSourceLanguageTextView = (TextView) findViewById(R.id.sourceLanguage);
        mSourceLanguageBar = findViewById(R.id.sourceLanguageBar); 
        mSourceTextView = (TextView)findViewById(R.id.sourceText);
        mSourcePlayButton = (ImageView)findViewById(R.id.sourceAudioPlayButton);
        mClearSourceButton = findViewById(R.id.clearSourceButton);
        
        mTargetLanguageTextView = (TextView) findViewById(R.id.targetLanguage);
        mTargetLanguageBar = findViewById(R.id.targetLanguageBar); 
        mTargetTextView = (TextView)findViewById(R.id.targetText);
        mTargetPhoneticView = (TextView)findViewById(R.id.targetPhonetics);
        mTargetPlayButton = (ImageView)findViewById(R.id.targetAudioPlayButton);

        mSourceEditTextView = (EditText) findViewById(R.id.sourceInput);
        mTranslateButton = (ImageView) findViewById(R.id.translateButton);

        mImageViewPager = (ViewPager)findViewById(R.id.imageSelectPager);
        mShowGoogleImageResultsButton = (Button)findViewById(R.id.showGoogleImageResultsButton);

        //
        // Install the language adapters on both the From and To spinners.
        //
        mSourceLanguageSelectButton.setOnClickListener(mClickListener);
        mTargetLanguageSelectButton.setOnClickListener(mClickListener);

        mTranslateButton.setOnClickListener(this);
        mSwapLanguagesButton.setOnClickListener(this);
        mSourceEditTextView.selectAll();

        mShowGoogleImageResultsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if (mTranslation.getImageUrlsList() != null && mTranslation.getImageUrlsList().size() > 0) {
                	mImageViewPager.setVisibility(View.VISIBLE);
                	mImageViewPager.setOnPageChangeListener(CPDecksTranslateActivity.this);
                	CPDecksImagePagerAdapter imagePagerAdapter = new CPDecksImagePagerAdapter(getFragmentManager(), mTranslation.getImageUrlsList());
                	mImageViewPager.setAdapter(imagePagerAdapter);
                	mTranslation.setImageUrl(mTranslation.getImageUrlsList().get(0).getImageUrl());
                }
                mShowGoogleImageResultsButton.setVisibility(View.GONE);
			}
		});
        
        mClearSourceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSourceEditTextView.setText(null);
				mSourceEditTextView.clearComposingText();
			}
		});
        
        checkForSpeechToText();
        checkForTextToSpeech();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = CPDecksApplication.getPrefs(this);
//        mDoTranslate = false;
        
        Language sourceLanguage = Language.findLanguageByShortName(prefs.getString(SOURCE_LOCALE, DEFAULT_SOURCE_LOCALE));
        Language targetLanguage = Language.findLanguageByShortName(prefs.getString(TARGET_LOCALE, DEFAULT_TARGET_LOCALE));
        String sourceText = prefs.getString(SOURCE_TEXT, "");
        
        if( mTranslation == null ){
        	mTranslation = new CPDecksTranslation();
        	mTranslation.setSource(sourceText);
        	mTranslation.setSourceLanguage(sourceLanguage);
        	mTranslation.setTargetLanguage(targetLanguage);
        }
        
        if( sourceLanguage != null && targetLanguage != null && sourceText != null && ! sourceText.isEmpty() ){
        	CPDecksTranslationManager.setFetchMode(NetworkUtilityModel.ONLY_USE_CACHE);
        	mTranslation = CPDecksTranslationManager.getTranslation(sourceLanguage, targetLanguage, sourceText);
        	refreshPage();
        	mDoTranslate = true;
        }
    }
    
    @Override 
    public void refreshPage(){
    	super.refreshPage();
    	
    	if( mTranslation != null ){
            updateButton(mSourceLanguageSelectButton, mTranslation.getSourceLanguage(), false);
            updateButton(mTargetLanguageSelectButton, mTranslation.getTargetLanguage(), false);

    		if( mTranslation.getTarget() != null && ! mTranslation.getTarget().isEmpty() ){
        		if( mSaveMenuItem != null ){
        			mSaveMenuItem.setVisible(true);
        		}

        		if(mSampleSentencesMenuItem != null ){
    				if( mTranslation.getTargetLanguage() == Language.CHINESE || mTranslation.getTargetLanguage() == Language.CHINESE_SIMPLIFIED || mTranslation.getTargetLanguage() == Language.ENGLISH ){
    					mSampleSentencesMenuItem.setVisible(true);
    				}
    				else {
    					mSampleSentencesMenuItem.setVisible(false);
    				}
    			}
        		
                mSourceLanguageBar.setVisibility(View.VISIBLE);
                mTargetLanguageBar.setVisibility(View.VISIBLE);
                
                mSourceLanguageTextView.setText(mTranslation.getSourceLanguage().getLongName());
                mTargetLanguageTextView.setText(mTranslation.getTargetLanguage().getLongName());
                
                mSourceTextView.setText(new Entities().unescape(mTranslation.getSource()));
                mSourceEditTextView.setText(mTranslation.getSource());
                mTargetTextView.setText(new Entities().unescape(mTranslation.getTarget()));
                mTargetPhoneticView.setText(new Entities().unescape(mTranslation.getTargetPhonetics()));

                if( mTranslation.getSourceAudio() != null ){
                	if( mTranslation.getSourceAudio().fileExists() ){
                		mSourcePlayButton.setImageResource(R.drawable.sound_gray_downloaded);
                	}
                	else {
                		mSourcePlayButton.setImageResource(R.drawable.sound_gray);
                	}

                	mSourcePlayButton.setVisibility(View.VISIBLE);
                	mSourcePlayButton.setClickable(true);
                	mSourcePlayButton.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						playSound(mTranslation.getSourceAudio(), v, R.drawable.sound_gray_downloaded, R.drawable.sound_gray_downloaded);
    					}
    				});
                }
                else {
                	mSourcePlayButton.setVisibility(View.INVISIBLE);
                	mSourcePlayButton.setClickable(false);
                }
                
                if( mTranslation.getTargetAudio() != null ){
                	if( mTranslation.getTargetAudio().fileExists() ){
                		mTargetPlayButton.setImageResource(R.drawable.sound_gray_downloaded);
                	}
                	else {
                		mTargetPlayButton.setImageResource(R.drawable.sound_gray);
                	}
                	
                	mTargetPlayButton.setVisibility(View.VISIBLE);
                	mTargetPlayButton.setClickable(true);
                	mTargetPlayButton.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						playSound(mTranslation.getTargetAudio(), v, R.drawable.sound_gray_downloaded, R.drawable.sound_gray_downloaded);
    					}
    				});
                }
                else {
                	mTargetPlayButton.setVisibility(View.INVISIBLE);
                	mTargetPlayButton.setClickable(false);
                }

                mImageViewPager.setVisibility(View.GONE);
                if (mTranslation.getImageUrlsList() != null && mTranslation.getImageUrlsList().size() > 0) {
                	mShowGoogleImageResultsButton.setVisibility(View.VISIBLE);
                	mShowGoogleImageResultsButton.setClickable(true);
                }
                else {
                	mShowGoogleImageResultsButton.setVisibility(View.GONE);
                	mShowGoogleImageResultsButton.setClickable(false);
                }
    		}
    		else {
    			mSourceLanguageBar.setVisibility(View.INVISIBLE);
    			mTargetLanguageBar.setVisibility(View.INVISIBLE);
    			mImageViewPager.setVisibility(View.GONE);
    			mShowGoogleImageResultsButton.setVisibility(View.GONE);
    		}
    	}
    }
    
    private void updateButton(Button button, Language language, boolean translate) {
        language.configureButton(this, button);
        if (translate) maybeTranslate();
    }

    /**
     * Launch the translation if the input text field is not empty.
     */
    private void maybeTranslate() {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(mSourceEditTextView.getWindowToken(), 0);
        if (mDoTranslate && !TextUtils.isEmpty(mSourceEditTextView.getText().toString())) {
            Language source = (Language) mSourceLanguageSelectButton.getTag();
            Language target = (Language) mTargetLanguageSelectButton.getTag();
            String input = mSourceEditTextView.getText().toString();
            log("Translating from " + source.getShortName() + " to " + target.getShortName());
            new RetrieveTranslationTask(source, target, input).execute();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        savePreferences();
    }

    // Save the content of our views to the shared preferences
    private void savePreferences() {
        Editor edit = CPDecksApplication.getPrefs(this).edit();
        if( mSourceLanguageSelectButton != null && mSourceLanguageSelectButton.getTag() != null){
        	String f = ((Language) mSourceLanguageSelectButton.getTag()).getShortName();
        	String t = ((Language) mTargetLanguageSelectButton.getTag()).getShortName();
        	String input = mSourceEditTextView.getText().toString();
        	String output = mTargetTextView.getText().toString();
        	savePreferences(edit, f, t, input, output);
        }
    }

    static void savePreferences(Editor edit, String from, String to, String input, String output) {
        log("Saving preferences " + from + " " + to + " " + input + " " + output);
        edit.putString(SOURCE_LOCALE, from);
        edit.putString(TARGET_LOCALE, to);
        edit.putString(SOURCE_TEXT, input);
        edit.putString(TARGET_TEXT, output);
        edit.commit();
    }
    
    public void onClick(View v) {
        if (v == mTranslateButton) {
            maybeTranslate();
        } else if (v == mSwapLanguagesButton) {
            Object newFrom = mTargetLanguageSelectButton.getTag();
            Object newTo = mSourceLanguageSelectButton.getTag();
            mSourceEditTextView.setText(mTargetTextView.getText());
            mSourceTextView.setText(mTargetTextView.getText());
            mSourceLanguageTextView.setText(((Language)newFrom).getLongName());

            mTargetLanguageBar.setVisibility(View.INVISIBLE);
            mTargetTextView.setText("");
            mTargetPhoneticView.setText("");
            
            setNewLanguage((Language) newFrom, true /* from */, false /* don't translate */);
            setNewLanguage((Language) newTo, false /* to */, true /* translate */);
            
            mSourceEditTextView.requestFocus();
            Toast.makeText(this, R.string.languages_swapped, Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.speechRecognitionButton) {
            startSpeechRecognitionActivity();
        }
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog d) {
        if (id == LANGUAGE_DIALOG_ID) {
            boolean from = mLatestButton == mSourceLanguageSelectButton;
            ((LanguageDialog) d).setFrom(from);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == LANGUAGE_DIALOG_ID) {
            return new LanguageDialog(this);
        }
        return null;
    }

    public void setNewLanguage(Language language, boolean from, boolean translate) {
        updateButton(from ? mSourceLanguageSelectButton : mTargetLanguageSelectButton, language, translate);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.translate_action_menu, menu);
        
        mSaveMenuItem = menu.findItem(R.id.save_to_deck);
        mSampleSentencesMenuItem = menu.findItem(R.id.sample_sentences);

        if( mTranslation == null || mTranslation.getTarget() == null || mTranslation.getTarget().isEmpty() ){
        	mSaveMenuItem.setVisible(false);
        	mSampleSentencesMenuItem.setVisible(false);
		}
        else if(mTranslation.getTargetLanguage() != Language.CHINESE && mTranslation.getTargetLanguage() != Language.CHINESE_SIMPLIFIED && mTranslation.getTargetLanguage() != Language.ENGLISH ){
    		mSampleSentencesMenuItem.setVisible(false);
    	}
        else {
        	mSaveMenuItem.setVisible(true);
        	mSampleSentencesMenuItem.setVisible(true);
        }
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.save_to_deck:
        	if( mTranslation != null ){
        		saveTermToDeck(mTranslation);
        	}
            break;
        case R.id.sample_sentences:
        	if( mTranslation != null ){
        		Intent i = new Intent(this, CPDecksSampleSentencesActivity.class);
        		i.putExtra(CPDecksSampleSentencesActivity.CPOD_VOCABULARY, mTranslation);
        		startActivity(i);
        	}
        	break;
        case R.id.show_history:
            showHistory();
            break;

        // We shouldn't need this menu item but because of a bug in 1.0, neither SMS nor Email
        // filter on the ACTION_SEND intent.  Since they won't be shown in the activity chooser,
        // I need to make an explicit menu for SMS
        case R.id.send_with_sms: {
//            Intent i = new Intent(Intent.ACTION_PICK, Contacts.Phones.CONTENT_URI);
        	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        	startActivityForResult(intent, PICK_CONTACT);       
            break;
        }

        case R.id.send_with_email:
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, mTargetTextView.getText());
            startActivity(Intent.createChooser(intent, null));
            break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
    	super.onActivityResult(requestCode, resultCode, resultIntent);
    	 
        if (requestCode == PICK_CONTACT && resultIntent != null) {
			Uri contactData = resultIntent.getData();
			Cursor c = managedQuery(contactData, null, null, null, null);
			if (c.moveToFirst()) {
				String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
				String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (hasPhone.equalsIgnoreCase("1")) {
					Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
					phones.moveToFirst();
					String phone = phones.getString(phones.getColumnIndex("data1"));
	                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto://" + phone));
	                intent.putExtra("sms_body", mTargetTextView.getText().toString());
	                startActivity(intent);
				}
			}        	
        }
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = resultIntent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        	mSpeechRecognitionResult = matches.get(0);
           	mSourceEditTextView.setText(mSpeechRecognitionResult);
           	mSourceLanguageBar.setVisibility(View.VISIBLE);
           	mSourceTextView.setText(mSpeechRecognitionResult);
           	savePreferences();
        	maybeTranslate();
        }
        else if (requestCode == CHECK_4_TTS_CODE) {
        	Log.d(TAG, "CHECK_4_TTS_CODE result " + resultCode);
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                // TODO: handle result
            }
        }
    }
    
    private void showHistory() {
        startActivity(new Intent(this, HistoryActivity.class));
    }
    
    private static void log(String s) {
        Log.d("CPDecksTranslateActivity", s);
    }

	// Check to see if a recognition activity is present
    private void checkForSpeechToText() {
    	ImageView speakButton = (ImageView)findViewById(R.id.speechRecognitionButton);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);
        } else {
            speakButton.setEnabled(false);
            speakButton.setImageResource(R.drawable.hl_device_access_mic_muted);
        }
    }

    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startSpeechRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition");
        Language language = (Language)mSourceLanguageSelectButton.getTag();
        Locale locale = Languages.toLocale(language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toString());
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    // Text to Speech //////////////////////////////////////////////////////
    
    private TextToSpeech mTts;
	public ImageView mSourcePlayButton;
	public ImageView mTargetPlayButton;
	public ViewPager mImageViewPager;

	public void onInit(int status) {
		Log.d(TAG, "OnInit");
		mTts.setLanguage(Locale.US);
	}

    private void checkForTextToSpeech() {
    	Intent checkIntent = new Intent();
    	checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
    	startActivityForResult(checkIntent, CHECK_4_TTS_CODE);
    }

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_translate;
	}

	class RetrieveTranslationTask extends AsyncTask<Integer, Integer, CPDecksResponse> {
		int mTypeId;
		private Language mSourceLanguage;
		private Language mTargetLanguage;
		private String mText;
		
		public RetrieveTranslationTask(Language sourceLanauge, Language targetLanguage, String text) {
			mSourceLanguage = sourceLanauge;
			mTargetLanguage = targetLanguage;
			mText = text;
		}

		@Override
		protected void onPreExecute() {
			mIsRetrieving = true;

			mRetrievingDialog = createLoadingDialog(R.string.translating_text);
			mRetrievingDialog.show();
			
			Log.i(TAG, "onPreExecute in retrieveing translation task");
			super.onPreExecute();
		}

		@Override
		protected CPDecksResponse doInBackground(Integer... params) {
			CPDecksResponse response = new CPDecksResponse();

			Log.i(TAG, "Started retrieveing transation");

			CPDecksTranslation translation = null;
			if( CPDecksUtility.isOnline(CPDecksTranslateActivity.this) ){
				CPDecksTranslationManager.setFetchMode(NetworkUtilityModel.FIRST_NET_THEN_CACHE);
				translation = CPDecksTranslationManager.getTranslation(mSourceLanguage, mTargetLanguage, mText);
			}
				
			if( translation == null ) {
				response.setError(new CPDecksError("Unable to translate the given phrase"));
				return response;
			}
			else {
				response.setObject(translation);
			}
			
			Log.i(TAG, "Finished retrieveing translation");
			return response;
		}

		@Override
		protected void onPostExecute(CPDecksResponse response) {
			mIsRetrieving = false;
			mRetrievingDialog.dismiss();

			if( response.isSuccessfull() ){
                mTranslation = (CPDecksTranslation)response.getObject();
                History.addHistoryRecord(CPDecksTranslateActivity.this, mTranslation);
                savePreferences();
                mSourceEditTextView.selectAll();
			}
			else {
				Toast.makeText(CPDecksTranslateActivity.this, response.getError().getErrorCode(), Toast.LENGTH_SHORT).show();
			}

            refreshPage();
            
			super.onPostExecute(response);
		}
	}


	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		if( position >= 0 && mTranslation != null && mTranslation.getImageUrlsList() != null && mTranslation.getImageUrlsList().size() > position ){
			mTranslation.setImageUrl(mTranslation.getImageUrlsList().get(position).getImageUrl());
		}
	}
}
