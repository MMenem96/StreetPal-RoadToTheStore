package com.sharekeg.streetpal.Home;


import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.Registration.ConfirmationActivity;
import com.sharekeg.streetpal.Registration.TrustedContact;
import com.sharekeg.streetpal.Settings.SettingsActivity;
import com.sharekeg.streetpal.googleanalytics.GoogleAnalyticsHelper;
import com.sharekeg.streetpal.homefragments.GuideTab;
import com.sharekeg.streetpal.homefragments.HomeTab;
import com.sharekeg.streetpal.homefragments.MapTab;
import com.sharekeg.streetpal.homefragments.StartHomeFragment;
import com.sharekeg.streetpal.userinfoforlogin.UserInfoForLogin;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Bidi;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;


public class HomeActivity extends AppCompatActivity implements MapTab.OnFragmentInteractionListener, GuideTab.OnFragmentInteractionListener {
    private static final int SEND_SMS_PERMISSIONS_REQUEST = 1;
    private static final int GALLERY_REQUEST = 1;
    HomeTab homeTab;
    MapTab mapTab;
    GuideTab guideTab;
    StartHomeFragment startHomeFragment;
    private String token;
    private ImageView ivHome, ivNavigation, ivPosts;
    private ImageButton ivSettings;
    private TextView tvHomeTitle;
    private String userName = null;
    private Retrofit retrofitforauthentication;
    private Uri mImageUri;
    private Bitmap bitmap;
    private ProgressDialog pDialouge;
    private String photoUrl = null;
    private String fullName;
    private GoogleAnalyticsHelper mGoogleHelper;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    SharedPreferences languagepref;
    String language, notificationToken;
    private String feedback;
    private String userType;
    private SharedPreferences mypreference;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        InitGoogleAnalytics();
        SendScreenNameGoogleAnalytics();
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_home);

        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        if (mypreference.getBoolean("loggedIn", false)) {
            token = mypreference.getString("token", null);
            userName = mypreference.getString("myUserName", "User Name");
            fullName = mypreference.getString("myFullName", "Full Name");
            notificationToken = mypreference.getString("NotificationToken", null);
            userType=mypreference.getString("userType",null);


            if (notificationToken == null) {
                //login again to get a token
                logout();
            }

        }


        long time = mypreference.getLong("dialogDisplayisplayedTime", 0);
        if (time < System.currentTimeMillis() - 1.728e+9) {
            displayDialog();
            mypreference.edit().putLong("dialogDisplayisplayedTime", System.currentTimeMillis()).apply();
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


//        userName.substring(0, 1).toUpperCase().substring(1);
        ivHome = (ImageView) findViewById(R.id.ivhome);
        ivNavigation = (ImageView) findViewById(R.id.ivmap);
        ivPosts = (ImageView) findViewById(R.id.ivguide);

        tvHomeTitle = (TextView) findViewById(R.id.tvHomeTitle);
        photoUrl = "https://streetpal.org/api/user/" + userName + "/photo";
        homeTab = new HomeTab();
        startHomeFragment = new StartHomeFragment();
        mapTab = new MapTab();
        guideTab = new GuideTab();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rlFragments, startHomeFragment)
                .commit();

        ivSettings = (ImageButton) findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SendEventGoogleAnalytics("SettingsActivity","SettingsButton","Button clicked" );

                Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(i);


            }
        });
        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeFragmentTab();
                ivNavigation.setImageResource(R.drawable.ic_map);
                ivHome.setImageResource(R.drawable.ic_home_or);
                ivPosts.setImageResource(R.drawable.ic_guide);
                tvHomeTitle.setText(getApplicationContext().getResources().getText(R.string.home_title_guide_me));


            }
        });


        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SendEventGoogleAnalytics("MapTab","MapTabButton","Button clicked" );
