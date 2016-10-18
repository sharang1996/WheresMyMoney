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

public class ExpenditureCategoryActivity extends ListActivity {

    public String outcomeDescription[];
    private TypedArray imgs;
    private List<Description> descriptionList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        populateDescriptionList();
        ArrayAdapter<ExpenditureCategoryActivity.Description> adapter = new ExpDescriptionListAdapter(this,descriptionList);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpenditureCategoryActivity.Description d = descriptionList.get(position);
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
        outcomeDescription = getResources().getStringArray(R.array.expenditureList);
        imgs = getResources().obtainTypedArray(R.array.expenditure_icons);
        for(int i =0;i<outcomeDescription.length;i++)
        {
            descriptionList.add(new ExpenditureCategoryActivity.Description(outcomeDescription[i],imgs.getDrawable(i)));
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
