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

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Activity.HomeActivity;
import com.sama.samacontactlenses.Activity.ShoppingCartActivity;
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
import static com.sama.samacontactlenses.Common.Constant.CART_MASTER_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY;
import static com.sama.samacontactlenses.Common.Constant.FREE_QTY;
import static com.sama.samacontactlenses.Common.Constant.LEFT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.LEFT_B_C;
import static com.sama.samacontactlenses.Common.Constant.LEFT_CYL;
import static com.sama.samacontactlenses.Common.Constant.LEFT_EYE;
import static com.sama.samacontactlenses.Common.Constant.LEFT_SPH;
import static com.sama.samacontactlenses.Common.Constant.POWER;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_FREE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
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
import static com.sama.samacontactlenses.Helper.MethodClass.noFormat;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public ShoppingCartAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sopping_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.pro_title_tv.setText(map.get(PRODUCT_TITLE));
        holder.pro_desc_tv.setText(map.get(CATEGORY));
        Double D = ((Double.parseDouble(map.get(PRODUCT_PRICE))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""))));

        holder.price_tv.setText(noFormat(D)+" "+PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code",""));
        holder.rm_prd_img.setVisibility(View.VISIBLE);
        holder.minus_count_tv.setVisibility(View.VISIBLE);
        holder.plus_count_tv.setVisibility(View.VISIBLE);
        holder.count_tv.setText(map.get(PRODUCT_QTY));
       /* if(map.get(PRODUCT_FREE).equals("N")){

        }else {
            holder.price_tv.setText("Free");
            holder.rm_prd_img.setVisibility(View.GONE);
            holder.minus_count_tv.setVisibility(View.GONE);
            holder.plus_count_tv.setVisibility(View.GONE);
            holder.count_tv.setText("Qty"+map.get(PRODUCT_QTY));
        }*/

        if (map.get(FREE_QTY).equals("0")){
            holder.free_qty_tv.setVisibility(View.GONE);
        }else {
            holder.free_qty_tv.setText(map.get(FREE_QTY)+" Free Item");
            holder.free_qty_tv.setVisibility(View.VISIBLE);
        }

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

        holder.plus_count_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(holder.count_tv.getText().toString().trim());
                count = count + 1;
                holder.count_tv.setText(String.valueOf(count));
                update_cart(map.get(PRODUCT_ID),String.valueOf(count),map.get(CART_MASTER_ID),map.get(CART_DETAILS_ID));
            }
        });

        holder.minus_count_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(holder.count_tv.getText().toString().trim());
                if (count > 1) {
                    count = count - 1;
                    holder.count_tv.setText(String.valueOf(count));
                    update_cart(map.get(PRODUCT_ID),String.valueOf(count),map.get(CART_MASTER_ID),map.get(CART_DETAILS_ID));
                }
            }
        });

       /* holder.count_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_to_Cart();
            }
        });*/

        holder.rm_prd_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_QUESTION)
                        .setTitle(activity.getString(R.string.remove_product))
                        .setDescription(activity.getString(R.string.do_you_want_remove_item))
                        .setPositiveText(activity.getString(R.string.yes))
                        .setNegativeText(activity.getString(R.string.no))
                        .setPositiveListener(new ClickListener() {
                            @Override
                            public void onClick(LottieAlertDialog lottieAlertDialog) {
                                rm_to_cart(map.get(CART_DETAILS_ID));
                                lottieAlertDialog.dismiss();
                            }
                        }).setNegativeListener(new ClickListener() {
                            @Override
                            public void onClick(LottieAlertDialog lottieAlertDialog) {
                                lottieAlertDialog.dismiss();
                            }
                        })
                        .build();
                alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pro_title_tv, pro_desc_tv, plus_count_tv, count_tv, minus_count_tv, price_tv,free_qty_tv;
        private ImageView prd_img, rm_prd_img;
        private TextView power_tv;
        private TextView shp_tv, cyl_tv, ax_tv, b_c_tv,r_shp_tv, r_cyl_tv, r_ax_tv, r_b_c_tv;
        private LinearLayout shp_lay, cyl_lay, ax_lay, b_c_lay,r_shp_lay, r_cyl_lay, r_ax_lay, r_b_c_lay;
        private LinearLayout power_lay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prd_img = itemView.findViewById(R.id.prd_img);
            free_qty_tv = itemView.findViewById(R.id.free_qty_tv);
            rm_prd_img = itemView.findViewById(R.id.rm_prd_img);
            pro_title_tv = itemView.findViewById(R.id.pro_title_tv);
            pro_desc_tv = itemView.findViewById(R.id.pro_desc_tv);
            plus_count_tv = itemView.findViewById(R.id.plus_count_tv);
            count_tv = itemView.findViewById(R.id.count_tv);
            minus_count_tv = itemView.findViewById(R.id.minus_count_tv);
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

    public void rm_to_cart(String cart_details_id) {
        MethodClass.showProgressDialog(activity);
        String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = activity.getString(R.string.SERVER_URL) + "remove-cart/" + cart_details_id;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", android_id);
        params.put("cart_details_id", cart_details_id);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("ADD TO CART", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try {
                    MethodClass.hideProgressDialog(activity);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(activity, response);
                    if (result_Object != null) {
                        String total_cart_item=result_Object.getString("total_cart_item");
                        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("cart_count",total_cart_item).commit();
                        ((ShoppingCartActivity) activity).getCart();
                        JSONObject MainMsg = result_Object.getJSONObject("message");
                        String message = result_Object.getString("message");
                        String meaning = result_Object.getString("meaning");
/*                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_SUCCESS)
                                .setTitle(message)
                                .setDescription(meaning)
                                .setPositiveText("Ok")
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        alertDialog.show();*/
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(activity);
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(activity);
                Log.e("error", error.toString());
                if (error.toString().contains("AuthFailureError")) {
                    Toast.makeText(activity, "Authentication Failure.Please uninstall the app and reinstall it.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Something went wrong.....", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(activity).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(activity).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
    }





    public void update_cart(String product_id,String quantity,String cart_master_id,String cart_detals_id){
        MethodClass.showProgressDialog(activity);
        String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = activity.getString(R.string.SERVER_URL) + "update-cart";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", android_id);
        params.put("quantity", quantity);
        params.put("cart_master_id", cart_master_id);
        params.put("cart_details_id", cart_detals_id);
        params.put("product_id", product_id);



        Log.e("ADD TO CART", new JSONObject(params).toString());

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try {
                    MethodClass.hideProgressDialog(activity);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(activity, response);
                    if (result_Object != null) {
                        ((ShoppingCartActivity) activity).getCart();
                        JSONObject MainMsg = result_Object.getJSONObject("message");
                        String message = MainMsg.getString("message");
                        String meaning = MainMsg.getString("meaning");
                        /*LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_SUCCESS)
                                .setTitle(message)
                                .setDescription(meaning)
                                .setPositiveText("Ok")
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        alertDialog.show();*/
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(activity);
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(activity);
                Log.e("error", error.toString());
                if (error.toString().contains("AuthFailureError")) {
                    Toast.makeText(activity, "Authentication Failure.Please uninstall the app and reinstall it.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Something went wrong.....", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(activity).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(activity).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };

        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
    }

}
