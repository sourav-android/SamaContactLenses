package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sama.samacontactlenses.Activity.ProductDetailsActivity;
import com.sama.samacontactlenses.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC_TYPE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_OFF;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Helper.MethodClass.noFormat;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public TrendingAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trending_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);

        holder.title.setText(map.get(PRODUCT_TITLE));
        holder.desc.setText(map.get(PRODUCT_DESC));

        if(map.get(PRODUCT_DISC_TYPE).equals("P")){
            holder.offer.setVisibility(View.VISIBLE);
            holder.offer.setText(activity.getString(R.string.sale_small));
            Double D = ((Double.parseDouble(map.get(PRODUCT_PRICE))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""))));
            holder.discount.setVisibility(View.GONE);
            holder.percent.setVisibility(View.VISIBLE);
            Double pri = Double.parseDouble(map.get(PRODUCT_DISC));
            holder.percent.setText(noFormat(pri)+activity.getString(R.string.percent_off));
            holder.price.setText(noFormat(D)+" "+PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code",""));
        }else if(map.get(PRODUCT_DISC_TYPE).equals("F")){
            holder.offer.setVisibility(View.VISIBLE);
            holder.offer.setText(activity.getString(R.string.sale_small));
            Double D = ((Double.parseDouble(map.get(PRODUCT_PRICE))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""))));

            Double E = Double.parseDouble(map.get(PRODUCT_DISC))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""));
            Double pri = D - E;
            holder.discount.setText(noFormat(D)+" ");
            holder.discount.setPaintFlags(holder.discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discount.setVisibility(View.VISIBLE);
            holder.price.setText(noFormat(pri)+" "+PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code",""));
        }else {
            holder.offer.setVisibility(View.GONE);
            Double D = ((Double.parseDouble(map.get(PRODUCT_PRICE))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""))));

            holder.price.setText(noFormat(D)+" "+PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code",""));
        }
        Glide.with(activity).load(PRODUCT_IMAGE_URL+map.get(PRODUCT_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(holder.image);

        holder.main_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, ProductDetailsActivity.class);
                i.putExtra("product_id",map.get(PRODUCT_ID));
                activity.startActivity(i);
            }
        });
    }
        @Override
        public int getItemCount() {
            return map_list == null ? 0 : map_list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView title,desc,price,offer,discount,percent;
            private RelativeLayout main_lay;
            private ImageView image;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.image);
                title = itemView.findViewById(R.id.title);
                desc = itemView.findViewById(R.id.desc);
                price = itemView.findViewById(R.id.price);
                offer = itemView.findViewById(R.id.offer);
                discount = itemView.findViewById(R.id.discount);
                main_lay = itemView.findViewById(R.id.main_lay);
                percent = itemView.findViewById(R.id.percent);

            }
        }
}
