/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Class to be implemented by classes which wish to play audio files
 * 
 */
package com.chinesepod.decks;

import java.io.File;
import java.io.IOException;

import android.R.integer;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksAudio;
import com.chinesepod.decks.utility.FileOperationHelper;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;

public interface CPDecksCanPlaySound {
	void playSound(CPDecksAudio audio, View v, int playingDrawableResourceId, int finishedDrawableResourceId);

	class PlaySoundTask extends AsyncTask<String, Integer, Boolean> {
		private Boolean isAlreadyExist = false;
		private MediaPlayer mMediaPlayer;
		private Dialog mDialog;
		public View mView;
		public CPDecksAudio mAudio;
		private int mNewDrawableResourceId;
		private int mFinishedDrawableResourceId;
		private int mPlayingDrawableResourceId;
		private Drawable mOriginalDrawable;

		public PlaySoundTask(CPDecksAudio audio, MediaPlayer mp, Dialog dialog, View view, int playingDrawableResourceId, int finishedDrawableResourceId) {
			if( audio == null || audio.getAudioFile() == null || audio.getAudioFile().isEmpty() ){
				cancel(true);
				return;
			}
			
			if( mMediaPlayer != null ){
				mMediaPlayer.stop();
			}
			mMediaPlayer = mp;
			mDialog = dialog;
			mView = view;
			mAudio = audio;
			mPlayingDrawableResourceId = playingDrawableResourceId;
			mFinishedDrawableResourceId = finishedDrawableResourceId;
		}

		@Override
		protected void onPreExecute() {
			if( isCancelled() ){
				return;
			}
			
			if (FileOperationHelper.checkSaveDir(new File(mAudio.getAudioFile()).getParent())) {
				if (mAudio.fileExists()) {
					isAlreadyExist = true;
				}
			}

			if (!isAlreadyExist) {
				mDialog.show();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			if (!isCancelled() && !isAlreadyExist && downloadFile()) {
				isAlreadyExist = true;
			}
			return isAlreadyExist;
		}

		private boolean downloadFile() {
			try {
				return HttpConnectionHelper.saveFile(mAudio.getAudioUrl(), mAudio.getAudioFile());
			} catch (IOException e) {
			}
			return false;

		}
		
		private void playAudio() {
			try {
				if (mMediaPlayer != null && mMediaPlayer.isPlaying())
					return;

				setPlayingDrawableInView();
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setDataSource(mAudio.getAudioFile());
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						setFinishedDrawableInView();
					}
				});
			} catch (Exception x) {
				File audio = new File(mAudio.getAudioFile());
				audio.delete();
			}
		}
		
		private void setPlayingDrawableInView() {
			if( mView != null && mPlayingDrawableResourceId > 0 ){
				if( mView instanceof ImageView ){
					mOriginalDrawable = ((ImageView)mView).getDrawable();
					((ImageView)mView).setImageResource(mPlayingDrawableResourceId);
				}
				else {
					mOriginalDrawable = mView.getBackground();
					mView.setBackgroundResource(mPlayingDrawableResourceId);
				}
			}
		}

		protected void setFinishedDrawableInView() {
			if( mView != null ){
				if( mFinishedDrawableResourceId > 0){
					if( mView instanceof ImageView ){
						((ImageView)mView).setImageResource(mFinishedDrawableResourceId);
					}
					else {
						mView.setBackgroundResource(mFinishedDrawableResourceId);
					}
				}
				else if( mPlayingDrawableResourceId > 0 && mOriginalDrawable != null ) {
					if( mView instanceof ImageView ){
						((ImageView)mView).setImageDrawable(mOriginalDrawable);
					}
					else {
						mView.setBackgroundDrawable(mOriginalDrawable);
					}
				}
			}
		}

		public boolean isPlaying(){
			try {
				if( mMediaPlayer != null ){
					return mMediaPlayer.isPlaying();
				}
			}
			catch( Exception e ){
				e.printStackTrace();
			}
			return false;
		}
		
		public void stopAudio(){
			if(isPlaying()){
				try {
					mMediaPlayer.stop();
					mMediaPlayer.release();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				setFinishedDrawableInView();
				mMediaPlayer = null;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (mDialog != null) {
				mDialog.dismiss();
			}
			if (!isCancelled() && result) {
				playAudio();
			} else {
				Toast.makeText(CPDecksApplication.getContext(), R.string.network_error_or_audio_does_not_exist, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}

}
