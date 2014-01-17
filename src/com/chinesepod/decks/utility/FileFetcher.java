/*
 * Copyright (C) 2008 The ChinesePod Dictionary Project
 *
 * Praxis Language Ltd.	http://praxislanguage.com
 * Website: ChinesePod	http://chinesepod.com
 * Author: liwenqin
 * Email: yiizzhoou@gmail.com
 * 
 */
package com.chinesepod.decks.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

//import android.util.Log;

public class FileFetcher extends Thread {
	// private static final String TAG = "FileFetcher";
	private String sURL; // File URL
	public long nStartPos; // File Snippet Start Position
	public long nEndPos; // File Snippet End Position
	public long nThreadID; // Thread's ID
	public boolean bDownOver = false; // Downing is over
	private boolean bStop = false; // Stop identical
	private FileAccess fileAccess = null; // File Access interface
	private HttpURLConnection httpConnection;
	private InputStream input;

	public FileFetcher(String sURL, String sName, long nStart, long nEnd, long id) throws IOException {
		this.sURL = sURL;
		this.nStartPos = nStart;
		this.nEndPos = nEnd;
		nThreadID = id;
		fileAccess = new FileAccess(sName, nStartPos);
	}

	public void run() {
		URL url = null;
		try {
			url = new URL(sURL);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		String rangeBytes;
		while (nStartPos < nEndPos && !bStop) {
			try {
				// HttpURLConnection.setFollowRedirects(true);
				httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setRequestMethod("GET");
				// httpConnection.addRequestProperty("User-Agent",
				// "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11");
				// httpConnection.addRequestProperty("Host",
				// "s3.amazonaws.com"); Why was it hard coded???
				Date now = new Date();
				// Log.v(TAG, "Date:"+now.toString());
				httpConnection.addRequestProperty("Date", now.toString());
				rangeBytes = "bytes=" + nStartPos + "-" + nEndPos;
				httpConnection.addRequestProperty("Range", rangeBytes);
				// Log.v(TAG, "Range:"+rangeBytes);
				input = httpConnection.getInputStream();
				// Log.v(TAG, "getURL:"+httpConnection.getURL());
				// Log.v(TAG, "ResponseCode:" +
				// httpConnection.getResponseCode());
				if (httpConnection.getResponseCode() == 200) {
					this.nStartPos = 0;
					// Log.v(TAG, "this.nStartPos:" + nStartPos);
					fileAccess.seekTo(nStartPos);
				}

				byte[] b = new byte[1024];
				int nRead;
				while ((nRead = input.read(b, 0, 1024)) > 0 && nStartPos < nEndPos && !bStop) {
					nStartPos += fileAccess.write(b, 0, nRead);
				}

				if (nStartPos >= nEndPos) {
					// Log.v(TAG, "Thread " + nThreadID + " is over!");
					bDownOver = true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (fileAccess != null) {
			fileAccess.close();
			fileAccess = null;
		}
	}

	// log response header
	public void logResponseHead(HttpURLConnection con) {
		for (int i = 1;; i++) {
			String header = con.getHeaderFieldKey(i);
			if (header != null) {
				// Log.v(TAG,header + " : " + con.getHeaderField(header));
			} else {
				break;
			}
		}
	}

	public void fetchStop() {
		bStop = true;
	}
}