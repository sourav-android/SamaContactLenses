package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static com.sama.samacontactlenses.Common.Constant.FLAG_IMAGE_URL;

public class CountryNameRecyAdapter extends RecyclerView.Adapter<CountryNameRecyAdapter.ViewHolder> {
    Activity activity;
    ArrayList<MethodClass.CountryModelName> map_list;
    ArrayList<MethodClass.CountryModelName> arraylist;
    ArrayList<MethodClass.CountryModelName> main_arraylist;
    Dialog dialog;
    Spinner shi_spin_country;
    public CountryNameRecyAdapter(Activity activity, ArrayList<MethodClass.CountryModelName> map_list, EditText edit_search, Dialog dialog, Spinner shi_spin_country) {
        this.activity = activity;
        this.map_list = map_list;
        this.dialog = dialog;
        this.shi_spin_country = shi_spin_country;
        this.arraylist = new ArrayList<MethodClass.CountryModelName>();
        this.arraylist.addAll(map_list);
        this.main_arraylist = new ArrayList<MethodClass.CountryModelName>();
        this.main_arraylist.addAll(map_list);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(edit_search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.country_spin_lay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MethodClass.CountryModelName CountryModelName = map_list.get(position);
        holder.spin_text.setText(CountryModelName.countryName);
        String sortname=CountryModelName.countrySortName.toLowerCase()+".png";
        Picasso.get().load(FLAG_IMAGE_URL+sortname).placeholder(R.drawable.flag_dummy).error(R.drawable.flag_dummy).into(holder.spin_image);
        holder.list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < main_arraylist.size(); i++) {
                    MethodClass.CountryModelName stringWithTag= main_arraylist.get(i);
                    if (Objects.equals(stringWithTag.tag, CountryModelName.tag)){
                        shi_spin_country.setSelection(i);
                        dialog.dismiss();
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView spin_text;
        private ImageView spin_image;
        private LinearLayout list_item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            list_item = itemView.findViewById(R.id.list_item);
            spin_text = itemView.findViewById(R.id.spin_text);
            spin_image = itemView.findViewById(R.id.spin_image);
        }
    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        map_list.clear();
        if (charText.length() == 0) {
            map_list.addAll(arraylist);
        } else {
            for (MethodClass.CountryModelName wp : arraylist) {
                if (wp.countryName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    map_list.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
