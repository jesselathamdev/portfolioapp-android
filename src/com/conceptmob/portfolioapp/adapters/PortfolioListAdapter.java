package com.conceptmob.portfolioapp.adapters;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.conceptmob.portfolioapp.R;


public class PortfolioListAdapter extends SimpleAdapter {
    
    private List<HashMap<String, String>> items;
    private HashMap<String, String> item;
    private Context context;
    DecimalFormat dollarFormat = new DecimalFormat("$#,##0.00;-$#,##0.00");
    DecimalFormat percentFormat = new DecimalFormat("+#,##0.00%;-#,##0.00%");
    
    public PortfolioListAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);
        
        this.context = context;
        this.items = items;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // View view = super.getView(position, convertView, parent);  // view acts as a row reference
        
        PortfolioViewHolder holder;
        
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            convertView = li.inflate(R.layout.actiivty_portfolios_list_item, parent, false);
            
            holder = new PortfolioViewHolder();
            holder.tvName = (TextView)convertView.findViewById(R.id.portfolio_item_name);
            holder.tvBookValue = (TextView)convertView.findViewById(R.id.portfolio_item_book_value);
            holder.tvMarketValue = (TextView)convertView.findViewById(R.id.portfolio_item_market_value);
            holder.tvNetGainDollar = (TextView)convertView.findViewById(R.id.portfolio_item_net_gain_dollar);
            holder.tvNetGainPercent = (TextView)convertView.findViewById(R.id.portfolio_item_net_gain_percent);
            
            convertView.setTag(holder);
        } else {
            holder = (PortfolioViewHolder)convertView.getTag();
        }
        
        item = items.get(position);
        
        if (item != null) {
            // set text values
            holder.tvName.setText(item.get("name"));
            holder.tvBookValue.setText(dollarFormat.format(Double.parseDouble(item.get("book_value"))));
            holder.tvMarketValue.setText(dollarFormat.format(Double.parseDouble(item.get("market_value"))));
            holder.tvNetGainDollar.setText(dollarFormat.format(Double.parseDouble(item.get("net_gain_dollar"))));
            holder.tvNetGainPercent.setText(" (" + percentFormat.format(Double.parseDouble(item.get("net_gain_percent"))/100) + ")");
            
            // update market value based on current status
            double bookValue = Double.parseDouble(item.get("book_value"));
            double marketValue = Double.parseDouble(item.get("market_value"));
            
            if (marketValue > bookValue)
                holder.tvMarketValue.setTextColor(Color.parseColor("#008800"));
            else if (marketValue < bookValue)
                holder.tvMarketValue.setTextColor(Color.parseColor("#aa0000"));
            else
                holder.tvMarketValue.setTextColor(Color.parseColor("#ff9b49"));
            
            // update net gain fields based on current status
            double netGainDollar = Double.parseDouble(item.get("net_gain_dollar"));
            if (netGainDollar > 0) {
                holder.tvNetGainDollar.setTextColor(Color.parseColor("#008800"));
                holder.tvNetGainPercent.setTextColor(Color.parseColor("#008800"));
            } else if (netGainDollar < 0) {
                holder.tvNetGainDollar.setTextColor(Color.parseColor("#aa0000"));
                holder.tvNetGainPercent.setTextColor(Color.parseColor("#aa0000"));
            } else {
                holder.tvNetGainDollar.setTextColor(Color.parseColor("#ff9b49"));
                holder.tvNetGainPercent.setTextColor(Color.parseColor("#ff9b49"));
            }            
        }
            
        return convertView;
    }
    
    static class PortfolioViewHolder {
        TextView tvName;
        TextView tvBookValue;
        TextView tvMarketValue;        
        TextView tvNetGainDollar;
        TextView tvNetGainPercent;
    }
}
