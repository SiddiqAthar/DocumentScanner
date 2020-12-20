package me.sid.smartcropper.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.ThumbnailCallback;
import me.sid.smartcropper.models.ThumbnailsManager;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.views.activities.BaseActivity;
import me.sid.smartcropper.views.activities.CropActivity;
import me.sid.smartcropper.views.activities.GernalCameraActivity;
import me.sid.smartcropper.views.activities.MultiScanActivity;
import me.sid.smartcropper.views.activities.OCRActivity;
import me.sid.smartcropper.views.activities.ViewTextActivity;
import me.sid.smartcropper.views.photoEditingFragments.PropertiesBSFragment;
import me.sid.smartcropper.views.photoEditingFragments.PropertiesOnlyBSFragment;
import me.sid.smartcropper.views.photoEditingFragments.TextEditorDialogFragment;

import static me.sid.smartcropper.views.activities.BaseActivity.croppedArrayBitmap;
import static me.sid.smartcropper.views.activities.BaseActivity.mutliCreatedArrayBitmap;

public class EditFilesAdapter extends RecyclerView.Adapter<EditFilesAdapter.EditFilesHolder> {

    private ArrayList<Bitmap> mCroppedarrayList;
    private Context mContext;
    DirectoryUtils mDirectoryUtils;
    int angle = 0;
    GenericCallback callback;
    RecyclerView mRecyclerView;
    SaveSettings saveSettings = new SaveSettings.Builder()
            .setClearViewsEnabled(true)
            .setTransparencyEnabled(true)
            .build();
    HashMap<Integer, EditFilesHolder> holderlist;
    final Calendar myCalendar = Calendar.getInstance();

    public EditFilesAdapter(ArrayList<Bitmap> croppedarrayList, Activity context, DirectoryUtils directoryUtils) {
        this.mCroppedarrayList = croppedarrayList;
        this.mContext = context;
        this.mDirectoryUtils = directoryUtils;
        holderlist = new HashMap<>();

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public EditFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_rv_layout, parent, false);

        return new EditFilesHolder(view);
    }

    public void setCallback(GenericCallback callback) {
        this.callback = callback;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull EditFilesHolder holder, int position) {


        Log.d("pos", String.valueOf(holder.getAdapterPosition()));

        if (!holderlist.containsKey(position)) {
            holderlist.put(position, holder);
        }

        if (holder.getAdapterPosition() == getItemCount() - 1) {
            holder.btn_done.setText("Done");
        } else
            holder.btn_done.setText("Next");

        holder.ivCrop.getSource().setImageBitmap(mCroppedarrayList.get(holder.getAdapterPosition()));

        holder.menu_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                angle = 90;
                Matrix matrix = new Matrix();
                matrix.postRotate(holder.ivCrop.getRotation() + angle);
                Bitmap editedBitmap = mCroppedarrayList.get(holder.getAdapterPosition());
                editedBitmap = Bitmap.createBitmap(editedBitmap, 0, 0, editedBitmap.getWidth(), editedBitmap.getHeight(), matrix, true);
                mCroppedarrayList.set(holder.getAdapterPosition(), editedBitmap);
                holder.ivCrop.getSource().setImageBitmap(mCroppedarrayList.get(holder.getAdapterPosition()));
            }
        });

        holder.menu_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = holder.getAdapterPosition();
                mCroppedarrayList.remove(pos);
                if (getItemCount() > 0) {
                    notifyItemRemoved(pos);
                } else {
                    Intent intent = new Intent(mContext, GernalCameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }
            }
        });


