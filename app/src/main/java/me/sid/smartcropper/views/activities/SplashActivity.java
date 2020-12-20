package me.sid.smartcropper.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.HashMap;
import java.util.Map;

import me.sid.smartcropper.App;
import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.PermissionCallback;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.PermissionUtils;
 
import static me.sid.smartcropper.utils.PermissionUtils.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS;

public class SplashActivity extends BaseActivity {
    TextView tv1, tv4;
    String[] array = new String[3];
    int PHONE_STATE_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        array[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
        array[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        array[2] = Manifest.permission.CAMERA;


        tv1 = findViewById(R.id.tv1);
        tv4 = findViewById(R.id.tv4);



        Spannable docScannerSpan = new SpannableString("DOC SCANNER");

        docScannerSpan.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.sea_green)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv1.setText(docScannerSpan);

        Spannable ourpolicySpan = new SpannableString("Read our Privacy Police. Tap to Agree and continue to accept the Terms of Service");

        ourpolicySpan.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.sea_green)), 9, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ourpolicySpan.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.sea_green)), 65, 81, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv4.setText(ourpolicySpan);

        if (PermissionUtils.hasPermissionGranted(
                this,
                array
        )
        ) {
            goToCamera();
        } else {

            requestRequiredPermissions();
        }
    }

    public void requestRequiredPermissions() {
        PermissionUtils.checkAndRequestPermissions(
                this,
                array,
                PHONE_STATE_CODE
        );
    }

    private void goToCamera() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(GernalCameraActivity.class, null);
                finish();
            }
        }, 2000);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 23) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Permission Needed To Run The App", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PHONE_STATE_CODE) {
            Map<String, Integer> perms = new HashMap<String, Integer>();

            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                goToCamera();

            } else {
                // Permission Denied
                Toast.makeText(SplashActivity.this, "Permission Needed To Run The App", Toast.LENGTH_SHORT)
                        .show();

                finish();
            }

        }
    }


}