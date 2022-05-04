package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sama.samacontactlenses.Activity.CelebrityDetailsActivity;
import com.sama.samacontactlenses.Activity.ProductDetailsActivity;
import com.sama.samacontactlenses.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.CELEB_ID;
import static com.sama.samacontactlenses.Common.Constant.CELEB_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CELEB_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.CELEB_TITLE;
import static com.sama.samacontactlenses.Common.Constant.IS_CLICKEBLE2;
import static com.sama.samacontactlenses.Common.Constant.IS_CLICKEBLE3;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;

public class CelebAdapter extends RecyclerView.Adapter<CelebAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public CelebAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.celeb_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);

        holder.cname1.setText(map.get(PRODUCT_TITLE));
        if(null != map.get(CATEGORY_ID)){
            holder.cname2.setText(map.get(CATEGORY_TITLE));
            Glide.with(activity).load(CELEB_IMAGE_URL+map.get(CATEGORY_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(holder.cimage2);
            if (Integer.parseInt(map.get(IS_CLICKEBLE2))>0){
                holder.cimage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, CelebrityDetailsActivity.class);
                        i.putExtra("celeb_id",map.get(CATEGORY_ID));
                        activity.startActivity(i);
                    }
                });
            }

            holder.part2.setVisibility(View.VISIBLE);
        }else {
            holder.part2.setVisibility(View.GONE);
        }

        if(null != map.get(CELEB_ID)){
            holder.cname3.setText(map.get(CELEB_TITLE));
            Glide.with(activity).load(CELEB_IMAGE_URL+map.get(CELEB_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(holder.cimage3);
            if (Integer.parseInt(map.get(IS_CLICKEBLE3))>0){
                holder.cimage3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, CelebrityDetailsActivity.class);
                        i.putExtra("celeb_id",map.get(CELEB_ID));
                        activity.startActivity(i);
                    }
                });
            }
            holder.part3.setVisibility(View.VISIBLE);
        }else {
            holder.part3.setVisibility(View.GONE);
        }




        Glide.with(activity).load(CELEB_IMAGE_URL+map.get(PRODUCT_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(holder.cimage1);




        holder.cimage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, CelebrityDetailsActivity.class);
                i.putExtra("celeb_id",map.get(PRODUCT_ID));
                activity.startActivity(i);
            }
        });




    }

    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cname1, cname2, cname3, mobile_tv, email_tv;
        private RelativeLayout part2,part3;
        private ImageView cimage1, cimage2, cimage3, edit_img, remove_img;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            cname1 = itemView.findViewById(R.id.cname1);
            cname2 = itemView.findViewById(R.id.cname2);
            cname3 = itemView.findViewById(R.id.cname3);
            cimage1 = itemView.findViewById(R.id.cimage1);
            cimage2 = itemView.findViewById(R.id.cimage2);
            cimage3 = itemView.findViewById(R.id.cimage3);
            part2 = itemView.findViewById(R.id.part2);
            part3 = itemView.findViewById(R.id.part3);

        }
    }
}