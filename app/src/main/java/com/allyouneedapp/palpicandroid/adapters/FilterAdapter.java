package com.allyouneedapp.palpicandroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.allyouneedapp.palpicandroid.R;
import com.allyouneedapp.palpicandroid.models.FilterData;


import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by Danny on 10/28/2016.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {

    Context mContext;
    ArrayList<FilterData> itemData = new ArrayList<>();
    Bitmap bitmap;

    public FilterAdapter(Context mContext, Bitmap bitmap, ArrayList<FilterData> arrayData) {
        super();
        this.mContext = mContext;
        this.itemData = arrayData;
        this.bitmap = bitmap;
    }
    @Override
    public FilterHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_filter, parent, false);
        FilterHolder viewHolder = new FilterHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, int position) {
        String filterTitle = itemData.get(position).filterTitle;
        GPUImageFilter filter = itemData.get(position).filterType;
        holder.title.setText(filterTitle);
        Bitmap imageBitmap;
        GPUImage image = new GPUImage(mContext);
        image.setFilter(filter);
        imageBitmap = image.getBitmapWithFilterApplied(this.bitmap);
        holder.imFilter.setImageBitmap(imageBitmap);
    }


    @Override
    public int getItemCount() {
        return this.itemData.size();
    }

    public class FilterHolder extends RecyclerView.ViewHolder {
        public ImageView imFilter;
        public TextView title;

        public FilterHolder(View itemView) {
            super(itemView);
            imFilter = (ImageView) itemView.findViewById(R.id.btn_filter_item);
            title = (TextView) itemView.findViewById(R.id.text_filter_item);
        }
    }

}

