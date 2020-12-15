package me.sid.smartcropper.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class MultiImagesModle {


    ArrayList<Bitmap> bitmapsMulti= new ArrayList<>();
    public ArrayList<Bitmap> getBitmapsMulti() {
        return bitmapsMulti;
    }

    public void setBitmapsMulti(ArrayList<Bitmap> bitmapsMulti) {
        this.bitmapsMulti = bitmapsMulti;
    }
}
