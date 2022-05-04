package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sama.samacontactlenses.Activity.SearchActivity;
import com.sama.samacontactlenses.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_PARENT_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_PARENT_ID2;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_OFF;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public CategoryAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        ViewTreeObserver vto = holder.main_lay.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                holder.main_lay.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalHeight = holder.main_lay.getMeasuredHeight();
                int finalWidth = holder.main_lay.getMeasuredWidth();
                ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) holder.main_lay.getLayoutParams();
                layoutParams.width = finalWidth-20;
                holder.main_lay.setLayoutParams(layoutParams);
                return true;
            }
        });

        if(null != map.get(PRODUCT_TITLE)){
            holder.title.setText(map.get(PRODUCT_TITLE));
            Glide.with(activity).load(CATEGORY_IMAGE_URL+map.get(PRODUCT_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(holder.image);
            holder.frst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent I = new Intent(activity, SearchActivity.class);
                    I.putExtra("category_id",map.get(PRODUCT_ID));
                    I.putExtra("parent_id",map.get(CATEGORY_PARENT_ID));
                    I.putExtra("key","");
                    I.putExtra("sort","");
                    I.putExtra("min_price", "");
                    I.putExtra("max_price", "");
                    activity.startActivity(I);
                }
            });
            holder.image.setVisibility(View.VISIBLE);
            holder.title.setVisibility(View.VISIBLE);
            if(map.get(PRODUCT_OFF).equals("N")){
                holder.sale.setVisibility(View.GONE);
            }else {
                holder.sale.setVisibility(View.VISIBLE);
            }
        }else {
            holder.image.setVisibility(View.GONE);
            holder.title.setVisibility(View.GONE);
        }

//        if(null != map.get(CATEGORY_TITLE)){
//            holder.title1.setText(map.get(CATEGORY_TITLE));
//
//            Glide.with(activity).load(CATEGORY_IMAGE_URL+map.get(CATEGORY_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(holder.image1);
//
//
//            holder.scnd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent I = new Intent(activity, SearchActivity.class);
//                    I.putExtra("category_id",map.get(CATEGORY_ID));
//                    I.putExtra("parent_id",map.get(CATEGORY_PARENT_ID2));
//                    I.putExtra("key","");
//                    I.putExtra("sort","");
//                    I.putExtra("min_price", "");
//                    I.putExtra("max_price", "");
//                    activity.startActivity(I);
//                }
//            });
//            holder.title1.setVisibility(View.VISIBLE);
//            holder.image1.setVisibility(View.VISIBLE);
//        }else {
//            holder.title1.setVisibility(View.GONE);
//            holder.image1.setVisibility(View.GONE);
//        }

    }

    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, title1,sale;
        private RelativeLayout frst,scnd;
        private ImageView image, image1;
        private LinearLayout main_lay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            //image1 = itemView.findViewById(R.id.image1);
            title = itemView.findViewById(R.id.title);
            //title1 = itemView.findViewById(R.id.title1);
            frst = itemView.findViewById(R.id.frst);
            main_lay = itemView.findViewById(R.id.main_lay);
            sale = itemView.findViewById(R.id.sale);
            //scnd = itemView.findViewById(R.id.scnd);
        }
    }
}
