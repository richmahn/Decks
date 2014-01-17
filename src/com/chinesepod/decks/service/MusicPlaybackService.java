package com.chinesepod.decks.service;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author li.wenqin MusicPlaybackService
 */
public class MusicPlaybackService extends Service {

	private static final String TAG = "MusicPlaybackService";

	public static final String MUSIC_PLAYBACK_SERVICE = "com.chinesepod.decks.musicplaybackservice";
	public static final String PLAYER_PREPARE_END = "com.chinesepod.decks.musicplaybackservice.prepared";
	public static final String PLAY_COMPLETED = "com.chinesepod.decks.musicplaybackservice.playcompleted";
	public static final String PLAY_PAUSED = "com.chinesepod.decks.musicplaybackservice.playpaused";
	public static final String PLAY_RESTART = "com.chinesepod.decks.musicplaybackservice.playrestart";

	private String oldPath = null;
	private final IBinder mBinder = new LocalBinder();
	private MediaPlayer mMediaPlayer = null;

	MediaPlayer.OnCompletionListener mCompleteListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			broadcastEvent(PLAY_COMPLETED);
		}
	};

	MediaPlayer.OnPreparedListener mPrepareListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			broadcastEvent(PLAYER_PREPARE_END);
		}
	};

	private void broadcastEvent(String what) {
		Intent i = new Intent(what);
		sendBroadcast(i);
	}

	protected BroadcastReceiver callReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				// Log.v("TAG", "new outgoing number:" + this.getResultData());
				if (mMediaPlayer != null) {
					mMediaPlayer.pause();
				}
				broadcastEvent(PLAY_PAUSED);
			}
		}
	};

	private OnErrorListener mErrorListener = new OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
			mMediaPlayer.stop();
			return false;
		}
	};

	protected class TelephoneListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			// Log.v(TAG, "state:" + state);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				// CALL_STATE_IDLE;
				Log.i(TAG, "Chinesepod CALL_STATE_IDLE");
				broadcastEvent(PLAY_RESTART);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// CALL_STATE_OFFHOOK;
				Log.i(TAG, "Chinesepod CALL_STATE_OFFHOOK");
				if (mMediaPlayer != null) {
					mMediaPlayer.pause();
				}
				broadcastEvent(PLAY_PAUSED);
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				// CALL_STATE_RINGING
				Log.i(TAG, "Chinesepod CALL_STATE_RINGING");
				if (mMediaPlayer != null) {
					mMediaPlayer.pause();
				}
				broadcastEvent(PLAY_PAUSED);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(mPrepareListener);
		mMediaPlayer.setOnCompletionListener(mCompleteListener);
		mMediaPlayer.setOnErrorListener(mErrorListener );

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(callReceiver, filter);

		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyMgr.listen(new TelephoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
	}

	public class LocalBinder extends Binder {
		public MusicPlaybackService getService() {
			return MusicPlaybackService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(callReceiver);
	}

	/**
	 * @param path
	 *            sngleton media player
	 */
	public void setDataSource(String path) {
		if (path != null && oldPath != null) {
			if (oldPath.equals(path))
				return;
		}
		oldPath = path;
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
		} catch (IOException e) {
			return;
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	public void start() {
		mMediaPlayer.start();
	}

	public void stop() {
		mMediaPlayer.stop();
	}

	public void pause() {
		mMediaPlayer.pause();
	}

	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	public int getPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	public long seek(long whereto) {
		try {
			mMediaPlayer.seekTo((int) whereto);
		} catch (Exception e) {
		}
		return whereto;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}
