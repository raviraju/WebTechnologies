package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StockListAdapter extends ArrayAdapter<StockData>{

    private List<StockData> stockData;
    public StockListAdapter(Context context, int resource, List<StockData> objects) {
        super(context, resource, objects);
        stockData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d("StockListAdapter", "getView()");
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.stock_item, parent, false);
        }
        StockData stockDataItem = stockData.get(position);

        TextView stockHeaderText = (TextView) convertView.findViewById(R.id.stockHeaderText);
        stockHeaderText.setText(stockDataItem.getHeader());

        TextView stockDetailText = (TextView) convertView.findViewById(R.id.stockDetailText);
        stockDetailText.setText(stockDataItem.getData());

        return convertView;
    }
}
