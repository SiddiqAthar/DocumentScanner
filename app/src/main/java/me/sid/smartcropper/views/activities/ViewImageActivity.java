package me.sid.smartcropper.views.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.SaveOCRFileDialog;
import me.sid.smartcropper.interfaces.GenericCallback;

import static me.sid.smartcropper.utils.Constants.folderDirectory;

public class ViewImageActivity extends BaseActivity {

    public ImageView view_image;
    Toolbar toolbar;
    File file;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
        toolbar = findViewById(R.id.toolbar);
        view_image = findViewById(R.id.view_image);

        toolbar.setTitle("Scanned Image");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        if (getIntent().getExtras() != null) {
            String path = getIntent().getStringExtra("path");
            if (path != null) {
                file = new File(Objects.requireNonNull(path));
                toolbar.setTitle(file.getName().substring(0, file.getName().lastIndexOf(".")));
                Glide.with(ViewImageActivity.this).load(Uri.fromFile(file)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(view_image);
            } else {
                uri = Uri.parse(getIntent().getStringExtra("uri"));
                Glide.with(ViewImageActivity.this).load(uri).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(view_image);
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}