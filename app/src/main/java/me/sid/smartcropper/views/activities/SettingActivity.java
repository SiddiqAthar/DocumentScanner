package me.sid.smartcropper.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.AllFilesAdapter;
import me.sid.smartcropper.adapters.SettingsAdapter;
import me.sid.smartcropper.dialogs.AlertDialogHelper;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.models.SettingsData;

public class SettingActivity extends BaseActivity implements View.OnClickListener, GenericCallback {

    ImageButton setting_back_btn, setting_exit_btn;
    RecyclerView recyclerView_setting;
    SettingsAdapter settingsAdapter;
    ArrayList<SettingsData> settingsData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    private void init() {
        setting_back_btn = findViewById(R.id.setting_back_btn);
        setting_back_btn.setOnClickListener(this);
        setting_exit_btn = findViewById(R.id.setting_exit_btn);
        setting_exit_btn.setOnClickListener(this);

        recyclerView_setting = findViewById(R.id.recyclerView_setting);
        recyclerView_setting.setLayoutManager(new LinearLayoutManager(this));

        settingsData.add(new SettingsData(R.drawable.ic_upgrade_s, getString(R.string.premium), R.drawable.ic_yellow_ic));
        settingsData.add(new SettingsData(R.drawable.ic_contact_s, getString(R.string.contact_us), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_trash, getString(R.string.trash), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_privacy_s, getString(R.string.policy), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_help_s, getString(R.string.how_to_use), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_rate_ic, getString(R.string.rate), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.ic_share_s, getString(R.string.share_app), R.drawable.ic_arrow_s));

        settingsAdapter = new SettingsAdapter(settingsData, SettingActivity.this, this);
        recyclerView_setting.setAdapter(settingsAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setting_back_btn) {
            finish();
        } else if (view.getId() == R.id.setting_exit_btn) {
            quitApp(this);
        }
    }

    @Override
    public void callback(Object o) {

        final String whereTo = (String) o;

        if (whereTo.equals(getResources().getString(R.string.rate))) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(SettingActivity.this, " unable to find market app", Toast.LENGTH_LONG).show();
            }
        } else if (whereTo.equals(getResources().getString(R.string.share_app))) {
            ShareCompat.IntentBuilder.from(SettingActivity.this)
                    .setType("text/plain")
                    .setChooserTitle("Share App")
                    .setText("http://play.google.com/store/apps/details?id=" + getPackageName()).startChooser();
        }
        else if (whereTo.equals(getResources().getString(R.string.trash))) {
            startActivity(TrashActivity.class, null);
        }
    }


}