package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sama.samacontactlenses.Activity.SearchActivity;
import com.sama.samacontactlenses.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;

public class CategorySearchAdapter extends RecyclerView.Adapter<CategorySearchAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;
    int selectedPosition = 10000;

    public CategorySearchAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cate_search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.title.setText(map.get(CATEGORY_TITLE));
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                ((SearchActivity) activity).SELECTED_CATEGORY = map.get(CATEGORY_ID);
                ((SearchActivity) activity).SELECTED_PARENT = map.get(PRODUCT_ID);
                ((SearchActivity) activity).searchResult();
                notifyDataSetChanged();
            }
        });

        if (selectedPosition == position) {
            holder.title.setBackground(activity.getDrawable(R.drawable.pink_strok));
        } else {
            holder.title.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }
}
