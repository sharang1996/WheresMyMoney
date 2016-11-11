package com.example.sharang.wheresmymoney;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryResult extends AppCompatActivity {

    Context context;
    ArrayList<HistoryItem> historyItems;
    HistoryCustomAdapter adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_query_result);
        context=QueryResult.this;
        historyItems = (ArrayList<HistoryItem>) getIntent().getSerializableExtra("queryresult");

        lv = (ListView)findViewById(R.id.lv);
        adapter = new HistoryCustomAdapter(context,historyItems,populateIconMapper());
        lv.setAdapter(adapter);

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
