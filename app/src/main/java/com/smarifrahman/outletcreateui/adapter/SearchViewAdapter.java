package com.smarifrahman.outletcreateui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smarifrahman.outletcreateui.R;

import java.util.ArrayList;
import java.util.List;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "SearchViewAdapter";

    private Context mContext;
    private List<String> categories;
    private List<String> categoriesFull;

    public SearchViewAdapter(Context mContext, List<String> categories) {
        this.mContext = mContext;
        this.categories = categories;
        categoriesFull = new ArrayList<>(categories);
        Log.d(TAG, "SearchViewAdapter: categories size" + categories.size());
        Log.d(TAG, "SearchViewAdapter: categories list item: " + categories);
        Log.d(TAG, "SearchViewAdapter: categoriesFull Item: "+ categoriesFull);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.searchItem.setText(categories.get(position));
        Log.d(TAG, "onBindViewHolder: " + categories.get(position));

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(categoriesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (String item : categoriesFull) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            categories.clear();
            Log.d(TAG, "publishResults: after clear: "+ categories);

            List data = (List) results.values;

            if (data != null) {
                categories.addAll(data);
                Log.d(TAG, "publishResults: after add: "+data);
            }

           notifyDataSetChanged();
        }
    };


    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView searchItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            searchItem = itemView.findViewById(R.id.search_item_tv);
        }
    }
}
