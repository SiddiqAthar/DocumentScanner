package me.sid.smartcropper.views.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.EditingToolsAdapter;
import me.sid.smartcropper.adapters.ThumbnailsAdapter;
import me.sid.smartcropper.dialogs.SaveFileDialog;
import me.sid.smartcropper.interfaces.CreateCallback;
import me.sid.smartcropper.interfaces.OnPDFCreatedInterface;
import me.sid.smartcropper.interfaces.ThumbnailCallback;
import me.sid.smartcropper.models.ThumbnailsManager;
import me.sid.smartcropper.utils.CreatePdfAsync;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ImageToPDFOptions;
import me.sid.smartcropper.utils.PageSizeUtils;
import me.sid.smartcropper.utils.StringUtils;
import me.sid.smartcropper.utils.ToolType;
import me.sid.smartcropper.views.photoEditingFragments.EmojiBSFragment;
import me.sid.smartcropper.views.photoEditingFragments.PropertiesBSFragment;
import me.sid.smartcropper.views.photoEditingFragments.PropertiesOnlyBSFragment;
import me.sid.smartcropper.views.photoEditingFragments.StickerBSFragment;
import me.sid.smartcropper.views.photoEditingFragments.TextEditorDialogFragment;

import static me.sid.smartcropper.utils.Constants.DEFAULT_PAGE_COLOR;
import static me.sid.smartcropper.utils.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static me.sid.smartcropper.utils.Constants.PG_NUM_STYLE_PAGE_X_OF_N;
import static me.sid.smartcropper.utils.Constants.folderDirectory;

public class EditActivity extends BaseActivity implements ThumbnailCallback, View.OnClickListener, OnPDFCreatedInterface
        , OnPhotoEditorListener,
        PropertiesBSFragment.Properties,
        PropertiesOnlyBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected {

    private static final String TAG = EditActivity.class.getSimpleName();

    Bitmap editedBitmap = null;
    ArrayList<String> imagesUri;

    ImageButton menu_undo, menu_delete, menu_crop, menu_rotate, menu_addText, menu_write, menu_calendar, menu_ocr, btn_nextImg, btn_backImg;
    Button btn_retake, btn_done;
    TextView tv_filter, tv_edited_count;
    RecyclerView thumbListView;
    DirectoryUtils mDirectoryUtils;
    int count = 0;
    int indexCount = 0;
    int arrayCount = 0;

    private Activity activity;

    int angle = 0;
    private ImageToPDFOptions mPdfOptions;
    GPUImage gpuImage;
    //GPUImageView gpuImageView;
    boolean mFromAlbum;

    File tempFile;
    private boolean filterShowing = false;
    ProgressDialog dialog;
    Boolean againCropped;


    PhotoEditorView ivCrop;
    PhotoEditor mPhotoEditor;
    private PropertiesBSFragment mPropertiesBSFragment;
    private PropertiesOnlyBSFragment mPropertiesOnlyBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);

    private boolean mIsFilterVisible;
    Toolbar toolbar;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    final Calendar myCalendar = Calendar.getInstance();
    private Typeface mWonderFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        activity = this;


        init();

    }

    private void init() {

        if (getIntent() != null) {
            againCropped = getIntent().getBooleanExtra("againCropped", false);
            if (againCropped) {
                editedBitmap = croppedArrayBitmap.get(againCropIndex);
                indexCount = againCropIndex;
            }
        }

        if (!againCropped && croppedArrayBitmap.size() > 0) {
            arrayCount = croppedArrayBitmap.size();
            editedBitmap = croppedArrayBitmap.get(indexCount);
        }


        mRvTools = findViewById(R.id.rvConstraintTools);

        dialog = new ProgressDialog(EditActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Creating pdf file");
        mPdfOptions = new ImageToPDFOptions();
        imagesUri = new ArrayList<>();
        mDirectoryUtils = new DirectoryUtils(EditActivity.this);

        ivCrop = findViewById(R.id.iv_crop);
        ivCrop.getSource().setImageBitmap(editedBitmap);
//        this.canvas = (CanvasView)this.findViewById(R.id.canvas);


        menu_undo = findViewById(R.id.menu_undo);
        menu_undo.setOnClickListener(this);
        menu_delete = findViewById(R.id.menu_delete);
        menu_delete.setOnClickListener(this);
        menu_crop = findViewById(R.id.menu_crop);
        menu_crop.setOnClickListener(this);
        menu_rotate = findViewById(R.id.menu_rotate);
        menu_rotate.setOnClickListener(this);
        menu_addText = findViewById(R.id.menu_addText);
        menu_addText.setOnClickListener(this);
        menu_write = findViewById(R.id.menu_write);
        menu_write.setOnClickListener(this);
        menu_calendar = findViewById(R.id.menu_calendar);
        menu_calendar.setOnClickListener(this);
        menu_ocr = findViewById(R.id.menu_ocr);
        menu_ocr.setOnClickListener(this);
        btn_retake = findViewById(R.id.btn_retake);
        btn_retake.setOnClickListener(this);
        btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);

        btn_nextImg = findViewById(R.id.btn_nextImg);
        btn_nextImg.setOnClickListener(this);
        btn_backImg = findViewById(R.id.btn_backImg);
        btn_backImg.setOnClickListener(this);

        tv_filter = findViewById(R.id.tv_filter);
        tv_filter.setOnClickListener(this);
        tv_edited_count = findViewById(R.id.tv_edited_count);
        tv_edited_count.setOnClickListener(this);
        thumbListView = findViewById(R.id.rv_filters);
        initHorizontalList();

        if (arrayCount > 1) {
            btn_done.setText("Next");
            setImageInfo(count);
            tv_edited_count.setVisibility(View.VISIBLE);
        }

        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mPropertiesOnlyBSFragment = new PropertiesOnlyBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);
        mPropertiesOnlyBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new GridLayoutManager(this, 5);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        mPhotoEditor = new PhotoEditor.Builder(this, ivCrop)
                .setPinchTextScalable(true)
                // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

    }
