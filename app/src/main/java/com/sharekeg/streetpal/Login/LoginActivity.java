package com.sharekeg.streetpal.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Home.HomeActivity;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.Registration.ConfirmationActivity;
import com.sharekeg.streetpal.Registration.SignUpActivity;
import com.sharekeg.streetpal.Registration.TrustedContact;
import com.sharekeg.streetpal.authentication.Result;
import com.sharekeg.streetpal.forgotpassword.ForgetPasswordActivity;
import com.sharekeg.streetpal.userinfoforlogin.UserInfoForLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Bidi;
import java.util.Arrays;
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

public class LoginActivity extends AppCompatActivity {
    View focusView = null;
    private EditText etUsername, etPassword;
    private Button btLogin;
    private TextView tvSignUp, tvForgotPassword;
    private String userName;
    private String password;
    private String token;
    private ProgressDialog pDialog;
    private Retrofit retrofitforauthentication;
    private static final String EMAIL = "email";

    SharedPreferences languagepref;
    String language;
    private LoginButton loginButton;
    private CallbackManager callbackManager = null;
    private int message;
    private String userReturnToken;
    private Retrofit retrofitForFbAuthentication;
    private SharedPreferences mypreference;
    private ProgressDialog mProgressDialog;
    private String gender;
    private String userFBAccessToken;


