package me.sid.smartcropper.views.customView.toggleButton;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;

public abstract class CompoundToggleButton extends FrameLayout implements ToggleButton {
    private static final String LOG_TAG = CompoundToggleButton.class.getSimpleName();

    private boolean mChecked;
    private boolean mBroadcasting;
    private OnCheckedChangeListener mOnCheckedWidgetListener;

    public CompoundToggleButton(Context context) {
        super(context);
        setClickable(true);
    }

    public CompoundToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompoundToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedWidgetListener = listener;
    }

    @Override
    @CallSuper
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;

            if (mBroadcasting) {
                return;
            }
            this.post(new Runnable() {
                @Override
                public void run() {
                    mBroadcasting = true;
                    if (mOnCheckedWidgetListener != null) {
                        mOnCheckedWidgetListener.onCheckedChanged(CompoundToggleButton.this, mChecked);
                    }
                    mBroadcasting = false;
                }
            });
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    @CallSuper
    public void toggle() {
        setChecked(!mChecked);
    }
}