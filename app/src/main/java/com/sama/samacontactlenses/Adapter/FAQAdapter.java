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
import com.sama.samacontactlenses.Activity.EditAddressActivity;
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
import static com.sama.samacontactlenses.Common.Constant.ANSWER;
import static com.sama.samacontactlenses.Common.Constant.CITY;
import static com.sama.samacontactlenses.Common.Constant.COUNTRY_NAME;
import static com.sama.samacontactlenses.Common.Constant.EMAIL_ADDRESS;
import static com.sama.samacontactlenses.Common.Constant.FULL_NAME;
import static com.sama.samacontactlenses.Common.Constant.HEADING;
import static com.sama.samacontactlenses.Common.Constant.IS_DEFAULT;
import static com.sama.samacontactlenses.Common.Constant.PHONE;
import static com.sama.samacontactlenses.Common.Constant.QUETION;
import static com.sama.samacontactlenses.Common.Constant.STATE;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;
    public FAQAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.faq_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.que_tv.setText(map.get(QUETION));
        holder.ans_tv.setText(map.get(ANSWER));
        holder.list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ans_tv.getVisibility()==View.VISIBLE){
                    holder.ans_tv.setVisibility(View.GONE);
                }else {
                    holder.ans_tv.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView que_tv,ans_tv;
        private LinearLayout list_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            que_tv = (TextView) itemView.findViewById(R.id.que_tv);
            ans_tv = (TextView) itemView.findViewById(R.id.ans_tv);
            list_item = (LinearLayout) itemView.findViewById(R.id.list_item);

        }
    }
}
