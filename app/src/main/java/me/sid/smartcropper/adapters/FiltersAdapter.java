package me.sid.smartcropper.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.models.FiltersData;
import me.sid.smartcropper.models.SettingsData;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.FiltersHolder> {

    private ArrayList<FiltersData> mFilterItems;
    private Context mContext;
    FilterItemClicked favoriteItemClicked;

    public FiltersAdapter(ArrayList<FiltersData> mFilterItems, Activity mContext,  FilterItemClicked favoriteItemClicked) {
        this.mFilterItems = mFilterItems;
        this.mContext = mContext;
        this.favoriteItemClicked=favoriteItemClicked;
    }


    @NonNull
    @Override
    public FiltersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item_layout, parent, false);

        return new FiltersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FiltersHolder holder, int position) {

            holder.filter_iv.setImageResource(mFilterItems.get(position).getFilterIcon());
            holder.filter_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        favoriteItemClicked.filterItemClicked(mFilterItems.get(position));

                }
            });

    }

    @Override
    public int getItemCount() {
        return mFilterItems.size();
    }

    class FiltersHolder extends RecyclerView.ViewHolder {
        ImageView filter_iv;

        public FiltersHolder(@NonNull View itemView) {
            super(itemView);
            filter_iv = itemView.findViewById(R.id.filter_iv);
        }

    }
    public interface FilterItemClicked {
     void filterItemClicked(FiltersData filtersData);
    }
}
