package com.sharekeg.streetpal.forgotpassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Home.HomeActivity;
import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.R;

import java.lang.annotation.Annotation;
import java.text.Bidi;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MMenem on 9/19/2017.
 */

public class ForgotPasswordAcivity2 extends AppCompatActivity {

    private EditText etCode, etUserId, etNewPassword, etConfirmNewPassword;
    private Button btnUpdate, btnCancel;
    private String code, userName, newPassword, userPassword;
    private View focusView;
    private ProgressDialog pDialog;
    private Retrofit retrofit;

    SharedPreferences languagepref;
    String language;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_forget_password2);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        etCode = (EditText) findViewById(R.id.etCode);
        etUserId = (EditText) findViewById(R.id.etUserId);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etConfirmNewPassword = (EditText) findViewById(R.id.etConfirmNewPassword);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptToUpdatePassword();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForgotPasswordAcivity2.this, ForgetPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    private void attemptToUpdatePassword() {
        boolean mCancel = this.sendPasswordValidation();
        if (mCancel) {

            focusView.requestFocus();
        } else {
            pDialog = new ProgressDialog(ForgotPasswordAcivity2.this);
            pDialog.setMessage(String.valueOf(getApplicationContext().getResources().getString(R.string.updating_password)));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            sendNewPasswordToServer(userName, code, userPassword);
        }
    }

    private void sendNewPasswordToServer(String userName, String code, String userPassword) {
        ApiInterface mApi = retrofit.create(ApiInterface.class);
        Call<ResponseBody> service = mApi.sendRequestToResetPassword(new ForgotPassword(userName, code, userPassword));
        service.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    pDialog.dismiss();
                    Toast.makeText(ForgotPasswordAcivity2.this, R.string.your_password_is_updated, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ForgotPasswordAcivity2.this, LoginActivity.class);
                    startActivity(i);
                } else if (response.code() == 403 && response.message() == "Forbidden") {
                    Toast.makeText(ForgotPasswordAcivity2.this, R.string.invalid_code, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                } else {
                    //handling the error body

                    pDialog.dismiss();
                    Converter<ResponseBody, PasswordSchemeError> converter = retrofit.responseBodyConverter(PasswordSchemeError.class, new Annotation[0]);

                    try {
                        PasswordSchemeError errors = converter.convert(response.errorBody());
                        if (errors.getErrors().getPass() != null) {
                            Toast.makeText(ForgotPasswordAcivity2.this, R.string.invalid_password, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.smthing_went_wrong, Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        });
    }

    private boolean sendPasswordValidation() {
        code = etCode.getText().toString().trim();
        userName = etUserId.getText().toString().trim();
        newPassword = etNewPassword.getText().toString().trim();
        userPassword = etConfirmNewPassword.getText().toString().trim();
        boolean cancel = false;
        if (!(newPassword.equals(userPassword))) {
            etNewPassword.setError(getText(R.string.edit_password_activity_two_password_dont_match));
            etConfirmNewPassword.setError(getText(R.string.edit_password_activity_two_password_dont_match));
            focusView = etNewPassword;
            focusView = etConfirmNewPassword;
            cancel = true;


        }
        if (TextUtils.isEmpty(userPassword)) {
            etConfirmNewPassword.setError(getText(R.string.signup_password_validation_empty_password));
            focusView = etConfirmNewPassword;
            cancel = true;

        } else if (userPassword.length() < 8) {
            etConfirmNewPassword.setError(getText(R.string.signup_password_validation_min_length_password));
            focusView = etConfirmNewPassword;
            cancel = true;
        } else if (!isPasswordValid(userPassword)) {
            etConfirmNewPassword.setError(getText(R.string.signup_password_validation_invalid_password));
            focusView = etConfirmNewPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError(getText(R.string.signup_password_validation_empty_password));
            focusView = etNewPassword;
            cancel = true;

        } else if (newPassword.length() < 8) {
            etNewPassword.setError(getText(R.string.signup_password_validation_min_length_password));
            focusView = etNewPassword;
            cancel = true;
        } else if (!isPasswordValid(newPassword)) {
            etNewPassword.setError(getText(R.string.signup_password_validation_invalid_password));
            focusView = etNewPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(userName)) {
            etUserId.setError(getText(R.string.signup_password_validation_empty_username));
            focusView = etUserId;
            cancel = true;
        } else if (userName.length() < 6) {
            etUserId.setError(getText(R.string.signup_password_validation_min_length_username));
            focusView = etUserId;
            cancel = true;
        }
        if (TextUtils.isEmpty(code)) {
            etCode.setError(getText(R.string.confimation_code_validation));
            focusView = etCode;
            cancel = true;
        }

        return cancel;
    }


    private boolean isPasswordValid(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,100})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(ForgotPasswordAcivity2.this, LoginActivity.class);
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