package com.example.sharang.wheresmymoney;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Share extends AppCompatActivity {

    EditText email,quoteAreaText;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        email = (EditText)findViewById(R.id.email);
        quoteAreaText = (EditText)findViewById(R.id.quoteTextArea);
        quoteAreaText.setText(getIntent().getStringExtra("transactions"));
        send = (Button)findViewById(R.id.bsend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                String[] recipients = new String[]{email.getText().toString(), "",};
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Transaction History for "+getIntent().getStringExtra("name"));
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getIntent().getStringExtra("transactions"));
                emailIntent.setType("text/plain");
                startActivity(Intent.createChooser(emailIntent, "Send mail client :"));
            }
        });
    }
}
