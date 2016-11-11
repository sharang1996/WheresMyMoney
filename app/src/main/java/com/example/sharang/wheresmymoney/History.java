package com.example.sharang.wheresmymoney;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class History extends AppCompatActivity {

    Context context;
    SwipeMenuListView lv;
    String email;
    ArrayList<HistoryItem> historyItems;
    HistoryCustomAdapter adapter;
    DatabaseReference rootref;
    User user;
    HistoryItem h;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_history);

        context=History.this;
        historyItems = (ArrayList<HistoryItem>) getIntent().getSerializableExtra("historyitem");
        email = getIntent().getStringExtra("email");
        rootref = FirebaseDatabase.getInstance().getReference();

        rootref.child("user").orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    user = messageSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        lv = (SwipeMenuListView)findViewById(R.id.lv);
        adapter = new HistoryCustomAdapter(context,historyItems,populateIconMapper());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(History.this,Details.class);
                i.putExtra("description",historyItems.get(position).getDescription());
                i.putExtra("amount",historyItems.get(position).getAmount());
                i.putExtra("category",historyItems.get(position).getCategory());
                i.putExtra("timestamp",historyItems.get(position).getTimestamp());
                if(historyItems.get(position).isIncome())
                    i.putExtra("isIncome",true);
                else i.putExtra("isIncome",false);
                startActivity(i);
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.argb(265,16,230,194)));
                // set item width
                openItem.setWidth(200);
                // set item icon
                openItem.setIcon(R.drawable.ic_edit);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.argb(265,16,230,194)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        lv.setMenuCreator(creator);

        lv.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                //Toast.makeText(History.this,"It works!!!!!",Toast.LENGTH_LONG).show();
            }
        });


        lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                HistoryItem item = historyItems.get(position);
                switch (index) {
                    case 0:

                        editTransaction(position);

                        break;
                    case 1:

                        historyItems.remove(position);
                        adapter.notifyDataSetChanged();

                        if(item.isIncome())
                        {
                            user.removeIncome(item);
                        }

                        else
                        {
                            user.removeExpenditure(item);
                        }

                        Map usermap = new ObjectMapper().convertValue(user, Map.class);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(user.getUid(),usermap);

                        rootref.child("user").updateChildren(childUpdates);

                        break;
                }
                return false;
            }});

    }

    private void editTransaction(int position) {
        h = historyItems.get(position);
        this.position = position;

        Intent i = new Intent(History.this,TransactionEdit.class);

        i.putExtra("Amount",h.getAmount());
        i.putExtra("Description",h.getDescription());
        startActivityForResult(i,5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5 && resultCode == RESULT_OK) {
            String description = data.getStringExtra("description");
            if (description == null) description = "no description";

            double amount = Double.parseDouble(data.getStringExtra("amount"));

            h.setDescription(description);
            h.setAmount(amount);

            historyItems.remove(position);
            historyItems.add(position,h);

            adapter.notifyDataSetChanged();

            user.makeEdit(h);

            Map usermap = new ObjectMapper().convertValue(user, Map.class);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(user.getUid(), usermap);

            rootref.child("user").updateChildren(childUpdates);

            Toast.makeText(History.this, description + " " + amount, Toast.LENGTH_SHORT).show();
        }
    }
    private HashMap<String,String> populateIconMapper() {

        HashMap<String,String> iconMapper = new HashMap<>();
        iconMapper.put("Financial Income","fincome");
        iconMapper.put("Gambling","gambling");
        iconMapper.put("Odd jobs","oddjobs");
        iconMapper.put("Personal Savings","personal");
        iconMapper.put("Pension","pension");
        iconMapper.put("Rent","rent");
        iconMapper.put("Salary","salary");
        iconMapper.put("Home","home");
        iconMapper.put("Debt","debt");
        iconMapper.put("Other","other");

        iconMapper.put("Accomodation","accomodation");
        iconMapper.put("Bar","bar");
        iconMapper.put("Books/Magazines","book");
        iconMapper.put("Car","car");
        iconMapper.put("Children","children");
        iconMapper.put("Cigarettes","cigarettes");
        iconMapper.put("Clothing","clothing");
        iconMapper.put("Eating Out","eatiingout");
        iconMapper.put("Entertainment","entertainment");
        iconMapper.put("Family","family");
        iconMapper.put("Fuel","fuel");
        iconMapper.put("Gifts","gifts");
        iconMapper.put("Health","health");
        iconMapper.put("Insurance","insurance");
        iconMapper.put("Pets","pets");
        iconMapper.put("Shoes","shoes");
        iconMapper.put("Shopping","shopping");
        iconMapper.put("Tax","tax");
        iconMapper.put("Transport","tansport");

        return iconMapper;
    }
}
