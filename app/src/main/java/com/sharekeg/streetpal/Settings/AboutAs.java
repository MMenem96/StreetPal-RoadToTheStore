package com.sharekeg.streetpal.Settings;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sharekeg.streetpal.Login.LoginActivity;
import com.sharekeg.streetpal.R;

public class AboutAs extends AppCompatActivity {
    private ImageView  IV_back;
    String contactNumber, contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_as);


        IV_back = (ImageView) findViewById(R.id.IV_back_About);
        IV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutAs.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }
}
