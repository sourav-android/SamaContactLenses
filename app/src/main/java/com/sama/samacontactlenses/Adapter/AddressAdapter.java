package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
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
import com.sama.samacontactlenses.Activity.AddressActivity;
import com.sama.samacontactlenses.Activity.CheckoutActivity;
import com.sama.samacontactlenses.Activity.EditAddressActivity;
import com.sama.samacontactlenses.Activity.HomeActivity;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.ADDRESS_ID;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_1;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_2;
import static com.sama.samacontactlenses.Common.Constant.CART_DETAILS_ID;
import static com.sama.samacontactlenses.Common.Constant.CITY;
import static com.sama.samacontactlenses.Common.Constant.COUNTRY_NAME;
import static com.sama.samacontactlenses.Common.Constant.EMAIL_ADDRESS;
import static com.sama.samacontactlenses.Common.Constant.FULL_NAME;
import static com.sama.samacontactlenses.Common.Constant.HEADING;
import static com.sama.samacontactlenses.Common.Constant.IS_DEFAULT;
import static com.sama.samacontactlenses.Common.Constant.PHONE;
import static com.sama.samacontactlenses.Common.Constant.STATE;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;
    int selectedPosition = 10000;
    public AddressAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.main_address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.heading.setText(map.get(HEADING));
        holder.name.setText(map.get(FULL_NAME));
        holder.email.setText(map.get(EMAIL_ADDRESS));
        String full_address="";
        full_address=map.get(ADDRESS_LINE_1);
        full_address=full_address+", "+MethodClass.checkNull(map.get(ADDRESS_LINE_2));
        full_address=full_address+", "+MethodClass.checkNull(map.get(CITY));
        full_address=full_address+", "+MethodClass.checkNull(map.get(STATE));
        full_address=full_address+", "+MethodClass.checkNull(map.get(COUNTRY_NAME));
        holder.address.setText(full_address);
        holder.phone.setText(map.get(PHONE));

        if (map.get(IS_DEFAULT).equals("Y")){
            holder.default_text.setVisibility(View.VISIBLE);
            holder.set_default.setVisibility(View.GONE);
        }else {
            holder.default_text.setVisibility(View.GONE);
            holder.set_default.setVisibility(View.VISIBLE);
        }

        holder.edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, EditAddressActivity.class);
                intent.putExtra("id",map.get(ADDRESS_ID));
                activity.startActivity(intent);
            }
        });
        holder.set_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefault(map.get(ADDRESS_ID));
            }
        });
        holder.delete_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_QUESTION)
                        .setTitle(activity.getString(R.string.remove_address))
                        .setDescription(activity.getString(R.string.do_you_want_remove_address))
                        .setPositiveText(activity.getString(R.string.yes))
                        .setNegativeText(activity.getString(R.string.no))
                        .setPositiveListener(new ClickListener() {
                            @Override
                            public void onClick(LottieAlertDialog lottieAlertDialog) {
                                deleteAddress(map.get(ADDRESS_ID));
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
        private TextView name,email,address,phone,default_text,heading;
        private ImageView edit_address,delete_address,set_default;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.phone);
            edit_address = itemView.findViewById(R.id.edit_address);
            delete_address = itemView.findViewById(R.id.delete_address);
            set_default = itemView.findViewById(R.id.set_default);
            default_text = itemView.findViewById(R.id.default_text);
            heading = itemView.findViewById(R.id.heading);
        }
    }

    public void setDefault(String id){
        MethodClass.showProgressDialog(activity);
        String server_url = activity.getString(R.string.SERVER_URL) + "set-default-address-book";
        Log.e("server_url",server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject",jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try{
                    MethodClass.hideProgressDialog(activity);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(activity, response);
                    if (result_Object != null) {
                        ((AddressActivity)activity).getList();
                        String message = result_Object.getString("message");
                        String meaning = result_Object.getString("meaning");
                        LottieAlertDialog alertDialog= new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_SUCCESS)
                                .setTitle(message)
                                .setDescription(meaning)
                                .setPositiveText(activity.getString(R.string.ok))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        alertDialog.show();
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



    public void deleteAddress(String id){
        MethodClass.showProgressDialog(activity);
        String server_url = activity.getString(R.string.SERVER_URL) + "delete-address-book";
        Log.e("server_url",server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject",jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try{
                    MethodClass.hideProgressDialog(activity);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(activity, response);
                    if (result_Object != null) {
                        ((AddressActivity)activity).getList();
                        String message = result_Object.getString("message");
                        String meaning = result_Object.getString("meaning");
                        LottieAlertDialog alertDialog= new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_SUCCESS)
                                .setTitle(message)
                                .setDescription(meaning)
                                .setPositiveText(activity.getString(R.string.ok))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        alertDialog.show();
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
