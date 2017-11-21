package com.sharekeg.streetpal.homefragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PointF;

import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.chatcomponents.ChatAdapter;
import com.sharekeg.streetpal.chatcomponents.ChatBlock;
import com.sharekeg.streetpal.chatcomponents.ChatMessage;
import com.sharekeg.streetpal.chatcomponents.OnUserStatusChangeListener;
import com.sharekeg.streetpal.chatcomponents.UserGuide;
import com.sharekeg.streetpal.chatcomponents.UserOptions;
import com.sharekeg.streetpal.googleanalytics.GoogleAnalyticsHelper;
import com.sharekeg.streetpal.locationservice.LocationProvider;
import com.sharekeg.streetpal.safeplace.SafePlaceActivity;
import com.sharekeg.streetpal.safeplace.UserSituation;
import com.sharekeg.streetpal.sms.SmsDeliveredReceiver;
import com.sharekeg.streetpal.sms.SmsSentReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by MMenem on 8/2/2017.
 */

public class StreetPalGuide extends Fragment implements View.OnClickListener, OnUserStatusChangeListener, LocationProvider.LocationCallback {


    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 95;
    private static final int REQUEST_PHONE_CALL = 189;
    private static final int SEND_SMS_PERMISSIONS_REQUEST = 1;
    public LinearLayoutManager mLinearLayoutManager;
    UserGuide userGuide;
    ChatAdapter adapter;
    List<ChatMessage> chatMessages = new ArrayList<>();
    int positiveButtonID, negativeButtonID, neutralButtonID;
    RecyclerView guideChatList;
    RelativeLayout homeActivity;
    private TextView firstChoice, secondChoice, thirdChoice, homeActivityTitle, homeActivityTitleTyping;
    private LinearLayout bottomButtonsLayout;
    private LinearLayout UpperBarlayoutId;
    private MyCustomLayoutManager mLayoutManager;
    private String trustedContactEmail, trustedContactNumber, trustedContactName;
    private Retrofit retrofitforauthentication;
    private String token, userCurrentFullName;
    private Intent telIntent;
    private double lat, lng;
    private LocationProvider mLocationProvider;
    private Context context;

    private GoogleAnalyticsHelper mGoogleHelper;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    public StreetPalGuide() {
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View streetPalGuideView = inflater.inflate(R.layout.fragment_street_pal_guide, container, false);


        bottomButtonsLayout = (LinearLayout) streetPalGuideView.findViewById(R.id.rlChoices);

        //Initialize the recycle view
        guideChatList = (RecyclerView) streetPalGuideView.findViewById(R.id.chatList);
        //   guideChatList.setHasFixedSize(true);

        //Access the items of Parent Activity
        homeActivity = (RelativeLayout) getActivity().findViewById(R.id.activity_home);
        homeActivityTitle = (TextView) homeActivity.findViewById(R.id.tvHomeTitle);
        homeActivityTitleTyping = (TextView) homeActivity.findViewById(R.id.tvHomeTitle_typing);
        UpperBarlayoutId = (LinearLayout) getActivity().findViewById(R.id.UpperBarlayoutId);

//        ViewFilter.getInstance(getContext())
//                .setRenderer(new BlurRenderer(16))
//                .applyFilterOnView(infoLayout, homeActivity);


        //find the TextViews (Choices)
        firstChoice = (TextView) streetPalGuideView.findViewById(R.id.user_first_choice);
        secondChoice = (TextView) streetPalGuideView.findViewById(R.id.user_second_choice);
        thirdChoice = (TextView) streetPalGuideView.findViewById(R.id.user_third_choice);


        firstChoice.setOnClickListener(this);
        secondChoice.setOnClickListener(this);
        thirdChoice.setOnClickListener(this);


        mLayoutManager = new MyCustomLayoutManager(getActivity());
//        guideChatList.setLayoutManager(mLayoutManager);


        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setStackFromEnd(true);
        guideChatList.setLayoutManager(mLinearLayoutManager);

        userGuide = new UserGuide(this);
        int id = 0;
        if (getArguments() != null) {
            id = getArguments().getInt("userCase");
        }
        ChatBlock chatBlock = userGuide.guideUserToSafety(id, getContext());

//        for (int i = 0; i < chatBlock.getChatMessages().size(); i++) {
//            chatMessages.add(chatBlock.getChatMessages().get(i));
//        }
        manageOptionsDisplay(chatBlock.getUserOptions());
        setButtonsIDs(chatBlock.getUserOptions());
        // Initialize the adapter for the messages

        adapter = new ChatAdapter(getActivity(), chatMessages, this);


        guideChatList.setAdapter(adapter);
        bottomButtonsLayout.setVisibility(View.GONE);
        displayNewMessage(chatBlock.getChatMessages());

        return streetPalGuideView;
    }

