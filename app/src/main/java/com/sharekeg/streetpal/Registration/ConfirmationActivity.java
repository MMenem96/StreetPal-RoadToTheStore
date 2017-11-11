package com.sharekeg.streetpal.Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Home.EditProfileActivity;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.Settings.ChangePassword;
import com.sharekeg.streetpal.Settings.ChangePasswordActivity;
import com.sharekeg.streetpal.Settings.SettingsActivity;

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


public class ConfirmationActivity extends AppCompatActivity {
    private TextView timerView, tvWelcomeUser, tvEditYourEmail;
    private String welcomeUserName, confirmationCodeText, token;
    private long startTime;
    private long countUp;
    private EditText etConfirmationCode;
    private Button btnSubmit, btn_ResendCode;
    private Retrofit retrofitforauthentication;
    private String userName;
    private ProgressDialog mProgressDialog;
    private ProgressDialog pDialog;
    private Chronometer stopWatch;
    private long timeDifference;

    SharedPreferences languagepref;
    String language;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_confirmation);

        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(ConfirmationActivity.this);
        if (mypreference.getBoolean("loggedIn", false)) {
            token = mypreference.getString("token", "any default value");
            userName = mypreference.getString("myUserName", "User Name");
            Log.i("Token in Home", token);
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
        mProgressDialog = new ProgressDialog(this);
        retrofitforauthentication = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        pDialog = new ProgressDialog(ConfirmationActivity.this);


        timerView = (TextView) findViewById(R.id.TimerView);
        stopWatch = (Chronometer) findViewById(R.id.chrono);
        startTime = SystemClock.elapsedRealtime();
        etConfirmationCode = (EditText) findViewById(R.id.ET_Code);
        timerView = (TextView) findViewById(R.id.TimerView);
        btnSubmit = (Button) findViewById(R.id.btn_Submit);
        btn_ResendCode = (Button) findViewById(R.id.btn_ResendCode);

        tvEditYourEmail = (TextView) findViewById(R.id.tvEditYourEmail);
        tvEditYourEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Toast.makeText(ConfirmationActivity.this, "Still developing this feature", Toast.LENGTH_SHORT).show();
                Intent startEditProfileIntent = new Intent(ConfirmationActivity.this, EditProfileActivity.class);
                startActivity(startEditProfileIntent);
            }
        });
        btn_ResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ReSendCodeWithRetrofit();

                new CountDownTimer(2 * 60 * 1000, 10) { //Set Timer for 2 minutes
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(ConfirmationActivity.this, getApplicationContext().getResources().getString(R.string.you_can_now_send_confirm_again), Toast.LENGTH_LONG).show();
                        btn_ResendCode.setEnabled(true);
                    }
                }.start();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationCodeText = etConfirmationCode.getText().toString();
                if (!TextUtils.isEmpty(confirmationCodeText)) {
                    SendCodeWithRetrofit();


                } else {
                    etConfirmationCode.setError(
                            getApplicationContext().getResources().getString(R.string.confimation_code_validation));
                }
            }
        });
        stopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer arg0) {
                countUp = (SystemClock.elapsedRealtime() - arg0.getBase()) / 1000;
                String asText = (countUp / 60) + ":" + (countUp % 60);
                timerView.setText(asText);
            }
        });
        stopWatch.start();

        tvWelcomeUser = (TextView) findViewById(R.id.tv_welcome_user);
        tvWelcomeUser.setText(getApplicationContext().getResources().getString(R.string.Welcome) + " " + userName + "!");

    }

    private void ReSendCodeWithRetrofit() {


        btn_ResendCode.setEnabled(false);

        pDialog.setMessage(String.valueOf(getApplicationContext().getResources().getString(R.string.dialog_resend_confirmation_code)));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> mService = mApi.ResendCode();
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    pDialog.dismiss();

                    Toast.makeText(ConfirmationActivity.this, getApplicationContext().getResources().getString(R.string.wait_two_minutes_to_send_conf_code_again), Toast.LENGTH_LONG).show();

                } else {
                    pDialog.dismiss();
                    Toast.makeText(ConfirmationActivity.this, getApplicationContext().getResources().getString(R.string.failed_to_send_conf_code), Toast.LENGTH_LONG).show();


                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(ConfirmationActivity.this, getApplicationContext().getResources().getString(R.string.smthing_went_wrong), Toast.LENGTH_LONG).show();

            }
        });


    }


    private void SendCodeWithRetrofit() {


        pDialog.setMessage(String.valueOf(getApplicationContext().getResources().getString(R.string.dialog_send_confirmation_code)));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        confirmationCodeText = etConfirmationCode.getText().toString();

        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> Service = mApi.sendCode(new ConfirmationCode(confirmationCodeText));

        Service.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    pDialog.dismiss();
                    Toast.makeText(ConfirmationActivity.this, getApplicationContext().getResources().getString(R.string.succ_code), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ConfirmationActivity.this, SelectTrustedContactsActivity.class);
                    startActivity(i);

                } else {
                    pDialog.dismiss();


                    Toast.makeText(ConfirmationActivity.this, getApplicationContext().getResources().getString(R.string.invalid_code), Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(ConfirmationActivity.this, getApplicationContext().getResources().getString(R.string.smthing_went_wrong), Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
