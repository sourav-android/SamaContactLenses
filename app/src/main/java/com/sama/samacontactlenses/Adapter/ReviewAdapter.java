package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.jsibbold.zoomage.ZoomageView;
import com.sama.samacontactlenses.Activity.ImageShowZoomActivity;
import com.sama.samacontactlenses.Activity.ProductDetailsActivity;
import com.sama.samacontactlenses.Activity.SearchActivity;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.ScaleRatingBar;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.CRAETED_AT;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_FAV;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Common.Constant.RATING_START;
import static com.sama.samacontactlenses.Common.Constant.REVIEW_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.USER_NAME;
import static com.sama.samacontactlenses.Helper.MethodClass.loginPopup;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public ReviewAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.user_name.setText(map.get(USER_NAME));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.desc.setText(Html.fromHtml(map.get(PRODUCT_DESC), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.desc.setText(Html.fromHtml(map.get(PRODUCT_DESC)));
        }
        Glide.with(activity).load(REVIEW_IMAGE+map.get(PRODUCT_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(holder.image);
        holder.simpleRatingBar.setRating(Float.parseFloat(map.get(RATING_START)));

        try{
            String datestr=map.get(CRAETED_AT);
            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date=dateFormat.parse(datestr);
            holder.datetv.setText(android.text.format.DateFormat.format("dd, MMM yyyy",date));
        }catch (Exception e){

        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, ImageShowZoomActivity.class);
                intent.putExtra("image_url",REVIEW_IMAGE+map.get(PRODUCT_IMAGE));
                activity.startActivity(intent);
                /*final Dialog dialog =new Dialog(activity);
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                dialog.setContentView(R.layout.photo_view_popup);
                dialog.setCancelable(false);
                ImageView close_iv =(ImageView)dialog.findViewById(R.id.close_iv);
                final ImageView photo_iv =(ImageView)dialog.findViewById(R.id.photo_iv);
                Glide.with(activity).load(REVIEW_IMAGE+map.get(PRODUCT_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(photo_iv);
                close_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();*/
            }
        });

    }
    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView datetv,desc,user_name;
        private ZoomageView image;
        private ScaleRatingBar simpleRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            datetv = itemView.findViewById(R.id.datetv);
            desc = itemView.findViewById(R.id.desc);
            user_name = itemView.findViewById(R.id.user_name);
            simpleRatingBar = itemView.findViewById(R.id.simpleRatingBar);
        }
    }

}
