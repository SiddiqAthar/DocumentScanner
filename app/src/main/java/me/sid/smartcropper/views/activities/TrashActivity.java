package me.sid.smartcropper.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ag.floatingactionmenu.OptionsFabLayout;

import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.AllFilesAdapter;
import me.sid.smartcropper.adapters.MultiFilesAdapter;
import me.sid.smartcropper.dialogs.SaveOCRFileDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.OnPDFCreatedInterface;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.CreatePdfAsync;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ImageToPDFOptions;
import me.sid.smartcropper.utils.PDFUtils;
import me.sid.smartcropper.utils.PageSizeUtils;
import me.sid.smartcropper.utils.StringUtils;

import static me.sid.smartcropper.utils.Constants.DEFAULT_PAGE_COLOR;
import static me.sid.smartcropper.utils.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static me.sid.smartcropper.utils.Constants.PG_NUM_STYLE_PAGE_X_OF_N;
import static me.sid.smartcropper.utils.Constants.folderDirectory;

public class TrashActivity extends BaseActivity {


    public Button btn_delete, btn_restore;
    RecyclerView trash_rv;
    Toolbar toolbar;
    AllFilesAdapter filesAdapter;
    DirectoryUtils mDirectoryUtils;
    ArrayList<FileInfoModel> fileInfoModelArrayList;
    RelativeLayout noFileLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Trash Documents");
        setSupportActionBar(toolbar);

         if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        mDirectoryUtils = new DirectoryUtils(this);
        fileInfoModelArrayList = new ArrayList<>();
        btn_delete = findViewById(R.id.btn_delete);
        btn_restore = findViewById(R.id.btn_restore);
        trash_rv = findViewById(R.id.trash_rv);
        trash_rv.setLayoutManager(new LinearLayoutManager(this));

        noFileLayout = findViewById(R.id.noFileLayout);

        getFiles();


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filesAdapter.getFilesArrayList().size() != 0)
                    deleteFiles(filesAdapter.getFilesArrayList());
            }
        });
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filesAdapter.getFilesArrayList().size() != 0)
                    restorFiles(filesAdapter.getFilesArrayList());
            }
        });


    }

    private void restorFiles(ArrayList<FileInfoModel> moveFile) {

        File dest = mDirectoryUtils.restorTrashFolder();
        if (filesAdapter.getFilesArrayList().size() > 0) {
            for (int i = 0; i < moveFile.size(); i++) {

                mDirectoryUtils.moveFile(moveFile.get(i).getFile().getAbsolutePath(), moveFile.get(i).getFile().getName(), dest.getAbsolutePath() + "/");
//                fileInfoModelArrayList.remove(moveFile.get(i).getCheckedIndex());
//                filesAdapter.notifyItemRemoved(i);

//                filesAdapter.setCallback(this);

                /* fileInfoModelArrayList.add(model);
                filesAdapter = new AllFilesAdapter(this, fileInfoModelArrayList);
                trash_rv.setAdapter(filesAdapter);
                noFileLayout.setVisibility(View.GONE);*/
            }
            filesAdapter.clearCeckBoxArray();
            filesAdapter.setData(new ArrayList<>());
            getFiles();
        }
    }


    private void deleteFiles(ArrayList<FileInfoModel> moveFile) {

        if (filesAdapter.getFilesArrayList().size() > 0) {
            for (int i = 0; i < moveFile.size(); i++) {

                mDirectoryUtils.deleteFile(moveFile.get(i).getFile());

            }
            filesAdapter.clearCeckBoxArray();
            filesAdapter.setData(new ArrayList<>());
            getFiles();
        }
    }

    private void getFiles() {
        mDirectoryUtils.clearFilterArray();

        ArrayList<File> arrayList = mDirectoryUtils.searchDir(new File(Environment.getExternalStorageDirectory(), Constants.trashfolderDirectory));

        fileInfoModelArrayList.clear();

        if (arrayList != null && arrayList.size() > 0) {


            for (File file : arrayList) {

                String[] fileInfo = file.getName().split("\\.");
                if (fileInfo.length == 2)
                    fileInfoModelArrayList.add(new FileInfoModel(fileInfo[0], fileInfo[1], file));
                else {
                    fileInfoModelArrayList.add(new FileInfoModel(fileInfo[0],
                            file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
                            file));
                }

            }

            filesAdapter = new AllFilesAdapter(TrashActivity.this, fileInfoModelArrayList);
            trash_rv.setAdapter(filesAdapter);
            filesAdapter.setShowCheckbox(true);
            filesAdapter.notifyDataSetChanged();
            noFileLayout.setVisibility(View.GONE);

        } else {
            if (filesAdapter != null) {
                trash_rv.setAdapter(filesAdapter);
                filesAdapter.setData(new ArrayList<FileInfoModel>());
            }
            noFileLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if (item.getItemId() == R.id.setting) {
            startActivity(SettingActivity.class,null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }


}