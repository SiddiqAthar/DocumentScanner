package me.sid.smartcropper.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ImageUtils;
import me.sid.smartcropper.utils.OCRUtils;
import me.sid.smartcropperlib.IvGenericCallback;
import me.sid.smartcropperlib.view.CropImageView;

public class CropActivity extends BaseActivity implements View.OnClickListener, IvGenericCallback {

    private static final String EXTRA_CROPPED_FILE = "extra_cropped_file";
    DirectoryUtils mDirectory;
    CropImageView ivCrop;
    ImageButton back_btn, settin_btn, pdf_btn;
    TextView tv_crop_count;
    Button btn_crop_cancel, btn_crop, btn_confirm;
    Bitmap selectedBitmap = null;
    private ArrayList<File> arrayListfile = null;
    Bitmap crop;
    ProgressDialog dialog;
    int arrayCount = 0;
    int count = 0;
    boolean againCrop = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Croping File");
        mDirectory=new DirectoryUtils(this);
        arrayListfile = new ArrayList<>();
        if (getIntent() != null) {
            againCrop = getIntent().getBooleanExtra("againCrop", false);//check if comes again for crop
            arrayListfile = (ArrayList<File>) getIntent().getSerializableExtra("FILES_TO_SEND");
            if (arrayListfile != null)
                arrayCount = arrayListfile.size();
        }

        init();

    }

    private void init() {

        ivCrop = findViewById(R.id.iv_crop);
        ivCrop.setOnClickListener(this);
        btn_crop_cancel = findViewById(R.id.btn_crop_cancel);
        btn_crop = findViewById(R.id.btn_crop);
        btn_confirm = findViewById(R.id.btn_confirm);
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        settin_btn = findViewById(R.id.settin_btn);
        settin_btn.setOnClickListener(this);
        pdf_btn = findViewById(R.id.pdf_btn);
        pdf_btn.setOnClickListener(this);

        tv_crop_count = findViewById(R.id.tv_crop_count);


        if (croppedArrayBitmap.size() > 0 && againCrop) //means come again for crop
        {
            selectedBitmap = croppedArrayBitmap.get(againCropIndex);
            new CropImagee(selectedBitmap).execute();
//             ivCrop.setImageToCrop(selectedBitmap, this);


        } else if (arrayCount > 0) {
            if (arrayCount > 1) {
                btn_confirm.setText("Next");
                tv_crop_count.setVisibility(View.VISIBLE);
            }
            setData(0);
        }


        btn_crop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCrop.setFullImgCrop();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (againCrop) {
                    croppedArrayBitmap.set(againCropIndex, ivCrop.crop());

                    Intent intent = new Intent(CropActivity.this, EditActivity.class);
                    intent.putExtra("againCropped", true);
                    startActivity(intent);
                } else {
                    if (addDataToArray()) {
                        if (count < arrayCount) {
                            setData(count);
                        } else if (croppedArrayBitmap.size() > 0) {
                            startActivity(EditActivity.class, null);
                        }
                    } else
                        Toast.makeText(CropActivity.this, "Cannot Crop Correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CropImagee(selectedBitmap).execute();
//                  ivCrop.setImageToCrop(selectedBitmap, CropActivity.this);
            }
        });


    }


    void setData(int innerCount) {

        if (arrayCount > 1) {

//             ivCrop.setImageToCrop(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath()), this);
            new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath())).execute();
            count = innerCount + 1;
            if (count == arrayCount) {
                btn_confirm.setText("Done");
            }
            tv_crop_count.setText("Page " + count + "/" + arrayCount);
        }
        else {
            count = 1;//default for size is 1
            //            ivCrop.setImageToCrop(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath()), this);
            new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath())).execute();
        }
        mDirectory.deleteFile(new File(arrayListfile.get(innerCount).getAbsolutePath()));
//        selectedBitmap = ivCrop.getBitmap();
    }

    public boolean addDataToArray() {
        if (ivCrop.canRightCrop()) {

            croppedArrayBitmap.add(ivCrop.crop());
            return true;
            /*crop = ivCrop.crop();
            croppedBitmap = crop;*/
        }
        return false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        if (requestCode == 999 && data != null) {
            ArrayList<File> file = (ArrayList<File>) data.getSerializableExtra("file");
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.back_btn) {
//            if (croppedArrayBitmap.size() < 0)
            onBackPressed();
//            else
//                startActivity(GernalCameraActivity.class, null);
        } else if (view.getId() == R.id.settin_btn) {
            startActivity(SettingActivity.class, null);
        }
    }

    @Override
    public void onBackPressed() {
        showSaveDialog(this);
    }


    @Override
    public void Ivcallback(Object o) {
        String res = (String) o;
        if (res.equals("done"))
            dialog.dismiss();
    }


    private class CropImagee extends AsyncTask<Bitmap, Void, Bitmap> {


        public CropImagee(Bitmap mselectedBitmap) {
            selectedBitmap = mselectedBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();

        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {


//            ivCrop.setImageToCrop(img, CropActivity.this);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    dialog.show();
                    ivCrop.setImageToCrop(selectedBitmap, CropActivity.this);
                }
            });
            return ivCrop.getBitmap();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            selectedBitmap=bitmap;
            dialog.dismiss();
        }
    }
}
