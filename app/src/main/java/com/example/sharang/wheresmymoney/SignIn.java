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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.sharang.wheresmymoney.Splash.calledAlready;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText password,email;
    Button login,signup;
    pl.tajchert.sample.DotsTextView loading;
    //static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        mAuth = FirebaseAuth.getInstance();
        password = (EditText)findViewById(R.id.input_password);
        email = (EditText)findViewById(R.id.input_email);
        login = (Button)findViewById(R.id.btn_login);
        signup = (Button)findViewById(R.id.signupredirect);
        loading = (pl.tajchert.sample.DotsTextView)findViewById(R.id.dots);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString()))
                {
                    Toast.makeText(SignIn.this, R.string.coaxuser,Toast.LENGTH_SHORT).show();
                }

                else{
                    loading.showAndPlay();

                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent i = new Intent(SignIn.this,Main2Activity.class);
                                        i.putExtra("email",email.getText().toString());
                                        startActivity(i);
                                        finish();
                                    }

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignIn.this, R.string.failedmsg,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                }

        });

       signup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i  = new Intent(SignIn.this,SignUp.class);
               startActivity(i);
               finish();
           }
       });

    }
}
