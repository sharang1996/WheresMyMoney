package com.example.sharang.wheresmymoney;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sharang on 11/10/16.
 */
public class DescriptionListAdapter extends ArrayAdapter<IncomeCategoryActivity.Description> {

    private final List<IncomeCategoryActivity.Description> descriptionList;
    private final Activity context;

    static class ViewHolder {
        protected TextView name;
        protected ImageView categoryIcon;
    }


    public DescriptionListAdapter(Activity context, List<IncomeCategoryActivity.Description> descriptionList) {

        super(context,R.layout.income_description_row,descriptionList);
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
            view = inflater.inflate(R.layout.income_description_row,null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
            view.setTag(viewHolder);
        }
        else view=convertView;

        ViewHolder viewHolder  = (ViewHolder)view.getTag();
        viewHolder.name.setText(descriptionList.get(position).getCategory());
        viewHolder.categoryIcon.setImageDrawable(descriptionList.get(position).getCategoryIcon());

        return view;
    }
}
