package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LookUpStockListAdapter extends ArrayAdapter<LookUpStock>{

    private List<LookUpStock> stocks;
    public LookUpStockListAdapter(Context context, int resource, List<LookUpStock> objects) {
        super(context, resource, objects);
        stocks = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d("LookUpStockListAdapter", "getView()");
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.drop_down_item, parent, false);
        }
        LookUpStock stock = stocks.get(position);

        TextView symbolText = (TextView) convertView.findViewById(R.id.symbolText);
        symbolText.setText(stock.getSymbol());

        TextView companyText = (TextView) convertView.findViewById(R.id.companyText);
        companyText.setText(stock.getCompanyName() + " (" + stock.getExchange() + ") ");

        return convertView;
    }
}
