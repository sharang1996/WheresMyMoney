package com.example.sharang.wheresmymoney;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sharang on 26/10/16.
 */
public class HistoryCustomAdapter extends BaseAdapter {

    Context context;
    ArrayList<HistoryItem> historyItems;
    HashMap<String,String> iconMapper;

    private static LayoutInflater inflater=null;

    public HistoryCustomAdapter(Context context, ArrayList<HistoryItem> historyItems, HashMap<String, String> iconMapper) {
        this.context=context;
        this.historyItems=historyItems;
        this.iconMapper=iconMapper;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return historyItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView;
        HistoryItem historyItem;

        historyItem = historyItems.get(position);
        rowView=inflater.inflate(R.layout.history_item,null);

        holder.tvCat = (TextView)rowView.findViewById(R.id.tv_category);
        holder.tvAmt = (TextView)rowView.findViewById(R.id.tv_amount);
        holder.tvDate = (TextView)rowView.findViewById(R.id.tv_date);
        holder.img = (ImageView)rowView.findViewById(R.id.iv);

        //ToDo : fill in the data for these fields

        holder.tvCat.setText(historyItem.getCategory());
        holder.tvAmt.setText(historyItem.getAmount()+"");

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
        Calendar c    = Calendar.getInstance();
        c.setTimeInMillis(historyItem.getTimestamp());
        Date day      = c.getTime();

        holder.tvDate.setText(df.format(day));

        Resources res = context.getResources();
        String mDrawableName = iconMapper.get(historyItem.getCategory());
        int resID = res.getIdentifier(mDrawableName , "drawable", context.getPackageName());
        holder.img.setImageResource(resID);

        return rowView;
    }

    public class Holder
    {
        TextView tvCat,tvAmt,tvDate;
        ImageView img;
    }

}