    @Override
    public void onBackPressed() {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        tvForgotPassword = (TextView) findViewById(R.id.link_to_reset_password);
        tvForgotPassword.setVisibility(View.GONE);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(i);
            }
        });


        //fb login button
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                getUserDetails(loginResult);

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        etUsername = (EditText) findViewById(R.id.etusername);
        etPassword = (EditText) findViewById(R.id.etpassword);
        btLogin = (Button) findViewById(R.id.btnLogin);
        tvSignUp = (TextView) findViewById(R.id.link_to_register);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), CongratulationActivity.class));
                attemptLogin();
            }
        });


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();
        OkHttpClient fbClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + userReturnToken)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        retrofitforauthentication = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitForFbAuthentication = new Retrofit.Builder()
                .client(fbClient)
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    protected void getUserDetails(final LoginResult loginResult) {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.dialog_logging));
        mProgressDialog.show();
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        try {
                            String userFBEmail = response.getJSONObject().get("email").toString();
                            gender = response.getJSONObject().get("gender").toString();
                            userFBAccessToken = AccessToken.getCurrentAccessToken().getToken().toString();
                            Log.d("accesstoken", userFBAccessToken);
                            Log.d("accesstoken", userFBEmail);


                            loginProcessWithRetrofitByFB(userFBEmail, userFBAccessToken, json_object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", e.toString());
                            mProgressDialog.dismiss();


                        }


                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,gender,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    private void openUserProfileActivity(JSONObject json_object) {
        Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
        intent.putExtra("userProfile", json_object.toString());
        intent.putExtra("accessToken", userFBAccessToken);
        startActivity(intent);
        Log.d("fbUserStatus", "doesn't exist before");
        mProgressDialog.dismiss();
        finish();
    }

    private void getFbUserDetails() {
        ApiInterface mApi = retrofitForFbAuthentication.create(ApiInterface.class);
        Call<UserInfoForLogin> mService = mApi.getUser();
        mService.enqueue(new Callback<UserInfoForLogin>() {
            @Override
            public void onResponse(Call<UserInfoForLogin> call, Response<UserInfoForLogin> response) {

                if (response.isSuccessful()) {
                    try {
                        String notificationToken = FirebaseInstanceId.getInstance().getToken();
                        mypreference = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        mypreference.edit().putBoolean("loggedIn", true).apply();
                        mypreference.edit().putString("token", userReturnToken).apply();
                        mypreference.edit().putString("myUserName", response.body().getUser()).apply();
                        mypreference.edit().putString("myFullName", response.body().getName()).apply();
                        mypreference.edit().putString("NotificationToken", notificationToken).apply();
                        mypreference.edit().putString("userType", "FB").apply();
                        mypreference.edit().putString("gender", gender).apply();
                        mypreference.edit().putLong("dialogDisplayisplayedTime", System.currentTimeMillis()).apply();
                        Intent openHomeActivity = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(openHomeActivity);
                        mProgressDialog.dismiss();
                        finish();


                    } catch (Exception e) {
                        mProgressDialog.dismiss();
                        Log.d("catch", e.toString());
                        Toast.makeText(LoginActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Log.d("else", "error");

                    mProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<UserInfoForLogin> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.d("onFailure", "getUserFbDetails");

                Toast.makeText(LoginActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void attemptLogin() {

        boolean mCancel = this.loginValidation();
        if (mCancel) {
            focusView.requestFocus();
        } else {

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(getApplicationContext().getResources().getString(R.string.dialog_logging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            loginProcessWithRetrofit(userName, password);
        }


    }

    // callback for fb login
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean loginValidation() {
        userName = etUsername.getText().toString();
        password = etPassword.getText().toString();
        boolean cancel = false;


        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getText(R.string.loginpass_validation));
            focusView = etPassword;
            cancel = true;

        }
        if (TextUtils.isEmpty(userName)) {
            etUsername.setError(getText(R.string.loginmail_validation));
            focusView = etUsername;
            cancel = true;

        }
        return cancel;
    }


    private void loginProcessWithRetrofit(String userName, String password) {

        ApiInterface mApiService = this.getInterfaceService();
        Call<Result> mService = mApiService.loginWithCredentials(new LoginCredentials(userName, password));
        mService.enqueue(new Callback<Result>() {
            @Override

            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    try {
                        token = response.body().getToken();
                        getUserData();
                    } catch (Exception e) {

                    }
                } else if (response.code() == 403) {
                    pDialog.dismiss();
                    Toast.makeText(LoginActivity.this, getApplicationContext().getResources().getText(R.string.invalid_username_or_password), Toast.LENGTH_LONG).show();
                    tvForgotPassword.setVisibility(View.VISIBLE);

                } else {
                    pDialog.dismiss();
                    Toast.makeText(LoginActivity.this, R.string.failed_to_login, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(LoginActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loginProcessWithRetrofitByFB(String email, String fbAccessToken, final JSONObject json_object) {
        ApiInterface mApiService = this.getInterfaceService();
        Call<Result> mService = mApiService.loginWithCredentialsWithFb(new LoginCredentialsWithFB(email, fbAccessToken));
        mService.enqueue(new Callback<Result>() {


            @Override

            public void onResponse(Call<Result> call, Response<Result> response) {

                if (response.isSuccessful()) {


                    try {
                        userReturnToken = response.body().getToken();
                        Log.d("userReturnToken", userReturnToken);
                        getFbUserDetails();
                    } catch (Exception e) {


                    }
                } else {
                    message = response.code();
                    if (message == 401) {
                        openUserProfileActivity(json_object);

                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();
                    }

                }
            }


            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("onFailure", "logwithretFB");
                Toast.makeText(LoginActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });
    }

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
                            String notificationToken = FirebaseInstanceId.getInstance().getToken();
                            SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            mypreference.edit().putBoolean("loggedIn", true).apply();
                            mypreference.edit().putString("token", token).apply();
                            mypreference.edit().putString("myUserName", response.body().getUser()).apply();
                            mypreference.edit().putString("myFullName", response.body().getName()).apply();
                            mypreference.edit().putString("NotificationToken", notificationToken).apply();
                            mypreference.edit().putLong("dialogDisplayisplayedTime", System.currentTimeMillis()).apply();
                            Intent openHomeActivity = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(openHomeActivity);
                            pDialog.dismiss();


                        } else {
                            try {
                                pDialog.dismiss();
                                SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                mypreference.edit().putBoolean("loggedIn", true).apply();
                                mypreference.edit().putString("token", token).apply();
                                mypreference.edit().putString("myUserName", response.body().getUser()).apply();
                                mypreference.edit().putString("myFullName", response.body().getName()).apply();
                                mypreference.edit().putLong("dialogDisplayisplayedTime", System.currentTimeMillis()).apply();
                                resendActivationCode();
                            } catch (Exception e) {

                            }

                        }
                    } catch (Exception e) {


                    }
                } else {
                    pDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<UserInfoForLogin> call, Throwable t) {

            }
        });

    }

    private void getTrustedContactDetails() {
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<TrustedContact> mService = mApi.getTrustedContactDetails();
        mService.enqueue(new Callback<TrustedContact>() {
            @Override
            public void onResponse(Call<TrustedContact> call, Response<TrustedContact> response) {

                if (response.isSuccessful()) {
                    try {
                        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        mypreference.edit().putString("contactName", response.body().getName()).apply();
                        mypreference.edit().putString("contactNumber", response.body().getPhone()).apply();
                        mypreference.edit().putString("contactEmail", response.body().getEmail()).apply();


                        Intent openHomeActivity = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(openHomeActivity);
                    } catch (Exception e) {

                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<TrustedContact> call, Throwable t) {


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

                    Intent openConfirmationCodeActivity = new Intent(LoginActivity.this, ConfirmationActivity.class);
                    startActivity(openConfirmationCodeActivity);
                    Toast.makeText(getApplicationContext(), R.string.resend_activation_code, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    private ApiInterface getInterfaceService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
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



