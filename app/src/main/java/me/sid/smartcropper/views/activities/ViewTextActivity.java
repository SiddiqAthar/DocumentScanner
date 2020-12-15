package me.sid.smartcropper.views.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import me.sid.smartcropper.R;

public class ViewTextActivity extends BaseActivity {

    public TextView view_text;
    Toolbar toolbar;
    File file;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_text);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
        toolbar = findViewById(R.id.toolbar);
        view_text = findViewById(R.id.view_text);

        toolbar.setTitle("OCR Text");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        if (getIntent().getExtras() != null) {
            String path = getIntent().getStringExtra("path");
            if (path != null) {
                file = new File(Objects.requireNonNull(path));
                String text=readFile(file);
                if (text!=null && !text.equals(""))
                view_text.setText(text);

                else {
                    view_text.setTextColor(getResources().getColor(R.color.pink));
                    view_text.setText("No data found in file.");
                }
            }

        }

    }

    public String readFile(File file) {

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to read File", Toast.LENGTH_SHORT).show();
            return "";
        }
        return String.valueOf(text);
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