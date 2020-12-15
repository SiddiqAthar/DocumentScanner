package me.sid.smartcropper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.FileInfoUtils;

public class AllFolderAdapter extends RecyclerView.Adapter {
    ArrayList<File> folderArray;
    GenericCallback callback;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public AllFolderAdapter(ArrayList<File> folderArray, Context mContext) {
        this.folderArray = folderArray;
        this.mContext = mContext;
    }
    public void filterList(ArrayList<File> filterdNames) {
        this.folderArray = filterdNames;
        notifyDataSetChanged();
    }

    Context mContext;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_delete_layout, parent, false);
        return new ViewHolderSwipe(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderSwipe holder1 = (ViewHolderSwipe) holder;

        if (folderArray != null && 0 <= position && position < folderArray.size()) {
            final File data = folderArray.get(position);

            holder1.bind(data);
        }
    }

    public void setFolderArray(ArrayList<File> array) {
        folderArray = array;
        notifyDataSetChanged();
    }

    public void setCallback(GenericCallback callback) {
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return folderArray.size();
    }

    public class MyFolderHolder extends RecyclerView.ViewHolder {
        TextView fileNameTv, fileSizeTv, dateTv;
        ConstraintLayout rootLayout;

        public MyFolderHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            fileSizeTv = itemView.findViewById(R.id.fileSizeTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

    private class ViewHolderSwipe extends RecyclerView.ViewHolder {
        SwipeRevealLayout swipeLayout;
        View frontLayout;
        View deleteLayout;
        TextView textView;
        TextView fileNameTv, fileSizeTv, dateTv;
        ConstraintLayout rootLayout;

        public ViewHolderSwipe(View itemView) {
            super(itemView);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
            textView = (TextView) itemView.findViewById(R.id.text);
            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            fileSizeTv = itemView.findViewById(R.id.fileSizeTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }

        public void bind(final File data) {
            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFile(data);
                    folderArray.remove(getAdapterPosition());
                    callback.callback(String.valueOf(folderArray.size()));
                    notifyItemRemoved(getAdapterPosition());

                }
            });
            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File temFile = new File(folderArray.get(getAdapterPosition()).getAbsolutePath());
                    if (callback != null) {
                        callback.callback(temFile);
                    }
                }
            });

            fileNameTv.setText(data.getName());
            fileSizeTv.setText("Files: " + data.listFiles().length);
            dateTv.setText(FileInfoUtils.getFormattedDate(data));
        }
    }

    public void deleteFile(File file) {
//        File deleteFile = new File(file.getAbsolutePath());
        if (file.listFiles().length == 0) {
            if (file.exists())
                file.delete();
        } else {
            for (File tempFile : file.listFiles()) {
                if (tempFile.exists())
                    tempFile.delete();
            }
            file.delete();
        }
    }
}
