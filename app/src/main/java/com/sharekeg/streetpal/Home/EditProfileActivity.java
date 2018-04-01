package com.sharekeg.streetpal.Home;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.Registration.UserPhoto;
import com.sharekeg.streetpal.Settings.SettingsActivity;
import com.sharekeg.streetpal.userinfoforeditingprofile.Birth;
import com.sharekeg.streetpal.userinfoforeditingprofile.UsersInfoForEditingProfile;
import com.sharekeg.streetpal.userinfoforlogin.UserInfoForLogin;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.Bidi;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EditProfileActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 999;
    View focusView = null;
    private EditText etEmail, etPhoneNumber, etWork, etfullName;
    private TextView tvCancel, tvDone, tvtUserName;
    private ImageView ivChanceProfilePicture;
    private String email, phone, work, fullname, userCurrentFullName, userCurrentUserName, userCurrentPhone, userCurrentEmail, userCurrentWork, userCurrentBirthYear, userCurrentBirthMonth, userCurrentBirthDay, userCurrentGender;
    private ProgressDialog pDialog;
    private String token;
    private Spinner spinner;
    private DatePicker datePicker;
    private Calendar calendar;
    private String gender;
    private int day, year, month;
    private Retrofit retrofitforauthentication;
    private Uri mImageUri;
    private Bitmap bitmap;
    private ProgressDialog pDialouge;
    private TextView TV_sex, tvBirthDate;
    private ProgressDialog mProgressDialoge;
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    //     Toast.makeText(SignUpActivity.this, "ana el date", Toast.LENGTH_SHORT).show();
                    showDate(arg1, arg2 + 1, arg3);
                    year = arg1;
                    month = arg2 + 1;
                    day = arg3;
                }
            };

    SharedPreferences languagepref;
    String language;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.edit_profile);


        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this);
        if (mypreference.getBoolean("loggedIn", false)) {
            token = mypreference.getString("token", null);
            userCurrentUserName = mypreference.getString("myUserName", null);
            gender = mypreference.getString("gender", null);
            Log.i("Token in Home", token);
        }

        tvtUserName = (TextView) findViewById(R.id.userNameEditTextId);
        etEmail = (EditText) findViewById(R.id.mailEditTextId);
        etPhoneNumber = (EditText) findViewById(R.id.phoneNumberEditTextId);
        etWork = (EditText) findViewById(R.id.workEditTextId);
        etfullName = (EditText) findViewById(R.id.fullnameEditTextId);
        ivChanceProfilePicture = (ImageView) findViewById(R.id.changeProfileImg);
        tvCancel = (TextView) findViewById(R.id.backTextId);
        tvDone = (TextView) findViewById(R.id.tvDoneId);
        tvBirthDate = (TextView) findViewById(R.id.tvBirthDate);
        tvBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                showDialog(999);
            }
        });
        TV_sex = (TextView) findViewById(R.id.TV_sex);


        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(EditProfileActivity.this)
                        .setTitle(R.string.saveChangesdialoge)
                        .setMessage(getApplicationContext().getResources().getString(R.string.saveChangesMessage))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                attemptEditProfile();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Do Nothing

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


            }
        });

        ivChanceProfilePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadProfilePhoto();

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

        retrofitforauthentication = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getUserData();
        // getUserImage();
    }

    private void getUserImage() {
        Picasso.with(getApplicationContext()).load("https://streetpal.org/api/user/" + userCurrentUserName + "/photo")
                .placeholder(R.drawable.ic_default_user).error(R.drawable.ic_default_user)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(ivChanceProfilePicture);
    }

    private void attemptEditProfile() {


        boolean mCancel = this.editProfileValidation();
        if (mCancel) {
            focusView.requestFocus();
        } else {
            pDialog = new ProgressDialog(EditProfileActivity.this);
            pDialog.setMessage(getText(R.string.dialog_updating_info));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            if (gender.equals("ذكر")) {
                gender = "male";
            } else if (gender.equals("أنثي")) {
                gender = "female";
            } else {
                gender = TV_sex.getText().toString().toLowerCase();
            }
            Birth birth = new Birth(day, month, year);
            editCurrentUser(fullname, email, phone, birth, gender, work);


        }


    }

    private boolean editProfileValidation() {
        fullname = etfullName.getText().toString();
        email = etEmail.getText().toString().toLowerCase();
        phone = etPhoneNumber.getText().toString();
        gender = TV_sex.getText().toString().toLowerCase();
        work = etWork.getText().toString();
        boolean cancel = false;

        if (TextUtils.isEmpty(work)) {
            etWork.setError(getText(R.string.signup_work_validation_empty_work));
            focusView = etWork;
            cancel = true;
        } else if (work.length() < 5 || work.length() > 50) {
            etWork.setError(getText(R.string.signup_work_validation_invalid_work));
            focusView = etWork;
            cancel = true;
        }
        if (year < 1900) {
            tvBirthDate.setError(getText(R.string.signup_year_validation_invalid_year));
            focusView = tvBirthDate;
            cancel = true;
        }
        if (TextUtils.isEmpty(tvBirthDate.getText().toString())) {
            tvBirthDate.setError(getText(R.string.signup_birth_date_empty));
            focusView = tvBirthDate;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhoneNumber.setError(getText(R.string.signup_phone_validation_empty_phone));
            focusView = etPhoneNumber;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            etPhoneNumber.setError(getText(R.string.signup_phone_validation_invalid_phone));
            focusView = etPhoneNumber;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getText(R.string.signup_email_validation_empty_email));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getText(R.string.signup_email_validation_invalid_email));
            focusView = etEmail;
            cancel = true;
        }


        if (TextUtils.isEmpty(fullname)) {
            etfullName.setError(getText(R.string.signup_name_validation_empty_name));
            focusView = etfullName;
            cancel = true;
        }
        return cancel;
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() > 5 && phone.length() < 30;

    }

    private boolean isEmailValid(String email) {

        return email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+") || email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+.[a-z]");
    }

    private void editCurrentUser(final String fullname, final String email, final String phone, Birth birth, final String gender, final String work) {

        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> mycall = mApi.editCurrentUser(new UsersInfoForEditingProfile(fullname, email, phone, birth, gender, work));
        mycall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    pDialog.dismiss();

                    Toast.makeText(EditProfileActivity.this, getApplicationContext().getResources().getString(R.string.user_info_updated), Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(EditProfileActivity.this, SettingsActivity.class);
                    startActivity(i);
                } else {
                    //handling the error body

                    pDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, R.string.failed_to_update_your_info, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });

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
                ivChanceProfilePicture.setImageURI(mImageUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadProfileimage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, R.string.failed_photo_crop, Toast.LENGTH_LONG).show();
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

    private void uploadProfileimage() {
        pDialouge = new ProgressDialog(this);
        pDialouge.setMessage(getApplicationContext().getString(R.string.uploading));
        pDialouge.setCancelable(false);
        pDialouge.show();
        final String image = imageToString();
        Log.d("base64Code", image);
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> service = mApi.uploadPhoto(new UserPhoto(image));
        service.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    pDialouge.dismiss();
                    Toast.makeText(EditProfileActivity.this, R.string.uploaded_successfully, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(EditProfileActivity.this, SettingsActivity.class);
                    startActivity(i);

                } else {
                    pDialouge.dismiss();
                    Toast.makeText(EditProfileActivity.this, R.string.failed_to_update_photo, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialouge.dismiss();
                Toast.makeText(EditProfileActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getUserData() {
        mProgressDialoge = new ProgressDialog(this);
        mProgressDialoge.setMessage(getApplicationContext().getString(R.string.txtLoading));
        mProgressDialoge.setCancelable(false);
        mProgressDialoge.show();

        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<UserInfoForLogin> mService = mApi.getUser();
        mService.enqueue(new Callback<UserInfoForLogin>() {
            @Override
            public void onResponse(Call<UserInfoForLogin> call, Response<UserInfoForLogin> response) {
                try {
                    if (response.isSuccessful()) {

                        etfullName.setText(response.body().getName());
                        tvtUserName.setText(response.body().getUser());
                        etEmail.setText(response.body().getEmail());
                        etPhoneNumber.setText(response.body().getPhone());
                        year = response.body().getBirth().getY();
                        month = response.body().getBirth().getM();
                        day = response.body().getBirth().getD();
                        etWork.setText(response.body().getWork());
                        showDate(year, month, day);
                        if (response.body().getGender().isEmpty()) {
                           TV_sex.setText(gender);
                        } else {
                            TV_sex.setText(response.body().getGender());

                        }
                        userCurrentUserName = response.body().getUser();
                        mProgressDialoge.dismiss();

                    } else {
                        mProgressDialoge.dismiss();
                    }
                } catch (Exception e) {
                    mProgressDialoge.dismiss();

                }
            }

            @Override
            public void onFailure(Call<UserInfoForLogin> call, Throwable t) {
                mProgressDialoge.dismiss();
            }
        });

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
//        Toast.makeText(getApplicationContext(), "ca",
//                Toast.LENGTH_SHORT)
//                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(EditProfileActivity.this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private void showDate(int year, int month, int day) {

        tvBirthDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(EditProfileActivity.this, SettingsActivity.class);
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
