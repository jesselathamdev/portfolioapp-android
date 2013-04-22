package com.conceptmob.portfolioapp.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.conceptmob.portfolioapp.R;


public class TransactionActivityListAdapter extends SimpleAdapter {
    
    private List<HashMap<String, String>> items;
    private HashMap<String, String> item;
    private Context context;
    
    public TransactionActivityListAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);
        
        this.context = context;
        this.items = items;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // View view = super.getView(position, convertView, parent);  // view acts as a row reference
        
        TransactionActivityViewHolder holder;
        
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            convertView = li.inflate(R.layout.activity_transaction_activity_list_item, parent, false);
            
            holder = new TransactionActivityViewHolder();
            
            holder.tvPortfolioId = (TextView)convertView.findViewById(R.id.activity_item_portfolio_id);
            
            convertView.setTag(holder);
        } else {
            holder = (TransactionActivityViewHolder)convertView.getTag();
        }
        
        item = items.get(position);
        
        if (item != null) {
            // set text values
            holder.tvPortfolioId.setText(item.get("id"));                        
        }
            
        return convertView;
    }
    
    static class TransactionActivityViewHolder {
        TextView tvPortfolioId;
    }
}
