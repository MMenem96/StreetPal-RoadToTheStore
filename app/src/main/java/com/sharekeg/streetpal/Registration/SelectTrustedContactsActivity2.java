package com.sharekeg.streetpal.Registration;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.R;

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

public class SelectTrustedContactsActivity2 extends AppCompatActivity {
    private TextView tvSkip;
    private ImageView ivBack, ivNext;
    private TextView tvName, tvNumber, tvEmail;
    private String contactName = null;
    private String contactNumber = null;
    private String contactEmail = null;
    private String email;
    private String token;
    private Retrofit retrofitforauthentication;
    private ProgressDialog pDialog;

    SharedPreferences languagepref;
    String language;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_select_trusted_contacts2);
        try {


            contactName = getIntent().getExtras().getString("contactName");
            contactNumber = getIntent().getExtras().getString("contactNumber");
            contactEmail = getIntent().getExtras().getString("contactEmail");
        } catch (Exception e) {

        }
        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(SelectTrustedContactsActivity2.this);
        if (mypreference.getBoolean("loggedIn", false)) {
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


        tvName = (TextView) findViewById(R.id.tvName);
        tvNumber = (TextView) findViewById(R.id.tvNumber);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        if (contactEmail.equals("null")) {
            tvName.setText(contactName);
            tvNumber.setText(contactNumber);
            tvEmail.setText(R.string.trusted_contact_email_hint);

        } else {
            tvName.setText(contactName);
            tvNumber.setText(contactNumber);
            tvEmail.setText(contactEmail);
        }


        ivBack = (ImageView) findViewById(R.id.ivback);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectTrustedContactsActivity2.this, SelectTrustedContactsActivity.class);
                startActivity(i);
            }
        });


        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SelectTrustedContactsActivity2.this)
                        .setTitle(R.string.select_trusted_contact_dialog_title)
                        .setMessage(R.string.select_trusted_contact_dialog_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (contactEmail.equals("null")) {
                                    sendTrustedContact(contactName, contactNumber, email);

                                } else {
                                    sendTrustedContact(contactName, contactNumber, contactEmail);

                                }

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();

            }
        });

        tvSkip = (TextView) findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SelectTrustedContactsActivity2.this)
                        .setTitle(R.string.select_trusted_contact_dialog_title)
                        .setMessage(R.string.select_trusted_contact_dialog_message_skipping)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });

    }

    private void sendTrustedContact(final String contactName, final String contactNumber, String email) {
        pDialog = new ProgressDialog(SelectTrustedContactsActivity2.this);
        pDialog.setMessage(String.valueOf(getApplicationContext().getResources().getString(R.string.dialog_select_trusted_contact)));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();


        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> mService = mApi.addTrustedContact(new TrustedContact(contactName, contactNumber, email));
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    pDialog.dismiss();

                    SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(SelectTrustedContactsActivity2.this);
                    mypreference.edit().putString("contactName", contactName).apply();
                    mypreference.edit().putString("contactNumber", contactNumber).apply();
                    mypreference.edit().putString("contactEmail", contactEmail).apply();

                    Intent i = new Intent(SelectTrustedContactsActivity2.this, CongratulationActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    pDialog.dismiss();
                    Toast.makeText(SelectTrustedContactsActivity2.this, R.string.cannot_upload_trusted_contact, Toast.LENGTH_LONG).show();

                    Intent i = new Intent(SelectTrustedContactsActivity2.this, CongratulationActivity.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(SelectTrustedContactsActivity2.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });


    }


    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(SelectTrustedContactsActivity2.this, SelectTrustedContactsActivity.class);
        startActivity(i);

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
