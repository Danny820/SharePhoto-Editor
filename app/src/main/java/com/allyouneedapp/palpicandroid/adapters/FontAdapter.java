package com.allyouneedapp.palpicandroid.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allyouneedapp.palpicandroid.R;


import java.util.ArrayList;

/**
 * Created by Soul on 11/1/2016.
 */

public class FontAdapter extends ArrayAdapter {
    Activity context;
    String word;
    ArrayList<Typeface> typefaces = new ArrayList<>();
    public FontAdapter(Activity context, String string, ArrayList<Typeface> typefaces) {
        super(context, R.layout.item_font, typefaces);
        this.context = context;
        this.word = string;
        this.typefaces = typefaces;
    }

    @Override
    public int getCount() {
        return typefaces.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.item_font, null);
        }
        TextView textView = (TextView) row.findViewById(R.id.text_item_font);
        textView.setTypeface(typefaces.get(position));
        textView.setText(word);
        return  row;
    }
}
