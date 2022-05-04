package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sama.samacontactlenses.Activity.UserActivity;
import com.sama.samacontactlenses.R;
import java.util.ArrayList;
import java.util.HashMap;
import static com.sama.samacontactlenses.Common.Constant.CURRENCY;
import static com.sama.samacontactlenses.Common.Constant.CURRENCY_CONVERSION;
import static com.sama.samacontactlenses.Common.Constant.CURRENCY_ID;
import static com.sama.samacontactlenses.Common.Constant.CURRENCY_NAME;

public class CurrencyPopUpAdapter extends RecyclerView.Adapter<CurrencyPopUpAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;
    public TextView currency_text;
    public Dialog dialog;

    public CurrencyPopUpAdapter(Dialog dialog,TextView currency_text, Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.currency_text = currency_text;
        this.activity = activity;
        this.map_list = map_list;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.currency_popup_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.name_tv.setText(map.get(CURRENCY));
        holder.item_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("currency", map.get(CURRENCY)).apply();
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("currency_code", map.get(CURRENCY)).commit();
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("conv_factor", map.get(CURRENCY_CONVERSION)).commit();
                currency_text.setText(activity.getString(R.string.curr) + " : "+map.get(CURRENCY));
                ((UserActivity)activity).change_currency(map.get(CURRENCY_ID),activity);
                dialog.dismiss();
            }
        });
    }
    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name_tv;
        private MaterialCardView item_lay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.name_tv);
            item_lay = itemView.findViewById(R.id.item_lay);
        }
    }
}
