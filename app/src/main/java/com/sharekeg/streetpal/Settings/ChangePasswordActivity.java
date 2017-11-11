package com.sharekeg.streetpal.Settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Home.HomeActivity;
import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.Login.LoginCredentials;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.Registration.ConfirmationActivity;
import com.sharekeg.streetpal.Registration.SignUpActivity;

import java.io.IOException;
import java.text.Bidi;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {
    String textnewpassword, textconfirmpassword;
    TextView btndone;
    String oldUserPassword;
    View focusView = null;
    private TextView cancel_action;
    private EditText newpassword, confirmpassword, oldpassword;
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
        setContentView(R.layout.activity_change_password);

        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(ChangePasswordActivity.this);
        if (mypreference.getBoolean("loggedIn", false)) {
            token = mypreference.getString("token", "any default value");
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

        retrofitforauthentication = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        btndone = (TextView) findViewById(R.id.btndone);
        oldpassword = (EditText) findViewById(R.id.oldpassword);
        newpassword = (EditText) findViewById(R.id.newpassword);
        confirmpassword = (EditText) findViewById(R.id.confirmpassword);
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();

            }

        });

        cancel_action = (TextView) findViewById(R.id.cancel_action);
        cancel_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    private boolean validateForm() {
        boolean cancel = false;
        textnewpassword = newpassword.getText().toString();
        textconfirmpassword = confirmpassword.getText().toString();
        oldUserPassword = oldpassword.getText().toString();
        if (!(textnewpassword.equals(textconfirmpassword))) {

            newpassword.setError(getText(R.string.edit_password_activity_two_password_dont_match));
            confirmpassword.setError(getText(R.string.edit_password_activity_two_password_dont_match));
            focusView = newpassword;
            focusView = confirmpassword;
            cancel = true;


        }

        if (TextUtils.isEmpty(textconfirmpassword)) {
            confirmpassword.setError(getText(R.string.signup_password_validation_empty_password));
            focusView = confirmpassword;
            cancel = true;

        } else if (textconfirmpassword.length() < 8) {
            confirmpassword.setError(getText(R.string.signup_password_validation_min_length_password));
            focusView = confirmpassword;
            cancel = true;
        } else if (!isPasswordValid(textconfirmpassword)) {
            confirmpassword.setError(getText(R.string.signup_password_validation_invalid_password));
            focusView = confirmpassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(textnewpassword)) {
            newpassword.setError(getText(R.string.signup_password_validation_empty_password));
            focusView = newpassword;
            cancel = true;

        } else if (textnewpassword.length() < 8) {
            newpassword.setError(getText(R.string.signup_password_validation_min_length_password));
            focusView = newpassword;
            cancel = true;
        } else if (!isPasswordValid(textnewpassword)) {
            newpassword.setError(getText(R.string.signup_password_validation_invalid_password));
            focusView = newpassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(oldUserPassword)) {
            oldpassword.setError(getText(R.string.signup_password_validation_empty_password));
            focusView = oldpassword;
            cancel = true;

        } else if (oldUserPassword.length() < 8) {
            oldpassword.setError(getText(R.string.signup_password_validation_min_length_password));
            focusView = oldpassword;
            cancel = true;
        } else if (!isPasswordValid(oldUserPassword)) {
            oldpassword.setError(getText(R.string.signup_password_validation_invalid_password));
            focusView = oldpassword;
            cancel = true;
        }


        return cancel;
    }

    private void attemptChangePassword() {

        boolean mCancel = this.validateForm();
        if (mCancel) {
            focusView.requestFocus();
        } else {
            SendPasswordWithRetrofit(oldUserPassword, textnewpassword);


        }
    }


    private void SendPasswordWithRetrofit(String oldUserPassword, String textnewpassword) {

        pDialog = new ProgressDialog(ChangePasswordActivity.this);
        pDialog.setMessage(getApplicationContext().getResources().getString(R.string.changepass));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> Service = mApi.changePassword(new ChangePassword(oldUserPassword, textnewpassword));
        Service.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    pDialog.dismiss();
                    Intent startSettingsActivity = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                    startActivity(startSettingsActivity);

                    Toast.makeText(ChangePasswordActivity.this, R.string.pass_updated, Toast.LENGTH_LONG).show();


                } else if (response.code() == 401) {

                    pDialog.dismiss();
                    oldpassword.setError(getText(R.string.check_ur_old_password));
                    focusView = oldpassword;
                } else {
                    Toast.makeText(ChangePasswordActivity.this, R.string.failed_to_update_pass2, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                pDialog.dismiss();
                Toast.makeText(ChangePasswordActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();


            }
        });


    }


    private boolean isPasswordValid(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,100})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
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
