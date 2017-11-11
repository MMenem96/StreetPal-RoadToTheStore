package com.sharekeg.streetpal.Settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sharekeg.streetpal.R;

public class Report_a_problem extends AppCompatActivity  {
    private TextView cancel;
    private TextView send;
    private String feedback;
    EditText edittext;
    String      contactNumber  ,contactName;
    int lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                if (edittext.getText().toString().equals("")){
                    send.setVisibility(View.GONE);
                }
                else{
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

                contactNumber=getIntent().getExtras().getString("contactNumber");
                contactName = getIntent().getExtras().getString("contactName");

                feedback = edittext.getText().toString();

                if (!feedback.matches("")) {
                    send.setVisibility(View.VISIBLE);
                    Intent email = new Intent(android.content.Intent.ACTION_SEND);
                    email.setType("plain/text");
                    email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"info@streetpal.com"});
                    email.putExtra(android.content.Intent.EXTRA_SUBJECT, "Lads to Leaders/Leaderettes Questions and/or Comments");
                    email.putExtra(android.content.Intent.EXTRA_TEXT,"Feedback :" + edittext.getText().toString() );
                    startActivity(Intent.createChooser(email, getApplicationContext().getResources()
                            .getString(R.string.sendmail_feedbackActivity)));
                    isMessageEmpty();
                    lang=1;
                    return;


                } else {
                    isMessageEmpty();
                    send.setVisibility(View.GONE);
                    Toast.makeText(Report_a_problem.this, R.string.feedback_err, Toast.LENGTH_SHORT).show();

                    lang=2;

                }



            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(Report_a_problem.this, SettingsActivity.class);
                i.putExtra("contactName", contactName);
                i.putExtra("contactNumber", contactNumber);
                startActivity(i);
            }
        });


    }

    private void isMessageEmpty() {

                if (lang==1) {
                    Toast.makeText(getApplicationContext(),
                            "Done!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Report_a_problem.this, SettingsActivity.class);
                    i.putExtra("contactName", contactName);
                    i.putExtra("contactNumber", contactNumber);
                    startActivity(i);


                }
                if (lang==2) {
                    Toast.makeText(Report_a_problem.this, R.string.feedback_err, Toast.LENGTH_SHORT).show();

                }

    }

}
