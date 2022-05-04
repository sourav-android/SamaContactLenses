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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Activity.LoginActivity;
import com.sama.samacontactlenses.Activity.ProductDetailsActivity;
import com.sama.samacontactlenses.Activity.SearchActivity;
import com.sama.samacontactlenses.Activity.ShoppingCartActivity;
import com.sama.samacontactlenses.Activity.VerifyActivity;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC_TYPE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_FAV;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_OFF;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Helper.MethodClass.loginPopup;
import static com.sama.samacontactlenses.Helper.MethodClass.noFormat;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public SearchAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.title.setText(map.get(PRODUCT_TITLE));
        //holder.desc.setText(map.get(PRODUCT_DESC));
        Log.e("LENGTH", map.get(PRODUCT_TITLE));
        holder.category.setText(map.get(CATEGORY_TITLE));
        Glide.with(activity).load(PRODUCT_IMAGE_URL+map.get(PRODUCT_IMAGE)).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(holder.image);

        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in",false)){
            if(map.get(PRODUCT_FAV).equals("Y")){
                Picasso.get().load(R.drawable.fav1).fit().placeholder(R.drawable.fav1).error(R.drawable.fav1).into(holder.favBtn);
            }else {
                Picasso.get().load(R.drawable.fav).fit().placeholder(R.drawable.fav).error(R.drawable.fav).into(holder.favBtn);
            }
        }else {
            Picasso.get().load(R.drawable.fav).fit().placeholder(R.drawable.fav).error(R.drawable.fav).into(holder.favBtn);
        }
        if(map.get(PRODUCT_DISC_TYPE).equals("P")){
            holder.offer.setVisibility(View.VISIBLE);
            holder.offer.setText(activity.getString(R.string.discount));
            Double D = ((Double.parseDouble(map.get(PRODUCT_PRICE))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""))));

            Double E = (D*Double.parseDouble(map.get(PRODUCT_DISC)))/100;
            Double pri = D - E;
            holder.discount.setText(noFormat(D)+" ");
            holder.discount.setPaintFlags(holder.discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discount.setVisibility(View.VISIBLE);
            holder.price.setText(noFormat(pri)+" "+PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code",""));
        }else if(map.get(PRODUCT_DISC_TYPE).equals("F")){
            holder.offer.setVisibility(View.VISIBLE);
            holder.offer.setText(activity.getString(R.string.discount));
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
        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in",false)){
                    if(map.get(PRODUCT_FAV).equals("Y")){
                        removeFav(map.get(PRODUCT_ID));
                    }else {
                        addFav(map.get(PRODUCT_ID));
                    }
                }else {
                    loginPopup(activity);
                }
            }
        });


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
        private TextView title,desc,price,category,offer,discount;
        private RelativeLayout main_lay;
        private ImageView image,favBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            favBtn = itemView.findViewById(R.id.favBtn);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            price = itemView.findViewById(R.id.price);
            discount = itemView.findViewById(R.id.discount);
            main_lay = itemView.findViewById(R.id.main_lay);
            category = itemView.findViewById(R.id.category);
            offer = itemView.findViewById(R.id.offer);
        }
    }

    public void addFav(String eventId){
        MethodClass.showProgressDialog(activity);
        String server_url = activity.getString(R.string.SERVER_URL) + "add-fav/"+eventId;
        Log.e("server_url",server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("event_id", eventId);

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try{
                    MethodClass.hideProgressDialog(activity);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(activity, response);
                    if (result_Object != null) {
                        ((SearchActivity)activity).getImages();
                        /*JSONObject MainMsg = result_Object.getJSONObject("message");
                        String message = MainMsg.getString("message");
                        String meaning = MainMsg.getString("meaning");
                        LottieAlertDialog alertDialog= new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_SUCCESS)
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
                } catch (Exception e) {
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
    public void removeFav(String eventId){
        MethodClass.showProgressDialog(activity);
        String server_url = activity.getString(R.string.SERVER_URL) + "add-fav/"+eventId;
        Log.e("server_url",server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("event_id", eventId);

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try{
                    MethodClass.hideProgressDialog(activity);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(activity, response);
                    if (result_Object != null) {
                        ((SearchActivity)activity).getImages();
//                        JSONObject MainMsg = result_Object.getJSONObject("message");
//                        String message = MainMsg.getString("message");
//                        String meaning = MainMsg.getString("meaning");
//                        LottieAlertDialog alertDialog= new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_SUCCESS)
//                                .setTitle(message)
//                                .setDescription(meaning)
//                                .setPositiveText("Ok")
//                                .setPositiveListener(new ClickListener() {
//                                    @Override
//                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
//                                        lottieAlertDialog.dismiss();
//                                    }
//                                })
//                                .build();
//                        alertDialog.show();
                    }
                } catch (Exception e) {
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
