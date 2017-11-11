package com.sharekeg.streetpal.Settings;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Home.EditProfileActivity;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.Registration.TrustedContact;

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

public class EditSelectedTrustedContactsActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    ImageView IV_add_new, IV_edit, IV_remove;
    private TextView tvName, tvNumber, cancel, tvDone, tvEmail;
    private String contactNumberfromSharedPrefrence;
    private String contactNamefromSharedPrefrence;
    private String contactEmailfromSharedPrefrence;
    private Retrofit retrofitforauthentication;
    private String token;
    private ProgressDialog pDialog;
    private Uri uriContact;
    private String contactID;
    private String email = null;
    private String contactNumber, contactName, contactEmail;

    SharedPreferences languagepref;
    String language;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_edit_selected_trusted_contacts);


        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(EditSelectedTrustedContactsActivity.this);
        if (mypreference.getBoolean("loggedIn", false)) {
            token = mypreference.getString("token", null);
            contactNamefromSharedPrefrence = mypreference.getString("contactName", null);
            contactNumberfromSharedPrefrence = mypreference.getString("contactNumber", null);
            contactEmailfromSharedPrefrence = mypreference.getString("contactEmail", null);
        }

        tvNumber = (TextView) findViewById(R.id.tvNumber);
        IV_edit = (ImageView) findViewById(R.id.IV_edit);
        IV_remove = (ImageView) findViewById(R.id.IV_remove);
        tvName = (TextView) findViewById(R.id.tvName);
        tvDone = (TextView) findViewById(R.id.tvDone);
        IV_add_new = (ImageView) findViewById(R.id.IV_add_new);
        tvEmail = (TextView) findViewById(R.id.tvEmail);


        if (contactNamefromSharedPrefrence == null) {
            IV_edit.setEnabled(false);
            IV_remove.setEnabled(false);
            IV_add_new.setEnabled(true);
            tvName.setVisibility(View.GONE);
            tvNumber.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);


        } else if (contactEmailfromSharedPrefrence == null && contactNamefromSharedPrefrence != null && contactNumberfromSharedPrefrence != null) {
            tvEmail.setText(getApplicationContext().getResources().getString(R.string.trusted_contact_email_hint));
            IV_add_new.setEnabled(false);
            IV_edit.setEnabled(true);
            IV_remove.setEnabled(true);
            tvName.setText(contactNamefromSharedPrefrence);
            tvNumber.setText(contactNumberfromSharedPrefrence);
        } else {
            IV_add_new.setEnabled(false);
            IV_edit.setEnabled(true);
            IV_remove.setEnabled(true);
            tvName.setText(contactNamefromSharedPrefrence);
            tvNumber.setText(contactNumberfromSharedPrefrence);
            tvEmail.setText(contactEmailfromSharedPrefrence);

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
                .

                        client(client)
                .

                        baseUrl("https://streetpal.org/api/")
                .

                        addConverterFactory(GsonConverterFactory.create())
                .

                        build();


        cancel = (TextView)

                findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditSelectedTrustedContactsActivity.this, SettingsActivity.class);

                startActivity(i);
            }
        });

        IV_add_new.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                askForContactPermission();


            }
        });

        IV_edit.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                askForContactPermission();

            }
        });
        IV_remove.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                RemoveTrustedContactWithRetrofit();


            }
        });


    }


    private void RemoveTrustedContactWithRetrofit() {


        pDialog = new ProgressDialog(EditSelectedTrustedContactsActivity.this);
        pDialog.setMessage(String.valueOf(getApplicationContext().getResources().getString(R.string.dialog_remove_trusted_contact)));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        ApiInterface mApiService = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> mService = mApiService.deleteTrustedContact();
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {


                    tvName.setVisibility(View.GONE);
                    tvNumber.setVisibility(View.GONE);
                    tvEmail.setVisibility(View.GONE);
                    IV_add_new.setEnabled(true);
                    IV_edit.setEnabled(false);
                    IV_remove.setEnabled(false);

                    SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(EditSelectedTrustedContactsActivity.this);
                    SharedPreferences.Editor editor = mySPrefs.edit();
                    editor.remove("contactName");
                    editor.remove("contactNumber");
                    editor.remove("contactEmail");
                    editor.apply();

                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.trusted_contact_is_deleted, Toast.LENGTH_LONG).show();
                } else {
                    pDialog.dismiss();
                    Toast.makeText(EditSelectedTrustedContactsActivity.this, R.string.failed_to_remove_your_trusted_contact, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(EditSelectedTrustedContactsActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });


    }


    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.txtcontactAccessNeeded);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage(R.string.txtcontactAccessNeededmessage);//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, R.string.txtNoPermissions, Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();

            retrieveContactName();
            retrieveContactNumber();
            retriveContactEmail();

        }
    }

    private void retrieveContactNumber() {


        // getting contacts ID

        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }


        cursorID.close();


        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();


    }


    private void retrieveContactName() {


        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();


    }

    private void retriveContactEmail() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactID}, null);
        if (cursor.moveToFirst()) {
            contactEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

        }
        cursor.close();
        if (contactEmail == null) {
            tvName.setText(contactName);
            tvNumber.setText(contactNumber);
            tvEmail.setText(getApplicationContext().getResources().getString(R.string.trusted_contact_email_hint));
            sendTrustedContact(contactName, contactNumber, email);


        } else {
            tvName.setText(contactName);
            tvNumber.setText(contactNumber);
            tvEmail.setText(contactEmail);
            sendTrustedContact(contactName, contactNumber, contactEmail);

        }


    }

    private void sendTrustedContact(final String contactName, final String contactNumber, final String contactEmail) {


        pDialog = new ProgressDialog(EditSelectedTrustedContactsActivity.this);
        pDialog.setMessage(String.valueOf(getApplicationContext().getResources().getString(R.string.dialog_select_trusted_contact)));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> mService = mApi.addTrustedContact(new TrustedContact(contactName, contactNumber, contactEmail));
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {


                    IV_edit.setEnabled(true);
                    IV_remove.setEnabled(true);
                    IV_add_new.setEnabled(false);
                    pDialog.dismiss();

                    SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(EditSelectedTrustedContactsActivity.this);
                    mypreference.edit().putString("contactName", contactName).apply();
                    mypreference.edit().putString("contactNumber", contactNumber).apply();
                    mypreference.edit().putString("contactEmail", contactEmail).apply();

                    Intent i = new Intent(EditSelectedTrustedContactsActivity.this, SettingsActivity.class);
                    startActivity(i);
                    Toast.makeText(EditSelectedTrustedContactsActivity.this, R.string.txtSuccess, Toast.LENGTH_LONG).show();

                } else {
                    pDialog.dismiss();
                    Toast.makeText(EditSelectedTrustedContactsActivity.this, R.string.failed_to_update_your_trusted_contact, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(EditSelectedTrustedContactsActivity.this, R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();


            }
        });
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(EditSelectedTrustedContactsActivity.this, SettingsActivity.class);
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
