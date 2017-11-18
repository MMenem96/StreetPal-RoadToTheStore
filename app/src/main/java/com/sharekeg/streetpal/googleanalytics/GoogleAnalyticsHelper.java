package com.sharekeg.streetpal.googleanalytics;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sharekeg.streetpal.R;


public class GoogleAnalyticsHelper {


    private Tracker mGaTracker = null;
    private static String TAG = "GoogleAnalyticsHelper";
    private static final String PROPERTY_ID = "UA-109833790-1";

    public GoogleAnalyticsHelper() {

    }

    public void init(Context ctx) {
        try {

            if (mGaTracker == null && ctx != null) {
                mGaTracker = GoogleAnalytics.getInstance(ctx).newTracker("UA-109833790-1");
            }
        } catch (Exception e) {
            Log.d(GoogleAnalyticsHelper.TAG, "init, e=" + e);
        }
    }

    public void SendScreenNameGoogleAnalytics(String screenName, Context iCtx) {
        init(iCtx);

        mGaTracker.setScreenName(screenName);
        mGaTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }


    public void SendEventGoogleAnalytics(Context iCtx, String iCategoryId, String iActionId, String iLabelId) {
        init(iCtx);

        // Build and send an Event.
        mGaTracker.send(new HitBuilders.EventBuilder()
                .setCategory(iCategoryId)
                .setAction(iActionId)
                .setLabel(iLabelId)
                .build());

    }
}