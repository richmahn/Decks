package com.chinesepod.decks.service;

import java.util.HashMap;

import android.app.Notification;
import android.widget.RemoteViews;

import com.chinesepod.decks.CPDecksDashboardActivity;
import com.chinesepod.decks.R;

public class CPodFileDownloadService extends FileDownloadService {

	@Override
	protected Class<?> getIntentForLatestInfo() {
		return CPDecksDashboardActivity.class;
	}

	@Override
	protected int getNotificationFlag() {
		return Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS;
	}

	@Override
	protected HashMap<String, String> getTargetFiles() {
		HashMap<String, String> files = new HashMap<String, String>();
		files.put("http://s3contents.chinesepod.com/1791/ead59ea60b8bbde0e70862d3a21389242aa5de12/mp3/chinesepod_B1791pb.mp3", "/sdcard/1.mp3");
		return files;
	}

	@Override
	protected void onFinishDownload(int successCount, HashMap<String, String> failedFiles) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getNotificationIcon() {
		return android.R.drawable.ic_dialog_info;
	}

	@Override
	protected int getConnectTimeout() {
		return 10000;
	}

	@Override
	protected int getReadTimeout() {
		return 10000;
	}

	protected RemoteViews getProgressView(int currentNumFile, int totalNumFiles, int currentReceivedBytes, int totalNumBytes) {
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.filedownload_progress);
		contentView.setImageViewResource(R.id.filedownload_image, R.drawable.icon);
		contentView.setTextViewText(R.id.filedownload_text, String.format("Progress (%d / %d)", currentNumFile, totalNumFiles));
		contentView.setProgressBar(R.id.filedownload_progress, 100, 100 * currentReceivedBytes / totalNumBytes, false);
		return contentView;
	}
}
