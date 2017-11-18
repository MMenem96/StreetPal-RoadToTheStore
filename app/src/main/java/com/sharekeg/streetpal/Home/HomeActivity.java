package com.sharekeg.streetpal.Home;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.Registration.ConfirmationActivity;
import com.sharekeg.streetpal.Registration.TrustedContact;
import com.sharekeg.streetpal.Registration.UserPhoto;
import com.sharekeg.streetpal.Settings.SettingsActivity;
import com.sharekeg.streetpal.googleanalytics.GoogleAnalyticsHelper;
import com.sharekeg.streetpal.homefragments.GuideTab;
import com.sharekeg.streetpal.homefragments.HomeTab;
import com.sharekeg.streetpal.homefragments.MapTab;
import com.sharekeg.streetpal.homefragments.StartHomeFragment;
import com.sharekeg.streetpal.userinfoforlogin.UserInfoForLogin;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
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
    private ImageView ivAddUserPhoto;
    private TextView tvusername, tvHomeTitle;
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
    String language;


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
        ivAddUserPhoto = (ImageView) findViewById(R.id.ivAddUserPhoto);

        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        if (mypreference.getBoolean("loggedIn", false)) {
            token = mypreference.getString("token", null);
            userName = mypreference.getString("myUserName", "User Name");
            fullName = mypreference.getString("myFullName", "Full Name");

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

        tvusername = (TextView) findViewById(R.id.tvusername);
        tvusername.setText(fullName);
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

    private void InitGoogleAnalytics() {
        mGoogleHelper = new GoogleAnalyticsHelper();
        mGoogleHelper.init(HomeActivity.this);
    }

    private void SendScreenNameGoogleAnalytics() {

        mGoogleHelper.SendScreenNameGoogleAnalytics("HomeActivity 1", HomeActivity.this);
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
            getUserImage();
            getTrustedContactDetails();

        }


    }

//    private boolean checkPermission(String permission) {
//        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
//        return checkPermission == PackageManager.PERMISSION_GRANTED;
//    }

    private void getUserData() {
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<UserInfoForLogin> mService = mApi.getUser();
        mService.enqueue(new Callback<UserInfoForLogin>() {
            @Override
            public void onResponse(Call<UserInfoForLogin> call, Response<UserInfoForLogin> response) {

                if (response.isSuccessful()) {


                    try {
                        boolean isUserActive = response.body().getEmailVerified();
                        if (isUserActive) {

                            SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
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
                            tvusername.setText(response.body().getName());
                            userName = response.body().getUser();
                        } else {
                            resendActivationCode();
                        }
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


    private void uploadProfileimage() {
        pDialouge = new ProgressDialog(this);
        pDialouge.setMessage(getApplicationContext().getString(R.string.uploading));
        pDialouge.setCancelable(false);
        pDialouge.show();
        String image = imageToString();
        Log.d("base64Code", image);
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> service = mApi.uploadPhoto(new UserPhoto(image));
        service.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    pDialouge.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.uploaded_successfully, Toast.LENGTH_LONG).show();
                } else {
                    pDialouge.dismiss();
//                    Toast.makeText(HomeActivity.this, "Error : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialouge.dismiss();
                Toast.makeText(HomeActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onBackPressed() {
    }


    private void getUserImage() {

        Picasso.with(getApplicationContext()).load("https://streetpal.org/api/user/" + userName + "/photo")
                .placeholder(R.drawable.ic_default_user).error(R.drawable.ic_default_user)
                .into(ivAddUserPhoto);
    }


    private void uploadProfilePhoto() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAutoZoomEnabled(true)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                ivAddUserPhoto.setImageURI(mImageUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadProfileimage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, R.string.failed_photo, Toast.LENGTH_LONG).show();
            }
        }
    }


    private String imageToString() {
        //Convert Image to String

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
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
