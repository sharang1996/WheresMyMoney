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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
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
    ImageView profile;
    DatabaseReference rootref;
    User user;
    String uid;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    MorphingButton btnMorph;
    MorphingButton.Params circle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.profile_password);
        btnMorph = (MorphingButton) findViewById(R.id.bchangepassword);
        profile = (ImageView)findViewById(R.id.profile_image);

        name.setText(getIntent().getStringExtra("name"));
        password.setText(getIntent().getStringExtra("password"));
        email.setText(getIntent().getStringExtra("email"));
        String pimage = PreferenceManager.getDefaultSharedPreferences(this).getString("encodedBitmap","nosavedimage");

        if(!pimage.contentEquals("nosavedimage"))
        {
            byte[] imageAsBytes = Base64.decode(pimage.getBytes(),Base64.DEFAULT);
            profile.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

        }

        circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius(86) // 56 dp
                .width(110) // 56 dp
                .height(110) // 56 dp
                .color(Color.GREEN) // normal state color
                .colorPressed(Color.GREEN) // pressed state color
                .icon(R.drawable.ic_done); // icon

        btnMorph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnMorph.morph(circle);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnMorph.getLayoutParams();
                params.setMargins(30, 70, 0, 0);//left, top, right, bottom
                btnMorph.setLayoutParams(params);

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
