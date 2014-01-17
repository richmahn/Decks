/*
 * Copyright (C) 2008 The ChinesePod Dictionary Project
 *
 * Praxis Language Ltd.	http://praxislanguage.com
 * Website: ChinesePod	http://chinesepod.com
 * Author: liwenqin
 * Email: yiizzhoou@gmail.com
 * 
 */

package com.chinesepod.decks.utility.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.chinesepod.decks.utility.FileOperationHelper;

public class HttpConnectionHelper {
	private static final String TAG = "HttpConnectionHelper";

	public static boolean saveFile(String urlParam, String fileFullPath) throws IOException {
		if( fileFullPath == null || fileFullPath.isEmpty() ){
			return false;
		}
		return saveFile(urlParam, new File(fileFullPath));
	}

	public static boolean saveFile(String urlParam, File file) throws IOException {
		if( file == null ){
			return false;
		}
		return saveFile(urlParam, file.getParent(), file.getName() );
	}
	
	public static boolean saveFile(String urlParam, String saveDir, String filename) throws IOException {
		// Log.v(TAG, "saveDir:"+saveDir);
		String filePath = saveDir + File.separator + filename;

		if (FileOperationHelper.checkFile(filePath)) {
			return true;
		}
		if (!FileOperationHelper.checkSaveDir(saveDir)) {
			return false;
		}
		boolean isDownload = false;
		InputStream is = null;
		FileOutputStream writer = null;
		HttpURLConnection huc = null;
		URL url = new URL(urlParam);
		huc = (HttpURLConnection) url.openConnection();
		huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11");
		HttpURLConnection.setFollowRedirects(true);
		huc.setDoInput(true);
		huc.setConnectTimeout(60000);
		huc.connect();
		is = huc.getInputStream();
		writer = new FileOutputStream(filePath);
		byte[] bytes = new byte[1024];
		int len = -1;
		while ((len = is.read(bytes)) > 0) {
			writer.write(bytes, 0, len);
		}
		writer.flush();
		writer.close();
		is.close();
		huc.disconnect();
		isDownload = true;
		return isDownload;
	}

	public static boolean isProperUrl(String audioUrl) {
		try {
			new URL(audioUrl);
			return true;
		} catch (MalformedURLException e) {

		}
		return false;
	}
	
	public static boolean isProperFileUrl(String audioUrl) {
		if( audioUrl == null || audioUrl.isEmpty() ){
			return false;
		}
		try {
			new URL(audioUrl);
			return audioUrl.matches("^.*/[^\\/]+\\.[^\\/]+$"); // make sure it is a file with an extension, like http://chinesepod.com/mp3/123123123.mp3
		} catch (MalformedURLException e) {

		}
		return false;
	}
}