package com.sharekeg.streetpal.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Home.EditProfileActivity;
import com.sharekeg.streetpal.Home.HomeActivity;
import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.R;

import java.text.Bidi;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private ImageView languageimg, IV_editProfile, changepasswordimg, IV_aboutas,
            IV_trusted_contact, IV_privacy_policy, IV_Report_a_problem;
    private TextView logout, tvTrustedContact, editProfile, changepassword, trusted_contact, language, privacy_policy, Report_a_problem, aboutas;
    private String contactName;
    private ImageButton IV_back;
    private String contactNumber = null;
    private String token;
    private TextView trustedContactName;

    SharedPreferences languagepref;
    String Language;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        Language = languagepref.getString("languageToLoad", "");
//        String language = Locale.getDefault().getLanguage();
//        setLanguage(language);
        checkLanguage(Language);
        setContentView(R.layout.activity_settings);


        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        if (mypreference.getBoolean("loggedIn", false)) {
            token = mypreference.getString("token", null);
            contactName = mypreference.getString("contactName", null);
            contactNumber = mypreference.getString("contactNumber", null);
        }

        IV_aboutas = (ImageView) findViewById(R.id.IV_aboutas);
        IV_aboutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AboutAsActivity.class);
                startActivity(intent);

            }
        });
        aboutas = (TextView) findViewById(R.id.aboutas);
        aboutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AboutAsActivity.class);
                startActivity(intent);

            }
        });

        tvTrustedContact = (TextView) findViewById(R.id.textView3);

        if (contactName == null) {
            tvTrustedContact.setText(getApplicationContext().getResources().getString(R.string.no_trusted_contact));

        } else {

            tvTrustedContact.setText(contactName);
        }

        IV_back = (ImageButton) findViewById(R.id.IV_back);
        IV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor editor = mySPrefs.edit();
                editor.remove("token");
                editor.remove("myUserName");
                editor.remove("contactName");
                editor.remove("contactNumber");
                editor.remove("contactEmail");
                editor.remove("myFullName");
                editor.remove("myPhone");
                editor.remove("myEmail");
                editor.remove("myWork");
                editor.remove("myBirthYear");
                editor.remove("myBirthMonth");
                editor.remove("myBirthDay");
                editor.remove("myGender");
                editor.apply();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        trustedContactName = (TextView) findViewById(R.id.textView3);
        trustedContactName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, EditSelectedTrustedContactsActivity.class);
                startActivity(i);
            }
        });
        IV_trusted_contact = (ImageView) findViewById(R.id.IV_trusted_contact);
        IV_trusted_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, EditSelectedTrustedContactsActivity.class);
                startActivity(i);


            }
        });
        trusted_contact = (TextView) findViewById(R.id.trusted_contact);
        trusted_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SettingsActivity.this, EditSelectedTrustedContactsActivity.class);
                startActivity(i);

            }
        });

        IV_Report_a_problem = (ImageView) findViewById(R.id.IV_Report_a_problem);
        IV_Report_a_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, R.string.still_developing, Toast.LENGTH_LONG).show();

//                Intent i = new Intent(SettingsActivity.this, ReportAProblemActivity.class);
//                startActivity(i);

            }
        });
        Report_a_problem = (TextView) findViewById(R.id.Report_a_problem);
        Report_a_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, R.string.still_developing, Toast.LENGTH_LONG).show();

//                Intent i = new Intent(SettingsActivity.this, ReportAProblemActivity.class);
//                startActivity(i);

            }
        });

        IV_privacy_policy = (ImageView) findViewById(R.id.IV_privacy_policy);
        IV_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(SettingsActivity.this, "Still developing this feature", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
                startActivity(i);

            }
        });
        privacy_policy = (TextView) findViewById(R.id.privacy_policy);
        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(SettingsActivity.this, "Still developing this feature", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
                startActivity(i);

            }
        });
        IV_editProfile = (ImageView) findViewById(R.id.IV_editProfile);
        IV_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(SettingsActivity.this, "Still developing this feature", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(i);

            }
        });
        editProfile = (TextView) findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SettingsActivity.this, "Still developing this feature", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(i);

            }
        });
        changepasswordimg = (ImageView) findViewById(R.id.changepasswordimg);
        changepasswordimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(i);

            }
        });

        changepassword = (TextView) findViewById(R.id.changepassword);
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, ChangePasswordActivity.class);

                startActivity(i);

            }
        });


        languageimg = (ImageView) findViewById(R.id.IV_language);
        languageimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, LanguageActivity.class);
                startActivity(i);

            }
        });

        language = (TextView) findViewById(R.id.TV_language);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SettingsActivity.this, LanguageActivity.class);
                startActivity(i);

            }
        });


    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(intent);
    }
    @Override
    public void onStart() {
        super.onStart();
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        Language = languagepref.getString("languageToLoad", "");
//        String language = Locale.getDefault().getLanguage();
//        setLanguage(language);
        checkLanguage(Language);
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
        Language = languagepref.getString("languageToLoad", "");
//        String language = Locale.getDefault().getLanguage();
//        setLanguage(language);
        checkLanguage(Language);
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
//        this.setContentView(R.layout.activity_home);

    }

    public void setLTR() {
        String languageToLoad = "en"; // ltr language English
//        setLanguage(languageToLoad);
//        String Local=this.getResources().getConfiguration()
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //layout direction
        Bidi b = new Bidi(languageToLoad, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        b.isLeftToRight();
//        this.setContentView(R.layout.activity_home);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
