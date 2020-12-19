package me.sid.smartcropper.utils;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;


public class CustomLinearLayoutManager extends GridLayoutManager {
    public CustomLinearLayoutManager(Context context, int span, int orientation, boolean reverseLayout) {
        super(context, span, GridLayoutManager.HORIZONTAL, false);


    }

    // it will always pass false to RecyclerView when calling "canScrollVertically()" method.
    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() { return false; }
}