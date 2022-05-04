package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sama.samacontactlenses.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sama.samacontactlenses.Common.Constant.BANNER_BUTTON;
import static com.sama.samacontactlenses.Common.Constant.BANNER_HEADING;
import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.BANNER_URL;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE_URL;

public class DetailsImageAdapter extends SliderViewAdapter<DetailsImageAdapter.SliderAdapterVH> {

    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public DetailsImageAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_image_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        final HashMap<String, String> map = map_list.get(position);

        Glide.with(activity)
                .load(PRODUCT_IMAGE_URL + map.get(BANNER_IMAGE))
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading)
                .into(viewHolder.image);


    }

    @Override
    public int getCount() {
        return map_list.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        ImageView image;


        public SliderAdapterVH(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}