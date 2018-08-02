package com.sharekeg.streetpal.Login;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.Registration.SelectTrustedContactsActivity;
import com.sharekeg.streetpal.Registration.SignUpActivity;
import com.sharekeg.streetpal.authentication.Result;
import com.sharekeg.streetpal.userinfoforsignup.UserInfoForSignupFromFB;
import com.sharekeg.streetpal.userinfoforsignup.signuperrors.ExpectedErrorsForSignup;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MMenem on 3/13/2018.
 */

public class UserProfileActivity extends AppCompatActivity {
    JSONObject response, profile_pic_data, profile_pic_url;

    private EditText etUserName, etUserFullName, etUserEmail;
    private ImageView ivUserProfile;

    private Button btnSignup;
    private String userName, userFullName, userEmail, userAccessToken, token;
    private Retrofit retrofit;
    private ProgressDialog mProgressDialog;
    //  private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("userProfile");
        userAccessToken = intent.getStringExtra("accessToken");
        retrofit = new Retrofit.Builder()
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.w("Jsondata", jsondata);
        etUserName = (EditText) findViewById(R.id.etusername);
        etUserFullName = (EditText) findViewById(R.id.etuserFullName);
        ivUserProfile = (ImageView) findViewById(R.id.ivUserProfile);
        etUserEmail = (EditText) findViewById(R.id.etuserEmail);
        btnSignup = (Button) findViewById(R.id.btnSignUp);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = etUserName.getText().toString();
                userFullName = etUserFullName.getText().toString();
                userEmail = etUserEmail.getText().toString();

                if (userName.isEmpty()) {
                    Toast.makeText(UserProfileActivity.this, R.string.signup_password_validation_empty_username, Toast.LENGTH_SHORT).show();
                } else {
                    completeUserInfo(userName, userFullName, userEmail, userAccessToken);
                }
            }
        });

        try {
            response = new JSONObject(jsondata);
            etUserEmail.setText(response.get("email").toString());
            etUserFullName.setText(response.get("name").toString());
//            if (response.has("gender")) {
//                gender = response.get("gender").toString();
//            }
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Picasso.with(this).load(profile_pic_url.getString("url"))
                    .into(ivUserProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void completeUserInfo(final String userName, final String userFullName, String userEmail, String userAccessToken) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.dialog_signing_up));
        mProgressDialog.show();


        ApiInterface mApi = retrofit.create(ApiInterface.class);
        Call<Result> mycall = mApi.insertUserinfoFromFB(new UserInfoForSignupFromFB(userName, userFullName, userEmail, userAccessToken));

        mycall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if (response.isSuccessful()) {


                    try {


                        token = response.body().getToken();
                        String notificationToken = FirebaseInstanceId.getInstance().getToken();

                        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(UserProfileActivity.this);
                        mypreference.edit().putBoolean("loggedIn", true).apply();
                        mypreference.edit().putString("token", token).apply();
                        mypreference.edit().putString("myUserName", userName).apply();
//                        mypreference.edit().putString("gender", gender).apply();
                        mypreference.edit().putString("myFullName", userFullName).apply();
                        mypreference.edit().putString("NotificationToken", notificationToken).apply();
                        mypreference.edit().putString("userType", "FB").apply();
                        mypreference.edit().putLong("dialogDisplayisplayedTime", System.currentTimeMillis()).apply();

                        Intent i = new Intent(UserProfileActivity.this, SelectTrustedContactsActivity.class);
                        startActivity(i);

                        mProgressDialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, getApplicationContext().getResources().getString(R.string.signed_up_success), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.d("exception ", e.toString());
                        mProgressDialog.dismiss();
                    }
                } else {
                    //handling the error body
                    mProgressDialog.dismiss();
                    Converter<ResponseBody, ExpectedErrorsForSignup> converter = retrofit.responseBodyConverter(ExpectedErrorsForSignup.class, new Annotation[0]);

                    try {
                        ExpectedErrorsForSignup errors = converter.convert(response.errorBody());
                        if (errors.getErrors().getUser() != null) {

                            Toast.makeText(UserProfileActivity.this, R.string.user_is_exist, Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(UserProfileActivity.this, R.string.failed_to_sign_up, Toast.LENGTH_SHORT).show();

                        }

                    } catch (Exception e) {
                    }
                }


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, getApplicationContext().getResources().getString(R.string.smthing_went_wrong), Toast.LENGTH_LONG).show();

                mProgressDialog.dismiss();
            }
        });
    }
}