package com.example.sharang.wheresmymoney;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class IncomeCategoryActivity extends ListActivity {

    public String incomeDescription[];
    private TypedArray imgs;
    private List<Description> descriptionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateDescriptionList();
        ArrayAdapter<Description> adapter = new DescriptionListAdapter(this,descriptionList);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Description d = descriptionList.get(position);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("category",d.getCategory());
                setResult(RESULT_OK,returnIntent);
                imgs.recycle();
                finish();
            }
        });
    }

    private void populateDescriptionList() {
        descriptionList = new ArrayList<>();
        incomeDescription = getResources().getStringArray(R.array.incomeList);
        imgs = getResources().obtainTypedArray(R.array.income_icons);
        for(int i =0;i<incomeDescription.length;i++)
        {
            descriptionList.add(new Description(incomeDescription[i],imgs.getDrawable(i)));
        }
    }

    public class Description {

        private String category;
        private Drawable categoryIcon;

        public Description(String category, Drawable categoryIcon) {
            this.category = category;
            this.categoryIcon = categoryIcon;
        }

        public String getCategory() {
            return category;
        }

        public Drawable getCategoryIcon() {
            return categoryIcon;
        }
    }
}
