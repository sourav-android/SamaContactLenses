package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.provider.Settings;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Activity.CheckoutActivity;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.CART_DETAILS_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.LEFT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.LEFT_B_C;
import static com.sama.samacontactlenses.Common.Constant.LEFT_CYL;
import static com.sama.samacontactlenses.Common.Constant.LEFT_EYE;
import static com.sama.samacontactlenses.Common.Constant.LEFT_SPH;
import static com.sama.samacontactlenses.Common.Constant.POWER;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_QTY;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_B_C;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_CYL;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_EYE;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_SPH;

public class OrderPlaceAdapter extends RecyclerView.Adapter<OrderPlaceAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public OrderPlaceAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_place_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.pro_title_tv.setText(map.get(PRODUCT_TITLE));
        holder.pro_desc_tv.setText(map.get(CATEGORY));

        holder.price_tv.setText(map.get(PRODUCT_PRICE)+" "+map.get(CURR_CODE));

        holder.count_tv.setText(map.get(PRODUCT_QTY) +" "+activity.getString(R.string.item)+" | ");
        if (map.get(POWER).equals("WP")){
            holder.power_tv.setText(activity.getString(R.string.with_power));
            holder.power_lay.setVisibility(View.VISIBLE);
            if (map.get(LEFT_EYE).equals("")){
                holder.shp_lay.setVisibility(View.GONE);
            }else {
                holder.shp_tv.setText(map.get(LEFT_EYE));
            }
            if (map.get(RIGHT_EYE).equals("")){
                holder.r_shp_lay.setVisibility(View.GONE);
            }else {
                holder.r_shp_tv.setText(map.get(RIGHT_EYE));
            }
            holder.cyl_lay.setVisibility(View.GONE);
            holder.ax_lay.setVisibility(View.GONE);
            holder.b_c_lay.setVisibility(View.GONE);
            holder.r_cyl_lay.setVisibility(View.GONE);
            holder.r_ax_lay.setVisibility(View.GONE);
            holder.r_b_c_lay.setVisibility(View.GONE);
        }else if (map.get(POWER).equals("AP")){
            holder.power_tv.setText(activity.getString(R.string.with_astigmatism));
            holder.power_lay.setVisibility(View.VISIBLE);

            if (map.get(LEFT_SPH).equals("")){
                holder.shp_lay.setVisibility(View.GONE);
            }else {
                holder.shp_tv.setText(map.get(LEFT_SPH));
            }

            if (map.get(LEFT_CYL).equals("")){
                holder.cyl_lay.setVisibility(View.GONE);
            }else {
                holder.cyl_tv.setText(map.get(LEFT_CYL));
            }

            if (map.get(LEFT_AXIS).equals("")){
                holder.ax_lay.setVisibility(View.GONE);
            }else {
                holder.ax_tv.setText(map.get(LEFT_AXIS));
            }

            if (map.get(LEFT_B_C).equals("")){
                holder.b_c_lay.setVisibility(View.GONE);
            }else {
                holder.b_c_tv.setText(map.get(LEFT_B_C));
            }

            if (map.get(RIGHT_SPH).equals("")){
                holder.r_shp_lay.setVisibility(View.GONE);
            }else {
                holder.r_shp_tv.setText(map.get(RIGHT_SPH));
            }

            if (map.get(RIGHT_CYL).equals("")){
                holder.r_cyl_lay.setVisibility(View.GONE);
            }else {
                holder.r_cyl_tv.setText(map.get(RIGHT_CYL));
            }

            if (map.get(RIGHT_AXIS).equals("")){
                holder.r_ax_lay.setVisibility(View.GONE);
            }else {
                holder.r_ax_tv.setText(map.get(RIGHT_AXIS));
            }

            if (map.get(RIGHT_B_C).equals("")){
                holder.r_b_c_lay.setVisibility(View.GONE);
            }else {
                holder.r_b_c_tv.setText(map.get(RIGHT_B_C));
            }

        }else {
            holder.power_tv.setVisibility(View.GONE);
            holder.power_lay.setVisibility(View.GONE);
        }

        Picasso.get().load(PRODUCT_IMAGE_URL + map.get(PRODUCT_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(holder.prd_img);
    }

    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pro_title_tv, pro_desc_tv, count_tv, price_tv;
        private ImageView prd_img;
        private TextView power_tv;
        private TextView shp_tv, cyl_tv, ax_tv, b_c_tv,r_shp_tv, r_cyl_tv, r_ax_tv, r_b_c_tv;
        private LinearLayout shp_lay, cyl_lay, ax_lay, b_c_lay,r_shp_lay, r_cyl_lay, r_ax_lay, r_b_c_lay;
        private LinearLayout power_lay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prd_img = itemView.findViewById(R.id.prd_img);
            pro_title_tv = itemView.findViewById(R.id.pro_title_tv);
            pro_desc_tv = itemView.findViewById(R.id.pro_desc_tv);
            count_tv = itemView.findViewById(R.id.count_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            power_tv = itemView.findViewById(R.id.power_tv);
            power_lay = itemView.findViewById(R.id.power_lay);

            shp_tv = itemView.findViewById(R.id.shp_tv);
            cyl_tv = itemView.findViewById(R.id.cyl_tv);
            ax_tv = itemView.findViewById(R.id.ax_tv);
            b_c_tv = itemView.findViewById(R.id.b_c_tv);
            r_shp_tv = itemView.findViewById(R.id.r_shp_tv);
            r_cyl_tv = itemView.findViewById(R.id.r_cyl_tv);
            r_ax_tv = itemView.findViewById(R.id.r_ax_tv);
            r_b_c_tv = itemView.findViewById(R.id.r_b_c_tv);

            shp_lay = itemView.findViewById(R.id.shp_lay);
            cyl_lay = itemView.findViewById(R.id.cyl_lay);
            ax_lay = itemView.findViewById(R.id.ax_lay);
            b_c_lay = itemView.findViewById(R.id.b_c_lay);
            r_shp_lay = itemView.findViewById(R.id.r_shp_lay);
            r_cyl_lay = itemView.findViewById(R.id.r_cyl_lay);
            r_ax_lay = itemView.findViewById(R.id.r_ax_lay);
            r_b_c_lay = itemView.findViewById(R.id.r_b_c_lay);
        }
    }

}
