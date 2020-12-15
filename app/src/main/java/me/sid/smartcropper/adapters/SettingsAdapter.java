package me.sid.smartcropper.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.models.SettingsData;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MySettingHolder> {

    private ArrayList<SettingsData> mSettingItems;
    private Context mContext;
    private GenericCallback callback;

    public SettingsAdapter(ArrayList<SettingsData> mSettingItems, Activity mContext, GenericCallback callback) {
        this.mSettingItems = mSettingItems;
        this.mContext = mContext;
        this.callback = callback;
    }


    @NonNull
    @Override
    public MySettingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_item_layout, parent, false);
        return new MySettingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySettingHolder holder, int position) {

            holder.setting_icon.setImageResource(mSettingItems.get(position).getIcon());
            holder.setting_goto.setImageResource(mSettingItems.get(position).getGoToIcon());
            holder.setting_itemtv.setText(mSettingItems.get(position).getItemName());


            if (position==0)
            holder.setting_itemtv.setTextColor(mContext.getResources().getColor(R.color.settings_yello));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.callback(mSettingItems.get(holder.getAdapterPosition()).getItemName());
                }
            });

    }

    @Override
    public int getItemCount() {
        return mSettingItems.size();
    }

    class MySettingHolder extends RecyclerView.ViewHolder {
        ConstraintLayout rootLayout;
        TextView setting_itemtv;
        ImageView setting_icon, setting_goto;

        public MySettingHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            setting_itemtv = itemView.findViewById(R.id.setting_itemtv);
            setting_icon = itemView.findViewById(R.id.setting_icon);
            setting_goto = itemView.findViewById(R.id.setting_goto);

        }

    }

}
