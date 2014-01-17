/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chinesepod.decks;

import static com.chinesepod.decks.utility.net.CPodGCMUtility.GCM_SENDER_ID;
import static com.chinesepod.decks.utility.net.CPodGCMUtility.displayMessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.utility.net.CPodGCMUtility;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(GCM_SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "REGISTERED");
        CPodGCMUtility.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, "UNREGISTERED");
        if (GCMRegistrar.isRegisteredOnServer(context) && CPDecksAccount.getInstance().isLoggedIn() ) {
            CPodGCMUtility.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // CPodGCMUtility when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getStringExtra(CPodGCMUtility.GCM_EXTRA_MESSAGE);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    private static void generateNotification(Context context, String message) {
    	if( ! CPDecksAccount.getInstance().isLoggedIn() ){
    		return;
    	}
    	int icon = R.drawable.icon;
    	long when = System.currentTimeMillis();
    	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	Notification notification = new Notification(icon, message, when);
    	String title = context.getString(R.string.app_name);
    	Intent notificationIntent = new Intent(context, CPDecksDashboardActivity.class);
    	// set intent so it does not start a new activity
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
    	notification.setLatestEventInfo(context, title, message, intent);
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	notification.defaults |= Notification.DEFAULT_SOUND;
    	notification.defaults |= Notification.DEFAULT_LIGHTS;
    	notification.defaults |= Notification.DEFAULT_VIBRATE;
    	notificationManager.notify(0, notification);
  	}
    
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = "Deleted messages";
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, "There was an error: "+errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, "There was a recoverable error: "+errorId);
        return super.onRecoverableError(context, errorId);
    }

}
