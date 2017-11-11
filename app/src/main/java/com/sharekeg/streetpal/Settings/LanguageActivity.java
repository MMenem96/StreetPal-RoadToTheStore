package com.sharekeg.streetpal.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sharekeg.streetpal.R;

import java.text.Bidi;
import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {
    private TextView english, arabic;
    SharedPreferences languagepref;
    private ImageView english1, arabic1;
    private ImageButton back_action;
    private RelativeLayout enLayout, arLayout;
    int lang;
    private String contactNumber, contactName;
    String language;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_language);
        enLayout = (RelativeLayout) findViewById(R.id.enLayout);
        arLayout = (RelativeLayout) findViewById(R.id.arLayout);
        english1 = (ImageView) findViewById(R.id.english1);
        arabic1 = (ImageView) findViewById(R.id.arabic1);
        back_action = (ImageButton) findViewById(R.id.cancel_action);
        english = (TextView) findViewById(R.id.english);
        enLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                english1.setBackgroundResource(R.drawable.ic_selected);
                english1.setVisibility(View.VISIBLE);
                arabic1.setVisibility(View.GONE);
                lang = 1;
            }
        });
        arabic = (TextView) findViewById(R.id.arabic);
        arLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                arabic1.setBackgroundResource(R.drawable.ic_selected);
                english1.setVisibility(View.GONE);
                arabic1.setVisibility(View.VISIBLE);
                lang = 2;
            }
        });

        SharedPreferences languagepref = getSharedPreferences("language", MODE_PRIVATE);
        String language = languagepref.getString("languageToLoad", "");
        switch (language) {
            case "ar":
                arabic1.setBackgroundResource(R.drawable.ic_selected);
                english1.setVisibility(View.GONE);
                arabic1.setVisibility(View.VISIBLE);
                lang = 2;
                break;
            case "en":
                english1.setBackgroundResource(R.drawable.ic_selected);
                english1.setVisibility(View.VISIBLE);
                arabic1.setVisibility(View.GONE);
                lang = 1;
                break;
        }

        back_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lang == 1) {
                    Toast.makeText(getApplicationContext(),
                            R.string.action_done, Toast.LENGTH_LONG).show();
                    setLTRAfterChange();

                    Intent i = new Intent(LanguageActivity.this, SettingsActivity.class);

                    startActivity(i);


                }
                if (lang == 2) {
                    Toast.makeText(getApplicationContext(),
                            R.string.action_done, Toast.LENGTH_LONG).show();
                    setRTLAfterChange();
                    Intent i = new Intent(LanguageActivity.this, SettingsActivity.class);

                    startActivity(i);

                }
            }
        });


    }

    public void setRTLAfterChange() {
        String languageToLoad = "ar"; // rtl language Arabic
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //layout direction
        Bidi b = new Bidi(languageToLoad, Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT);
        b.isRightToLeft();
        //save current locale in SharedPreferences
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("languageToLoad", languageToLoad);
        editor.commit();
    }

    public void setLTRAfterChange() {
        String languageToLoad = "en"; // ltr language English
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //layout direction
        Bidi b = new Bidi(languageToLoad, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        b.isLeftToRight();
        //save current locale in SharedPreferences
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("languageToLoad", languageToLoad);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        //Do Nothing
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
