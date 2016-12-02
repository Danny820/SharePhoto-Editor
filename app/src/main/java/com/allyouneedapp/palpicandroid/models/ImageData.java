package com.allyouneedapp.palpicandroid.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Danny on 10/26/2016.
 */

public class ImageData {
    public String filePath;
    public String thumbFilePaht;
    public Bitmap bitmap;

    public ImageData(){
        this.filePath = "";
        this.thumbFilePaht = "";
        this.bitmap = BitmapFactory.decodeFile(filePath);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
