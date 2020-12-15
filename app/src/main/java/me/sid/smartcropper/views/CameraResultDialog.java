package me.sid.smartcropper.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;

import me.sid.smartcropper.R;

public class CameraResultDialog extends Dialog implements View.OnClickListener {
    TextView titleTv, descTv;
    ImageView frontBackImg;
    Button okayBtn, cancelBtn;
    private CallbacksForCNIC callbacksForCNIC;
    File imgFile;

    public CameraResultDialog(@NonNull Context context, CallbacksForCNIC callbacksForCNIC, String titleValue, String descValue, File imageFrontBack) {

        super(context);

        this.imgFile = imageFrontBack;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.front_back_cnic_layout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        lp.height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.60);
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        okayBtn = findViewById(R.id.ok_btn);
        okayBtn.setOnClickListener(this);
        cancelBtn = findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);
        this.callbacksForCNIC = callbacksForCNIC;

        frontBackImg = findViewById(R.id.front_back_image);
//        titleTv.setText(titleValue);
//        descTv.setText(descValue);


//        Glide.with(context).load(imageFrontBack).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(frontBackImg);

         Bitmap bitmap = BitmapFactory.decodeFile(imageFrontBack.getAbsolutePath());
        frontBackImg.setImageBitmap(bitmap);


    }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.ok_btn:
                callbacksForCNIC.okCallback(imgFile);
                dismiss();
                break;
            case R.id.cancel_btn:
                callbacksForCNIC.cancelCallback();
                dismiss();
                break;
        }*/
    }

    public interface CallbacksForCNIC {
        void okCallback(File file);

        void cancelCallback();
    }
}