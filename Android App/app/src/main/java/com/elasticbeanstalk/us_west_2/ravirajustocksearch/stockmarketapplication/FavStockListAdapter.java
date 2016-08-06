package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class FavStockListAdapter extends ArrayAdapter<FavStock> {

    private List<FavStock> favStocks;
    public FavStockListAdapter(Context context, int resource, List<FavStock> objects) {
        super(context, resource, objects);
        favStocks = objects;
    }

    public String remove(int position) {
        FavStock remFavStock = favStocks.get(position);
        String symbol = remFavStock.getSymbol();
        super.remove(favStocks.get(position));
        return symbol;
    }

    public String getCompany(int position) {
        FavStock favStock = favStocks.get(position);
        String company = favStock.getCompany();
        return company;
    }

    public String getSymbol(int position) {
        FavStock favStock = favStocks.get(position);
        String symbol = favStock.getSymbol();
        return symbol;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d("LookUpStockListAdapter", "getView()");
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.fav_item, parent, false);
        }
        FavStock favStock = favStocks.get(position);

        TextView stockSymbolTextView = (TextView) convertView.findViewById(R.id.stockSymbolTextView);
        stockSymbolTextView.setText(favStock.getSymbol() );

        TextView stockPriceTextView = (TextView) convertView.findViewById(R.id.stockPriceTextView);
        stockPriceTextView.setText(favStock.getPrice());

        TextView stockChangeTextView = (TextView) convertView.findViewById(R.id.stockChangeTextView);
        double val = favStock.getChange();
        DecimalFormat df = new DecimalFormat("###.##");
        df.setRoundingMode(RoundingMode.UP);
        String changePercent = df.format(val) + "%";
        if(val<0)
            stockChangeTextView.setBackgroundResource(R.color.red);
        else if(val>0)
            stockChangeTextView.setBackgroundResource(R.color.green);
        else
            stockChangeTextView.setText("  0.00%");
        stockChangeTextView.setText(changePercent);

        TextView stockCompanyNameTextView = (TextView) convertView.findViewById(R.id.stockCompanyNameTextView);
        int threshold_chars = 15;
        String company_name = favStock.getCompany();
        if(company_name.length() > threshold_chars){
            stockCompanyNameTextView.setText(company_name.substring(0,threshold_chars) + "...");
        }else {
            stockCompanyNameTextView.setText(company_name);
        }


        TextView stockMarketCapTextView = (TextView) convertView.findViewById(R.id.stockMarketCapTextView);
        stockMarketCapTextView.setText(favStock.getMarketCap());

        return convertView;
    }
}
