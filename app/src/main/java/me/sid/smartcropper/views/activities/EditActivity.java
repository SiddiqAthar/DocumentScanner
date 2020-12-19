package me.sid.smartcropper.views.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.EditFilesAdapter;
import me.sid.smartcropper.dialogs.SaveFileDialog;
import me.sid.smartcropper.interfaces.CreateCallback;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.OnPDFCreatedInterface;
import me.sid.smartcropper.utils.CreatePdfAsync;
import me.sid.smartcropper.utils.CustomLinearLayoutManager;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ImageToPDFOptions;
import me.sid.smartcropper.utils.PageSizeUtils;
import me.sid.smartcropper.utils.StringUtils;

import static me.sid.smartcropper.utils.Constants.DEFAULT_PAGE_COLOR;
import static me.sid.smartcropper.utils.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static me.sid.smartcropper.utils.Constants.PG_NUM_STYLE_PAGE_X_OF_N;
import static me.sid.smartcropper.utils.Constants.folderDirectory;

public class EditActivity extends BaseActivity implements OnPDFCreatedInterface, View.OnClickListener, GenericCallback {


    ArrayList<String> imagesUri;

    CustomLinearLayoutManager customLayoutManager;
    DirectoryUtils mDirectoryUtils;
    ImageToPDFOptions mPdfOptions;
    ProgressDialog dialog;
    Boolean againCropped;

    RecyclerView edit_image_rv;
    EditFilesAdapter editFilesAdapter;
    ImageButton btn_nextImg, btn_backImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
    }

    private void init() {

        if (getIntent() != null) {
            againCropped = getIntent().getBooleanExtra("againCropped", false);
        }

        edit_image_rv = findViewById(R.id.edit_image_rv);

        dialog = new ProgressDialog(EditActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Creating pdf file");
        dialog.setCancelable(false);

        mPdfOptions = new ImageToPDFOptions();
        imagesUri = new ArrayList<>();
        mDirectoryUtils = new DirectoryUtils(EditActivity.this);

        btn_nextImg = findViewById(R.id.btn_nextImg);
        btn_nextImg.setOnClickListener(this);
        btn_backImg = findViewById(R.id.btn_backImg);
        btn_backImg.setOnClickListener(this);

        customLayoutManager = new CustomLinearLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);

        if (againCropped) {
            customLayoutManager.scrollToPosition(againCropIndex);
        }
        editFilesAdapter = new EditFilesAdapter(croppedArrayBitmap, this, mDirectoryUtils);
        editFilesAdapter.setCallback(this);
        edit_image_rv.setAdapter(editFilesAdapter);
        edit_image_rv.setLayoutManager(customLayoutManager);


    }


    private void createFile(Bitmap img) {
        new SaveFileDialog(this, img, new CreateCallback() {
            @Override
            public void onSaveAs(int isFrom, String fileName) {

                if (isFrom == 1 && !fileName.isEmpty()) {
                    //convert to PDF
                    createImgFile(fileName, img);
                } else if (isFrom == 2 && !fileName.isEmpty()) {
                    imagesUri.add(creatTempImg(img, 0));
                    createImgToPDF(fileName);
                } else {
                    Toast.makeText(EditActivity.this, "File Name Can't be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    private void createImgFile(String fileName, Bitmap img) {
        mDirectoryUtils.saveImageFile(img, fileName);
        startActivity(DocumentsActivity.class, null);
    }

    private void createImgToPDF(String fileName) {
        mPdfOptions.setImagesUri(imagesUri);
        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        mPdfOptions.setImageScaleType(IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setPageNumStyle(PG_NUM_STYLE_PAGE_X_OF_N);
        mPdfOptions.setPageColor(DEFAULT_PAGE_COLOR);
        mPdfOptions.setMargins(20, 20, 20, 20);
        mPdfOptions.setOutFileName(fileName);
        new CreatePdfAsync(mPdfOptions, new File(Environment.getExternalStorageDirectory().toString(), folderDirectory).getPath(), EditActivity.this).execute();

    }

    @Override
    public void onPDFCreationStarted() {
        dialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, final String path) {
        dialog.dismiss();
        if (success) {
            imagesUri.clear();
            StringUtils.getInstance().showSnackbar(EditActivity.this, getString(R.string.created_success));
            startActivity(DocumentsActivity.class, null);
        } else {
            StringUtils.getInstance().showSnackbar(EditActivity.this, getString(R.string.convert_error));
        }
    }

    @Override
    public void onBackPressed() {
        showSaveDialog(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_nextImg) {
            edit_image_rv.post(new Runnable() {
                @Override
                public void run() {
                    scrollNext();
                }
            });
        } else if (view.getId() == R.id.btn_backImg) {
            edit_image_rv.post(new Runnable() {
                @Override
                public void run() {
                    scrollBack();
                }
            });
        }
    }

    private void scrollNext() {
        if (customLayoutManager.findLastCompletelyVisibleItemPosition() < (editFilesAdapter.getItemCount() - 1)) {
            customLayoutManager.scrollToPosition(customLayoutManager.findLastCompletelyVisibleItemPosition() + 1);
        }
    }

    private void scrollBack() {
        if (customLayoutManager.findFirstCompletelyVisibleItemPosition() <= (editFilesAdapter.getItemCount() - 1)) {
            customLayoutManager.scrollToPosition(customLayoutManager.findLastCompletelyVisibleItemPosition() - 1);
        }
    }

    @Override
    public void callback(Object o) {
        Bitmap image = (Bitmap) o;
        createFile(image);
    }
}
