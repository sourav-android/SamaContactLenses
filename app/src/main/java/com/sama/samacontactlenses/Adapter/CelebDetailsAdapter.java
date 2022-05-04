package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sama.samacontactlenses.Activity.CelebrityDetailsActivity;
import com.sama.samacontactlenses.Activity.ProductDetailsActivity;
import com.sama.samacontactlenses.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.RequiresApi;

import static com.sama.samacontactlenses.Common.Constant.BANNER_BUTTON;
import static com.sama.samacontactlenses.Common.Constant.BANNER_HEADING;
import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.BANNER_URL;
import static com.sama.samacontactlenses.Common.Constant.CELEB_ID;
import static com.sama.samacontactlenses.Common.Constant.CELEB_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CELEB_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.CELEB_TITLE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;

public class CelebDetailsAdapter extends SliderViewAdapter<CelebDetailsAdapter.SliderAdapterVH> {

    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public CelebDetailsAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.celeb_details_item, null);
        return new SliderAdapterVH(inflate);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        final HashMap<String, String> map = map_list.get(position);
        viewHolder.cname1.setText(map.get(CELEB_TITLE));
        Glide.with(activity).load(CELEB_IMAGE_URL+map.get(CELEB_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(viewHolder.cimage1);


        //((CelebrityDetailsActivity)activity).setValue(map.get(PRODUCT_TITLE),map.get(CELEB_ID));

//        viewHolder.itemView.setOnDragListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        viewHolder.itemView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                Log.e("SCROLL", "SCROLL");
                ((CelebrityDetailsActivity)activity).startCycle();
                return false;
            }
        });

    }

    @Override
    public int getCount() {
        return map_list.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        ImageView cimage1;
        TextView cname1, pname, text4;
        Button view;
        RelativeLayout main_lay;
        View itemView;
        public SliderAdapterVH(View itemView) {
            super(itemView);
            cimage1 = itemView.findViewById(R.id.cimage1);
            cname1 = itemView.findViewById(R.id.cname1);
            this.itemView = itemView;
//            pname = itemView.findViewById(R.id.pname);
//            view = itemView.findViewById(R.id.view);
        }
    }
}