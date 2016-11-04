package com.example.sharang.wheresmymoney;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.sharang.wheresmymoney.Splash.calledAlready;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SELECT_FILE = 9;
    private static final int REQUEST_CAMERA = 10;
    String uid;
    DatabaseReference rootref;
    String email;
    User user;
    private Income newIncome;
    private Expenditure newExpenditure;
    TextView tv,navname,navemail;
    ImageView profilePicture;
    private String userChoosenTask;
    private String strBase64;
    NavigationView navigationView;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    //Done : add profile picture from camera intent or local storage...

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);



        profilePicture = (de.hdodenhof.circleimageview.CircleImageView)headerView.findViewById(R.id.profile_image);
        navemail =(TextView) headerView.findViewById(R.id.email);
        navname =(TextView) headerView.findViewById(R.id.name);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String encodedBitmap = preferences.getString("encodedBitmap","nosavedimage");
        if(encodedBitmap.equals("nosavedimage"))
            profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.nopp));
        else{
            byte[] imageAsBytes = Base64.decode(encodedBitmap.getBytes(),Base64.DEFAULT);
            profilePicture.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

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
                    navname.setText(user.getName());
                    navemail.setText(user.getEmail());
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
                // DONE: 20/10/16  update the firebase database

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
                // DONE: 20/10/16  update the firebase database

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
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String category = data.getStringExtra("category");
            if (category == null) category = "other";

            newIncome = new Income(category);

            startActivityForResult(new Intent(Main2Activity.this, IncomeDialogActivity.class), 2);

            Toast.makeText(Main2Activity.this, category + " selected", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            String description = data.getStringExtra("description");
            if (description == null) description = "no description";

            double amount = Double.parseDouble(data.getStringExtra("amount"));

            newIncome.setDescription(description);
            newIncome.setAmount(amount);
            newIncome.setTimestamp(System.currentTimeMillis());

            user.addIncome(newIncome);

            Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(uid, usermap);

            rootref.child("user").updateChildren(childUpdates);

            Toast.makeText(Main2Activity.this, description + " " + amount, Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 3 && resultCode == RESULT_OK) {
            String category = data.getStringExtra("category");
            if (category == null) category = "other";

            newExpenditure = new Expenditure(category);

            startActivityForResult(new Intent(Main2Activity.this, ExpenditureDialogActivity.class), 4);

            Toast.makeText(Main2Activity.this, category + " selected", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 4 && resultCode == RESULT_OK) {
            String description = data.getStringExtra("description");
            if (description == null) description = "no description";

            double amount = Double.parseDouble(data.getStringExtra("amount"));

            newExpenditure.setDescription(description);
            newExpenditure.setAmount(amount);
            newExpenditure.setTimestamp(System.currentTimeMillis());

            user.addExpenditure(newExpenditure);

            Map<String, Object> usermap = new ObjectMapper().convertValue(user, Map.class);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(uid, usermap);
            rootref.child("user").updateChildren(childUpdates);
            Toast.makeText(Main2Activity.this, description + " " + amount, Toast.LENGTH_SHORT).show();
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);

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

        if(id==R.id.profile){
            Intent i = new Intent(Main2Activity.this,Profile.class);
            i.putExtra("name",user.getName());
            i.putExtra("email",user.getEmail());
            i.putExtra("password",user.getPassword());
            //i.putExtra("profilepic",PreferenceManager.getDefaultSharedPreferences(this).getString("encodedBitmap","nosavedimage"));
            i.putExtra("name",user.getName());
            startActivity(i);
        }
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

        } else if (id == R.id.nav_total_income) {

            Intent  i = new Intent(Main2Activity.this,TotalIncome.class);
            i.putExtra("total_income",user.getTotalIncome());
            startActivity(i);

        } else if (id == R.id.nav_total_expenditure) {

            Intent  i = new Intent(Main2Activity.this,TotalExpenditure.class);
            i.putExtra("total_exp",user.getTotalExpenditure());
            startActivity(i);

        } else if (id == R.id.nav_share) {
            StringBuilder sb = composeString(user.getChronologicalEvent());
            Intent i = new Intent(Main2Activity.this,Share.class);
            i.putExtra("transactions",sb.toString());
            i.putExtra("name",user.getName());
            startActivity(i);

        }else if (id == R.id.nav_about) {
            Intent  i = new Intent(Main2Activity.this,AboutUs.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private StringBuilder composeString(ArrayList<HistoryItem> chronologicalEvent) {

        StringBuilder sb = new StringBuilder();
        for(HistoryItem hi : chronologicalEvent)
        {
            sb.append(hi.toString());
        }
        return sb;
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(Main2Activity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                    profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.nopp));
                }
                break;
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        profilePicture.setImageBitmap(bm);

        //Convert Bitmap to Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        strBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        editor.putString("encodedBitmap",strBase64);
        editor.apply();
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            file.createNewFile();
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        profilePicture.setImageBitmap(bitmap);

        //Convert Bitmap to Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        strBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        editor.putString("encodedBitmap",strBase64);
        editor.apply();
    }

}
