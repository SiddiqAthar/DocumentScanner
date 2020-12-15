package me.sid.smartcropper.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rishabhharit.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.models.FiltersData;

public class MultiFilesAdapter extends RecyclerView.Adapter<MultiFilesAdapter.FilesHolder> {

    private ArrayList<Bitmap> mImages;
    private Context mContext;
 
    public MultiFilesAdapter(ArrayList<Bitmap> images, Activity context) {
        this.mImages = images;
        this.mContext = context;
    }


    @Override
    public FilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_item_layout, parent, false);

        return new FilesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesHolder holder, int position) {

        Glide.with(mContext).load(mImages.get(position)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.multi_image_item);
         holder.count_textView.setText(String.valueOf(position+1));
     }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    class FilesHolder extends RecyclerView.ViewHolder {
        RoundedImageView multi_image_item;
        TextView count_textView;

        public FilesHolder(@NonNull View itemView) {
            super(itemView);
            multi_image_item = itemView.findViewById(R.id.multi_image_item);
            count_textView = itemView.findViewById(R.id.count_textView);
        }

    }

}
