package com.sharekeg.streetpal.Registration;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Home.EditProfileActivity;
import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.authentication.Result;
import com.sharekeg.streetpal.userinfoforsignup.Birth;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.userinfoforsignup.UserInfoForSignup;
import com.sharekeg.streetpal.userinfoforsignup.signuperrors.ExpectedErrorsForSignup;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.Bidi;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final int GALLERY_REQUEST = 999;
    private static String fileProfiePhotoPath = null;
    Retrofit retrofit;
    View focusView = null;
    ApiInterface apiInterface;
    private ImageView IV_profile;
    private Button btnSignUp;
    private Spinner spinner;
    private String name, email, userName, phone, gender, work, password;
    private int year, month, day;
    private ProgressDialog pDialog;
    private EditText etEmail, etName, etUserName, etPhone, etWork, etPassword;
    private TextView tvback, tvBirthDate, TV_sex;
    private DatePicker datePicker;
    private Calendar calendar;
    private String token;
    private Uri mImageUri;
    private Bitmap bitmap;
    private ProgressDialog pDialouge;
    private Retrofit retrofitforauthentication;
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
        setContentView(R.layout.activity_sign_up);


        // object of retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        etEmail = (EditText) findViewById(R.id.etemail);
        tvBirthDate = (TextView) findViewById(R.id.tvBirthDate);
        tvBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                showDialog(999);
            }
        });
        etName = (EditText) findViewById(R.id.etName);
        etWork = (EditText) findViewById(R.id.etWork);
        etPassword = (EditText) findViewById(R.id.TV_passward);
        etUserName = (EditText) findViewById(R.id.etuserName);
        etPhone = (EditText) findViewById(R.id.etphone);
        TV_sex = (TextView) findViewById(R.id.TV_sex);
        IV_profile = (ImageView) findViewById(R.id.IV_profile);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        tvback = (TextView) findViewById(R.id.tvback);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openLoginActivity = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(openLoginActivity);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptSignUp();


            }
        });


        IV_profile
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        uploadProfilePhoto();


                    }
                });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Sex, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //  Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
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
    }

    private void uploadProfilePhoto() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);

    }

    private void attemptSignUp() {

        boolean mCancel = this.signupValidation();
        if (mCancel) {
            focusView.requestFocus();
        } else {
            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setMessage(String.valueOf(getApplicationContext().getResources().getString(R.string.dialog_signing_up)));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            if (gender.equals("ذكر")) {
                gender = "male";
            } else if (gender.equals("أنثى")) {
                gender = "female";
            } else {
                gender = TV_sex.getText().toString().toLowerCase();
            }
            Birth birth = new Birth(day, month, year);
            insertNewUser(userName, password, name, gender, email, phone, birth, work);


        }


    }

    private boolean signupValidation() {
        name = etName.getText().toString();
        userName = etUserName.getText().toString();
        password = etPassword.getText().toString();
        email = etEmail.getText().toString().toLowerCase();
        phone = etPhone.getText().toString();
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

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getText(R.string.signup_password_validation_empty_password));
            focusView = etPassword;
            cancel = true;

        } else if (password.length() < 8) {
            etPassword.setError(getText(R.string.signup_password_validation_min_length_password));
            focusView = etPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            etPassword.setError(getText(R.string.signup_password_validation_invalid_password));
            focusView = etPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError(getText(R.string.signup_phone_validation_empty_phone));
            focusView = etPhone;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            etPhone.setError(getText(R.string.signup_phone_validation_invalid_phone));
            focusView = etPhone;
            cancel = true;
        }
        if (year <= 1900) {
            tvBirthDate.setError(getText(R.string.signup_year_validation_invalid_year));
            focusView = tvBirthDate;
            cancel = true;
        }
        if (TextUtils.isEmpty(tvBirthDate.getText().toString())) {
            tvBirthDate.setError(getText(R.string.signup_birth_date_empty));
            focusView = tvBirthDate;
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
        if (TextUtils.isEmpty(userName)) {
            etUserName.setError(getText(R.string.signup_password_validation_empty_username));
            focusView = etUserName;
            cancel = true;
        } else if (userName.length() < 6) {
            etUserName.setError(getText(R.string.signup_password_validation_min_length_username));
            focusView = etUserName;
            cancel = true;


        }
        if (gender.equals("sex")) {
            Toast.makeText(this, R.string.choose_valid_sex_type, Toast.LENGTH_LONG).show();
            cancel = true;

        }
        if (TextUtils.isEmpty(name)) {
            etName.setError(getText(R.string.signup_name_validation_empty_name));
            focusView = etName;
            cancel = true;
        }
        return cancel;
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() > 5 && phone.length() < 30;

    }

    private boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
        //  return email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+") || email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+.[a-z]");
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,100})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void insertNewUser(final String userName, String password, final String name, final String gender, final String email, final String phone, final Birth birth, final String work) {

        ApiInterface mApi = retrofit.create(ApiInterface.class);
        Call<Result> mycall = mApi.insertUserinfo(new UserInfoForSignup(userName, password, name, gender, birth, email, phone, work));

        mycall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if (response.isSuccessful()) {

                    pDialog.dismiss();

                    try {


                        token = response.body().getToken();
                        String notificationToken = FirebaseInstanceId.getInstance().getToken();

                        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
                        mypreference.edit().putBoolean("loggedIn", true).apply();
                        mypreference.edit().putString("token", token).apply();
                        mypreference.edit().putString("myUserName", userName).apply();
                        mypreference.edit().putString("myFullName", name).apply();
                        mypreference.edit().putString("NotificationToken", notificationToken).apply();
                        if (mImageUri != null) {
                            uploadProfileimage();

                        } else {
                            Intent i = new Intent(SignUpActivity.this, ConfirmationActivity.class);
                            startActivity(i);
                        }


                        Toast.makeText(SignUpActivity.this, getApplicationContext().getResources().getString(R.string.signed_up_success), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.d("exception ", e.toString());
                    }
                } else {
                    //handling the error body

                    pDialog.dismiss();
                    Converter<ResponseBody, ExpectedErrorsForSignup> converter = retrofit.responseBodyConverter(ExpectedErrorsForSignup.class, new Annotation[0]);

                    try {
                        ExpectedErrorsForSignup errors = converter.convert(response.errorBody());
                        if (errors.getErrors().getEmail() != null) {
                            Toast.makeText(SignUpActivity.this, R.string.email_is_exist, Toast.LENGTH_LONG).show();
                        } else if (errors.getErrors().getUser() != null) {

                            Toast.makeText(SignUpActivity.this, R.string.user_is_exist, Toast.LENGTH_LONG).show();

                        } else if (errors.getErrors().getPhone() != null) {
                            if (errors.getErrors().getPhone().equals("unique")) {
                                Toast.makeText(SignUpActivity.this, R.string.phone_is_exist, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, R.string.signup_phone_validation_invalid_phone, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, R.string.failed_to_sign_up, Toast.LENGTH_SHORT).show();

                        }

                    } catch (Exception e) {
                    }

                }


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(SignUpActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });


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
                IV_profile.setImageURI(mImageUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, R.string.failed_photo_crop, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadProfileimage() {
        pDialouge = new ProgressDialog(this);
        pDialouge.setMessage("Initializing your data");
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
                    Intent i = new Intent(SignUpActivity.this, ConfirmationActivity.class);
                    startActivity(i);
                } else {
                    pDialouge.dismiss();
                    Toast.makeText(SignUpActivity.this, R.string.failed_to_uploade_photo, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SignUpActivity.this, ConfirmationActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialouge.dismiss();
                Toast.makeText(SignUpActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SignUpActivity.this, ConfirmationActivity.class);
                startActivity(i);

            }
        });
    }

    private String imageToString() {
        //Convert Image to String

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = spinner.getSelectedItem().toString();
        TV_sex.setText(text);
        TV_sex.getText();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        Intent openLoginActivity = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(openLoginActivity);
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
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(SignUpActivity.this,
                    myDateListener, mYear, month, mDay);
        }
        return null;
    }

    private void showDate(int year, int month, int day) {

        tvBirthDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
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
