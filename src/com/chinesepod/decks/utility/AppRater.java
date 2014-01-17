package com.chinesepod.decks.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.chinesepod.decks.R;

public class AppRater {
	private static String mAppTitle;

	private final static int DAYS_UNTIL_PROMPT = 3;
	private final static int LAUNCHES_UNTIL_PROMPT = 7;

	public static void app_launched(Context mContext) {
		String appTitle = mContext.getString(mContext.getResources().getIdentifier("app_title", "string", mContext.getPackageName()));
		if (appTitle == null) {
			appTitle = mContext.getString(mContext.getResources().getIdentifier("app_name", "string", mContext.getPackageName()));
		}
		app_launched(mContext, appTitle, DAYS_UNTIL_PROMPT, LAUNCHES_UNTIL_PROMPT);
	}

	public static void app_launched(Context mContext, int days, int launches) {
		String appTitle = "This App";
		try {
			appTitle = mContext.getString(mContext.getResources().getIdentifier("app_title", "string", mContext.getPackageName()));
		} catch (Exception e) {
			try {
				appTitle = mContext.getString(mContext.getResources().getIdentifier("app_name", "string", mContext.getPackageName()));
			} catch (Exception e2) {
			}
		}
		app_launched(mContext, appTitle, days, launches);
	}

	public static void app_launched(Context mContext, String appTitle) {
		app_launched(mContext, appTitle, DAYS_UNTIL_PROMPT, LAUNCHES_UNTIL_PROMPT);
	}

	public static void app_launched(Context mContext, String appTitle, int days, int launches) {
		if (appTitle != null) {
			mAppTitle = appTitle;
		} else {
		}

		SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
		if (prefs.getBoolean("dontshowagain", false)) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_firstlaunch", date_firstLaunch);
		}

		// Wait at least n days before opening
		if (launch_count >= launches) {
			if (System.currentTimeMillis() >= date_firstLaunch + (days * 24 * 60 * 60 * 1000)) {
				showRateDialog(mContext, editor);
			}
		}

		editor.commit();
	}

	public static void showRateDialog(final Context context, final SharedPreferences.Editor editor) {

		AlertDialog.Builder rateDialog = new AlertDialog.Builder(context);
		rateDialog.setTitle(R.string.rate_title);
		rateDialog.setMessage(R.string.rate_message);
		rateDialog.setPositiveButton(R.string.rate_rate_now, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
				if (editor != null) {
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}
				dialog.dismiss();
			}
		});
		rateDialog.setNeutralButton(R.string.rate_rate_later, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		rateDialog.setNegativeButton(R.string.rate_rate_never, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (editor != null) {
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}

			}
		});
		rateDialog.create().show();
	}
}