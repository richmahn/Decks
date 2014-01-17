package com.chinesepod.decks.utility.net;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.chinesepod.decks.CPDecksApplication;

public class NetworkSettings {

	public static final String LESSON_TASK_UPLOADING_TAG = "Filedata";
	protected String mCharset;
	protected String mClientPlatform;
	protected String mClientVersion;
	protected String mTestingHost;
	protected String mHost;
	protected String mApiVersion;
	private int mConnectTimeOut;
	private int mReadTimeOut;

	public NetworkSettings() {
		// Read values from resource file
		mConnectTimeOut = 60000;
		mReadTimeOut = 60000;
		mCharset = "UTF-8";
		mClientPlatform = "Android";
		PackageInfo pinfo;
		try {
			pinfo = CPDecksApplication
					.getContext()
					.getPackageManager()
					.getPackageInfo(
							CPDecksApplication.getContext().getPackageName(), 0);
			mClientVersion = pinfo.versionName;
		} catch (NameNotFoundException e) {
			mClientVersion = "Unknown version";
		}
		// mHost = "http://10.0.2.2"; // Local IP from Emulator

		mTestingHost = "https://prototype.chinesepod.com";
		// mHost = mTestingHost;
		mHost = "https://chinesepod.com";
		mApiVersion = "v0.5";
	}

	/**
	 * @return the mCharset
	 */
	public String getCharset() {
		return mCharset;
	}

	/**
	 * @param mCharset
	 *            the mCharset to set
	 */
	public void setCharset(String mCharset) {
		this.mCharset = mCharset;
	}

	/**
	 * @return the mClientPlatform
	 */
	public String getClientPlatform() {
		return mClientPlatform;
	}

	/**
	 * @param mClientPlatform
	 *            the mClientPlatform to set
	 */
	public void setClientPlatform(String mClientPlatform) {
		this.mClientPlatform = mClientPlatform;
	}

	/**
	 * @return the mClientVersion
	 */
	public String getClientVersion() {
		return mClientVersion;
	}

	/**
	 * @param mClientVersion
	 *            the mClientVersion to set
	 */
	public void setClientVersion(String mClientVersion) {
		this.mClientVersion = mClientVersion;
	}

	/**
	 * @return the mHost
	 */
	public String getHost() {
		return mHost;
	}

	/**
	 * @param mHost
	 *            the mHost to set
	 */
	public void setHost(String mHost) {
		this.mHost = mHost;
	}

	/**
	 * @return the mApiVersion
	 */
	public String getApiVersion() {
		return mApiVersion;
	}

	/**
	 * @param mApiVersion
	 *            the mApiVersion to set
	 */
	public void setApiVersion(String mApiVersion) {
		this.mApiVersion = mApiVersion;
	}

	public CharSequence getTestingHost() {
		return mTestingHost;
	}

	public int getConnectTimeOut() {
		return mConnectTimeOut;
	}

	public int getReadTimeOut() {
		return mReadTimeOut;
	}
}
