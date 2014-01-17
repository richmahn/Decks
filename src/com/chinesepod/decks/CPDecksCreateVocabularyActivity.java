/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Activity for creating a new vocabulary locally for a deck
 */
package com.chinesepod.decks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;
import com.chinesepod.decks.utility.CPDecksUtility;

public class CPDecksCreateVocabularyActivity extends CPDecksActivity {
	public static String DECK_ID = "deck_id";

	public static final int PICK_FROM_CAMERA = 0;
	public static final int PICK_FROM_FILE = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int CROP_FROM_FILE = 3;
	private static final String IMAGE_CAPTURE_URI = "image_capture_uri";

	private EditText sourceEditText;
	private EditText targetEditText;
	private EditText targetPhoneticsEditText;
	protected Uri mImageCaptureUri;
	private CPDecksDeck mDeck;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

		long deckId = 0;
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(IMAGE_CAPTURE_URI)) {
				mImageCaptureUri = savedInstanceState.getParcelable(IMAGE_CAPTURE_URI);
			}
			if(savedInstanceState.containsKey(DECK_ID)) {
				deckId = savedInstanceState.getLong(DECK_ID);			
			}
		}
		else {
			deckId = getIntent().getLongExtra(DECK_ID, 0);
		}
		
		if( deckId < 1 ){
			finish();
			return;
		}
		
		mDeck = CPDecksVocabularyManager.getDeck(deckId);
		
		if( mDeck == null ){
			finish();
			return;
		}
					
		sourceEditText = (EditText) findViewById(R.id.sourceEditText);
		targetEditText = (EditText) findViewById(R.id.targetEditText);
		targetPhoneticsEditText = (EditText) findViewById(R.id.targetPhoneticsEditText);
		final Button createVocabularyButton = (Button) findViewById(R.id.CreateVocabularyButton);
		Button cancelButton = (Button) findViewById(R.id.CancelButton);
		createVocabularyButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				createVocabulary();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});
		
		findViewById(android.R.id.content).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mImageCaptureUri != null) {
			outState.putParcelable(IMAGE_CAPTURE_URI, mImageCaptureUri);
		}
	}

	protected void createVocabulary() {
		if ( sourceEditText.getText() == null || sourceEditText.getText().toString().isEmpty() ) {
			Toast.makeText(this, R.string.create_vocabulary_invalid_source, Toast.LENGTH_SHORT).show();
			sourceEditText.requestFocus();
			return;
		} else if ( targetEditText.getText() == null || targetEditText.getText().toString().isEmpty() ) {
			Toast.makeText(this, R.string.create_vocabulary_invalid_target, Toast.LENGTH_SHORT).show();
			targetEditText.requestFocus();
			return;
		}
		CPDecksVocabulary vocabulary = new CPDecksVocabulary();
		vocabulary.setSource(sourceEditText.getText().toString());
		vocabulary.setTarget(targetEditText.getText().toString());
		vocabulary.setTargetPhonetics(targetPhoneticsEditText.getText().toString());

		vocabulary = CPDecksVocabularyManager.createVocabulary(vocabulary);
		boolean result = false;
		if( vocabulary != null ){
			result = CPDecksVocabularyManager.saveVocabularyToDeck(vocabulary, mDeck);
		}
		if( result ){
			if (mImageCaptureUri != null) {
				CPDecksUtility.copy(new File(mImageCaptureUri.getPath()), new File(vocabulary.getImageFile()) );
			}
			
			Intent returnIntent = new Intent();
			returnIntent.putExtra("result", vocabulary);
			setResult(RESULT_OK, returnIntent);     
			finish();
		}
		else {
			CPDecksVocabularyManager.removeVocabulary(vocabulary);
			Toast.makeText(this, R.string.create_vocabulary_failed, Toast.LENGTH_SHORT).show();
		}
	}

	private boolean doCrop(int requestCode) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

			return false;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 160);
			intent.putExtra("outputY", 160);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

				startActivityForResult(i, requestCode);
			}
		}
		return true;
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);

		if (cursor == null)
			return null;

		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_create_vocabulary_dialog;
	}
}
