package com.conceptmob.portfolioapp.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.conceptmob.portfolioapp.R;


public class TransactionActivityListAdapter extends ArrayAdapter<HashMap<String, String>> {
    
    private List<HashMap<String, String>> items;
    private HashMap<String, String> item;
    private Context context;
    
    public TransactionActivityListAdapter(Context context, List<HashMap<String, String>> items) {
        super(context, R.layout.activity_transaction_activity_list_item, items);
        
        this.context = context;
        this.items = items;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        TransactionActivityViewHolder holder;
        
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            convertView = li.inflate(R.layout.activity_transaction_activity_list_item, parent, false);
            
            holder = new TransactionActivityViewHolder();
            
            holder.tvId = (TextView)convertView.findViewById(R.id.activity_item_id);
            holder.tvPortfolioId = (TextView)convertView.findViewById(R.id.activity_item_portfolio_id);
            holder.tvDateTransacted = (TextView)convertView.findViewById(R.id.activity_item_date_transacted);
            holder.tvStockName = (TextView)convertView.findViewById(R.id.activity_item_stock_name);
            holder.tvValue = (TextView)convertView.findViewById(R.id.activity_item_value);
            holder.tvQuantity = (TextView)convertView.findViewById(R.id.activity_item_quantity);
            
            convertView.setTag(holder);
        } else {
            holder = (TransactionActivityViewHolder)convertView.getTag();            
        }
        
        item = items.get(position);
        
        if (item != null) {
            // set text values
            holder.tvId.setText(item.get("id"));
            holder.tvPortfolioId.setText(item.get("portfolio_id"));
            holder.tvDateTransacted.setText(item.get("date_transacted"));
            holder.tvStockName.setText(item.get("name"));
            holder.tvValue.setText(item.get("value"));
            holder.tvQuantity.setText(item.get("quantity"));
        }
            
        return convertView;
    }
    
    
    static class TransactionActivityViewHolder {
        TextView tvId;
        TextView tvPortfolioId;
        TextView tvDateTransacted;
        TextView tvStockName;
        TextView tvValue;
        TextView tvQuantity;
    }
}
