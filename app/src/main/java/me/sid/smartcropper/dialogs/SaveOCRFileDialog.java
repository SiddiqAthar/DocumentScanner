package me.sid.smartcropper.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;

import me.sid.smartcropper.App;
import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.CreateCallback;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.PermissionUtils;

public class SaveOCRFileDialog extends Dialog {

     Button btn_SaveFile;
    EditText fileNameEd;
    GenericCallback callback;

    public SaveOCRFileDialog(@NonNull Context context, final GenericCallback fileCreated) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.save_ocr_file_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.callback = fileCreated;


        btn_SaveFile = findViewById(R.id.btn_SaveFile);
        fileNameEd = findViewById(R.id.fileNameEd);

        btn_SaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.hasPermissionGranted(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {

                    if (!fileNameEd.getText().equals("") && fileNameEd.getText() != null) {
                      callback.callback(fileNameEd.getText().toString());
                    } else {
                        Toast.makeText(context, "File name Can't be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    PermissionUtils.checkAndRequestPermissions(App.getInstance().getmCurrentActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_EXTERNAL_STORAGE);
                }
                fileNameEd.setText("");
                dismiss();
            }
        });
    }


}