//    public CanvasView getCanvas() {
//        return this.canvas;
//    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.tv_filter) {
            if (!filterShowing) {
                btn_done.setVisibility(View.GONE);
                btn_retake.setVisibility(View.GONE);
                thumbListView.setVisibility(View.VISIBLE);
                tv_filter.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.ic__down), null, null);
                filterShowing = true;
            } else {
                btn_done.setVisibility(View.VISIBLE);
                btn_retake.setVisibility(View.VISIBLE);
                thumbListView.setVisibility(View.GONE);
                tv_filter.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.ic_up), null, null);
                filterShowing = false;
            }

        } else if (view.getId() == R.id.btn_retake) {
            croppedBitmap = null;
            croppedArrayBitmap.clear();
            mutliCreatedArrayBitmap.clear();
            startActivity(GernalCameraActivity.class, null);

        } else if (view.getId() == R.id.btn_done) {

//            ivCrop.getSource().setBackgroundColor(getResources().getColor(R.color.white));
            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .build();

            mPhotoEditor.saveAsBitmap(saveSettings, new OnSaveBitmap() {
                @Override
                public void onBitmapReady(Bitmap saveBitmap) {
                    saveNGotoNext(saveBitmap);
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        } else if (view.getId() == R.id.btn_nextImg) {
            slideNext();
        } else if (view.getId() == R.id.btn_backImg) {
            slideBack();
        }

        //menu btns
        else if (view.getId() == R.id.menu_undo) {
//            mPhotoEditor.brushEraser();
            mPhotoEditor.undo();
        } else if (view.getId() == R.id.menu_delete) {

            arrayCount = croppedArrayBitmap.size();
            if (arrayCount > 0) {

                croppedArrayBitmap.remove(indexCount);

                if (arrayCount == 1) {
                    croppedBitmap = null;
                    croppedArrayBitmap.clear();
                    mutliCreatedArrayBitmap.clear();
                    startActivity(GernalCameraActivity.class, null);
                } else {
                    indexCount--;
                    if (indexCount < 0) {
                        indexCount = 0;
                    }
                    arrayCount = croppedArrayBitmap.size();
                    setImageInfo(indexCount);
                    ivCrop.getSource().setImageBitmap(croppedArrayBitmap.get(indexCount));
                    editedBitmap = ((BitmapDrawable) ivCrop.getSource().getDrawable()).getBitmap();

//                    ivCrop.setImageBitmap(croppedArrayBitmap.get(indexCount));
//                    editedBitmap = ivCrop.getBitmap();
                }
            } else {
                croppedBitmap = null;
                croppedArrayBitmap.clear();
                mutliCreatedArrayBitmap.clear();
                startActivity(GernalCameraActivity.class, null);
            }
        } else if (view.getId() == R.id.menu_crop) {
            if (arrayCount > 0) {

                againCropIndex = indexCount;

                Intent intent = new Intent(EditActivity.this, CropActivity.class);
                intent.putExtra("againCrop", true);
                startActivity(intent);
            } else
                finish();
        } else if (view.getId() == R.id.menu_rotate) {
            rotateImage();
        } else if (view.getId() == R.id.menu_addText) {

//            mPhotoEditor.brushEraser();

            TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
            textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                @Override
                public void onDone(String inputText, int colorCode) {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(colorCode);

                    mPhotoEditor.addText(inputText, styleBuilder);
//                        mTxtCurrentTool.setText(R.string.label_text);
                }
            });

        } else if (view.getId() == R.id.menu_write) {

            mPhotoEditor.setBrushDrawingMode(true);
            mPhotoEditor.setOpacity(100);
//                mTxtCurrentTool.setText(R.string.label_brush);
            showBottomSheetDialogFragment(mPropertiesBSFragment);

        } else if (view.getId() == R.id.menu_calendar) {
            new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if (view.getId() == R.id.menu_ocr) {
            startActivity(OCRActivity.class, null);
        }

    }

    private void slideNext() {
        arrayCount = croppedArrayBitmap.size();
        if (arrayCount > 1 || finalArrayBitmap.size() > 0) {
            editedBitmap = croppedArrayBitmap.get(indexCount);

/*
            if (mutliCreatedArrayBitmap.size() - 1 < indexCount)
                mutliCreatedArrayBitmap.add(indexCount, editedBitmap);
*/
            try {
                mutliCreatedArrayBitmap.set(indexCount, editedBitmap);
            } catch (IndexOutOfBoundsException e) {
                mutliCreatedArrayBitmap.add(indexCount, editedBitmap);
            }

            if (indexCount == arrayCount - 1) {

                btn_done.setText("Done");
//                goToMutliActivity();
            } else {
                indexCount++;
                if (indexCount < arrayCount) {
                    editedBitmap = croppedArrayBitmap.get(indexCount);
//                        ivCrop.setImageBitmap(editedBitmap);
                    ivCrop.getSource().setImageBitmap(editedBitmap);
                    mPhotoEditor.clearAllViews();
                     setImageInfo(indexCount);
                    bindDataToAdapter();
                } else {
                    Toast.makeText(this, "Done,", Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    private void slideBack() {
        arrayCount = croppedArrayBitmap.size();
        if (arrayCount > 1 || mutliCreatedArrayBitmap.size() > 0) { //can slide


            indexCount--;
            if (indexCount < arrayCount && indexCount > -1) {

                editedBitmap = croppedArrayBitmap.get(indexCount);

                /*if (indexCount < 0) {
                    indexCount = 0;
                    mutliCreatedArrayBitmap.add(indexCount, editedBitmap);
                } else {*/
                mutliCreatedArrayBitmap.set(indexCount, editedBitmap);
                //                }
                ivCrop.getSource().setImageBitmap(editedBitmap);
                btn_done.setText("Next");
                mPhotoEditor.clearAllViews();
                setImageInfo(indexCount);
                bindDataToAdapter();
            } else {
                indexCount = 0;
//                Toast.makeText(this, "Done,", Toast.LENGTH_SHORT).show();
            }

        }
    }



    private void saveNGotoNext(Bitmap bitmap) {
        //            editedBitmap = ivCrop.getBitmap();

        editedBitmap = bitmap;

        arrayCount = croppedArrayBitmap.size();
        if (arrayCount > 1 || finalArrayBitmap.size() > 0) // check size > 0, coz if its come from multi more capture cycle
        {
            // if item exist on indexCount replace the value, otherwise add the value
            try {
                mutliCreatedArrayBitmap.set(indexCount, editedBitmap);
            } catch (IndexOutOfBoundsException e) {
                mutliCreatedArrayBitmap.add(indexCount, editedBitmap);
            }

//                croppedArrayBitmap.add(count,editedBitmap);

//                count = count + 1;

            if (indexCount == arrayCount - 1) {

                btn_done.setText("Done");
                goToMutliActivity();
            } else {
                indexCount++;
                if (indexCount < arrayCount) {
                    editedBitmap = croppedArrayBitmap.get(indexCount);
//                        ivCrop.setImageBitmap(editedBitmap);
                    ivCrop.getSource().setImageBitmap(editedBitmap);

                    setImageInfo(indexCount);
                    bindDataToAdapter();
                } else {
                    Toast.makeText(this, "Done,", Toast.LENGTH_SHORT).show();
                }
            }

        }

        else {
            createFile();
        }
    }


    private void setImageInfo(int mcount) {
        mcount = mcount + 1;
        tv_edited_count.setText("Page " + mcount + "/" + arrayCount);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    private void initHorizontalList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        thumbListView.setHasFixedSize(true);


        bindDataToAdapter();
    }

    private void bindDataToAdapter() {
        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage = scaledBitmap(editedBitmap);
                ThumbnailsManager.clearThumbs();
                List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

                for (Filter filter : filters) {
                    ThumbnailItem thumbnailItem = new ThumbnailItem();
                    thumbnailItem.image = thumbImage;
                    thumbnailItem.filter = filter;
                    ThumbnailsManager.addThumb(thumbnailItem);
                }

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) activity);
                thumbListView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    @Override
    public void onThumbnailClick(Filter filter) {
        Handler handler = new Handler();

        Runnable r = new Runnable() {
            public void run() {
//                ivCrop.setImageBitmap(filter.processFilter(Bitmap.createBitmap(editedBitmap)));
                ivCrop.getSource().setImageBitmap(filter.processFilter(Bitmap.createBitmap(editedBitmap)));
            }
        };
        handler.post(r);


    }

    private void rotateImage() {

        angle = 90;

        Matrix matrix = new Matrix();

        matrix.postRotate(ivCrop.getRotation() + angle);

//      editedBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight(), matrix, true);
        editedBitmap = Bitmap.createBitmap(editedBitmap, 0, 0, editedBitmap.getWidth(), editedBitmap.getHeight(), matrix, true);


//        ivCrop.setImageBitmap(editedBitmap);
        ivCrop.getSource().setImageBitmap(editedBitmap);

    }


    private void createFile() {
//        if (editedBitmap == null) {
//        editedBitmap = ivCrop.getBitmap();
//        }
        new SaveFileDialog(this, editedBitmap, new CreateCallback() {
            @Override
            public void onSaveAs(int isFrom, String fileName) {

                if (isFrom == 1 && !fileName.isEmpty()) {
                    //convert to PDF
                    createImgFile(fileName);
                } else if (isFrom == 2 && !fileName.isEmpty()) {
                    imagesUri.add(creatTempImg(editedBitmap, 0));
                    createImgToPDF(fileName);
                } else {
                    Toast.makeText(EditActivity.this, "File Name Can't be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    private void createImgFile(String fileName) {
        mDirectoryUtils.saveImageFile(editedBitmap, fileName);
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


    private void goToMutliActivity() {
        //croppedArrayBitmap need to clear

        croppedArrayBitmap.clear();

        startActivity(MultiScanActivity.class, null);

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
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {

        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);

                mPhotoEditor.editText(rootView, inputText, styleBuilder);
//                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");

    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };


    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(ContextCompat.getColor(this, R.color.black));

        mPhotoEditor.addText(sdf.format(myCalendar.getTime()), styleBuilder);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case SIGNATURE:
                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setOpacity(100);
//                mTxtCurrentTool.setText(R.string.label_brush);
                showBottomSheetDialogFragment(mPropertiesBSFragment);
                break;
            case MARKER:
                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setOpacity(30);
//                mTxtCurrentTool.setText(R.string.label_brush);
                showBottomSheetDialogFragment(mPropertiesOnlyBSFragment);
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);

                        mPhotoEditor.addText(inputText, styleBuilder);
//                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case UNDU:
                mPhotoEditor.brushEraser();
//                mTxtCurrentTool.setText(R.string.label_eraser_mode);
                break;
            case CALENDAR:

                new DatePickerDialog(this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

//                mTxtCurrentTool.setText(R.string.label_filter);
                // showFilter(true);
               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);*/
                break;
           /* case EMOJI:
                showBottomSheetDialogFragment(mEmojiBSFragment);
                break;
            case STICKER:
                showBottomSheetDialogFragment(mStickerBSFragment);
                break;*/
        }

    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
    }

    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }
}
