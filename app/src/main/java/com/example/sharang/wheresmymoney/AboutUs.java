package com.example.sharang.wheresmymoney;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutUs extends AppCompatActivity {

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        tv = (TextView)findViewById(R.id.textView5);
        tv.setText("This is a free open source project developed by sharang gupta using a firebase backend." +
                " reviews , compliments and criticism are welcome at sharang.gupta@sitpune.edu.in ");
    }
}
