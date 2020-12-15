package me.sid.smartcropper.views.customView.toggleButton;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import me.sid.smartcropper.R;


public abstract class MarkerButton extends CompoundToggleButton {
    private static final String LOG_TAG = MarkerButton.class.getSimpleName();
    private static final int DEFAULT_TEXT_SIZE_SP = 14;

    protected static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    protected TextView mTvText;
    protected ImageView mIvBg;
    protected int mMarkerColor;
    protected boolean mRadioStyle;

    public MarkerButton(Context context) {
        this(context, null);
    }

    public MarkerButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_marker_button, this, true);
        mIvBg = (ImageView) findViewById(R.id.iv_bg);
        mTvText = (TextView) findViewById(R.id.tv_text);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.MarkerButton, 0, 0);
        try {
            CharSequence text = a.getText(R.styleable.MarkerButton_android_text);
            mTvText.setText(text);

            ColorStateList colors = a.getColorStateList(R.styleable.MarkerButton_android_textColor);
            if (colors == null) {
                colors = ContextCompat.getColorStateList(context, R.color.selector_marker_text);
            }
            mTvText.setTextColor(colors);

            float textSize = a.getDimension(R.styleable.MarkerButton_android_textSize, dpToPx(DEFAULT_TEXT_SIZE_SP));
            mTvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            mMarkerColor = a.getColor(R.styleable.MarkerButton_tbgMarkerColor, ContextCompat.getColor(getContext(), R.color.sea_green));

            mRadioStyle = a.getBoolean(R.styleable.MarkerButton_tbgRadioStyle, false);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void toggle() {
        // Do not allow toggle to unchecked state when mRadioStyle is true
        if (mRadioStyle && isChecked()) {
            return;
        }
        super.toggle();
    }

    public boolean isRadioStyle() {
        return mRadioStyle;
    }

    public void setRadioStyle(boolean radioStyle) {
        mRadioStyle = radioStyle;
    }

    public void setText(CharSequence text) {
        mTvText.setText(text);
    }

    public CharSequence getText() {
        return mTvText.getText();
    }

    public void setTextColor(int color) {
        mTvText.setTextColor(color);
    }

    public void setTextColor(ColorStateList colors) {
        mTvText.setTextColor(colors);
    }

    public ColorStateList getTextColors() {
        return mTvText.getTextColors();
    }

    public void setTextSize(float size) {
        mTvText.setTextSize(size);
    }

    public void setTextSize(int unit, float size) {
        mTvText.setTextSize(unit, size);
    }

    public float getTextSize() {
        return mTvText.getTextSize();
    }

    public void setTextBackground(Drawable drawable) {
        mTvText.setBackgroundDrawable(drawable);
    }

    public Drawable getTextBackground() {
        return mTvText.getBackground();
    }

    public void setCheckedImageDrawable(Drawable drawable) {
        mIvBg.setImageDrawable(drawable);
    }

    public Drawable getCheckedImageDrawable() {
        return mIvBg.getDrawable();
    }

    public int getMarkerColor() {
        return mMarkerColor;
    }

    public void setMarkerColor(int markerColor) {
        mMarkerColor = markerColor;
    }

    public TextView getTextView() {
        return mTvText;
    }

    protected int getDefaultTextColor() {
        return getTextColors().getDefaultColor();
    }

    protected int getCheckedTextColor() {
        return getTextColors().getColorForState(CHECKED_STATE_SET, getDefaultTextColor());
    }

    protected float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}