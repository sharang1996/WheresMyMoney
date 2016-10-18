package com.example.sharang.wheresmymoney;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    String uid;
    DatabaseReference rootref;
    String email;
    User user;
    private Income newIncome;
    private Expenditure newExpenditure;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.textView);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent i = getIntent();
        email = i.getStringExtra("email");
        rootref = FirebaseDatabase.getInstance().getReference();

        rootref.child("user").orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    user = messageSnapshot.getValue(User.class);
                    uid = user.getUid();
                    user.calculateBalance();
                    tv.setText(user.getBalance()+"");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final FloatingActionButton fab_main = (FloatingActionButton) findViewById(R.id.fab_main);
        final FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_income);
        final FloatingActionButton fab_remove = (FloatingActionButton) findViewById(R.id.fab_expendature);
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fab_add.getVisibility()==View.INVISIBLE) {
                    fab_add.setVisibility(View.VISIBLE);
                    fab_remove.setVisibility(View.VISIBLE);
                    fab_main.setImageResource(R.drawable.ic_clear);
                }
                else{
                    fab_add.setVisibility(View.INVISIBLE);
                    fab_remove.setVisibility(View.INVISIBLE);
                    fab_main.setImageResource(R.drawable.ic_addw);

                }
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIncomeDialog();
                user.calculateBalance();
                tv.setText(user.getBalance()+"");
            }
        });

        fab_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpenditureDialog();
                user.calculateBalance();
                tv.setText(user.getBalance()+"");
            }
        });
    }

    private void addIncomeDialog() {
        startActivityForResult(new Intent(MainActivity.this,IncomeCategoryActivity.class), 1);
    }

    private void addExpenditureDialog() {
        startActivityForResult(new Intent(MainActivity.this,ExpenditureCategoryActivity.class), 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            String category = data.getStringExtra("category");
            if(category==null) category = "other";

            newIncome = new Income(category);

            startActivityForResult(new Intent(MainActivity.this,IncomeDialogActivity.class) , 2);

            Toast.makeText(MainActivity.this,category+" selected",Toast.LENGTH_SHORT).show();
        }
        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            String description = data.getStringExtra("description");
            if(description==null) description = "no description";

            double amount = Double.parseDouble(data.getStringExtra("amount"));

            newIncome.setDescription(description);
            newIncome.setAmount(amount);

            user.addIncome(newIncome);

            Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(uid,usermap);

            rootref.child("user").updateChildren(childUpdates);

            Toast.makeText(MainActivity.this,description+" " + amount,Toast.LENGTH_SHORT).show();
        }

        if(requestCode == 3 && resultCode == RESULT_OK)
        {
            String category = data.getStringExtra("category");
            if(category==null) category = "other";

            newExpenditure = new Expenditure(category);

            startActivityForResult(new Intent(MainActivity.this,ExpenditureDialogActivity.class) , 4);

            Toast.makeText(MainActivity.this,category+" selected",Toast.LENGTH_SHORT).show();
        }

        if(requestCode == 4 && resultCode == RESULT_OK)
        {
            String description = data.getStringExtra("description");
            if(description==null) description = "no description";

            double amount = Double.parseDouble(data.getStringExtra("amount"));

            newExpenditure.setDescription(description);
            newExpenditure.setAmount(amount);

            user.addExpenditure(newExpenditure);

            Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(uid,usermap);
            rootref.child("user").updateChildren(childUpdates);
            Toast.makeText(MainActivity.this,description+" " + amount,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,SignIn.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
