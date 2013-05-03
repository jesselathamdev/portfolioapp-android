package com.conceptmob.portfolioapp.adapters;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.conceptmob.portfolioapp.R;


public class PortfolioHoldingsListAdapter extends ArrayAdapter<HashMap<String, String>> {
    
    private List<HashMap<String, String>> items;
    private HashMap<String, String> item;
    private Context context;
    DecimalFormat dollarFormat = new DecimalFormat("$#,##0.00;-$#,##0.00");
    DecimalFormat numberFormat = new DecimalFormat("#,##0.000");
    
    public PortfolioHoldingsListAdapter(Context context, List<HashMap<String, String>> items) {
        super(context, R.layout.activity_portfolio_holdings_list_item, items);
        
        this.context = context;
        this.items = items;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        PortfolioViewHolder holder;
        
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            convertView = li.inflate(R.layout.activity_portfolio_holdings_list_item, parent, false);
            
            holder = new PortfolioViewHolder();
            holder.tvName = (TextView)convertView.findViewById(R.id.portfolio_holding_item_name);
            holder.tvLastPrice = (TextView)convertView.findViewById(R.id.portfolio_holding_item_last_price);
            holder.tvTotalQuantity = (TextView)convertView.findViewById(R.id.portfolio_holding_item_total_quantity);
                        
            convertView.setTag(holder);
        } else {
            holder = (PortfolioViewHolder)convertView.getTag();
        }
        
        item = items.get(position);
        
        if (item != null) {
            // set text values
            holder.tvName.setText(item.get("stock_name"));
            holder.tvLastPrice.setText(dollarFormat.format(Double.parseDouble(item.get("last_price"))));
            holder.tvTotalQuantity.setText(numberFormat.format(Double.parseDouble(item.get("total_quantity"))));            
        }
            
        return convertView;
    }
    
    
    static class PortfolioViewHolder {
        TextView tvName;
        TextView tvLastPrice;
        TextView tvTotalQuantity;        
    }
}