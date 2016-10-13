package com.example.sharang.wheresmymoney;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent i = getIntent();
        email = i.getStringExtra("email");
        rootref = FirebaseDatabase.getInstance().getReference();

        rootref.child("user").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    user = messageSnapshot.getValue(User.class);
                    uid = user.getUid();
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
            }
        });

        fab_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpenditureDialog();
            }
        });
    }

    private void addIncomeDialog() {

        startActivityForResult(new Intent(MainActivity.this,IncomeCategoryActivity.class), 1);

        /*DialogFragment df = new DialogFragment(){

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.addincome,null);
                builder.setView(view);
                //capture
                final EditText amountEditText=(EditText)view.findViewById(R.id.edit_amount);
                final EditText descriptionEditText=(EditText)view.findViewById(R.id.edit_description);
                builder.setNegativeButton(android.R.string.cancel,null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newIncome.setAmount(Double.parseDouble(amountEditText.getText().toString()));
                        newIncome.setDescription(descriptionEditText.getText().toString());
                        user.incomes.add(newIncome);
                        HashMap<String,User> modified = new HashMap<>();
                        modified.put(uid,user);
                        rootref.setValue(modified);
                    }
                });
                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(),"addIncome");*/

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
            Log.i("#####",category);
        }
        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            String description = data.getStringExtra("description");
            if(description==null) description = "no description";
            double amount = Double.parseDouble(data.getStringExtra("amount"));
            newIncome.setDescription(description);
            newIncome.setAmount(amount);
            if(newIncome == null) Log.i("#####","im fuckkeeedddddd!!!!");
            else{
                Log.i("#####",newIncome.getDescription());
                Log.i("#####",newIncome.getAmount()+"");
            }

            //Log.i("$$$$$before",user.incomes.size()+"");

            user.addIncome(newIncome);

            Log.i("$$$$$after",user.incomes.size()+"");

            Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(uid,usermap);
            Log.i("$$$$$", childUpdates.toString());
            rootref.child("user").updateChildren(childUpdates);
            Toast.makeText(MainActivity.this,description+" " + amount,Toast.LENGTH_SHORT).show();
            Log.i("#####",description);
        }

        if(requestCode == 3 && resultCode == RESULT_OK)
        {
            String category = data.getStringExtra("category");
            if(category==null) category = "other";
            newExpenditure = new Expenditure(category);
            startActivityForResult(new Intent(MainActivity.this,ExpenditureDialogActivity.class) , 4);
            Toast.makeText(MainActivity.this,category+" selected",Toast.LENGTH_SHORT).show();
            Log.i("#####",category);
        }

        if(requestCode == 4 && resultCode == RESULT_OK)
        {
            String description = data.getStringExtra("description");
            if(description==null) description = "no description";
            double amount = Double.parseDouble(data.getStringExtra("amount"));
            newExpenditure.setDescription(description);
            newExpenditure.setAmount(amount);
            if(newExpenditure == null) Log.i("#####","im fuckkeeedddddd!!!!");
            else{
                Log.i("#####",newExpenditure.getDescription());
                Log.i("#####",newExpenditure.getAmount()+"");
            }

            //Log.i("$$$$$before",user.expenditures.size()+"");

            user.addExpenditure(newExpenditure);

            Log.i("$$$$$after",user.expenditures.size()+"");

            Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(uid,usermap);
            Log.i("$$$$$", childUpdates.toString());
            rootref.child("user").updateChildren(childUpdates);
            Toast.makeText(MainActivity.this,description+" " + amount,Toast.LENGTH_SHORT).show();
            Log.i("#####",description);
        }
    }

    private void addExpenditureDialog() {
        startActivityForResult(new Intent(MainActivity.this,ExpenditureCategoryActivity.class), 3);
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