    @Override
    public void onClick(View v) {
        bottomButtonsLayout.setVisibility(View.GONE);
        Log.i("Button_ID_selected", String.valueOf(positiveButtonID));
        if (v == firstChoice) {
            if (positiveButtonID == UserGuide.TERMINATE_CHAT) {
                //Close chat

                HomeTab homeTab = new HomeTab();

                this.getFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.rlFragments, homeTab)
                        .commit();

            } else {
                ChatMessage userMessage = new ChatMessage(firstChoice.getText().toString(), true);
                displayNewMessage(userMessage);
                ChatBlock newChatBlock = userGuide.guideUserToSafety(positiveButtonID, getContext());
                manageOptionsDisplay(newChatBlock.getUserOptions());
                setButtonsIDs(newChatBlock.getUserOptions());
                displayNewMessage(newChatBlock.getChatMessages());
            }
        } else if (v == secondChoice) {
            Log.i("Button_ID_selected", String.valueOf(negativeButtonID));
            ChatMessage userMessage = new ChatMessage(secondChoice.getText().toString(), true);
            displayNewMessage(userMessage);
            ChatBlock newChatBlock = userGuide.guideUserToSafety(negativeButtonID, getContext());
            manageOptionsDisplay(newChatBlock.getUserOptions());
            setButtonsIDs(newChatBlock.getUserOptions());
            displayNewMessage(newChatBlock.getChatMessages());
        } else if (v == thirdChoice) {
            Log.i("Button_ID_selected", String.valueOf(neutralButtonID));
            ChatMessage userMessage = new ChatMessage(thirdChoice.getText().toString(), true);
            displayNewMessage(userMessage);
            ChatBlock newChatBlock = userGuide.guideUserToSafety(neutralButtonID, getContext());
            manageOptionsDisplay(newChatBlock.getUserOptions());
            setButtonsIDs(newChatBlock.getUserOptions());
            displayNewMessage(newChatBlock.getChatMessages());
        }
    }


    private void showNearstSafePlace() {
        Intent startSafePlaceActivity = new Intent(getActivity(), SafePlaceActivity.class);
        startActivity(startSafePlaceActivity);
    }

    private void manageOptionsDisplay(UserOptions userOptions) {
        if (userOptions != null) {
            switch (userOptions.getOptionsCount()) {
                case 3:
                    firstChoice.setText(userOptions.getPositiveButtonText());
                    secondChoice.setText(userOptions.getNegativeButtonText());
                    thirdChoice.setText(userOptions.getNeutralButtonText());
                    secondChoice.setVisibility(View.VISIBLE);
                    thirdChoice.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    firstChoice.setText(userOptions.getPositiveButtonText());
                    secondChoice.setText(userOptions.getNegativeButtonText());
                    thirdChoice.setVisibility(View.GONE);
                    break;
                case 1:
                    firstChoice.setText(userOptions.getPositiveButtonText());
                    secondChoice.setVisibility(View.GONE);
                    thirdChoice.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void setButtonsIDs(UserOptions userOptions) {
        if (userOptions != null) {
            switch (userOptions.getOptionsCount()) {
                case 3:
                    positiveButtonID = userOptions.getPositiveButtonId();
                    negativeButtonID = userOptions.getNegativeButtonId();
                    neutralButtonID = userOptions.getNeutralButtonId();
                    break;
                case 2:
                    positiveButtonID = userOptions.getPositiveButtonId();
                    negativeButtonID = userOptions.getNegativeButtonId();
                    break;
                case 1:
                    positiveButtonID = userOptions.getPositiveButtonId();
                    break;
            }
        }
    }

    private void displayNewMessage(final ArrayList<ChatMessage> messages) {
        startTyping();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                for (int counter = 0; counter < messages.size(); counter++) {
                    final int value = counter;
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            chatMessages.add(messages.get(value));
                            adapter.notifyDataSetChanged();
                            guideChatList.scrollToPosition(adapter.getItemCount() - 1);
                            if (value == messages.size() - 1) {
                                showBottomButtons();
                            }
                        }
                    });


                }
            }
        };
        new Thread(runnable).start();


    }

    public void startTyping() {
        homeActivityTitle.setVisibility(View.GONE);
        homeActivityTitleTyping.setVisibility(View.VISIBLE);
    }

    public void stopTyping() {
        homeActivityTitle.setVisibility(View.VISIBLE);
        homeActivityTitleTyping.setVisibility(View.GONE);
    }

    public void showBottomButtons() {
        stopTyping();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomButtonsLayout.setVisibility(View.VISIBLE);
                try {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
                    bottomButtonsLayout.startAnimation(animation);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, 2000);


    }

    private void displayNewMessage(ChatMessage message) {
        chatMessages.add(message);
        adapter.notifyDataSetChanged();
        guideChatList.scrollToPosition(adapter.getItemCount() - 1);

    }

    @Override
    public void OnUserStatusChange(int statusId) {
        // here you should listen for changes made by user guide class, to handle sending messages to server
        //Toasts are used as illustrators ONLY , REMOVE them once you started implementation
        if (statusId == UserGuide.USER_IS_SAFE) {
            SendEventGoogleAnalytics("Guide chat", "MarkSafe", "User is safe");
            sendUserSituationToTheServer(lat, lng, "safe");
            if (trustedContactNumber != null && checkPermission(Manifest.permission.SEND_SMS)) {
                sendLocationViaSMS(trustedContactNumber, userCurrentFullName + " " + getString(R.string.user_informed_us_that_she_is_safe_now)
                );
            }
        } else if (statusId == UserGuide.USER_FEELS_IN_DANGER) {
            // is being harassed now
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    sendUserSituationToTheServer(lat, lng, "high");
                    if (trustedContactNumber != null && checkPermission(Manifest.permission.SEND_SMS)) {
                        sendLocationViaSMS(trustedContactNumber, userCurrentFullName + " " + getString(R.string.user_informed_us_that_she_is_threatened)
                        );
                    } else {
                        Toast.makeText(getContext(), R.string.trusted_contact_not_found, Toast.LENGTH_LONG).show();
                    }
                }
            }, 3000);

        } else if (statusId == UserGuide.IN_WAY_TO_POLICE) {
            sendUserSituationToTheServer(lat, lng, "mid");
            if (trustedContactNumber != null && checkPermission(Manifest.permission.SEND_SMS)) {
                sendLocationViaSMS(trustedContactNumber, userCurrentFullName + " " + getString(R.string.user_informed_us_that_she_has_been_harassed_heading_to_police)
                );
            } else {
                Toast.makeText(getContext(), R.string.trusted_contact_not_found, Toast.LENGTH_LONG).show();
            }
        } else if (statusId == UserGuide.IN_WAY_TO_HOSPITAL) {
            sendUserSituationToTheServer(lat, lng, "mid");
            if (trustedContactNumber != null && checkPermission(Manifest.permission.SEND_SMS)) {
                sendLocationViaSMS(trustedContactNumber, userCurrentFullName + " " + getString(R.string.user_informed_us_that_she_has_been_harassed_heading_to_hospital)
                );
            } else {
                Toast.makeText(getContext(), R.string.trusted_contact_not_found, Toast.LENGTH_LONG).show();
            }

        } else if (statusId == UserGuide.SEND_STRESS_SIGNAL) {
            // is followed
            // equals feels threatened
            sendUserSituationToTheServer(lat, lng, "low");
            if (trustedContactNumber != null && checkPermission(Manifest.permission.SEND_SMS)) {
                sendLocationViaSMS(trustedContactNumber, userCurrentFullName + " " + getString(R.string.user_informed_us_that_she_is_threatened)
                );
            } else {
                Toast.makeText(getContext(), R.string.trusted_contact_not_found, Toast.LENGTH_LONG).show();
            }
        } else if (statusId == UserGuide.USER_HARASSED_WHAT_TO_DO) {
            //hsa been harassed already
            sendUserSituationToTheServer(lat, lng, "high");
            if (trustedContactNumber != null && checkPermission(Manifest.permission.SEND_SMS)) {
                sendLocationViaSMS(trustedContactNumber, userCurrentFullName + " " + getString(R.string.user_informed_us_that_she_has_been_harassed)
                );
            } else {
                Toast.makeText(getContext(), R.string.trusted_contact_not_found, Toast.LENGTH_LONG).show();
            }
        } else if (statusId == UserGuide.USER_HAS_BEEN_HARASSED) {
            // this is just for navigation , don't do anything here
            startListOfChoicesFragment();
        }

    }

    private void startListOfChoicesFragment() {


        ListOfChoices listOfChoicesFragment = new ListOfChoices();
        this.getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.rlFragments, listOfChoicesFragment)
                .commit();


    }

    private void sendUserSituationToTheServer(double lati, double lngi, String severity) {
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> mService = mApi.sendUserSituation(new UserSituation(lati, lngi, severity));
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    SendEventGoogleAnalytics("sendLocation", "E-mail", "E-mail sent");
                } else {
//                    Toast.makeText(context, response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(getActivity(), R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }

        });

    }


    @Override
    public void OnUserNavigation(String navigationTag) {
        // you can use navigation tag to search with it in map directly,
        // if you thins that tag value is not suitable for search feel free to change it
        if (navigationTag.equals(UserGuide.SAFE_PLACE)) {
            //navigate to nearest safe place
//            Toast.makeText(getContext(), "Safe place", Toast.LENGTH_SHORT).show();
        } else if (navigationTag.equals(UserGuide.POLICE_INSTRUCTIONS)) {
            bottomButtonsLayout.setVisibility(View.GONE);
            ChatMessage userMessage = new ChatMessage(this.getResources().getText(R.string.going_to_police).toString(), true);
            displayNewMessage(userMessage);
            ChatBlock newChatBlock = userGuide.guideUserToSafety(UserGuide.IN_WAY_TO_POLICE, getContext());
            manageOptionsDisplay(newChatBlock.getUserOptions());
            setButtonsIDs(newChatBlock.getUserOptions());
            displayNewMessage(newChatBlock.getChatMessages());
        } else if (navigationTag.equals(UserGuide.PEOPLE_NOW_HELPING)) {
            bottomButtonsLayout.setVisibility(View.GONE);
            ChatMessage userMessage = new ChatMessage(this.getResources().getText(R.string.user_in_danger_first_question_answer_3).toString(), true);
            displayNewMessage(userMessage);
            ChatBlock newChatBlock = userGuide.guideUserToSafety(UserGuide.GOING_TO_POLICE_STATION, getContext());
            manageOptionsDisplay(newChatBlock.getUserOptions());
            setButtonsIDs(newChatBlock.getUserOptions());
            displayNewMessage(newChatBlock.getChatMessages());
        } else if (navigationTag.equals(UserGuide.HOSPITAL_INSTRUCTIONS)) {
            bottomButtonsLayout.setVisibility(View.GONE);
            ChatMessage userMessage = new ChatMessage(this.getResources().getText(R.string.going_to_hospital).toString(), true);
            displayNewMessage(userMessage);
            ChatBlock newChatBlock = userGuide.guideUserToSafety(UserGuide.IN_WAY_TO_HOSPITAL, getContext());
            manageOptionsDisplay(newChatBlock.getUserOptions());
            setButtonsIDs(newChatBlock.getUserOptions());
            displayNewMessage(newChatBlock.getChatMessages());
        } else if (navigationTag.equals(UserGuide.CALL_NADEEM)) {
            telIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01006662404"));
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else {
                startActivity(telIntent);
            }


        } else if (navigationTag.equals(UserGuide.CALL_NAZRA)) {
            telIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01011910917"));
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else {
                startActivity(telIntent);
            }
        } else {
//            Toast.makeText(getContext(), navigationTag, Toast.LENGTH_SHORT).show();

            SharedPreferences navTagPref = this.getActivity().getSharedPreferences("Tag", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = navTagPref.edit();
            editor.putString("NavigationTag", navigationTag);
            editor.apply();
            startActivity(new Intent(getActivity(), SafePlaceActivity.class));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sAnalytics = GoogleAnalytics.getInstance(getActivity());


        InitGoogleAnalytics();
        SendScreenNameGoogleAnalytics();
        context = getContext();


        if (checkPermission(Manifest.permission.SEND_SMS)) {


        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSIONS_REQUEST);
        }

        checkLocationPermission();

        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (mypreference.getBoolean("loggedIn", false)) {
            trustedContactEmail = mypreference.getString("contactEmail", null);
            trustedContactNumber = mypreference.getString("contactNumber", null);
            trustedContactName = mypreference.getString("contactName", null);
            userCurrentFullName = mypreference.getString("myFullName", null);
            token = mypreference.getString("token", null);
        }


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        retrofitforauthentication = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mLocationProvider = new LocationProvider(context, this);


    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                break;
            case SEND_SMS_PERMISSIONS_REQUEST:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Read SMS permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }


    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(context, permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    public void sendLocationViaSMS(String trustedContactNumber, String status) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = status + " \n http://maps.google.com/maps?q=" + lat + "," + lng + "&ll=" + lat + "," + lng + "&z=17";
            ArrayList<String> messageParts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(trustedContactNumber, null, messageParts, null, null);
            Toast.makeText(this.getActivity(), getActivity().getResources().getString(R.string.we_have_sent_mess_to_your_trusted_contact), Toast.LENGTH_LONG).show();
            SendEventGoogleAnalytics("sendLocation", "SMS", "SMS sent");
        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    @Override
    public void handleNewLocation(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }


    public class MyCustomLayoutManager extends LinearLayoutManager {
        private static final float MILLISECONDS_PER_INCH = 2500f;
        private Context mContext;

        public MyCustomLayoutManager(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView,
                                           RecyclerView.State state, final int position) {

            LinearSmoothScroller smoothScroller =
                    new LinearSmoothScroller(mContext) {

                        //This controls the direction in which smoothScroll looks
                        //for your view
                        @Override
                        public PointF computeScrollVectorForPosition
                        (int targetPosition) {
                            return MyCustomLayoutManager.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        //This returns the milliseconds it takes to
                        //scroll one pixel.
                        @Override
                        protected float calculateSpeedPerPixel
                        (DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                        }
                    };

            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }


    @Override
    public void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    private void InitGoogleAnalytics() {
        mGoogleHelper = new GoogleAnalyticsHelper();
        mGoogleHelper.init(getContext());
    }

    private void SendScreenNameGoogleAnalytics() {

        mGoogleHelper.SendScreenNameGoogleAnalytics("StreetPalGuideFragment", getContext());
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {

        mGoogleHelper.SendEventGoogleAnalytics(getContext(), iCategoryId, iActionId, iLabelId);
    }
}
