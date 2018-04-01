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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
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

import org.json.JSONException;
import org.json.JSONObject;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import static android.content.Context.MODE_PRIVATE;

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
    private String token, userName, fullName;
    private Animation mEnterAnimation, mExitAnimation;
    private ImageView ivNavigation, ivPosts;
    SharedPreferences languagepref;
    String language;
    private AccessToken accessToken;
    private String userType;

    public StartHomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sAnalytics = GoogleAnalytics.getInstance(getActivity());
        InitGoogleAnalytics();
        SendScreenNameGoogleAnalytics();
        accessToken = AccessToken.getCurrentAccessToken();
        languagepref = getActivity().getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");

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
            userType = mypreference.getString("userType", null);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View startHomeFragmentView = inflater.inflate(R.layout.fragment_start_home, container, false);

        ivNavigation = this.getActivity().findViewById(R.id.ivmap);
        ivPosts = this.getActivity().findViewById(R.id.ivguide);
        IV_message = (ImageButton) startHomeFragmentView.findViewById(R.id.IV_message);
        ivAddUserPhoto = (ImageView) startHomeFragmentView.findViewById(R.id.ivAddUserPhoto);

        if (userType != "FB") {
            getUserImage();

        } else {
            getUserDetails();

        }
        tvusername = (TextView) startHomeFragmentView.findViewById(R.id.tvusername);
        tvusername.setText(fullName);

        IV_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendEventGoogleAnalytics("StartHomeFragment", "CallForHelp", "Button clicked");
                startHomeTabFragment();
            }
        });
        hello = (TextView) startHomeFragmentView.findViewById(R.id.hello);
        And = (TextView) startHomeFragmentView.findViewById(R.id.And);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean previouslyStarted = prefs.getBoolean("home_activity_previously_started", false);
        if (!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("home_activity_previously_started", Boolean.TRUE);
            edit.commit();
            if (language.equals("ar")) {
                runOverlay_ContinueMethod_ar();
            } else {
                runOverlay_ContinueMethod_en();
            }
        } else {
        }
        return startHomeFragmentView;
    }

    private void runOverlay_ContinueMethod_en() {
        // the return handler is used to manipulate the cleanup of all the tutorial elements
        ChainTourGuide tourGuide1 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle(getString(R.string.tourguide_tip_home_activity_chat_title))
                        .setDescription(getString(R.string.tourguide_tip_home_activity_chat))
                        .setGravity(Gravity.BOTTOM)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(IV_message);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle(getString(R.string.tourguide_tip_home_activity_navigation_tab_title))
                        .setDescription(getString(R.string.tourguide_tip_home_activity_navigation_tab))
                        .setGravity(Gravity.TOP | Gravity.LEFT)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(ivNavigation);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                                .setTitle(getString(R.string.tourguide_tip_home_activity_posts_tab_title))
                                .setDescription(getString(R.string.tourguide_tip_home_activity_posts_tab))
                                .setGravity(Gravity.TOP | Gravity.LEFT)
//                        .setBackgroundColor(Color.parseColor("#c0392b"))
                )
                .setOverlay(new Overlay()
//                        .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                )
                .playLater(ivPosts);
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.OVERLAY)
                .build();


        ChainTourGuide.init(getActivity()).playInSequence(sequence);
    }

    private void runOverlay_ContinueMethod_ar() {
        // the return handler is used to manipulate the cleanup of all the tutorial elements
        ChainTourGuide tourGuide1 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle(getString(R.string.tourguide_tip_home_activity_chat_title))
                        .setDescription(getString(R.string.tourguide_tip_home_activity_chat))
                        .setGravity(Gravity.TOP | Gravity.AXIS_PULL_AFTER)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(IV_message);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle(getString(R.string.tourguide_tip_home_activity_navigation_tab_title))
                        .setDescription(getString(R.string.tourguide_tip_home_activity_navigation_tab))
                        .setGravity(Gravity.TOP | Gravity.RIGHT)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(ivNavigation);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                                .setTitle(getString(R.string.tourguide_tip_home_activity_posts_tab_title))
                                .setDescription(getString(R.string.tourguide_tip_home_activity_posts_tab))
                                .setGravity(Gravity.TOP | Gravity.AXIS_PULL_BEFORE)
//                        .setBackgroundColor(Color.parseColor("#c0392b"))
                )
                .setOverlay(new Overlay()
//                        .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                )
                .playLater(ivPosts);
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.OVERLAY)
                .build();


        ChainTourGuide.init(getActivity()).playInSequence(sequence);
    }

    private void startHomeTabFragment() {
        HomeTab homeTabFragment = new HomeTab();
        this.getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.rlFragments, homeTabFragment)
                .commit();
    }


    private void InitGoogleAnalytics() {
        mGoogleHelper = new GoogleAnalyticsHelper();
        mGoogleHelper.init(getContext());
    }

    private void SendScreenNameGoogleAnalytics() {

        mGoogleHelper.SendScreenNameGoogleAnalytics("StartHomeFragment", getContext());
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {

        mGoogleHelper.SendEventGoogleAnalytics(getContext(), iCategoryId, iActionId, iLabelId);
    }

    private void getUserImage() {

        Picasso.with(getActivity().getApplication().getApplicationContext()).load("https://streetpal.org/api/user/" + userName + "/photo")
                .placeholder(R.drawable.ic_default_user).error(R.drawable.ic_default_user)
                .into(ivAddUserPhoto);
    }

    protected void getUserDetails() {


        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
                            JSONObject profile_pic_data = new JSONObject(object.get("picture").toString());
                            JSONObject profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                            Picasso.with(getContext()).load(profile_pic_url.getString("url"))
                                    .into(ivAddUserPhoto);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
        request.setParameters(parameters);
        request.executeAsync();


        //////////

    }
}
