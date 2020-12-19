package me.sid.smartcropper.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.AlertDeleteDialogHelper;
import me.sid.smartcropper.dialogs.AlertDialogHelper;
import me.sid.smartcropper.dialogs.CreateFolderDialog;
import me.sid.smartcropper.dialogs.MoveFileDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.FileInfoUtils;
import me.sid.smartcropper.views.activities.PDFViewerAcitivity;
import me.sid.smartcropper.views.activities.ViewImageActivity;
import me.sid.smartcropper.views.activities.ViewTextActivity;

public class AllFilesAdapter extends RecyclerView.Adapter<AllFilesAdapter.MyFilesHolder> {

    Context context;
    ArrayList<FileInfoModel> filesArrayList, checkBoxArray;
    DirectoryUtils mDirectory;
    RecyclerView recyclerView;
    GenericCallback callback;
    boolean showCheckbox = false;

    public void setCallback(GenericCallback callback) {
        this.callback = callback;
    }

    public AllFilesAdapter(Context context, ArrayList<FileInfoModel> filesArrayList) {
        this.context = context;
        this.filesArrayList = filesArrayList;
        mDirectory = new DirectoryUtils(context);
        checkBoxArray = new ArrayList<>();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_item_layout, parent, false);
        return new MyFilesHolder(view);
    }

    public void setData(ArrayList<FileInfoModel> filesArrayList) {
        this.filesArrayList = new ArrayList<>();
        this.filesArrayList = filesArrayList;
        notifyDataSetChanged();
    }

    public void setShowCheckbox(boolean showMoreBtn) {
        this.showCheckbox = showMoreBtn;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyFilesHolder holder, int position) {
        if (showCheckbox)
            holder.setViewsAllignment();

        if (filesArrayList.get(holder.getAdapterPosition()).getSelect()) {
            holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.colorGrayHightted));
        }
        holder.fileNameTv.setText(filesArrayList.get(holder.getAdapterPosition()).getFileName());
        holder.sizeTv.setText(FileInfoUtils.getFormattedSize(filesArrayList.get(holder.getAdapterPosition()).getFile()));
        holder.dateTv.setText(FileInfoUtils.getFormattedDate(filesArrayList.get(holder.getAdapterPosition()).getFile()));

        if (filesArrayList.get(holder.getAdapterPosition()).getFileType().equals("pdf")) {
            holder.fileTypeTv.setText("P");
            holder.fileTypeRl.setBackgroundResource(R.drawable.pdf_bg);
        } else if (filesArrayList.get(holder.getAdapterPosition()).getFileType().equals("jpg")) {
            holder.fileTypeTv.setText("I");
            holder.fileTypeRl.setBackgroundResource(R.drawable.img_bg);
        } else if (filesArrayList.get(holder.getAdapterPosition()).getFileType().equals("txt")) {
            holder.fileTypeTv.setText("T");
            holder.fileTypeRl.setBackgroundResource(R.drawable.word_bg);
        }

         //more button Visible or not

        holder.moreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                final PopupMenu menu = new PopupMenu(wrapper, holder.fileNameTv, Gravity.END);
                menu.inflate(R.menu.navigation);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.rename) {
                            menu.dismiss();
                            showRenameDialog(filesArrayList.get(holder.getAdapterPosition()).getFile(), holder.getAdapterPosition());
                            return true;
                        } else if (menuItem.getItemId() == R.id.share) {
                            shareFile(filesArrayList.get(holder.getAdapterPosition()).getFile());
                            menu.dismiss();
                            return true;
                        } else if (menuItem.getItemId() == R.id.delete) {
                            showDeleteFileDialog(filesArrayList.get(holder.getAdapterPosition()).getFile(), holder.getAdapterPosition());
                            menu.dismiss();
                            return true;
                        } else if (menuItem.getItemId() == R.id.moveToFolder) {
                            showAllFolderDialog(filesArrayList.get(holder.getAdapterPosition()).getFile(), holder.getAdapterPosition());
                            menu.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
                menu.show();
            }

        });

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (holder.fileTypeTv.getText().toString().equals("P")) {
                        Intent intent = new Intent(context, PDFViewerAcitivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("path", filesArrayList.get(holder.getAdapterPosition()).getFile().getAbsolutePath());
                        context.startActivity(intent);
                    } else if (holder.fileTypeTv.getText().toString().equals("I")) {
                        Intent intent = new Intent(context, ViewImageActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("path", filesArrayList.get(holder.getAdapterPosition()).getFile().getAbsolutePath());
                        context.startActivity(intent);
                    } else if (holder.fileTypeTv.getText().toString().equals("T")) {
                        Intent intent = new Intent(context, ViewTextActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("path", filesArrayList.get(holder.getAdapterPosition()).getFile().getAbsolutePath());
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.d("exxx", "" + e.getMessage());
                }
            }
        });


        /*if (holder.checkBox.getVisibility() == View.VISIBLE) {
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        checkBoxArray.add(filesArrayList.get(holder .getAdapterPosition()));
                    } else
                        checkBoxArray.remove(filesArrayList.get(holder.getAdapterPosition()));
                    callback.callback(checkBoxArray);
                }
            });
        }*/

        if (holder.checkBox.getVisibility() == View.VISIBLE) {
            if (filesArrayList.get(holder.getAdapterPosition()).getSelect()) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }

        }



    }


    public void refreshArray(File pdfFile) {
        String[] names = pdfFile.getName().split("\\.");
        FileInfoModel model = new FileInfoModel(names[0],
                pdfFile.getAbsolutePath().substring(pdfFile.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
                pdfFile);
        ArrayList<FileInfoModel> arrayList = filesArrayList;
        arrayList.add(filesArrayList.size(), model);
        setData(arrayList);
        recyclerView.smoothScrollToPosition(filesArrayList.size());
    }

    private void shareFile(File file) {
        Constants.shareFile(context, file);
    }

    private void showDeleteFileDialog(final File file, final int pos) {
      /*  AlertDialogHelper.showAlert(context, new AlertDialogHelper.Callback() {
            @Override
            public void onSucess(int t) {
                if (t == 0) {
                    if (mDirectory.deleteFile(file)) {
                        Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();
                        filesArrayList.remove(pos);
                        notifyItemRemoved(pos);
                    } else {
                        Toast.makeText(context, "File not deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, "Delete", "Are you really want to delete this file?");
*/
        AlertDeleteDialogHelper dialog = new AlertDeleteDialogHelper(context, new AlertDeleteDialogHelper.Callback() {


            @Override
            public void onSucess(int t) {

                File dest=mDirectory.createFolder("Trash");

                if (t == 0) {

                    if (mDirectory.moveFile(file.getAbsolutePath(), file.getName(),dest.getAbsolutePath() + "/"))
                    {
                        Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();
                        filesArrayList.remove(pos);
                        callback.callback(String.valueOf(filesArrayList.size()));
                        notifyItemRemoved(pos);
                    } else {
                        Toast.makeText(context, "File not deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
         dialog.show();
    }


    private void showRenameDialog(final File file, final int pos) {
        CreateFolderDialog dialog = new CreateFolderDialog(context, new GenericCallback() {
            @Override
            public void callback(Object o) {
                if (mDirectory.renameFile(file, (String) o)) {
                    Toast.makeText(context, "File is renamed", Toast.LENGTH_SHORT).show();
                    FileInfoModel model = getItem(pos);
                    model.setFileName((String) o);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "File renamed failed", Toast.LENGTH_SHORT).show();
                }
            }
        }, false);
        dialog.setSaveBtn("Rename File");
        dialog.setTitle("Enter New File Name");
        dialog.show();
    }

    public void filterList(ArrayList<FileInfoModel> filterdNames) {
        this.filesArrayList = filterdNames;
        notifyDataSetChanged();
    }
    public ArrayList<FileInfoModel> getFilesArrayList() {
        return checkBoxArray;
    }

    public void clearCeckBoxArray () {
        checkBoxArray=new ArrayList<>();
    }

    public ArrayList<FileInfoModel> getRealArray() {
        return filesArrayList;
    }

    public FileInfoModel getItem(int position) {
        return filesArrayList.get(position);
    }

    private void showAllFolderDialog(File moveFile, final int pos) {
        new MoveFileDialog(context, moveFile, new GenericCallback() {
            @Override
            public void callback(Object o) {
                if ((boolean) o) {
                    Toast.makeText(context, "File move to folder", Toast.LENGTH_SHORT).show();
                    filesArrayList.remove(pos);
                    callback.callback(String.valueOf(filesArrayList.size()));
                    notifyItemRemoved(pos);
                } else {
                    Toast.makeText(context, "File not move", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    @Override
    public int getItemCount() {
        return filesArrayList.size();
    }

    public class MyFilesHolder extends RecyclerView.ViewHolder {
        RelativeLayout fileTypeRl;
        TextView fileTypeTv, fileNameTv, sizeTv, dateTv;
        ImageView moreImg;
        ConstraintLayout rootLayout;
        CheckBox checkBox;

        public MyFilesHolder(@NonNull View itemView) {
            super(itemView);
            fileTypeRl = itemView.findViewById(R.id.fileTypeRl);
            fileTypeTv = itemView.findViewById(R.id.fileTypeTv);
            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            sizeTv = itemView.findViewById(R.id.sizeTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            moreImg = itemView.findViewById(R.id.moreImg);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            checkBox = itemView.findViewById(R.id.checkbox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    FileInfoModel model = filesArrayList.get(getAdapterPosition());
//                    public FileInfoModel(String fileName, String fileType, File file,int index) {
/*
                    FileInfoModel model = new FileInfoModel(filesArrayList.get(getAdapterPosition()).getFileName(),
                            filesArrayList.get(getAdapterPosition()).getFileType(),
                            filesArrayList.get(getAdapterPosition()).getFile(),
                            getAdapterPosition());
*/
                    if (b) {
                        if (model.getSelect())
                            return;
                        else {
                            model.setSelect(true);
                            checkBoxArray.add(model);
                        }

                    } else {
                        if (!model.getSelect()) {
                            return;
                        } else {
                            model.setSelect(false);
                            checkBoxArray.remove(model);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void selectedItem(FileInfoModel model) {
            for (FileInfoModel model1 : filesArrayList) {
                model1.setSelect(model.getFileName().equals(model.getFileName()));
            }
            notifyDataSetChanged();
        }

        public void setViewsAllignment() {
            ConstraintLayout constraintLayout = rootLayout;
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.fileNameTv, ConstraintSet.END, R.id.checkbox, ConstraintSet.START, 0);
            constraintSet.connect(R.id.dateTv, ConstraintSet.END, R.id.checkbox, ConstraintSet.START, 10);
            constraintSet.applyTo(constraintLayout);
            moreImg.setVisibility(View.GONE);
            checkBox.setVisibility(View.VISIBLE);
        }
    }
}
