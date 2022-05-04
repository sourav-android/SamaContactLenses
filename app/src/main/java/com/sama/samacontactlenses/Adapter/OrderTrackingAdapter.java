package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sama.samacontactlenses.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.sama.samacontactlenses.Common.Constant.BALANCE;
import static com.sama.samacontactlenses.Common.Constant.BALANCE_TYPE;
import static com.sama.samacontactlenses.Common.Constant.CRAETED_AT;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.DESC;
import static com.sama.samacontactlenses.Common.Constant.TRACK_ACTIVITY;
import static com.sama.samacontactlenses.Common.Constant.TRACK_DATE;
import static com.sama.samacontactlenses.Common.Constant.TRACK_DETAILS;
import static com.sama.samacontactlenses.Common.Constant.TRACK_LOCATION;

public class OrderTrackingAdapter extends RecyclerView.Adapter<OrderTrackingAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;
    public OrderTrackingAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tracking_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.title.setText(map.get(TRACK_ACTIVITY));
        holder.desc.setText(map.get(TRACK_DETAILS)+" on "+map.get(TRACK_DATE));
        if (map.get(TRACK_ACTIVITY).contains("CANCELLED")){
            holder.image_track.setImageDrawable(activity.getDrawable(R.drawable.ic_remove));
            holder.line_lin.setBackgroundColor(activity.getResources().getColor(R.color.red_clr));
        }else {
            holder.image_track.setImageDrawable(activity.getDrawable(R.drawable.ic_check));
            holder.line_lin.setBackgroundColor(activity.getResources().getColor(R.color.blue_clr));
        }
    }
    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title,desc;
        private ImageView image_track;
        private LinearLayout line_lin;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            image_track = itemView.findViewById(R.id.image_track);
            line_lin = itemView.findViewById(R.id.line_lin);
        }
    }





}
