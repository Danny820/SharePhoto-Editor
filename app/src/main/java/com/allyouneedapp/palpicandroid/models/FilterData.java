package com.allyouneedapp.palpicandroid.models;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by Danny on 10/28/2016.
 */

public class FilterData {
    public GPUImageFilter filterType;
    public String filterTitle;
    public FilterData(GPUImageFilter type, String title) {
        this.filterTitle = title;
        this.filterType = type;
    }
}
