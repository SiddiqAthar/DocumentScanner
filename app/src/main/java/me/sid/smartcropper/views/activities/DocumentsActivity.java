package me.sid.smartcropper.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.AllFilesAdapter;
import me.sid.smartcropper.adapters.AllFolderAdapter;
import me.sid.smartcropper.dialogs.AlertDialogHelper;
import me.sid.smartcropper.dialogs.CreateFolderDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.OnPDFCreatedInterface;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.GetFilesUtility;
import me.sid.smartcropper.utils.PermissionUtils;
import me.sid.smartcropper.utils.StringUtils;
import me.sid.smartcropper.views.customView.toggleButton.SingleSelectToggleGroup;

import static androidx.camera.core.CameraX.getContext;

public class DocumentsActivity extends BaseActivity implements SingleSelectToggleGroup.OnCheckedChangeListener, View.OnClickListener
        , TextWatcher, GenericCallback, GetFilesUtility.getFilesCallback {

    ImageButton new_folder_btn, settin_btn;
    SingleSelectToggleGroup singleSelectToggleGroup;
    EditText searchEd1, searchEd2;
    RecyclerView filesRecyclerView;
    TextView filterTv, emptyView, countTv;
    LottieAnimationView btn_goToCamera;
    ArrayList<FileInfoModel> fileInfoModelArrayList;
    ArrayList<File> foldersArray;
    AllFilesAdapter filesAdapter;
    RelativeLayout noFileLayout;
    DirectoryUtils mDirectoryUtils;
    ProgressDialog dialog;
    AllFolderAdapter adapter;
    private int mChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        init();
    }

    private void init() {
        foldersArray = new ArrayList<>();
        fileInfoModelArrayList = new ArrayList<>();
        mDirectoryUtils = new DirectoryUtils(this);

        dialog = new ProgressDialog(DocumentsActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Creating pdf file");

        noFileLayout = findViewById(R.id.noFileLayout);
        new_folder_btn = findViewById(R.id.new_folder_btn);
        new_folder_btn.setOnClickListener(this);
        settin_btn = findViewById(R.id.settin_btn);
        settin_btn.setOnClickListener(this);

        filesRecyclerView = findViewById(R.id.filesRecyclerView);
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchEd1 = findViewById(R.id.searchEd1);
        searchEd1.clearFocus();
        searchEd1.addTextChangedListener(this);

        searchEd2 = findViewById(R.id.searchEd2);
        searchEd2.clearFocus();
        searchEd2.addTextChangedListener(this);

        singleSelectToggleGroup = findViewById(R.id.singleSelectedToggleGroup);
        singleSelectToggleGroup.setOnCheckedChangeListener(this);

        singleSelectToggleGroup.check(R.id.tab_yourScannedDocs);

        filterTv = findViewById(R.id.filterTv);
        filterTv.setOnClickListener(this);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setOnClickListener(this);
        countTv = findViewById(R.id.countTv);
        btn_goToCamera = findViewById(R.id.btn_goToCamera);
        btn_goToCamera.loop(true);
        btn_goToCamera.playAnimation();
         croppedArrayBitmap.clear();
        mutliCreatedArrayBitmap.clear();

        btn_goToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(GernalCameraActivity.class, null);
            }
        });

    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.new_folder_btn) {
            createFolder();
        }
        if (view.getId() == R.id.settin_btn) {

            Intent intent = new Intent(DocumentsActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.filterTv) {
            showSortMenu();
        }
    }


    @Override
    public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
        if (PermissionUtils.hasPermissionGranted(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
            this.mChecked = checkedId;

            if (mChecked == R.id.tab_yourScannedDocs) {
                mDirectoryUtils.clearFilterArray();
                clearText();
                getFiles();
             } else if (mChecked == R.id.tab_yourScannedPDF) {
                mDirectoryUtils.clearSelectedArray();
                clearText();
                new GetFilesUtility(this, this).execute(Constants.pdfExtension + "," + Constants.pdfExtension);
             } else if (mChecked == R.id.tab_yourCreatedFolders) {
                adapter = new AllFolderAdapter(foldersArray, this);
                adapter.setCallback(this);
                filesRecyclerView.setAdapter(adapter);

                if (PermissionUtils.hasPermissionGranted(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
                    getAllFolders();
                } else {

                    PermissionUtils.checkAndRequestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.READ_EXTERNAL_STORAGE);
                }
            }

        } else {
            PermissionUtils.checkAndRequestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.READ_EXTERNAL_STORAGE);
        }
    }

    private void clearText() {
        searchEd1.setText("");
        searchEd2.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (mChecked == R.id.tab_yourCreatedFolders) {
            filterForFolder(charSequence.toString());
        } else {
            filter(charSequence.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void getAllFolders() {
        foldersArray = mDirectoryUtils.getAllFolders();
        if (foldersArray != null && foldersArray.size() > 0) {
            adapter.setFolderArray(foldersArray);
            countTv.setText(String.valueOf(foldersArray.size()));
            noFileLayout.setVisibility(View.GONE);
        } else {
            countTv.setText("0");
            noFileLayout.setVisibility(View.VISIBLE);
        }
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<FileInfoModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (FileInfoModel s : fileInfoModelArrayList) {
            //if the existing elements contains the search input
            if (s.getFileName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //        if (filterdNames.size() > 0) {
        if (filesAdapter != null)
            filesAdapter.filterList(filterdNames);

//        }
    }

    private void filterForFolder(String text) {

        ArrayList<File> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (File s : foldersArray) {
            //if the existing elements contains the search input
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        if (adapter != null)
            adapter.filterList(filterdNames);
    }

    @Override
    public void callback(Object o) {
        if (o instanceof String) {
            countTv.setText((String) o);
        } else if (o instanceof File) {
            Intent intent = new Intent(this, AllFilesInFolderActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("path", ((File) o).getAbsolutePath());
            Objects.requireNonNull(this).startActivity(intent);
        }
    }


/*    class GetFiles extends AsyncTask<String, Void, ArrayList<File>> {
        @Override
        protected ArrayList<File> doInBackground(String... strings) {
            return mDirectoryUtils.getSelectedFiles(new File(Environment.getExternalStorageDirectory(), Constants.folderDirectory), strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<File> arrayList) {
            super.onPostExecute(arrayList);
            Log.d("count", arrayList.size() + "");
            fileInfoModelArrayList.clear();

            if (arrayList.size() > 0) {
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
                filesAdapter = new AllFilesAdapter(DocumentsActivity.this, fileInfoModelArrayList);
                countTv.setText(String.valueOf(fileInfoModelArrayList.size()));
                filesRecyclerView.setAdapter(filesAdapter);
                filesAdapter.notifyDataSetChanged();
                noFileLayout.setVisibility(View.GONE);
            } else {
                if (filesAdapter != null) {
                    filesRecyclerView.setAdapter(filesAdapter);
                    filesAdapter.setData(new ArrayList<FileInfoModel>());
                }
                countTv.setText("0");
                noFileLayout.setVisibility(View.VISIBLE);
            }
        }
    }*/

    private void createFolder() {
        new CreateFolderDialog(Objects.requireNonNull(this), new GenericCallback() {
            @Override
            public void callback(Object o) {
                foldersArray.add((File) o);
                if (adapter != null)
                    adapter.notifyDataSetChanged();
//                noFolderLayout.setVisibility(View.GONE);
            }
        }, true).show();
    }

    private void showSortMenu() {

        Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        final PopupMenu menu = new PopupMenu(wrapper, emptyView, Gravity.END);
        menu.inflate(R.menu.sortby_menu);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nameTv) {// name
                    sortArray(6);
                    menu.dismiss();
                    return true;
                } else if (menuItem.getItemId() == R.id.timeTv) {//
                    sortArray(5);
                    menu.dismiss();
                    return true;
                } else if (menuItem.getItemId() == R.id.createDateTv) { //create date
                    sortArray(4);
                    menu.dismiss();
                    return true;
                } else if (menuItem.getItemId() == R.id.zToATv) { // z to a
                    sortArray(3);
                    return true;
                } else if (menuItem.getItemId() == R.id.sizeTv) { //by size
                    sortArray(2);
                    menu.dismiss();
                    return true;
                } else if (menuItem.getItemId() == R.id.aTozTv) { //a to z
                    sortArray(1);
                    menu.dismiss();
                    return true;
                }
                return false;
            }
        });
        menu.show();
    }

    public void sortArray(final int sortBy) {
        if (mChecked == R.id.tab_yourCreatedFolders) {
            Collections.sort(foldersArray, new Comparator<File>() {
                @Override
                public int compare(File fileInfoModel, File t1) {
                    if (sortBy == 1)
                        return fileInfoModel.getName().compareToIgnoreCase(t1.getName());//A to Z
                    else if (sortBy == 2)
                        return Long.compare(fileInfoModel.length(), t1.length());//File size
                    else if (sortBy == 3)
                        return t1.getName().compareToIgnoreCase(fileInfoModel.getName());//Z to A
                    else if (sortBy == 4)
                        return Long.compare(fileInfoModel.lastModified(), t1.lastModified());//Create Date By
                    else if (sortBy == 5)
                        return Long.compare(t1.lastModified(), fileInfoModel.lastModified());//Recent updated Date By

                    return fileInfoModel.getName().compareToIgnoreCase(t1.getName());

                }
            });
            adapter.notifyDataSetChanged();
        } else {
            Collections.sort(fileInfoModelArrayList, new Comparator<FileInfoModel>() {
                @Override
                public int compare(FileInfoModel fileInfoModel, FileInfoModel t1) {
                    if (sortBy == 1)
                        return fileInfoModel.getFileName().compareToIgnoreCase(t1.getFileName());//A to Z
                    else if (sortBy == 2)
                        return Long.compare(fileInfoModel.getFile().length(), t1.getFile().length());//File size
                    else if (sortBy == 3)
                        return t1.getFileName().compareToIgnoreCase(fileInfoModel.getFileName());//Z to A
                    else if (sortBy == 4)
                        return Long.compare(fileInfoModel.getFile().lastModified(), t1.getFile().lastModified());//Create Date By
                    else if (sortBy == 5)
                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());//Recent updated Date By
                    else if (sortBy == 6)
                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());//By name

                    return fileInfoModel.getFileName().compareToIgnoreCase(t1.getFileName());

                }
            });
            filesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        quitApp(this);
    }

    private void getFiles() {
        ArrayList<File> arrayList = mDirectoryUtils.searchDir(new File(Environment.getExternalStorageDirectory(), Constants.folderDirectory));
        mDirectoryUtils.clearSelectedArray();

        if (arrayList != null && arrayList.size() > 0) {
            fileInfoModelArrayList = new ArrayList<>();
            fileInfoModelArrayList.clear();

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
            filesAdapter = new AllFilesAdapter(DocumentsActivity.this, fileInfoModelArrayList);
            countTv.setText(String.valueOf(fileInfoModelArrayList.size()));
            filesRecyclerView.setAdapter(filesAdapter);
            filesAdapter.notifyDataSetChanged();
            filesAdapter.setCallback(this);
            noFileLayout.setVisibility(View.GONE);
        }


        else {
            if (filesAdapter != null) {
                filesRecyclerView.setAdapter(filesAdapter);
                filesAdapter.setData(new ArrayList<FileInfoModel>());
            }
            countTv.setText("0");
            noFileLayout.setVisibility(View.VISIBLE);
        }
    }


    public void getFiles(ArrayList<File> arrayList) {
        //for allPdfs
        Log.d("count", arrayList.size() + "");
        if (arrayList.size() > 0) {
            noFileLayout.setVisibility(View.GONE);
            fileInfoModelArrayList.clear();
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
            filesAdapter = new AllFilesAdapter(DocumentsActivity.this, fileInfoModelArrayList);
            countTv.setText(String.valueOf(fileInfoModelArrayList.size()));
            filesRecyclerView.setAdapter(filesAdapter);
            filesAdapter.notifyDataSetChanged();
            filesAdapter.setCallback(this);
            noFileLayout.setVisibility(View.GONE);

        } else {
            if (filesAdapter != null) {
                filesRecyclerView.setAdapter(filesAdapter);
                filesAdapter.setData(new ArrayList<FileInfoModel>());
            }
            countTv.setText("0");
            noFileLayout.setVisibility(View.VISIBLE);
        }
    }

}