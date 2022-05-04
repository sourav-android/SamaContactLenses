package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.material.card.MaterialCardView;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Activity.OrderDetailsActivity;
import com.sama.samacontactlenses.Activity.OrderTrackActivity;
import com.sama.samacontactlenses.Activity.ShoppingCartActivity;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.AWB_CODE;
import static com.sama.samacontactlenses.Common.Constant.CREATE_DATE;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.INTERNAL_STATUS;
import static com.sama.samacontactlenses.Common.Constant.ORDER_ID;
import static com.sama.samacontactlenses.Common.Constant.ORDER_NO;
import static com.sama.samacontactlenses.Common.Constant.PAYMENT_MODE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.RESCHEDULE_DATE;
import static com.sama.samacontactlenses.Common.Constant.RESCHEDULE_TIME;
import static com.sama.samacontactlenses.Common.Constant.SHIPPING_PRICE;
import static com.sama.samacontactlenses.Common.Constant.STATUS;
import static com.sama.samacontactlenses.Common.Constant.TOTAL_ITEM;
import static com.sama.samacontactlenses.Common.Constant.TOTAL_PRICE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class RecentOrderHistoryAdapter extends RecyclerView.Adapter<RecentOrderHistoryAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public RecentOrderHistoryAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recent_order_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.order_no.setText(activity.getString(R.string.order_no) + map.get(ORDER_NO));

        holder.total_price.setText(map.get(TOTAL_PRICE)+" "+map.get(CURR_CODE));

        holder.item_count.setText("(" + activity.getString(R.string.item_colon) + map.get(TOTAL_ITEM) + ")");

        if (map.get(PAYMENT_MODE).equals("C")) {
            holder.paybale_mode.setText(activity.getString(R.string.cod_breaket));
        } else if (map.get(PAYMENT_MODE).equals("O")){
            holder.paybale_mode.setText(activity.getString(R.string.online_breaket));
        }else if (map.get(PAYMENT_MODE).equals("W")){
            holder.paybale_mode.setText(activity.getString(R.string.wallet_breaket));
        }

        switch (map.get(STATUS)) {
            case "N":
                holder.status.setText(activity.getString(R.string.new_str));
                holder.status.setTextColor(activity.getResources().getColor(R.color.blue_clr));
                holder.trackorder.setVisibility(View.VISIBLE);
                holder.reorder.setVisibility(View.GONE);
                break;
            case "C":
                holder.status.setText(activity.getString(R.string.cancelled));
                holder.trackorder.setVisibility(View.GONE);
                holder.reorder.setVisibility(View.VISIBLE);
                holder.status.setTextColor(activity.getResources().getColor(R.color.red_clr));
                break;
            case "R":
                holder.status.setText(activity.getString(R.string.ready_to_ship));
                holder.trackorder.setVisibility(View.VISIBLE);
                holder.reorder.setVisibility(View.GONE);
                holder.status.setTextColor(activity.getResources().getColor(R.color.green_txt_clr));
                break;
            case "S":
                holder.status.setText(activity.getString(R.string.shipped));
                holder.trackorder.setVisibility(View.VISIBLE);
                holder.reorder.setVisibility(View.GONE);
                holder.status.setTextColor(activity.getResources().getColor(R.color.green_txt_clr));
                break;
            case "D":
                holder.status.setText(activity.getString(R.string.delivered));
                holder.trackorder.setVisibility(View.GONE);
                holder.reorder.setVisibility(View.VISIBLE);
                holder.status.setTextColor(activity.getResources().getColor(R.color.green_txt_clr));
                break;
        }

        if (!map.get(RESCHEDULE_DATE).equals("null") && !map.get(RESCHEDULE_DATE).equals("null")
                && !map.get(STATUS).equals("D") && !map.get(INTERNAL_STATUS).equals("NONE")){
            holder.reschedule_date.setText(activity.getResources().getString(R.string.reschedule_on) + " " + map.get(RESCHEDULE_DATE) + " " + map.get(RESCHEDULE_TIME));
        } else
            holder.reschedule_date.setVisibility(View.GONE);


        if(map.get(AWB_CODE).equals("null")){
            holder.trackorder.setVisibility(View.GONE);
        }

        try {
            String datestr = map.get(CREATE_DATE);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(datestr);
            holder.order_date.setText(activity.getString(R.string.order_on) + android.text.format.DateFormat.format("dd, MMM yyyy", date));
        } catch (Exception e) {

        }

        if (Double.parseDouble(map.get(SHIPPING_PRICE)) != 0){
            holder.shipping_price_tv.setText(activity.getString(R.string.shippin_price_coln)+(map.get(SHIPPING_PRICE) +" "+map.get(CURR_CODE)));
            holder.shipping_price_tv.setVisibility(View.VISIBLE);
        }else {
            holder.shipping_price_tv.setVisibility(View.GONE);
        }

        holder.trackorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, OrderTrackActivity.class);
                intent.putExtra("order_id",map.get(ORDER_ID));
                activity.startActivity(intent);
            }
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, OrderDetailsActivity.class);
                intent.putExtra("order_id", map.get(ORDER_ID));
                activity.startActivity(intent);
            }
        });
        holder.reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorder(map.get(ORDER_ID));
            }
        });
    }

    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView order_no, order_date, reschedule_date, total_price, paybale_mode, item_count, status,shipping_price_tv;
        private MaterialCardView item;
        private Button reorder;
        private Button trackorder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            order_no = itemView.findViewById(R.id.order_no);
            order_date = itemView.findViewById(R.id.order_date);
            reschedule_date = itemView.findViewById(R.id.reschedule_date);
            total_price = itemView.findViewById(R.id.total_price);
            paybale_mode = itemView.findViewById(R.id.paybale_mode);
            item_count = itemView.findViewById(R.id.item_count);
            status = itemView.findViewById(R.id.status);
            item = itemView.findViewById(R.id.item);
            reorder=itemView.findViewById(R.id.reorder);
            trackorder=itemView.findViewById(R.id.trackorder);
            shipping_price_tv=itemView.findViewById(R.id.shipping_price_tv);
        }
    }
    public void reorder(String order_id){
        if (!isNetworkConnected(activity)) {
            MethodClass.network_error_alert(activity);
            return;
        }
        MethodClass.showProgressDialog(activity);
        String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = "";
        server_url = activity.getString(R.string.SERVER_URL) + "get-cart";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_id", order_id);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("server_url", server_url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(activity);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(activity, response);
                    if (resultResponse != null) {
                        String message = resultResponse.getString("message");
                        String meaning = resultResponse.getString("meaning");
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_SUCCESS)
                                .setTitle(message)
                                .setDescription(meaning)
                                .setPositiveText(activity.getString(R.string.ok))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                        Intent I= new Intent(activity, ShoppingCartActivity.class);
                                        activity.startActivity(I);
                                    }
                                })
                                .build();
                        alertDialog.show();
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(activity);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());

                MethodClass.hideProgressDialog(activity);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(activity);
                } else {
                    MethodClass.error_alert(activity);
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
