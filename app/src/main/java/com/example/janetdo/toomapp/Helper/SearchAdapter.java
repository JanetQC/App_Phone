package com.example.janetdo.toomapp.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.janetdo.toomapp.MainActivity;
import com.example.janetdo.toomapp.MapActivity;
import com.example.janetdo.toomapp.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by janetdo on 30.01.18.
 */

public class SearchAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<Item> items;

    public SearchAdapter(Activity context, List<Item> items, String[] itemnames) {
        super(context, R.layout.search_item_view, itemnames);
        this.context = context;
        this.items = items;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.search_item_view, null, true);

        TextView itemName = (TextView) rowView.findViewById(R.id.itemName);
        ImageView navigation = (ImageView) rowView.findViewById(R.id.navigation);
        ImageView information = (ImageView) rowView.findViewById(R.id.information);
        String name = items.get(position).getName();
        itemName.setText(name);

    MainActivity.setNavigationOnClickListener(navigation, position);
    return rowView;
    }

}