package com.example.sharang.wheresmymoney;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IncomeDialogActivity extends AppCompatActivity {
    EditText amountEditText;
    EditText descriptionEditText;
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addincome);
        amountEditText=(EditText)findViewById(R.id.edit_amount);
        descriptionEditText=(EditText)findViewById(R.id.edit_description);
        b = (Button)findViewById(R.id.bclick);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("amount",amountEditText.getText().toString());
                returnIntent.putExtra("description",descriptionEditText.getText().toString());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
