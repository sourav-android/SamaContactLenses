package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sama.samacontactlenses.Activity.SearchActivity;
import com.sama.samacontactlenses.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_PARENT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;

public class AvailableOfferAdapter extends RecyclerView.Adapter<AvailableOfferAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public AvailableOfferAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.offer_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);


        if (null != map.get(PRODUCT_TITLE)) {
            holder.offertext.setText(map.get(PRODUCT_TITLE));
        }

    }

    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView offertext, title1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            offertext = itemView.findViewById(R.id.offertext);
        }
    }
}