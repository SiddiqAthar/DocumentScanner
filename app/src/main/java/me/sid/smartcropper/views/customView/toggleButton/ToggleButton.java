package me.sid.smartcropper.views.customView.toggleButton;

import android.widget.Checkable;

public interface ToggleButton extends Checkable {

    void setOnCheckedChangeListener(OnCheckedChangeListener listener);
}

