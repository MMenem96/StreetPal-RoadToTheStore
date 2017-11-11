package com.sharekeg.streetpal.forgotpassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.R;

import java.text.Bidi;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgetPasswordActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText etEmailForForgetPassword;
    private Button btnSend, btnCancel;
    private ProgressDialog pDialog;
    private View focusView;
    private String userEmail;
    private Retrofit retrofit;
    SharedPreferences languagepref;
    String language;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_forget_password);

        etEmailForForgetPassword = (EditText) findViewById(R.id.etEmailForForgetPassword);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptToSendEmail();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void attemptToSendEmail() {

        boolean mCancel = this.sendEmailValidation();
        if (mCancel) {
            focusView.requestFocus();
        } else {
            pDialog = new ProgressDialog(ForgetPasswordActivity.this);
            pDialog.setMessage(String.valueOf(getApplicationContext().getResources().getString(R.string.sending_your_email)));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            sendEmailToServer(userEmail);


        }

    }

    private void sendEmailToServer(String userEmail) {
        ApiInterface mApi = retrofit.create(ApiInterface.class);
        Call<ResponseBody> service = mApi.sendEmailToResetPassword(new UserEmailForForgetPassword(userEmail));
        service.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    pDialog.dismiss();
                    Toast.makeText(ForgetPasswordActivity.this, R.string.we_have_sent_to_you_an_email, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ForgetPasswordActivity.this, ForgotPasswordAcivity2.class);
                    startActivity(i);
                } else {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.failed_to_send_mail, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.smthing_went_wrong, Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        });
    }

    private boolean sendEmailValidation() {
        userEmail = etEmailForForgetPassword.getText().toString();
        boolean cancel = false;
        if (TextUtils.isEmpty(userEmail)) {
            etEmailForForgetPassword.setError(getText(R.string.signup_email_validation_empty_email));
            focusView = etEmailForForgetPassword;
            cancel = true;
        } else if (!isEmailValid(userEmail)) {
            etEmailForForgetPassword.setError(getText(R.string.signup_email_validation_invalid_email));
            focusView = etEmailForForgetPassword;
            cancel = true;
        }

        return cancel;
    }

    private boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
        //  return email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+") || email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+.[a-z]");
    }


    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
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