/*
        int pos = holder.getAdapterPosition() + 1;
        holder.tv_edited_count.setText("Page " + pos + "/" + getItemCount());
*/

        holder.btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItemCount() > 1 || mutliCreatedArrayBitmap.size() > 0) {
                    int posi = holder.getAdapterPosition() + 1;
                    if (posi < getItemCount()) {
                        scrollToPosition(posi);
                    } else {
                        for (int i = 0; i < mCroppedarrayList.size(); i++) {

                            EditFilesAdapter.EditFilesHolder filesHolder = getViewByPosition(i);
                            saveFile(filesHolder.mPhotoEditor, i);
                        }
                    }
                } else {
                    callback.callback(((BitmapDrawable) holder.ivCrop.getSource().getDrawable()).getBitmap());
                    saveSingleFile(holder.mPhotoEditor, holder.getAdapterPosition());

                }
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public EditFilesAdapter.EditFilesHolder getViewByPosition(int position) {
        return holderlist.get(position);
    }

    @Override
    public int getItemCount() {
        return mCroppedarrayList.size();
    }

    void saveFile(PhotoEditor photoEditor, int pos) {

        photoEditor.saveAsBitmap(saveSettings, new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                mutliCreatedArrayBitmap.add(saveBitmap);
                if (pos == mCroppedarrayList.size() - 1) {
                    croppedArrayBitmap.clear();
                    Intent intent = new Intent(mContext, MultiScanActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(mContext, "Error While saving", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void saveSingleFile(PhotoEditor photoEditor, int pos) {

        photoEditor.saveAsBitmap(saveSettings, new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                callback.callback(saveBitmap);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(mContext, "Error While saving", Toast.LENGTH_SHORT).show();
            }
        });

    }


    class EditFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnPhotoEditorListener,
            PropertiesBSFragment.Properties,
            PropertiesOnlyBSFragment.Properties {
        ImageButton menu_undo, menu_delete, menu_crop, menu_rotate, menu_addText, menu_write, menu_calendar, menu_ocr;
        Button btn_retake, btn_done;
        TextView tv_filter, tv_edited_count;
        PhotoEditorView ivCrop;
        DatePickerDialog.OnDateSetListener date;
        private PhotoEditor mPhotoEditor;
        private PropertiesBSFragment mPropertiesBSFragment;
        private PropertiesOnlyBSFragment mPropertiesOnlyBSFragment;
        private boolean filterShowing = false;
        RecyclerView thumbListView;

        public EditFilesHolder(@NonNull View itemView) {
            super(itemView);

            ivCrop = itemView.findViewById(R.id.iv_crop);
            mPhotoEditor = new PhotoEditor.Builder(mContext, ivCrop).setPinchTextScalable(true).build();

            mPhotoEditor.setOnPhotoEditorListener(this);

            menu_delete = itemView.findViewById(R.id.menu_delete);
            menu_crop = itemView.findViewById(R.id.menu_crop);
            menu_crop.setOnClickListener(this);
            menu_undo = itemView.findViewById(R.id.menu_undo);
            menu_undo.setOnClickListener(this);
            menu_rotate = itemView.findViewById(R.id.menu_rotate);
            menu_rotate.setOnClickListener(this);
            menu_addText = itemView.findViewById(R.id.menu_addText);
            menu_addText.setOnClickListener(this);
            menu_write = itemView.findViewById(R.id.menu_write);
            menu_write.setOnClickListener(this);
            menu_calendar = itemView.findViewById(R.id.menu_calendar);
            menu_calendar.setOnClickListener(this);
            menu_ocr = itemView.findViewById(R.id.menu_ocr);
            menu_ocr.setOnClickListener(this);
            btn_retake = itemView.findViewById(R.id.btn_retake);
            btn_retake.setOnClickListener(this);
            btn_done = itemView.findViewById(R.id.btn_done);
            btn_done.setOnClickListener(this);

            tv_filter = itemView.findViewById(R.id.tv_filter);
            tv_filter.setOnClickListener(this);

            tv_edited_count = itemView.findViewById(R.id.tv_edited_count);
            tv_edited_count.setOnClickListener(this);

            thumbListView = itemView.findViewById(R.id.rv_filters);

            mPropertiesBSFragment = new PropertiesBSFragment();
            mPropertiesOnlyBSFragment = new PropertiesOnlyBSFragment();
            mPropertiesBSFragment.setPropertiesChangeListener(this);
            mPropertiesOnlyBSFragment.setPropertiesChangeListener(this);




            initHorizontalList();

            date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(mPhotoEditor);
                }

            };
        }

        private void initHorizontalList() {
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            layoutManager.scrollToPosition(0);
            thumbListView.setLayoutManager(layoutManager);
            thumbListView.setHasFixedSize(true);

            bindDataToAdapter();
        }

        private void bindDataToAdapter() {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    Bitmap thumbImage = scaledBitmap(((BitmapDrawable) ivCrop.getSource().getDrawable()).getBitmap());
                    ThumbnailsManager.clearThumbs();
                    List<Filter> filters = FilterPack.getFilterPack(mContext);

                    for (Filter filter : filters) {
                        ThumbnailItem thumbnailItem = new ThumbnailItem();
                        thumbnailItem.image = thumbImage;
                        thumbnailItem.filter = filter;
                        ThumbnailsManager.addThumb(thumbnailItem);
                    }

                    List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(mContext);

                    ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, new ThumbnailCallback() {
                        @Override
                        public void onThumbnailClick(Filter filter) {
                            android.os.Handler handler = new Handler();

                            Runnable r = new Runnable() {
                                public void run() {
                                    ivCrop.getSource().setImageBitmap(filter.processFilter(((BitmapDrawable) ivCrop.getSource().getDrawable()).getBitmap()));
                                }
                            };
                            handler.post(r);
                        }
                    });
                    thumbListView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                }
            };
            handler.post(r);
        }

        public Bitmap scaledBitmap(Bitmap bitmap) {
            return Bitmap.createScaledBitmap(bitmap, 840, 840, false);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.menu_undo) {
                mPhotoEditor.undo();

            } else if (view.getId() == R.id.menu_addText) {

                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show((AppCompatActivity) mContext);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);

                        mPhotoEditor.addText(inputText, styleBuilder);
                    }
                });

            } else if (view.getId() == R.id.menu_write) {

                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setOpacity(100);
                showBottomSheetDialogFragment(mPropertiesBSFragment);

            } else if (view.getId() == R.id.menu_calendar) {
                new DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            } else if (view.getId() == R.id.menu_ocr) {

                Intent intent = new Intent(mContext, OCRActivity.class);
                mContext.startActivity(intent);
            } else if (view.getId() == R.id.menu_crop) {
                Intent intent = new Intent(mContext, CropActivity.class);
                intent.putExtra("againCrop", true);
                BaseActivity.againCropIndex = getAdapterPosition();
                mContext.startActivity(intent);
            } else if (view.getId() == R.id.btn_retake) {
                croppedArrayBitmap.clear();
                mutliCreatedArrayBitmap.clear();
                Intent intent = new Intent(mContext, GernalCameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);

            } else if (view.getId() == R.id.tv_filter) {
                if (view.getId() == R.id.tv_filter) {
                    if (!filterShowing) {
                        btn_done.setVisibility(View.GONE);
                        btn_retake.setVisibility(View.GONE);
                        thumbListView.setVisibility(View.VISIBLE);
                        tv_filter.setCompoundDrawablesWithIntrinsicBounds(null, mContext.getResources().getDrawable(R.drawable.ic__down), null, null);
                        filterShowing = true;
                    } else {
                        btn_done.setVisibility(View.VISIBLE);
                        btn_retake.setVisibility(View.VISIBLE);
                        thumbListView.setVisibility(View.GONE);
                        tv_filter.setCompoundDrawablesWithIntrinsicBounds(null, mContext.getResources().getDrawable(R.drawable.ic_up), null, null);
                        filterShowing = false;
                    }

                }
            }

        }

        @Override
        public void onEditTextChangeListener(View rootView, String text, int colorCode) {
            TextEditorDialogFragment textEditorDialogFragment =
                    TextEditorDialogFragment.show((AppCompatActivity) mContext, text, colorCode);
            textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                @Override
                public void onDone(String inputText, int colorCode) {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(colorCode);
                    mPhotoEditor.editText(rootView, inputText, styleBuilder);
                }
            });
        }

        @Override
        public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

        }

        @Override
        public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

        }

        @Override
        public void onStartViewChangeListener(ViewType viewType) {

        }

        @Override
        public void onStopViewChangeListener(ViewType viewType) {

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
    }

    public void updateLabel(PhotoEditor photoEditor) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(ContextCompat.getColor(mContext, R.color.black));

        photoEditor.addText(sdf.format(myCalendar.getTime()), styleBuilder);
    }

    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), fragment.getTag());
    }


}
