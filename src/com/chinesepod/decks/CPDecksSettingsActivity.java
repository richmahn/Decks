/**
 * Copyright (C) 2013 ChinesePod 
 *
 * Author: Richard Mahn
 * 
 * Settings Activity, this is to be expanded when we allow users to login as their ChinesePod user account and sync their decks
 */
package com.chinesepod.decks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.logic.CPDecksResponse;
import com.chinesepod.decks.settings.AvatarPreference;
import com.chinesepod.decks.settings.BioPreference;
import com.chinesepod.decks.settings.FullnamePreference;
import com.chinesepod.decks.settings.UsernamePreference;
import com.chinesepod.decks.utility.CPodAccountManager;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.chinesepod.decks.utility.FileOperationHelper;
import com.chinesepod.decks.utility.net.CPodAccountNetworkUtilityModel;
import com.chinesepod.decks.utility.net.NetworkSettings;

public class CPDecksSettingsActivity extends PreferenceActivity {
	public static final int PICK_FROM_CAMERA = 0;
	public static final int PICK_FROM_FILE = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int CROP_FROM_FILE = 3;
	private Uri mImageCaptureUri;
	private String mAvatarDir;
	private CPDecksAccount account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_settings_headers);
		// Add a button to the header list.
		if (CPDecksUtility.isTv()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else if (CPDecksUtility.isTablet(this)) {
			String orientation = CPDecksAccount.getInstance().getOrientation();
			if (orientation.equals("portrait"))
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			else if (orientation.equals("landscape"))
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}
		if (hasHeaders()) {
			LinearLayout ll = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			ll.setOrientation(LinearLayout.VERTICAL);
			params.setMargins(20, 10, 20, 20);
			ll.setLayoutParams(params);
			// Contact us
			TextView contact = new TextView(this);
			LinearLayout.LayoutParams contactParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			contactParams.setMargins(30, 30, 30, 30);
			contact.setLayoutParams(contactParams);
			contact.setText(getString(R.string.settings_contact_us));
			Linkify.addLinks(contact, Linkify.EMAIL_ADDRESSES);
			ll.addView(contact);
			// Version name
			try {
				TextView version = new TextView(this);
				LinearLayout.LayoutParams versionParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				versionParams.setMargins(30, 0, 30, 0);
				version.setLayoutParams(versionParams);
				PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				version.setText(getString(R.string.version) + "  " + pinfo.versionName);
				ll.addView(version);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			// help button
			Button helpButton = new Button(this);
			helpButton.setLayoutParams(params);
			helpButton.setOnClickListener(helpButtonOnClickListener);
			helpButton.setText(R.string.help_button_title);
			helpButton.setBackgroundResource(R.drawable.black_button);
			helpButton.setTextColor(getResources().getColorStateList(R.drawable.black_button_text_color));
			helpButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			ll.addView(helpButton);

			setListFooter(ll);
		}
		mAvatarDir = FileOperationHelper.USER_AVATAR_DIR;
		if (FileOperationHelper.checkSaveDir(mAvatarDir)) {
			File file = new File(mAvatarDir + "/tmp_avatar_" + String.valueOf(System.currentTimeMillis())
					+ ".jpg");
			mImageCaptureUri = Uri.fromFile(file);
			account = CPDecksAccount.getInstance();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		final String showFragment = getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT);
		if (showFragment != null && mHeaders != null) {
			for (final Header header : mHeaders) {
				if (showFragment.equals(header.fragment)) {
					switchToHeader(header);
					break;
				}
			}
		}
	}

	public Uri getImageCaptureUri() {
		return mImageCaptureUri;
	}

	OnClickListener helpButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(CPDecksSettingsActivity.this, CPDecksSettingsHelpActivity.class);
			CPDecksSettingsActivity.this.startActivity(i);
		}
	};

	public boolean isInProgress;
	public ProgressDialog loadingDialog;

	public UsernamePreference usernamePref;
	public FullnamePreference fullnamePref;
	public BioPreference bioPref;
	public AvatarPreference avatarPref;

	// Notification preferences
	public CheckBoxPreference newLessonNotePref;
	public CheckBoxPreference newShowNotePref;
	public CheckBoxPreference newsletterNotePref;
	public CheckBoxPreference generalNotePref;
	public List<Header> mHeaders;
	
	/**
	 * Populate the activity with the top-level headers.
	 */
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
		mHeaders = target;
	}
	
	public void changeUsername(String newValue) {
		ChangeSettingsTask task = new ChangeSettingsTask("username");
		task.execute(newValue);
	}

	public void changeFullname(String newValue) {
		ChangeSettingsTask task = new ChangeSettingsTask("name");
		task.execute(newValue);

	}

	public void uploadAvatar() {
		UploadAvatarTask task = new UploadAvatarTask();
		task.execute();

	}

	public void changeBio(String newValue) {
		ChangeSettingsTask task = new ChangeSettingsTask("bio");
		task.execute(newValue);
	}

	public void changePassword(String oldPassword, String newPassword) {
		ChangeSettingsTask task = new ChangeSettingsTask("password");
		task.execute(oldPassword, newPassword);
	}

	public void changeNotification(String key, Boolean newValue) {
		ChangeSettingsTask task = new ChangeSettingsTask("notification");
		task.execute(key, newValue.toString());
	}

	public void changeFlashcardWhatToShow(String key, Boolean newValue) {
//		ChangeSettingsTask task = new ChangeSettingsTask("notification");
//		task.execute(key, newValue.toString());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		Bitmap bitmap = null;
		File file = null;
		switch (requestCode) {
		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();
			doCrop(CROP_FROM_FILE);
			break;
		case PICK_FROM_CAMERA:
			doCrop(CROP_FROM_CAMERA);
			break;
		case CROP_FROM_FILE:
			String avatarDir = FileOperationHelper.USER_AVATAR_DIR;
			if (FileOperationHelper.checkSaveDir(avatarDir)) {
				file = new File(avatarDir + "/tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
				mImageCaptureUri = Uri.fromFile(file);
			}
		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();
			if (extras != null) {
				try {
					bitmap = extras.getParcelable("data");
					file = new File(mImageCaptureUri.getPath());
					FileOutputStream fOut = new FileOutputStream(file);

					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
					ImageView imageView = avatarPref.getAvatarImageView();
					imageView.setImageBitmap(bitmap);
					avatarPref.setAvatarChangeButtonVisible(false);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

	private void doCrop(int requestCode) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

			return;
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
	}

	public String getRealPathFromURI(Uri contentUri) {
		String returnVal = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		if (cursor != null && cursor.moveToFirst() ){
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			returnVal = cursor.getString(column_index);
			cursor.close();
		}
		return returnVal;
	}

	/**
	 * This fragment shows the general preferences.
	 */
	public class GeneralSettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.general_preferences);

			usernamePref = (UsernamePreference) findPreference("username");
			fullnamePref = (FullnamePreference) findPreference("fullname");
			bioPref = (BioPreference) findPreference("bio");
			avatarPref = (AvatarPreference) findPreference("avatar");

			ListPreference orientationList = ((ListPreference) findPreference("screen_orientation"));
			if (orientationList != null) {
				orientationList.setValue(CPDecksAccount.getInstance().getOrientation());
				orientationList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference, Object val) {
						CPDecksAccount.getInstance().setOrientation((String) val);
						return true;
					}
				});
			} else {
				CPDecksAccount.getInstance().setOrientation("landscape");

			}
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setAvatar();
		}

	}

	/**
	 * This fragment shows the notification preferences.
	 */
	public class NotificationSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.notification_preferences);

			newLessonNotePref = (CheckBoxPreference) findPreference("new_lesson_notification");
			newShowNotePref = (CheckBoxPreference) findPreference("new_show_notification");
			newsletterNotePref = (CheckBoxPreference) findPreference("newsletter_notification");
			generalNotePref = (CheckBoxPreference) findPreference("general_notification");

			CPDecksSettingsActivity.this.newLessonNotePref.setOnPreferenceChangeListener(this);
			CPDecksSettingsActivity.this.newShowNotePref.setOnPreferenceChangeListener(this);
			CPDecksSettingsActivity.this.newsletterNotePref.setOnPreferenceChangeListener(this);
			CPDecksSettingsActivity.this.generalNotePref.setOnPreferenceChangeListener(this);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			CPDecksSettingsActivity.this.changeNotification(preference.getKey(), (Boolean) newValue);
			return false;
		}

	}

	public ProgressDialog createLoadingDialog(int messageId) {
		ProgressDialog loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage(getString(messageId));
		loadingDialog.setIndeterminate(true);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setCancelable(true);
		return loadingDialog;
	}

	public void setAvatar() {
		if (avatarPref != null) {
			new DownloadAvatarTask().execute();
		}
	}

	protected class ChangeSettingsTask extends AsyncTask<String, Integer, CPDecksResponse> implements OnCancelListener {
		Dialog loadingDialog;
		String param;
		String newValue;
		EditTextPreference pref;
		CheckBoxPreference notificationPreference = null;

		public ChangeSettingsTask(String param) {
			this.param = param;
		}

		@Override
		protected CPDecksResponse doInBackground(String... params) {
			if (params.length == 0)
				return null;
			isInProgress = true;

			// Cancel notification
			boolean isConn = CPDecksUtility.isConnectionNet(CPDecksSettingsActivity.this);
			CPDecksResponse response = null;
			if (isConn) {
				if (param.equals("username")) {
					pref = usernamePref;
					newValue = params[0];
					response = CPodAccountManager.changeUsername(account, params[0]);

					if (response.isSuccessfull()) {
						account.setUsername(params[0]);
					}
				} else if (param.equals("name")) {
					pref = fullnamePref;
					newValue = params[0];
					response = CPodAccountManager.changeFullname(account, params[0]);
					if (response.isSuccessfull()) {
						account.setName(params[0]);
					}
				} else if (param.equals("bio")) {
					pref = bioPref;
					newValue = params[0];
					response = CPodAccountManager.changeBio(account, params[0]);
					if (response.isSuccessfull()) {
						account.setBio(params[0]);
					}
				} else if (param.equals("password")) {
					String oldPassword = params[0];
					String newPassword = params[1];
					response = CPodAccountManager.changePassword(account, oldPassword, newPassword);

				} else if (param.equals("notification")) {
					String key = params[0];
					String value = params[1];
					response = CPodAccountManager.changeNotificationLevel(account, key, value);
					notificationPreference = getNotificationPreference(response.isSuccessfull(), key);
				}

			}
			isInProgress = false;
			return response;
		}

		private CheckBoxPreference getNotificationPreference(boolean success, String key) {
			if (success) {
				if (key.equals("new_lesson_notification")) {
					return newLessonNotePref;
				} else if (key.equals("new_show_notification")) {
					return newShowNotePref;
				} else if (key.equals("newsletter_notification")) {
					return newsletterNotePref;
				} else if (key.equals("general_notification")) {
					return generalNotePref;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(CPDecksResponse result) {

			if (notificationPreference != null) {

				if (result.getObject().equals("off"))
					notificationPreference.setChecked(false);
				else if (result.getObject().equals("on"))
					notificationPreference.setChecked(true);
				Toast.makeText(CPDecksSettingsActivity.this, R.string.notification_changed_successfully, Toast.LENGTH_SHORT).show();
			} else if (result != null && result.isSuccessfull()) {
				if (pref != null) {
					pref.setText(newValue);
					pref.setSummary(newValue + " ");
				}
				Toast.makeText(CPDecksSettingsActivity.this, R.string.notification_changed_successfully, Toast.LENGTH_SHORT).show();

			} else if (result != null && !result.isSuccessfull()) {

				Toast.makeText(CPDecksSettingsActivity.this, result.getError().getErrorExpalanation(), Toast.LENGTH_SHORT).show();

			} else {

				Toast.makeText(CPDecksSettingsActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();

			}

			loadingDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			loadingDialog = createLoadingDialog(R.string.settings_changing_profile_settings);
			loadingDialog.setOnCancelListener(this);
			loadingDialog.show();

		}

		public void onCancel(DialogInterface di) {
			if (!isCancelled()) {
				cancel(true);
			}
		}
	}

	protected class UploadAvatarTask extends AsyncTask<String, Integer, CPDecksResponse> implements OnCancelListener {

		@Override
		protected CPDecksResponse doInBackground(String... params) {

			CPDecksResponse response = CPodAccountManager.uploadAvatar(account, mImageCaptureUri.getPath());
			return response;
		}

		@Override
		protected void onPreExecute() {
			loadingDialog = createLoadingDialog(R.string.settings_uploading_avatar);
			loadingDialog.setOnCancelListener(this);
			loadingDialog.show();
		}

		@Override
		protected void onPostExecute(CPDecksResponse response) {
			if (response.isSuccessfull() && response.getObject() != null && ((JSONObject)response.getObject()).has("avatar_url") ){
				try {
					NetworkSettings networkSettings = new NetworkSettings();
					account.setAvatarUrl(networkSettings.getHost()+((JSONObject)response.getObject()).getString("avatar_url"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				avatarPref.setAvatarChangeButtonVisible(true);
				Toast.makeText(CPDecksSettingsActivity.this, R.string.uploaded_successfully, Toast.LENGTH_SHORT).show();
			} else if (response.getError().isNetworkError()) {
				Toast.makeText(CPDecksSettingsActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(CPDecksSettingsActivity.this, R.string.uploaded_unsuccessfully, Toast.LENGTH_SHORT).show();
			}

			loadingDialog.dismiss();
		}

		@Override
		public void onCancel(DialogInterface arg0) {
			if (!isCancelled()) {
				cancel(true);
			}
		}
	}

	protected class DownloadAvatarTask extends AsyncTask<String, Integer, Boolean> {

		private Bitmap bitmap;

		@Override
		protected Boolean doInBackground(String... params) {
			Boolean result = false;
			if (account != null && account.getAvatarUrl() != null && !account.getAvatarUrl().contains("default160.jpg")) {
				File file = null;
				if (account.getAvatarFile() != null && !account.getAvatarFile().isEmpty())
					file = new File(account.getAvatarFile());
				if (file == null || !file.exists()) {
					CPodAccountNetworkUtilityModel an = new CPodAccountNetworkUtilityModel();
					bitmap = an.downloadAvatar(account);
					if (bitmap != null)
						result = true;
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				avatarPref.getAvatarImageView().setImageBitmap(bitmap);
			}
		}
	}
}
