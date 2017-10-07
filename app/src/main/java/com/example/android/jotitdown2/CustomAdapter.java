package com.example.android.jotitdown2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Simra Afreen on 24-09-2017.
 */

public class CustomAdapter extends ArrayAdapter<Todo> {

    Context mContext;
    ArrayList<Todo> mItems;

    public CustomAdapter(@NonNull Context context, ArrayList<Todo> todoArrayList) {
        super(context, 0);

        mContext = context;
        mItems = todoArrayList;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.detail_row_layout, null);
            holder = new ViewHolder();
            TextView title = (TextView)convertView.findViewById(R.id.title);
            TextView note = convertView.findViewById(R.id.note);
            TextView date = convertView.findViewById(R.id.date);
            TextView time = convertView.findViewById(R.id.time);
            holder.title = title;
            holder.content = note;
            holder.date = date;
            holder.time = time;
            convertView.setTag(holder);
        }

        holder = (ViewHolder)convertView.getTag();
        Todo todo = mItems.get(position);
        holder.title.setText(todo.getTitle());
        holder.content.setText(todo.getNote());
        long epoch = todo.getEpoch();
        int hr = todo.getHour();
        int min = todo.getMinute();
        holder.time.setText(hr + " : " + min +"");

        Date date = new Date(epoch);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("India"));
        String formatted = format.format(date);

        holder.date.setText(formatted);
        return convertView;
    }


    static class ViewHolder {

        TextView title;
        TextView content;
        TextView date;
        TextView time;
        Button button;
    }
}
