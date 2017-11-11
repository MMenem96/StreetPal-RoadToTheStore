package com.sharekeg.streetpal.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.R;

import java.text.Bidi;
import java.util.Locale;

public class ReportAProblemActivity extends AppCompatActivity {
    private TextView cancel;
    private TextView send;
    private String feedback;
    EditText edittext;
    String contactNumber, contactName;
    int lang;
    SharedPreferences languagepref;
    String language;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_report_a_problem);
        cancel = (TextView) findViewById(R.id.cancel);
        edittext = (EditText) findViewById(R.id.edittext);
        send = (TextView) findViewById(R.id.send);

        send.setVisibility(View.GONE);
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edittext.getText().toString().equals("")) {
                    send.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contactNumber = getIntent().getExtras().getString("contactNumber");
                contactName = getIntent().getExtras().getString("contactName");

                feedback = edittext.getText().toString();

                if (!feedback.matches("")) {
                    send.setVisibility(View.VISIBLE);
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@streetpal.com"});
                    email.putExtra(Intent.EXTRA_SUBJECT, "Lads to Leaders/Leaderettes Questions and/or Comments");
                    email.putExtra(Intent.EXTRA_TEXT, "Feedback :" + edittext.getText().toString());
                    startActivity(Intent.createChooser(email, getApplicationContext().getResources()
                            .getString(R.string.sendmail_feedbackActivity)));
                    isMessageEmpty();
                    lang = 1;
                    return;


                } else {
                    isMessageEmpty();
                    send.setVisibility(View.GONE);
                    Toast.makeText(ReportAProblemActivity.this, R.string.feedback_err, Toast.LENGTH_LONG).show();

                    lang = 2;

                }


            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


    }

    private void isMessageEmpty() {

        if (lang == 1) {
            Toast.makeText(getApplicationContext(),
                    R.string.action_done, Toast.LENGTH_LONG).show();
            Intent i = new Intent(ReportAProblemActivity.this, SettingsActivity.class);
            startActivity(i);


        }
        if (lang == 2) {
            Toast.makeText(ReportAProblemActivity.this, R.string.feedback_err, Toast.LENGTH_LONG).show();

        }

    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ReportAProblemActivity.this, SettingsActivity.class);
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
