package me.sid.smartcropper.views.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import id.zelory.compressor.Compressor;
import me.sid.smartcropper.R;
import me.sid.smartcropper.utils.FilePath;
import me.sid.smartcropper.views.CameraResultDialog;

public class GernalCameraActivity extends BaseActivity implements View.OnClickListener {


    private PreviewView previewView = null;

    private static final String EXTRA_FROM_ALBUM = "extra_from_album";
     private static final int REQUEST_CODE_SELECT_ALBUM = 200;
    boolean mFromAlbum, multiSelected;
     Bitmap selectedBitmap = null;


    CameraResultDialog.CallbacksForCNIC callbacks;


    private ArrayList<File> arrayListfile = null;

    private Executor executor = Executors.newSingleThreadExecutor();
     @SuppressLint("RestrictedApi")
    private CameraSelector lensFacing = CameraSelector.DEFAULT_BACK_CAMERA;
    private ImageCapture imageCapture = null;

    ImageButton settin_btn, pdf_btn, close_btn, btnCapture;
    Button menu_Whiteboard, menu_Form, menu_Document, menu_BusinessCard, btn_single, btn_multi, btn_done_mutli;
    ImageView gallery_iv, sample_iv, scan_shape;
    TextView tv_images_count;
    ConstraintLayout mode_selection;
    int fromMulti = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gernal_camera);

        init();

        if (getIntent() != null) {
            fromMulti = getIntent().getIntExtra("fromMulti", 0);//check if comes again for crop

            if (fromMulti == 1) // need to stop selection of multi or single, if they comes from multi, screens. Only multi now allow.
            {
                mode_selection.setVisibility(View.GONE);
                mFromAlbum = true;
                selectPhoto();
            } else if (fromMulti == 2) // need to stop selection of multi or single, if they comes from multi, screens. Only multi now allow.
            {
                mode_selection.setVisibility(View.GONE);
                multiSelected = true;
            }
        }

        arrayListfile = new ArrayList<>();

        startCamera();

        callbacks = new CameraResultDialog.CallbacksForCNIC() {
            @Override
            public void okCallback(File imgFile) {
                try {
                    File compressor = new Compressor(GernalCameraActivity.this)
                            .setMaxHeight(600)
                            .setMaxWidth(600)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToFile(imgFile);
                    setResult(999, new Intent().putExtra("file", compressor));
                    finish();
                } catch (Exception e) {
                    setResult(999, new Intent().putExtra("file", imgFile));
                    finish();
                }
            }

            @Override
            public void cancelCallback() {
                setResult(999);
                finish();
            }
        };
    }


    private void init() {

        settin_btn = findViewById(R.id.settin_btn);
        settin_btn.setOnClickListener(this);
        pdf_btn = findViewById(R.id.pdf_btn);
        pdf_btn.setOnClickListener(this);
        close_btn = findViewById(R.id.close_btn);
        close_btn.setOnClickListener(this);
        btn_single = findViewById(R.id.btn_single);
        btn_single.setOnClickListener(this);
        btn_multi = findViewById(R.id.btn_multi);
        btn_multi.setOnClickListener(this);
        btn_done_mutli = findViewById(R.id.btn_done_mutli);
        btn_done_mutli.setOnClickListener(this);
        btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(this);
        menu_Whiteboard = findViewById(R.id.menu_Whiteboard);
        menu_Whiteboard.setOnClickListener(this);
        menu_Form = findViewById(R.id.menu_Form);
        menu_Form.setOnClickListener(this);
        menu_Document = findViewById(R.id.menu_Document);
        menu_Document.setOnClickListener(this);
        menu_BusinessCard = findViewById(R.id.menu_BusinessCard);
        menu_BusinessCard.setOnClickListener(this);
        gallery_iv = findViewById(R.id.gallery_iv);
        gallery_iv.setOnClickListener(this);
        sample_iv = findViewById(R.id.sample_iv);
        sample_iv.setOnClickListener(this);
        previewView = findViewById(R.id.previewView);
        previewView.setOnClickListener(this);
        scan_shape = findViewById(R.id.scan_shape);
        tv_images_count = findViewById(R.id.tv_images_count);
        mode_selection = findViewById(R.id.mode_selection);

    }

    private void goToCrop() {
        if (arrayListfile.size() > 0) {
            if (fromMulti == 0) // if not coming from multi screen, then clear the array.
                croppedArrayBitmap.clear();

            Intent intent = new Intent(GernalCameraActivity.this, CropActivity.class);
            intent.putExtra("FILES_TO_SEND", arrayListfile);
            startActivity(intent);
        }

    }

    private void selectPhoto() {
        if (mFromAlbum) {
            Intent selectIntent = new Intent(Intent.ACTION_PICK);
            selectIntent.setType("image/*");
            if (selectIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(selectIntent, REQUEST_CODE_SELECT_ALBUM);
            }
        }

    }


    protected void takePicture() {

        File file = new File(getExternalFilesDir(null), "image-temp1" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new
                ImageCapture.OutputFileOptions.Builder(file).build();


        imageCapture.takePicture(
                outputFileOptions,
                executor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                        runOnUiThread(() ->
                                Glide.with(GernalCameraActivity.this).load(file).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(sample_iv)

                        );


                        runOnUiThread(() ->
                                addDataToArray(file)
                        );
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(getApplicationContext(), "Captured Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void addDataToArray(File file) {
        if (multiSelected) {
            arrayListfile.add(file);
            if (arrayListfile.size() > 0) {
                tv_images_count.setVisibility(View.VISIBLE);
                btn_done_mutli.setVisibility(View.VISIBLE);
                tv_images_count.setText(String.valueOf(arrayListfile.size()));
            }
        } else {
            arrayListfile.add(0, file);
            goToCrop();
        }
    }

    @SuppressLint("RestrictedApi")
    private void bindCamera(ProcessCameraProvider cameraProviderFuture) {
        CameraX.unbindAll();

        // Preview config for the camera
        Preview previewConfig = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Handles the output data of the camera
        previewConfig.setSurfaceProvider(previewView.createSurfaceProvider());


        // Image capture config which controls the Flash and Lens
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .setCameraSelector(lensFacing)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                .build();

        ProcessCameraProvider cameraProvider = cameraProviderFuture;
        // Bind the camera to the lifecycle
        cameraProvider.bindToLifecycle(
                ((LifecycleOwner) this),
                cameraSelector,
                imageCapture,
                previewConfig
        );
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {

                ProcessCameraProvider cameraProvider = null;
                try {
                    cameraProvider = cameraProviderFuture.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bindCamera(cameraProvider);

            }
        }, ContextCompat.getMainExecutor(this));

    }




    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int destWidth = 1000;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_ALBUM && data != null && data.getData() != null) {
            ContentResolver cr = getContentResolver();
            Uri bmpUri = data.getData();
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(cr.openInputStream(bmpUri), new Rect(), options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = calculateSampleSize(options);
                selectedBitmap = BitmapFactory.decodeStream(cr.openInputStream(bmpUri), new Rect(), options);

                String selectedFilePath = FilePath.getPath(this, bmpUri);
                final File file = new File(selectedFilePath);
                arrayListfile.add(file);
                sample_iv.setImageBitmap(selectedBitmap);
                goToCrop();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.settin_btn) {
            startActivity(SettingActivity.class, null);
        } else if (view.getId() == R.id.pdf_btn) {
            final String appPackageName = "com.pdfreader.pdfviewer.pdfeditor.pdfcreator.securepdf"; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

        } else if (view.getId() == R.id.btnCapture) {
            takePicture();
        } else if (view.getId() == R.id.close_btn) {
            if (fromMulti != 0) {
                showSaveDialog(this);
            } else {
                startActivity(DocumentsActivity.class, null);
                finish();
            }
        } else if (view.getId() == R.id.gallery_iv) {
            mFromAlbum = getIntent().getBooleanExtra(EXTRA_FROM_ALBUM, true);
            selectPhoto();

            /*            clearMyFiles();*/
        } else if (view.getId() == R.id.btn_done_mutli) {
            goToCrop();
        }

        //single click
        else if (view.getId() == R.id.btn_single) {
            changeColor(R.id.btn_single);
            multiSelected = false;
        }
        //multi click
        else if (view.getId() == R.id.btn_multi) {
            changeColor(R.id.btn_multi);
            multiSelected = true;
        }

        //menu
        else if (view.getId() == R.id.menu_Whiteboard) {
            changeColor(R.id.menu_Whiteboard);
        } else if (view.getId() == R.id.menu_Form) {
            changeColor(R.id.menu_Form);
        } else if (view.getId() == R.id.menu_Document) {
            changeColor(R.id.menu_Document);
        } else if (view.getId() == R.id.menu_BusinessCard) {
            changeColor(R.id.menu_BusinessCard);
        }


    }

    private void changeColor(int id) {
        if (id == R.id.menu_Whiteboard) {
            menu_Whiteboard.setTextColor(this.getResources().getColor(R.color.sea_green));
            menu_Form.setTextColor(this.getResources().getColor(R.color.white));
            menu_Document.setTextColor(this.getResources().getColor(R.color.white));
            menu_BusinessCard.setTextColor(this.getResources().getColor(R.color.white));
            scan_shape.setImageResource(R.drawable.ic_camera_whiteboard_c);
        } else if (id == R.id.menu_Form) {
            menu_Whiteboard.setTextColor(this.getResources().getColor(R.color.white));
            menu_Form.setTextColor(this.getResources().getColor(R.color.sea_green));
            menu_Document.setTextColor(this.getResources().getColor(R.color.white));
            menu_BusinessCard.setTextColor(this.getResources().getColor(R.color.white));
            scan_shape.setImageResource(R.drawable.ic_camera_form_c);
        } else if (id == R.id.menu_Document) {
            menu_Whiteboard.setTextColor(this.getResources().getColor(R.color.white));
            menu_Form.setTextColor(this.getResources().getColor(R.color.white));
            menu_Document.setTextColor(this.getResources().getColor(R.color.sea_green));
            menu_BusinessCard.setTextColor(this.getResources().getColor(R.color.white));
            scan_shape.setImageResource(R.drawable.ic_camera_scan_b);
        } else if (id == R.id.menu_BusinessCard) {
            menu_Whiteboard.setTextColor(this.getResources().getColor(R.color.white));
            menu_Form.setTextColor(this.getResources().getColor(R.color.white));
            menu_Document.setTextColor(this.getResources().getColor(R.color.white));
            menu_BusinessCard.setTextColor(this.getResources().getColor(R.color.sea_green));
            scan_shape.setImageResource(R.drawable.ic_camera_business_c);
        } else if (id == R.id.btn_single) {
            arrayListfile.clear();
            sample_iv.setImageDrawable(null);
            btn_multi.setTextColor(this.getResources().getColor(R.color.white));
            btn_single.setTextColor(this.getResources().getColor(R.color.sea_green));
            btn_done_mutli.setVisibility(View.GONE);
            tv_images_count.setVisibility(View.GONE);
            tv_images_count.setText("");
        } else if (id == R.id.btn_multi) {
            arrayListfile.clear();
            sample_iv.setImageDrawable(null);
            btn_single.setTextColor(this.getResources().getColor(R.color.white));
            btn_multi.setTextColor(this.getResources().getColor(R.color.sea_green));
         }
    }

    @Override
    public void onBackPressed() {
        if (fromMulti != 0) {
            showSaveDialog(this);
        } else {
            startActivity(DocumentsActivity.class, null);
            finish();
        }
    }
}