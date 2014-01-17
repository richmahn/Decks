/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Abstract class for activities to instantiate.
 */
package com.chinesepod.decks;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.chinesepod.decks.CPDecksCanPlaySound.PlaySoundTask;
import com.chinesepod.decks.logic.CPDecksContent;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.logic.CPDecksAudio;
import com.chinesepod.decks.logic.CPDecksError;
import com.chinesepod.decks.logic.CPDecksResponse;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;
import com.chinesepod.decks.utility.FileOperationHelper;

import com.google.analytics.tracking.android.EasyTracker;

abstract public class CPDecksActivity extends Activity {
	public final String TAG = this.getClass().getName();
	
	public final static int DOWNLOADING = 0;
	public final static int DOWNLOADED = 1;
	public final static int DOWNLOAD_CANCELED = 2;
	public final static int DOWNLOAD_ERROR = 3;
	public final static int DOWNLOAD_ERROR_NO_SPACE = 4;
	
	private static final int REQUEST_RECORD_SOUND = 7;
	private CPDecksVocabulary mLastWordOrSentence;
	private MediaPlayer mMediaPlayer;
	protected ActionBar mActionBar;

	protected Button mCoursesButton;
	protected Button mVocabularyButton;
	protected Button mLibraryButton;
	protected Button mProfileButton;
	protected Button mSelfStudyButton;
	protected Button mHomeButton;
	
	protected ArrayList<String> mActionBarSpinnerTitles;

	protected PlaySoundTask mPlaySoundTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActionBar = getActionBar();
		if( mActionBar != null){
			mActionBar.setTitle(R.string.app_name);
			mActionBar.setHomeButtonEnabled(true);
			mActionBar.setDisplayHomeAsUpEnabled(true);
//			makeOverflowMenuVisible();
		}

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		
		setContentView(getLayoutResourceId());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	public void openDashboard() {
		Intent i = new Intent(this, CPDecksDashboardActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();
	}

	public ProgressDialog createLoadingDialog(int messageId) {
		ProgressDialog loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage(getString(messageId));
		loadingDialog.setIndeterminate(true);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setCancelable(true);
		return loadingDialog;
	}

	void handleError(CPDecksResponse response) {

		CPDecksError error = response.getError();
		if (error.isNetworkError()) {
			Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
//			finish();
		} else if (error.isAuthenticationError()) {
			Toast.makeText(this, R.string.authentication_error, Toast.LENGTH_SHORT).show();
		}
	}

	public void openFileForViewing(String filePath) throws ActivityNotFoundException {
		openFileForViewing(filePath, null);
	}
	public void openFileForViewing(String filePath, String fileUrl) throws ActivityNotFoundException {
		if( filePath == null || filePath.isEmpty() ){
			return;
		}
		
		File file = new File(filePath);
		
		if( file.exists() ){
			String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString()).toLowerCase();
			if( extension.isEmpty() ) {
				return;
			}
			try {
				String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), mimetype);
				startActivity(intent);
				return;
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		if( fileUrl != null && ! fileUrl.isEmpty() ){
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
				startActivity(intent);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	protected abstract int getLayoutResourceId();
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar, menu);
	    menu.findItem(R.id.action_refresh).setVisible(false);

	    return true;
	}
	 
