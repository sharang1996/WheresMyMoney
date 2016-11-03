package com.example.sharang.wheresmymoney;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.sharang.wheresmymoney.Splash.calledAlready;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_main2);

        //Date date = new Date(System.currentTimeMillis());
        //Log.i("#####",DateFormat.getDateInstance(DateFormat.LONG).format(date));

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
        Calendar c    = Calendar.getInstance();
        Date day      = c.getTime();
        String s2     = df.format(day);

        Log.i("#####",s2);

        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        tv = (TextView)findViewById(R.id.textView);

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
                    tv.setTextSize(40);
                    if(user.getBalance()>0)
                        tv.setTextColor(Color.GREEN);
                    else
                    if(user.getBalance()<0)
                        tv.setTextColor(Color.RED);



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
                // TODO: 20/10/16  update the firebase database

                Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(uid,usermap);

                rootref.child("user").updateChildren(childUpdates);
            }
        });

        fab_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpenditureDialog();
                user.calculateBalance();
                tv.setText(user.getBalance()+"");
                // TODO: 20/10/16  update the firebase database

                Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(uid,usermap);

                rootref.child("user").updateChildren(childUpdates);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void addIncomeDialog() {
        startActivityForResult(new Intent(Main2Activity.this,IncomeCategoryActivity.class), 1);
    }



    private void addExpenditureDialog() {
        startActivityForResult(new Intent(Main2Activity.this,ExpenditureCategoryActivity.class), 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            String category = data.getStringExtra("category");
            if(category==null) category = "other";

            newIncome = new Income(category);

            startActivityForResult(new Intent(Main2Activity.this,IncomeDialogActivity.class) , 2);

            Toast.makeText(Main2Activity.this,category+" selected",Toast.LENGTH_SHORT).show();
        }
        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            String description = data.getStringExtra("description");
            if(description==null) description = "no description";

            double amount = Double.parseDouble(data.getStringExtra("amount"));

            newIncome.setDescription(description);
            newIncome.setAmount(amount);
            newIncome.setTimestamp(System.currentTimeMillis());

            user.addIncome(newIncome);

            Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(uid,usermap);

            rootref.child("user").updateChildren(childUpdates);

            Toast.makeText(Main2Activity.this,description+" " + amount,Toast.LENGTH_SHORT).show();
        }

        if(requestCode == 3 && resultCode == RESULT_OK)
        {
            String category = data.getStringExtra("category");
            if(category==null) category = "other";

            newExpenditure = new Expenditure(category);

            startActivityForResult(new Intent(Main2Activity.this,ExpenditureDialogActivity.class) , 4);

            Toast.makeText(Main2Activity.this,category+" selected",Toast.LENGTH_SHORT).show();
        }

        if(requestCode == 4 && resultCode == RESULT_OK)
        {
            String description = data.getStringExtra("description");
            if(description==null) description = "no description";

            double amount = Double.parseDouble(data.getStringExtra("amount"));

            newExpenditure.setDescription(description);
            newExpenditure.setAmount(amount);
            newExpenditure.setTimestamp(System.currentTimeMillis());

            user.addExpenditure(newExpenditure);

            Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(uid,usermap);
            rootref.child("user").updateChildren(childUpdates);
            Toast.makeText(Main2Activity.this,description+" " + amount,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Main2Activity.this,SignIn.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.history) {


            Intent  i = new Intent(Main2Activity.this,History.class);

            Bundle information = new Bundle();
            Log.i("#####","main2activity : "+user.getChronologicalEvent().toString());
            information.putSerializable("historyitem", user.getChronologicalEvent());
            i.putExtras(information);
            i.putExtra("email",email);

            startActivity(i);
            /*for(HistoryItem historyItem : historyItems)
            {
             Log.i("blahhh","   "+historyItem.toString());
            }

              String mDrawableName = "myimageName";
              int resID = res.getIdentifier(mDrawableName , "drawable", getPackageName());
              imgView.setImageResource(resID);
             */

        } else if (id == R.id.nav_max_income) {
            Intent  i = new Intent(Main2Activity.this,MaxIncome.class);

            Income maxIncome = user.getMaxIncome();
            if(maxIncome==null) Toast.makeText(Main2Activity.this,"No income presently",Toast.LENGTH_SHORT).show();
            else{
                i.putExtra("description",maxIncome.getDescription());
                i.putExtra("amount",maxIncome.getAmount());
                i.putExtra("category",maxIncome.getCategory());
                i.putExtra("timestamp",maxIncome.getTimestamp());

                startActivity(i);
            }


        } else if (id == R.id.nav_max_expenditure) {
            Intent  i = new Intent(Main2Activity.this,MaxExpenditure.class);
            Expenditure maxExpenditure = user.getMaxExpenditure();
            if(maxExpenditure==null) Toast.makeText(Main2Activity.this,"No expenditure presently",Toast.LENGTH_SHORT).show();
            else{
                i.putExtra("description",maxExpenditure.getDescription());
                i.putExtra("amount",maxExpenditure.getAmount());
                i.putExtra("category",maxExpenditure.getCategory());
                i.putExtra("timestamp",maxExpenditure.getTimestamp());

                startActivity(i);
            }

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_exit) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
