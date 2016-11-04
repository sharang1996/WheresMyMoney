package com.example.sharang.wheresmymoney;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TotalIncome extends AppCompatActivity {

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_income);
        tv = (TextView)findViewById(R.id.textView3);
        tv.setText(getIntent().getStringExtra("total_income"));
        tv.setTextColor(Color.GREEN);
    }
}
