package com.sharekeg.streetpal.Language;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.R;

import com.sharekeg.streetpal.splashscreeen.SplashScreen;

import java.text.Bidi;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChooseLanguage extends AppCompatActivity  {

    private RelativeLayout enlay,arlay;
    private TextView tven,tvar;
    private ImageView btnDone;
    private String lang="English";
    SharedPreferences languagepref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);



//        ReplceFontForEnglish.replceEnglishFont(getApplicationContext(),"DEFAULT","Roboto-Regular.ttf");
//        ReplaceFontForArabic.replaceArabicFont(getApplicationContext(),"DEFAULT","cocon-858df15efcbd1077e4131a6b1673af62.ttf");


        enlay=(RelativeLayout)findViewById(R.id.enLay);
        arlay=(RelativeLayout)findViewById(R.id.arLay);
        tven=(TextView)findViewById(R.id.tv_en);
        tvar=(TextView)findViewById(R.id.tv_ar);

        enlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enlay.setBackgroundResource(R.drawable.rounded_corner_or);
                tven.setTextColor(Color.parseColor("#ffffff"));
                arlay.setBackgroundResource(R.drawable.rounded_corner);
                tvar.setTextColor(Color.parseColor("#000000"));
                lang="English";
            }
        });
        arlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enlay.setBackgroundResource(R.drawable.rounded_corner);
                tven.setTextColor(Color.parseColor("#000000"));
                arlay.setBackgroundResource(R.drawable.rounded_corner_or);
                tvar.setTextColor(Color.parseColor("#ffffff"));
                lang="Arabic";
            }
        });
        btnDone=(ImageView) findViewById(R.id.btnDone);


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lang=="English"){
                    setLTR();

                    Intent intent = new Intent(ChooseLanguage.this, LoginActivity.class);

                    startActivity(intent);

                } if(lang=="Arabic") {

                    setRTL();
                    Intent intent = new Intent(ChooseLanguage.this, LoginActivity.class);
                    startActivity(intent);

                }

            }
        });


    }
    public void setRTL(){
        String languageToLoad  = "ar"; // rtl language Arabic
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
        languagepref = getSharedPreferences("language",MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("languageToLoad",languageToLoad );
        editor.apply();
//        ReplaceFontForArabic.replaceArabicFont(ChooseLanguage.this,"DEFAULT","cocon-858df15efcbd1077e4131a6b1673af62.ttf");
    }
    public void setLTR(){
        String languageToLoad  = "en"; // ltr language English
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
        languagepref = getSharedPreferences("language",MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("languageToLoad",languageToLoad );
        editor.apply();
//        ReplceFontForEnglish.replceEnglishFont(ChooseLanguage.this,"DEFAULT","Roboto-Regular.ttf");
    }


}
