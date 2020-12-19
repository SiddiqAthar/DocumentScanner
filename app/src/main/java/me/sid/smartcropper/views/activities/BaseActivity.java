package me.sid.smartcropper.views.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.AlertDialogHelper;

public class BaseActivity extends AppCompatActivity {
//    AppContro appContro;

    public static int againCropIndex = 0;
    public static ArrayList<Bitmap> croppedArrayBitmap = new ArrayList<>();
    public static ArrayList<Bitmap> mutliCreatedArrayBitmap = new ArrayList<>();


    static {
        System.loadLibrary("NativeImageProcessor");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        appContro= (AppContro) getApplication();

    }

    public void startActivity(Class<?> calledActivity, Bundle bundle) {
        Intent myIntent = new Intent(this, calledActivity).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null)
            myIntent.putExtras(bundle);
        this.startActivity(myIntent);
    }

    public void showToast(String toast, Context context) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        appContro.setmCurrentActivity(this);
    }

    public Bitmap scaledBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, 840, 840, false);
    }

    public void showSaveDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                croppedArrayBitmap.clear();
                mutliCreatedArrayBitmap.clear();
                if (activity instanceof EditActivity || activity instanceof CropActivity || activity instanceof MultiScanActivity)
                    startActivity(GernalCameraActivity.class, null);
                else if (activity instanceof GernalCameraActivity)
                    startActivity(DocumentsActivity.class, null);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.create().show();

    }

    public void quitApp(Activity activity) {
        if (activity instanceof SettingActivity || activity instanceof DocumentsActivity)
            AlertDialogHelper.showAlert(activity, new AlertDialogHelper.Callback() {
                @Override
                public void onSucess(int t) {
                    if (t == 0) {
                        finishAffinity();
                        System.exit(0);
                    }
                }
            }, "Quit", "Do you want to quit this app?");

    }


    public String creatTempImg(Bitmap bitmap, int id) {


        String filename = "temp" + id + ".jpeg";
        File dest = getOutputMediaFile(filename);
        try {
            dest.createNewFile();
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return dest.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public File getOutputMediaFile(String filename) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
//        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + filename + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}