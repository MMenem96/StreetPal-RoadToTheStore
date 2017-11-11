package com.sharekeg.streetpal.Registration;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Home.HomeActivity;
import com.sharekeg.streetpal.R;

import java.text.Bidi;
import java.util.Locale;


public class SelectTrustedContactsActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private ImageView btnSelectTrustedContact;
    private TextView tvSkip;
    private Uri uriContact;
    private String contactID;
    private String contactName = null;
    private String contactNumber = null;
    private String contactEmail;

    SharedPreferences languagepref;
    String language;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_select_trusted_contacts);
        btnSelectTrustedContact = (ImageView) findViewById(R.id.btnSelectTrustedContact);


        btnSelectTrustedContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForContactPermission();

            }
        });
        tvSkip = (TextView) findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SelectTrustedContactsActivity.this)
                        .setTitle(R.string.select_trusted_contact_dialog_title)
                        .setMessage(R.string.select_trusted_contact_dialog_message_skipping)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (contactName == null && contactNumber == null) {
                                    Intent i = new Intent(SelectTrustedContactsActivity.this, CongratulationActivity.class);
                                    startActivity(i);
                                    finish();
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
            Intent i = new Intent(SelectTrustedContactsActivity.this, SelectTrustedContactsActivity2.class);

            i.putExtra("contactName", contactName);
            i.putExtra("contactNumber", contactNumber);
            i.putExtra("contactEmail", "null");
            startActivity(i);


        } else {

            Intent i = new Intent(SelectTrustedContactsActivity.this, SelectTrustedContactsActivity2.class);

            i.putExtra("contactName", contactName);
            i.putExtra("contactNumber", contactNumber);
            i.putExtra("contactEmail", contactEmail);
            startActivity(i);

        }


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
