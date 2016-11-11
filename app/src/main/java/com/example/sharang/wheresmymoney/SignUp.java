package com.example.sharang.wheresmymoney;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.sharang.wheresmymoney.Splash.calledAlready;

public class SignUp extends AppCompatActivity {


    private FirebaseAuth mAuth;
    EditText name,email,password;
    Button submit,signin;
    DatabaseReference rootref;
    pl.tajchert.sample.DotsTextView loading;
    //static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        mAuth = FirebaseAuth.getInstance();
        rootref = FirebaseDatabase.getInstance().getReference();
        name = (EditText)findViewById(R.id.input_name);
        email = (EditText)findViewById(R.id.input_email);
        password = (EditText)findViewById(R.id.input_password);
        submit = (Button)findViewById(R.id.btn_signup);
        signin = (Button)findViewById(R.id.loginredirect);
        loading = (pl.tajchert.sample.DotsTextView)findViewById(R.id.dots);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString()))
                {
                    Toast.makeText(SignUp.this, R.string.coaxsignup,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loading.showAndPlay();

                    mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){

                                        final User newUser = new User();

                                        newUser.setEmail(email.getText().toString());
                                        newUser.setName(name.getText().toString());
                                        newUser.setPassword(password.getText().toString());

                                        String key = rootref.child("user").push().getKey();
                                        newUser.setUid(key);

                                        Map usermap = new ObjectMapper().convertValue(newUser, Map.class);
                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put(key,usermap);

                                        rootref.child("user").updateChildren(childUpdates);

                                        Intent i = new Intent(SignUp.this,SignIn.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(SignUp.this, R.string.failedsignin,
                                                Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                }

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(SignUp.this,SignIn.class);
                startActivity(i);
                finish();
            }
        });
    }


}
