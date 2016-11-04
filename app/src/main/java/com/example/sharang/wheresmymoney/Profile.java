package com.example.sharang.wheresmymoney;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    TextView name,email;
    EditText password;
    Button changePassword;
    ImageView profile;
    DatabaseReference rootref;
    User user;
    String uid;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = (TextView)findViewById(R.id.tvname);
        email = (TextView)findViewById(R.id.tvemail);
        password = (EditText)findViewById(R.id.tvpassword);
        changePassword = (Button)findViewById(R.id.bchangepassword);
        profile = (ImageView)findViewById(R.id.profileiv);

        name.setText(getIntent().getStringExtra("name"));
        password.setText(getIntent().getStringExtra("password"));
        byte[] imageAsBytes = Base64.decode(PreferenceManager.getDefaultSharedPreferences(this).getString("encodedBitmap","nosavedimage").getBytes(),Base64.DEFAULT);
        profile.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rootref = FirebaseDatabase.getInstance().getReference();

                rootref.child("user").orderByChild("email").equalTo(email.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {

                            user = messageSnapshot.getValue(User.class);
                            uid = user.getUid();
                            user.setPassword(password.getText().toString());

                            firebaseUser.updatePassword(password.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("#####", "User password updated.");
                                                Toast.makeText(Profile.this,"Password Updated",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}
