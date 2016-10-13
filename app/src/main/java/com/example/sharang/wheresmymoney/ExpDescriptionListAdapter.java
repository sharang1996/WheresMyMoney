package com.example.sharang.wheresmymoney;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sharang on 12/10/16.
 */

public class ExpDescriptionListAdapter extends ArrayAdapter<ExpenditureCategoryActivity.Description>{

    private final List<ExpenditureCategoryActivity.Description> descriptionList;
    private final Activity context;

    static class ViewHolder {
        protected TextView name;
        protected ImageView categoryIcon;
    }

    public ExpDescriptionListAdapter(Activity context, List<ExpenditureCategoryActivity.Description> descriptionList) {

        super(context,R.layout.expenditure_description_row,descriptionList);
        this.context = context;
        this.descriptionList = descriptionList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        if(convertView == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.expenditure_description_row,null);
            final ExpDescriptionListAdapter.ViewHolder viewHolder = new ExpDescriptionListAdapter.ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.namtv);
            viewHolder.categoryIcon = (ImageView) view.findViewById(R.id.ctIcon);
            view.setTag(viewHolder);
        }
        else view=convertView;

        ViewHolder viewHolder  = (ViewHolder) view.getTag();
        viewHolder.name.setText(descriptionList.get(position).getCategory());
        viewHolder.categoryIcon.setImageDrawable(descriptionList.get(position).getCategoryIcon());

        return view;
    }
}
