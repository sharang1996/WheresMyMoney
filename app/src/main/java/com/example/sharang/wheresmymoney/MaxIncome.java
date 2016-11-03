package com.example.sharang.wheresmymoney;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MaxIncome extends AppCompatActivity {

    TextView category,description,amount,timestamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_income);

        category = (TextView)findViewById(R.id.tvcategory);
        description = (TextView)findViewById(R.id.tvdescription);
        amount = (TextView)findViewById(R.id.tvamount);
        timestamp = (TextView)findViewById(R.id.tvtimestamp);

        category.setText(getIntent().getStringExtra("category"));
        description.setText(getIntent().getStringExtra("description"));
        amount.setText(getIntent().getDoubleExtra("amount",0.0)+"");

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
        Calendar c    = Calendar.getInstance();
        c.setTimeInMillis(getIntent().getLongExtra("timestamp",0));
        Date day      = c.getTime();

        timestamp.setText(df.format(day));
    }
}
