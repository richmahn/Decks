package com.chinesepod.decks.gcm;

import android.content.Context;

public class GCMBroadcastReceiver extends
        com.google.android.gcm.GCMBroadcastReceiver {
    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        return "com.chinesepod.decks.gcm.GCMIntentService";
    }
}