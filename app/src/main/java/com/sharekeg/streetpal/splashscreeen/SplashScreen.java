package com.sharekeg.streetpal.splashscreeen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.akexorcist.localizationactivity.LocalizationActivity;
import com.sharekeg.streetpal.Home.HomeActivity;
import com.sharekeg.streetpal.Language.ChooseLanguage;
import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.safeplace.SafePlaceActivity;

import org.w3c.dom.Text;

import java.text.Bidi;
import java.util.Locale;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreen extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setupCustomActivityOnCrashLib();

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences languagepref = getSharedPreferences("language", MODE_PRIVATE);
                String language = languagepref.getString("languageToLoad", "");
//                String language=Locale.getDefault().getLanguage();
//                setLanguage(language);
                switch (language) {
                    case "ar":
                        Locale localeAR = new Locale(language);
                        Locale.setDefault(localeAR);

                        Configuration configAR = new Configuration();
                        configAR.locale = localeAR;
                        getBaseContext().getResources().updateConfiguration(configAR,
                                getBaseContext().getResources().getDisplayMetrics());
                        //layout direction
                        Bidi bAR = new Bidi(language, Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT);
                        bAR.isRightToLeft();
                        break;
                    case "en":
                        Locale localeEN = new Locale(language);
                        Locale.setDefault(localeEN);

                        Configuration configEN = new Configuration();
                        configEN.locale = localeEN;
                        getBaseContext().getResources().updateConfiguration(configEN,
                                getBaseContext().getResources().getDisplayMetrics());
                        //layout direction
                        Bidi bEN = new Bidi(language, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
                        bEN.isLeftToRight();
                        break;
                    default:
                        Locale locale = new Locale("en");
                        Locale.setDefault(locale);

                        Configuration config = new Configuration();
                        config.locale = locale;
                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());
                        //layout direction
                        Bidi b = new Bidi(language, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
                        b.isLeftToRight();
                }


                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean previouslyStarted = prefs.getBoolean("previously_started", false);
                if (!previouslyStarted)

                {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("previously_started", Boolean.TRUE);
                    edit.commit();
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, ChooseLanguage.class));
//                    SplashScreen.this.finish();
                } else

                {
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                    SplashScreen.this.finish();
                }

            }
        }, secondsDelayed * 3000);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setupCustomActivityOnCrashLib(){
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                .enabled(true) //default: true
                .showErrorDetails(false) //default: true
                .showRestartButton(true) //default: true
                .trackActivities(true) //default: false
                .minTimeBetweenCrashesMs(1000) //default: 3000
//                .errorDrawable(R.drawable.ic_custom_drawable)
                .restartActivity(SplashScreen.class) //default: null (your app's launch activity)
//                .errorActivity(YourCustomErrorActivity.class) //default: null (default error activity)
                .eventListener(null) //default: null
                .apply();
    }
}
