package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sama.samacontactlenses.Activity.ProductDetailsActivity;
import com.sama.samacontactlenses.Activity.SearchActivity;
import com.sama.samacontactlenses.Activity.SeeAllActivity;
import com.sama.samacontactlenses.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sama.samacontactlenses.Common.Constant.BANNER_BUTTON;
import static com.sama.samacontactlenses.Common.Constant.BANNER_HEADING;
import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.BANNER_URL;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_PARENT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;

public class HomeSliderAdapter extends SliderViewAdapter<HomeSliderAdapter.SliderAdapterVH> {

    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public HomeSliderAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_slider_layout, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {


        final HashMap<String, String> map = map_list.get(position);
        Log.e("position", position + "");

        if (!map.get(BANNER_HEADING).equals("null")) {
            viewHolder.text4.setText(map.get(BANNER_HEADING));
            viewHolder.text4.setVisibility(View.VISIBLE);
        } else {
            viewHolder.text4.setVisibility(View.GONE);
        }

        if (map.get(BANNER_URL).equals("null") || map.get(BANNER_URL).equals("") || map.get(BANNER_URL).equals(null) || map.get(BANNER_URL).equals("H")) {
            viewHolder.btn.setVisibility(View.GONE);
        } else if (map.get(BANNER_URL).equals("T")) {
            viewHolder.btn.setVisibility(View.VISIBLE);
            viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(activity, SeeAllActivity.class);
                    I.putExtra("from", "trend");
                    activity.startActivity(I);
                }
            });

        } else if (map.get(BANNER_URL).equals("N")) {
            viewHolder.btn.setVisibility(View.VISIBLE);
            viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(activity, SeeAllActivity.class);
                    I.putExtra("from", "new");
                    activity.startActivity(I);
                }
            });
        }else if (map.get(BANNER_URL).equals("O")) {
            viewHolder.btn.setVisibility(View.VISIBLE);
            viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(activity, SeeAllActivity.class);
                    I.putExtra("from", "off");
                    activity.startActivity(I);
                }
            });
        }else {
            viewHolder.btn.setVisibility(View.VISIBLE);
            viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(activity, SearchActivity.class);
                    I.putExtra("category_id",map.get(BANNER_URL));
                    I.putExtra("parent_id","");
                    I.putExtra("key","");
                    I.putExtra("sort","");
                    I.putExtra("min_price", "");
                    I.putExtra("max_price", "");
                    activity.startActivity(I);
                }
            });
        }

        if (!map.get(BANNER_BUTTON).equals("null")) {
            viewHolder.btn.setText(map.get(BANNER_BUTTON));
            viewHolder.btn.setVisibility(View.VISIBLE);
        } else {
            viewHolder.btn.setVisibility(View.GONE);
        }

        Glide.with(activity)
                .load(BANNER_IMAGE_URL + map.get(BANNER_IMAGE))
                .placeholder(R.drawable.home_banner)
                .error(R.drawable.home_banner)
                .into(viewHolder.image);


    }

    @Override
    public int getCount() {
        return map_list.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        ImageView image;
        TextView text2, text3, text4;
        Button btn;
        RelativeLayout main_lay;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text4 = itemView.findViewById(R.id.text4);
            btn = itemView.findViewById(R.id.btn);
        }
    }
}

/*
            viewHolder.btn.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        Uri webpage = Uri.parse(map.get(BANNER_URL));

        if (!map.get(BANNER_URL).startsWith("http://") && !map.get(BANNER_URL).startsWith("https://")) {
        webpage = Uri.parse("http://" + map.get(BANNER_URL));
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
        activity.startActivity(intent);
        }
        }
        });*/