	public boolean onPrepareOptionsMenu(Menu menu) {
	    //  preparation code here
	    return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
	        Intent upIntent = NavUtils.getParentActivityIntent(this);
	        if (upIntent != null ) {
	        	if(NavUtils.shouldUpRecreateTask(this, upIntent)) {
	        		// This activity is NOT part of this app's task, so create a new task
	        		// when navigating up, with a synthesized back stack.
	        		TaskStackBuilder.create(this)
	                    // Add all of this activity's parents to the back stack
	                    .addNextIntentWithParentStack(upIntent)
	                    // Navigate up to the closest parent
	                    .startActivities();
	        	} else {
	        		// This activity is part of this app's task, so simply
	        		// navigate up to the logical parent activity.
	        		NavUtils.navigateUpTo(this, upIntent);
	        	}
	        } else {
	        	finish();
        	}
	        return true;
		case R.id.action_settings:
			openActivity(CPDecksSettingsActivity.class);
			return true;
		case R.id.action_accuracy:
			openActivity(CPDecksAccuracyActivity.class);
			return true;
		case R.id.action_about:
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://chinesepod.com/about"));
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void openActivity(Class<?> cls) {
		Intent i = new Intent(this, cls);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
	
	@Override
	protected void onResume(){
		refreshPage();
		super.onResume();
	}

	@Override
	protected void onPause(){
		if( mPlaySoundTask != null ){
			mPlaySoundTask.stopAudio();
		}
		super.onPause();
	}
	
	@Override
	public void setTitle(int resId){
		super.setTitle(resId);
		if( mActionBar != null ){
			mActionBar.setTitle(resId);
		}
	}

	@Override
	public void setTitle(CharSequence title){
		super.setTitle(title);
		if( mActionBar != null ){
			mActionBar.setTitle(title);
		}
	}

	public void setSubtitle(int resId){
		if( mActionBar != null ){
			mActionBar.setSubtitle(resId);
		}
	}

	public void setSubtitle(CharSequence title){
		if( mActionBar != null ){
			mActionBar.setSubtitle(title);
		}
	}

	public ProgressDialog getProgressDialog(int stringId) {
		ProgressDialog dialog = createLoadingDialog(stringId);
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		return dialog;
	}

	public void recordSound(CPDecksVocabulary wordOrSentence, View v) {
		try {
			Intent recordIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
			mLastWordOrSentence = wordOrSentence;
			startActivityForResult(recordIntent, REQUEST_RECORD_SOUND);
		} catch (Exception e) {
			Toast.makeText(this, R.string.problem_recording, Toast.LENGTH_SHORT).show();
		}
	}
	
	public String recordSound(final CPDecksVocabulary wordOrSentence, View recordButton, final View listenRecordedButton) {
		return recordSound(wordOrSentence, recordButton, listenRecordedButton, null);
	}
	public String recordSound(final CPDecksVocabulary wordOrSentence, final View recordButton, final View listenRecordedButton, String filePath) {
		try {
			if( filePath == null || filePath.isEmpty() ){
				filePath = wordOrSentence.getRecordAudioFile();
			}
			FileOperationHelper.checkSaveDir(new File(filePath).getParent());
			final MediaRecorder recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(filePath);
			recorder.prepare();
			final ProgressDialog mProgressDialog = new RecorderProgressDialog(this);
			final Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						sleep((wordOrSentence instanceof CPDecksVocabulary) ? 10000 : 20000);
						recorder.stop();
						recorder.release();
						mProgressDialog.dismiss();
						if( listenRecordedButton instanceof Button ){
							((Button)listenRecordedButton).setBackgroundResource(R.drawable.play_record_green_dot);
						}
					} catch (InterruptedException e) {
					}
				}
			};
			mProgressDialog.setTitle(R.string.recording);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(wordOrSentence.toString());
//			mProgressDialog.setButton(0, getString(R.string.stop_recording), new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					thread.interrupt();
//					mProgressDialog.dismiss();
//					recorder.stop();
//					recorder.release();
//					if( listenRecordedButton != null ){
//						listenRecordedButton.setBackgroundResource(R.drawable.play_record_green_dot);
//					}
//				}
//			});

			mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface p1) {
					thread.interrupt();
					recorder.stop();
					recorder.release();
					if( listenRecordedButton instanceof Button ){
						((Button)listenRecordedButton).setBackgroundResource(R.drawable.play_record_green_dot);
					}
				}
			});
			recorder.start();

			thread.start();
			mProgressDialog.show();
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.problem_recording, Toast.LENGTH_SHORT).show();
		}
		return null;
	}

	public void playRecordedSound(CPDecksVocabulary wordOrSentence, final View v, final int onCompletionDrawableResourceId) {
		playRecordedSound(wordOrSentence, v, onCompletionDrawableResourceId, null);
	}
	public void playRecordedSound(CPDecksVocabulary wordOrSentence, final View v, final int onCompletionDrawableResourceId, String filePath) {
		mMediaPlayer = new MediaPlayer();
		if( filePath == null || filePath.isEmpty() ){
			filePath = wordOrSentence.getRecordAudioFile();
		}
		try {
			mMediaPlayer.setDataSource(filePath);
			mMediaPlayer.prepare();
			v.setBackgroundResource(R.drawable.play_record_red);
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					v.setBackgroundResource(onCompletionDrawableResourceId);
				}
			});
			mMediaPlayer.start();
		} catch (Exception x) {
			Toast.makeText(this, R.string.no_record, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void playRecordedSound(CPDecksVocabulary wordOrSentence, final View v) {
		playRecordedSound(wordOrSentence, v, R.drawable.play_record_green_dot);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_RECORD_SOUND) {
				String sourcePath = getRealPathFromURI(intent.getData());
				String destPath = mLastWordOrSentence.getRecordAudioFile();
				File sourceF = new File(sourcePath);
				try {
					sourceF.renameTo(new File(destPath));
				} catch (Exception e) {
					Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String returnVal = "";
		
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		if( cursor != null && cursor.moveToFirst() ){
			returnVal = cursor.getString(column_index);
			cursor.close();
		}
		
		return returnVal;
	}

	class RecorderProgressDialog extends ProgressDialog {

		public RecorderProgressDialog(Context context) {
			super(context);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

		}
	}
	
	public void saveTermToDeck(final CPDecksVocabulary term) {
		ArrayList<CPDecksDeck> decksWithout = null;
		if( term instanceof CPDecksContent ){
			 decksWithout = CPDecksVocabularyManager.getDecksWithoutAppDecksContentId(term.getAppDecksContentId());
		}
		else {
			 decksWithout = CPDecksVocabularyManager.getDecksWithoutVocabulary(term);
		}
		
		final ArrayList<CPDecksDeck> decks = decksWithout;
		
		if( decks.size() > 0 ){
			final ArrayAdapter<CPDecksDeck> myAdapter = new ArrayAdapter<CPDecksDeck>(this, android.R.layout.simple_list_item_1, decks);
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.choose_deck);
			builder.setAdapter(myAdapter, new android.content.DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int position) {
					CPDecksDeck deck = decks.get(position);
					if( deck.containsVocabulary(term) ){
						Toast.makeText(CPDecksActivity.this, R.string.word_adding_exists, Toast.LENGTH_SHORT).show();
						return;
					}
					if (term != null) {
						if( CPDecksVocabularyManager.saveVocabularyToDeck(term, deck) ){
							Toast.makeText(CPDecksActivity.this, "Successfully added.", Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(CPDecksActivity.this, "There was an error adding this term.", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
			final AlertDialog chooseDeckDialog = builder.create();
			chooseDeckDialog.show();
		}
		else {
			Toast.makeText(this, "All decks contain this term,  or you haven't any existing decks.", Toast.LENGTH_SHORT).show();
		}
	}

	public void playSound(CPDecksVocabulary wordOrSentence) {
		playSound(wordOrSentence, null, 0, 0);
	}
	public void playSound(CPDecksVocabulary wordOrSentence, View v, int playingDrawableResourceId, int finishedDrawableResourceId) {
		if( wordOrSentence == null || wordOrSentence.getTargetAudio() == null){
			return;
		}
		playSound(wordOrSentence.getTargetAudio(), v, playingDrawableResourceId, finishedDrawableResourceId);
	}
	public void playSound(CPDecksAudio audio) {
		playSound(audio, null, 0, 0);
	}
	public void playSound(CPDecksAudio audio, View v, int playingDrawableResourceId, int finishedDrawableResourceId) {
		boolean stoppedPlayer = false;
		if( mPlaySoundTask != null && mPlaySoundTask.isPlaying() ){
			mPlaySoundTask.stopAudio();
			stoppedPlayer = true;
		}
		if( mPlaySoundTask == null || ! stoppedPlayer || (stoppedPlayer && ! mPlaySoundTask.mView.equals(v)) ) {
			mPlaySoundTask = new PlaySoundTask(audio, mMediaPlayer, createLoadingDialog(R.string.audio_is_downloading), v, playingDrawableResourceId, finishedDrawableResourceId);
			mPlaySoundTask.execute();
		}
	}

	public void refreshPage() {
		invalidateOptionsMenu();
	}

//	private void makeOverflowMenuVisible() {
//		try {
//			ViewConfiguration config = ViewConfiguration.get(this);
//			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
//			if(menuKeyField != null) {
//	            menuKeyField.setAccessible(true);
//	            menuKeyField.setBoolean(config, false);
//	        }
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	}

	public void copyTermToAnotherDeck(final CPDecksVocabulary word) {
		try {
			final ArrayList<CPDecksDeck> decks = CPDecksVocabularyManager.getDecksWithoutVocabulary(word);
			if( decks.size() < 1 ){
				Toast.makeText(this, "All decks already contain this term", Toast.LENGTH_SHORT).show();
				return;
			}
			ArrayAdapter<CPDecksDeck> myAdapter = new ArrayAdapter<CPDecksDeck>(this, android.R.layout.simple_list_item_1, decks);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.choose_deck);
			builder.setAdapter(myAdapter, new android.content.DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int position) {
					boolean result = CPDecksVocabularyManager.saveVocabularyToDeck(word, decks.get(position));
					if( result ){
						Toast.makeText(CPDecksActivity.this, "Successfully copied to "+decks.get(position).getTitle(), Toast.LENGTH_SHORT).show();
					}
					else {
						Toast.makeText(CPDecksActivity.this, "Copying to "+decks.get(position).getTitle()+" FAILED. Please try again.", Toast.LENGTH_SHORT).show();
					}
				}
			});
			builder.create().show();
		} catch (Exception x) {
			Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
		}
	}

	public void moveTermToAnotherDeck(final CPDecksVocabulary word, final CPDecksDeck fromDeck) {
		try {
			final ArrayList<CPDecksDeck> decks = CPDecksVocabularyManager.getDecksWithoutVocabulary(word);
			if( decks.size() < 1 ){
				Toast.makeText(this, "All decks already contain this term", Toast.LENGTH_SHORT).show();
				return;
			}
			ArrayAdapter<CPDecksDeck> myAdapter = new ArrayAdapter<CPDecksDeck>(this, android.R.layout.simple_list_item_1, decks);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.choose_deck);
			builder.setAdapter(myAdapter, new android.content.DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int position) {
					boolean result = CPDecksVocabularyManager.saveVocabularyToDeck(word, decks.get(position));
					if( result && fromDeck != null ){
						result = CPDecksVocabularyManager.removeVocabularyFromDeck(word, fromDeck);
					}
					if( result ){
						Toast.makeText(CPDecksActivity.this, "Successfully moved to "+decks.get(position).getTitle(), Toast.LENGTH_SHORT).show();
						refreshPage();
					}
					else {
						Toast.makeText(CPDecksActivity.this, "Moving to "+decks.get(position).getTitle()+" FAILED. Please try again.", Toast.LENGTH_SHORT).show();
					}
				}
			});
			builder.create().show();
		} catch (Exception x) {
			Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
		}
	}

	public void renameDeck(final CPDecksDeck deck) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(CPDecksActivity.this);
		alertDialog.setTitle(R.string.deck_renaming);
		alertDialog.setMessage(R.string.deck_renaming_confirmation);
		final EditText input = new EditText(CPDecksActivity.this);
		input.setText(deck.getTitle());
		input.setSelection(input.getText().length(), input.getText().length());
		alertDialog.setView(input);
		// alertDialog.setIcon(R.drawable.search);
		alertDialog.setPositiveButton(R.string.deck_renaming_rename, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String title = input.getText().toString();
				if( CPDecksVocabularyManager.getDeckByTitle(title) != null ){
					Toast.makeText(CPDecksActivity.this,  "You already have a deck with that name.", Toast.LENGTH_SHORT).show();
					return;
				}
				deck.setTitle(title);
				boolean result = CPDecksVocabularyManager.updateDeck(deck);
				if( result ){
					Toast.makeText(CPDecksActivity.this, R.string.deck_renamed_successfully, Toast.LENGTH_SHORT).show();
					refreshPage();
				}
				else {
					Toast.makeText(CPDecksActivity.this, R.string.deck_renamed_unsuccessfully, Toast.LENGTH_SHORT).show();
				}
			}
		});
		alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alertDialog.create().show();
	}

	public void removeDeck(final CPDecksDeck deck) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(CPDecksActivity.this);
		alertDialog.setTitle(R.string.deck_removal);
		alertDialog.setMessage(R.string.deck_removal_confirmation);
		// alertDialog.setIcon(R.drawable.search);
		alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				boolean result = CPDecksVocabularyManager.removeDeck(deck);
				if( result ){
					Toast.makeText(CPDecksActivity.this, "Deck deleted.", Toast.LENGTH_SHORT).show();
					refreshPage();
				}
				else {
					Toast.makeText(CPDecksActivity.this, "Failed to delete deck.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alertDialog.create().show();
	}
}