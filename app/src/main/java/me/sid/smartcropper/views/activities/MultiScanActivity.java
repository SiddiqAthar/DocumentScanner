package me.sid.smartcropper.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ag.floatingactionmenu.OptionsFabLayout;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.AllFilesAdapter;
import me.sid.smartcropper.adapters.MultiFilesAdapter;
import me.sid.smartcropper.dialogs.SaveOCRFileDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.OnPDFCreatedInterface;
import me.sid.smartcropper.utils.CreatePdfAsync;
import me.sid.smartcropper.utils.ImageToPDFOptions;
import me.sid.smartcropper.utils.PageSizeUtils;
import me.sid.smartcropper.utils.StringUtils;

import static me.sid.smartcropper.utils.Constants.DEFAULT_PAGE_COLOR;
import static me.sid.smartcropper.utils.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static me.sid.smartcropper.utils.Constants.PG_NUM_STYLE_PAGE_X_OF_N;
import static me.sid.smartcropper.utils.Constants.folderDirectory;

public class MultiScanActivity extends BaseActivity implements OnPDFCreatedInterface {


    public Button btn_done;
    RecyclerView multi_imgs_rv;
    Toolbar toolbar;
    MultiFilesAdapter adapter;

    ProgressDialog dialog;
    Boolean captureAgain = false;
    ArrayList<String> imagesUri;

    private ImageToPDFOptions mPdfOptions;
    private OptionsFabLayout fabWithOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_scan);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
        toolbar = findViewById(R.id.toolbar);
        fabWithOptions = (OptionsFabLayout) findViewById(R.id.fab_l);

        toolbar.setTitle("Scan Document");
        setSupportActionBar(toolbar);

        dialog = new ProgressDialog(MultiScanActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Creating pdf file");
        dialog.setCancelable(false);

        mPdfOptions = new ImageToPDFOptions();
        imagesUri = new ArrayList<>();


        btn_done = findViewById(R.id.btn_done);
        multi_imgs_rv = findViewById(R.id.multi_imgs_rv);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        multi_imgs_rv.setLayoutManager(mLayoutManager);


         adapter = new MultiFilesAdapter(mutliCreatedArrayBitmap, MultiScanActivity.this);
        multi_imgs_rv.setAdapter(adapter);


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFile_forMulti();
            }
        });

        fabWithOptions.setMiniFabsColors(
                R.color.colorPrimary,
                R.color.colorPrimary);
        fabWithOptions.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabWithOptions.closeOptionsMenu();
            }
        });

        //Set mini fabs clicklisteners.
        fabWithOptions.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.fab_gallery:
                        gotoCamera(1);
                        break;
                    case R.id.fab_camera:
                        gotoCamera(2);
                    default:
                        break;
                }
            }
        });

    }

    void gotoCamera(int flowId) {
        captureAgain = true;
        Intent intent = new Intent(MultiScanActivity.this, GernalCameraActivity.class);
        intent.putExtra("fromMulti", flowId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        showSaveDialog(this);
    }


    private void createFile_forMulti() {
        new SaveOCRFileDialog(MultiScanActivity.this, new GenericCallback() {
            @Override
            public void callback(Object o) {
                String name = (String) o;
                if (!name.equals("")) {
                    createImgToPDF(name);
                } else {
                    Toast.makeText(MultiScanActivity.this, "File Name Can't be Empty", Toast.LENGTH_SHORT).show();
                }
            }

        }).show();
    }

    private void createImgToPDF(String fileName) {

        for (int i = 0; i < mutliCreatedArrayBitmap.size(); i++) {
//        for (int i = 0; i < mutliCreatedArrayBitmap.size(); i++) {
//            imagesUri.add(creatTempImg(mutliCreatedArrayBitmap.get(i), i));
            imagesUri.add(creatTempImg(mutliCreatedArrayBitmap.get(i), i));
        }

        mPdfOptions.setImagesUri(imagesUri);
        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        mPdfOptions.setImageScaleType(IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setPageNumStyle(PG_NUM_STYLE_PAGE_X_OF_N);
        mPdfOptions.setPageColor(DEFAULT_PAGE_COLOR);
        mPdfOptions.setMargins(20, 20, 20, 20);
        mPdfOptions.setOutFileName(fileName);
        new CreatePdfAsync(mPdfOptions, new File(Environment.getExternalStorageDirectory().toString(), folderDirectory).getPath(), MultiScanActivity.this).execute();

    }


    @Override
    public void onPDFCreationStarted() {
        dialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, final String path) {
        dialog.dismiss();
        if (success) {
            mutliCreatedArrayBitmap.clear();
            imagesUri.clear();
            StringUtils.getInstance().showSnackbar(MultiScanActivity.this, getString(R.string.created_success));
            startActivity(DocumentsActivity.class, null);
        } else {
            StringUtils.getInstance().showSnackbar(MultiScanActivity.this, getString(R.string.convert_error));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!captureAgain) {
            mutliCreatedArrayBitmap.clear();
        }
     }
}