//                sTracker.send(new HitBuilders.EventBuilder()
//                        .setCategory("Action")
//                        .setAction("Share")
//                        .build());


                openNavigationTab();

                ivNavigation.setImageResource(R.drawable.ic_map_or);
                ivHome.setImageResource(R.drawable.ic_home);
                ivPosts.setImageResource(R.drawable.ic_guide);
                tvHomeTitle.setText(getApplicationContext().getResources().getText(R.string.home_title_safe_places));
            }
        });

        ivPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendEventGoogleAnalytics("GuideTab", "GuideTabButton", "Static guide is opened ");

                openGuideTab();
                ivNavigation.setImageResource(R.drawable.ic_map);
                ivHome.setImageResource(R.drawable.ic_home);
                ivPosts.setImageResource(R.drawable.ic_guide_or);
                tvHomeTitle.setText(getApplicationContext().getResources().getText(R.string.home_title_guides));
            }
        });

        isUserLoggedin(token);
    }

    private void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.generalfeedback));

        final EditText feedBackMessageEditText = new EditText(this);

        feedBackMessageEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(feedBackMessageEditText);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_feedback));
        builder.setMessage(getResources().getString(R.string.Briefly));
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                feedback = feedBackMessageEditText.getText().toString();

                if (feedback.isEmpty()) {
                    Toast.makeText(HomeActivity.this, getResources().getText(R.string.popfeedback_dialog_empty), Toast.LENGTH_SHORT).show();
                    displayDialog();
                } else {
                    sendFeedback();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }

    private void sendFeedback() {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("plain/text");
        email.putExtra(Intent.EXTRA_USER, new String[]{"lablabla@lablablan.com"});
        email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"info@streetpal.com"});
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, "Lads to Leaders/Leaderettes Questions and/or Comments");
        email.putExtra(android.content.Intent.EXTRA_TEXT, "Feedback :" + feedback);
        startActivity(Intent.createChooser(email, getApplicationContext().getResources()
                .getString(R.string.sendmail_feedbackActivity)));
    }


    private void logout() {
        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.remove("token");
        editor.remove("myUserName");
        editor.remove("contactName");
        editor.remove("contactNumber");
        editor.remove("contactEmail");
        editor.remove("myFullName");
        editor.remove("myPhone");
        editor.remove("myEmail");
        editor.remove("myWork");
        editor.remove("myBirthYear");
        editor.remove("myBirthMonth");
        editor.remove("myBirthDay");
        editor.remove("myGender");
        editor.apply();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void InitGoogleAnalytics() {
        mGoogleHelper = new GoogleAnalyticsHelper();
        mGoogleHelper.init(HomeActivity.this);
    }

    private void SendScreenNameGoogleAnalytics() {

        mGoogleHelper.SendScreenNameGoogleAnalytics("HomeActivity", HomeActivity.this);
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {

        mGoogleHelper.SendEventGoogleAnalytics(HomeActivity.this, iCategoryId, iActionId, iLabelId);
    }

    private void isUserLoggedin(String token) {


        if (token == null) {
            Intent openLoginActivity = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(openLoginActivity);
        } else {
            getUserData();
            getTrustedContactDetails();

        }


    }


    private void getUserData() {
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<UserInfoForLogin> mService = mApi.getUser();
        mService.enqueue(new Callback<UserInfoForLogin>() {
            @Override
            public void onResponse(Call<UserInfoForLogin> call, Response<UserInfoForLogin> response) {

                if (response.isSuccessful()) {


                    try {


//                        if (userType!="FB") {

                            boolean isUserActive = response.body().getEmailVerified();
//                            if (isUserActive) {

                                mypreference = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                                mypreference.edit().putBoolean("loggedIn", true).apply();
                                mypreference.edit().putString("myUserName", response.body().getUser()).apply();
                                mypreference.edit().putString("myFullName", response.body().getName()).apply();
                                mypreference.edit().putString("myPhone", response.body().getPhone()).apply();
                                mypreference.edit().putString("myEmail", response.body().getEmail()).apply();
                                mypreference.edit().putString("myWork", response.body().getWork()).apply();
                                mypreference.edit().putString("myBirthYear", response.body().getBirth().getY().toString()).apply();
                                mypreference.edit().putString("myBirthMonth", response.body().getBirth().getM().toString()).apply();
                                mypreference.edit().putString("myBirthDay", response.body().getBirth().getD().toString()).apply();
                                mypreference.edit().putString("myGender", response.body().getGender()).apply();
                                userName = response.body().getUser();
//                            } else {
//                                resendActivationCode();
//                            }
//                        }else{
//                            mypreference.edit().putBoolean("loggedIn", true).apply();
//                            mypreference.edit().putString("myUserName", response.body().getUser()).apply();
//                            mypreference.edit().putString("myFullName", response.body().getName()).apply();
//                            mypreference.edit().putString("myPhone", response.body().getPhone()).apply();
//                            mypreference.edit().putString("myEmail", response.body().getEmail()).apply();
//                            mypreference.edit().putString("myWork", response.body().getWork()).apply();
//                            mypreference.edit().putString("myBirthYear", response.body().getBirth().getY().toString()).apply();
//                            mypreference.edit().putString("myBirthMonth", response.body().getBirth().getM().toString()).apply();
//                            mypreference.edit().putString("myBirthDay", response.body().getBirth().getD().toString()).apply();
//                            mypreference.edit().putString("myGender", response.body().getGender()).apply();
//                            userName = response.body().getUser();
//
//                        }
                    } catch (Exception e) {
                    }


                }

            }


            @Override
            public void onFailure(Call<UserInfoForLogin> call, Throwable t) {

            }
        });
    }


    private void resendActivationCode() {
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> mService = mApi.ResendCode();
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(HomeActivity.this, R.string.resend_activation_code, Toast.LENGTH_SHORT).show();
                    Intent openConfirmationCodeActivity = new Intent(HomeActivity.this, ConfirmationActivity.class);
                    startActivity(openConfirmationCodeActivity);

                } else {


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(HomeActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void openGuideTab() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.rlFragments, guideTab).addToBackStack("startHomeFragment").addToBackStack("mapTab")
                .commit();
    }


    private void openNavigationTab() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.rlFragments, mapTab).addToBackStack("startHomeFragment").addToBackStack("guideTab")
                .commit();

    }

    private void startHomeFragmentTab() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.rlFragments, startHomeFragment)
                .commit();


    }


    @Override
    public void onFragmentInteraction(Uri uri) {
    }


    @Override
    public void onBackPressed() {
    }


    private void uploadProfilePhoto() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);

    }


    private void getTrustedContactDetails() {
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<TrustedContact> mService = mApi.getTrustedContactDetails();
        mService.enqueue(new Callback<TrustedContact>() {
            @Override
            public void onResponse(Call<TrustedContact> call, Response<TrustedContact> response) {

                if (response.isSuccessful()) {
                    try {
                        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                        mypreference.edit().putString("contactName", response.body().getName()).apply();
                        mypreference.edit().putString("contactNumber", response.body().getPhone()).apply();
                        mypreference.edit().putString("contactEmail", response.body().getEmail()).apply();
                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onFailure(Call<TrustedContact> call, Throwable t) {

            }
        });


    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case SEND_SMS_PERMISSIONS_REQUEST:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                }
//                break;
//        }
//    }


    @Override
    public void onStart() {
        super.onStart();
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
//        String language = Locale.getDefault().getLanguage();
//        setLanguage(language);
        checkLanguage(language);
    }

    private void checkLanguage(String languageToLoad) {

        switch (languageToLoad) {
            case "ar":
                setRTL();
                break;
            case "en":
                setLTR();
                break;
            default:
                setLTR();
        }
    }

    //
    @Override
    public void onResume() {
        super.onResume();
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
//        String language = Locale.getDefault().getLanguage();
//        setLanguage(language);
        checkLanguage(language);


    }


    //
    public void setRTL() {
        String languageToLoad = "ar"; // rtl language Arabic
//        setLanguage(languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //layout direction
        Bidi b = new Bidi(languageToLoad, Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT);
        b.isRightToLeft();
//        this.setContentView(R.layout.activity_home);

    }

    public void setLTR() {
        String languageToLoad = "en"; // ltr language English
//        setLanguage(languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //layout direction
        Bidi b = new Bidi(languageToLoad, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        b.isLeftToRight();
//        this.setContentView(R.layout.activity_home);
    }






}
