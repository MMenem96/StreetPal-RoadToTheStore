package com.sharekeg.streetpal.homefragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.googleanalytics.GoogleAnalyticsHelper;
import com.squareup.picasso.Picasso;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by MMenem on 8/21/2017.
 */

public class StartHomeFragment extends Fragment {

    private ImageButton IV_message;
    private TextView hello, And;
    private Context context;
    private GoogleAnalyticsHelper mGoogleHelper;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    private ImageView ivAddUserPhoto;
    private TextView tvusername;
    private String token,userName,fullName;
    private Animation mEnterAnimation, mExitAnimation;
    TourGuide mTourGuide;


    public StartHomeFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sAnalytics = GoogleAnalytics.getInstance(getActivity());
        InitGoogleAnalytics();
        SendScreenNameGoogleAnalytics();
        /* setup enter and exit animation */
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
        context = getContext();

        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(context);
        if (mypreference.getBoolean("loggedIn", false)) {
            token = mypreference.getString("token", null);
            userName = mypreference.getString("myUserName", "User Name");
            fullName = mypreference.getString("myFullName", "Full Name");



        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View startHomeFragmentView = inflater.inflate(R.layout.fragment_start_home, container, false);


        IV_message = (ImageButton) startHomeFragmentView.findViewById(R.id.IV_message);
        ivAddUserPhoto = (ImageView)startHomeFragmentView.findViewById(R.id.ivAddUserPhoto);
        getUserImage();
        tvusername = (TextView) startHomeFragmentView.findViewById(R.id.tvusername);
        tvusername.setText(fullName);

        IV_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTourGuide.cleanUp();
                SendEventGoogleAnalytics("StartHomeFragment","CallForHelp","Button clicked" );
                startHomeTabFragment();
            }
        });
        hello = (TextView) startHomeFragmentView.findViewById(R.id.hello);
        And = (TextView) startHomeFragmentView.findViewById(R.id.And);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean previouslyStarted = prefs.getBoolean("home_tab_previously_started", false);
        if (!previouslyStarted)
        {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("home_tab_previously_started", Boolean.TRUE);
            edit.commit();
            mTourGuide = TourGuide.init(getActivity()).with(TourGuide.Technique.CLICK)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip()
                            .setTitle("Hey!")
                            .setDescription("I'm the top fellow")
                            .setGravity(Gravity.RIGHT)
                    )
                    .setOverlay(new Overlay()
                            .setEnterAnimation(mEnterAnimation)
                            .setExitAnimation(mExitAnimation)
                    );
//            runOverlay_ContinueMethod();
        } else {
        }
        return startHomeFragmentView;
    }

    private void startHomeTabFragment() {
        HomeTab homeTabFragment = new HomeTab();
        this.getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.rlFragments, homeTabFragment)
                .commit();
    }


    private void InitGoogleAnalytics()
    {
        mGoogleHelper = new GoogleAnalyticsHelper();
        mGoogleHelper.init(getContext());
    }

    private void SendScreenNameGoogleAnalytics()
    {

        mGoogleHelper.SendScreenNameGoogleAnalytics("StartHomeFragment",getContext());
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId,    String iLabelId)
    {

        mGoogleHelper.SendEventGoogleAnalytics(getContext(),iCategoryId,iActionId,iLabelId );
    }

    private void getUserImage() {

        Picasso.with(getActivity().getApplication().getApplicationContext()).load("https://streetpal.org/api/user/" + userName + "/photo")
                .placeholder(R.drawable.ic_default_user).error(R.drawable.ic_default_user)
                .into(ivAddUserPhoto);
    }

}
