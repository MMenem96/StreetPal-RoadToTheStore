package com.sharekeg.streetpal.langutil;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by Lmis on 9/4/2017.
 */

public class orientationConfig extends Application {
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale();
    }



    private void setLocale() {
        SharedPreferences languagepref = getSharedPreferences("language", MODE_PRIVATE);
        String language = languagepref.getString("languageToLoad", "");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}
