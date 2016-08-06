package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.senab.photoview.PhotoViewAttacher;

public class CurrentFragment extends Fragment {
    ImageView img;
    ProgressDialog pDialog;

    public static Bitmap bitmap;
    public static String MyStockPrice;
    public static String MyStockSymbol;
    public static String MyStockCompanyName;
    public static String MyStockCaption;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(ResultActivity.LOG_TAG, "CurrentFragment:onCreateView");
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_current,container,false);
    }
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Loading ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
                Log.d(ResultActivity.LOG_TAG, "bitmap : " + bitmap.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                img.setImageBitmap(image);
                PhotoViewAttacher mAttacher = new PhotoViewAttacher(img);
                Log.d(ResultActivity.LOG_TAG, "LoadImage:onPostExecute");
                pDialog.dismiss();
            }else{
                pDialog.dismiss();
                Toast.makeText(getContext(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(ResultActivity.LOG_TAG, "CurrentFragment:onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        Log.i(ResultActivity.LOG_TAG, "CurrentFragment : query_symbol : " + ResultActivity.query_symbol);
        new GetCurrentStock().execute(ResultActivity.query_symbol, "get_quote");
        //img.setVisibility();
        Drawable tempbitmap = getResources().getDrawable(R.drawable.ic_action_important);
        img = (ImageView) getActivity().findViewById(R.id.yahooImageView);
        img.setImageDrawable(tempbitmap);
        new LoadImage().execute("http://chart.finance.yahoo.com/t?s=" + ResultActivity.query_symbol + "&lang=en-US&width=960&height=520");
    }

    public String getCaption(String price,String value,String percent){
        double valueDouble = Double.parseDouble(value);
        double percentDouble = Double.parseDouble(percent);
        DecimalFormat df = new DecimalFormat("#.##");
        return "LAST TRADE PRICE: " + "$ " + price + " , CHANGE: " + df.format(valueDouble) + " (" + df.format(percentDouble) + "% ) ";
    }

    public String mapValues(String value, String percent){
        double valueDouble = Double.parseDouble(value);
        double percentDouble = Double.parseDouble(percent);
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(valueDouble) + "(" + df.format(percentDouble) + "% ) ";
        /*if(percentDouble > 0)
        {
            return df.format(valueDouble) + "(" + df.format(percentDouble) + "% ) ";
        }else{
            return df.format(valueDouble) + "(" + df.format(percentDouble) + "% ) ";
        }

        double valueRound = Double.parseDouble(df.format(valueDouble));
        double percentRound = Double.parseDouble(df.format(percentDouble));*/

        /*var result;
        var valueRound = value.toFixed(2);
        var percentRound = percent.toFixed(2);
        var textDisplay = valueRound + "(" + percentRound + "% ) ";
        if(percentRound > 0)	//+ve
        {
            result = "<span style=\"color:green;\">&nbsp;" + textDisplay + "</span>";
            return result + "&nbsp;" + "<img src=\"http://cs-server.usc.edu:45678/hw/hw8/images/up.png\" height=\"32\" width=\"32\" >";
        }
        else if(percentRound < 0) // -ve
        {
            result = "<span style=\"color:red;\">" + textDisplay + "</span>";
            return result + "<img src=\"http://cs-server.usc.edu:45678/hw/hw8/images/down.png\" height=\"32\" width=\"32\" >";
        }
        else	//0
        {
            return textDisplay;
        }*/
    }
    public String mapMarketCap(double value){
        double quotient = value/1000000000;
        double in_billion = quotient;
        DecimalFormat df = new DecimalFormat("#.##");
        if(in_billion <= 0)
        {
            quotient = value/1000000;
            return df.format(quotient) + " Million";
        }
        else
        {
            return df.format(in_billion) + " Billion";
        }
    }
    class GetCurrentStock extends GetAsync {
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Log.i(ResultActivity.LOG_TAG, "GetCurrentStock : jsonData : " + jsonData);
            List<StockData> stockDataList = new ArrayList<StockData>();
            if (jsonData != null) {
                try {
                    JSONObject stockJson = new JSONObject(jsonData);
                    stockDataList.add(new StockData("NAME", stockJson.getString("Name")));
                    MyStockCompanyName = stockJson.getString("Name");
                    stockDataList.add(new StockData("SYMBOL", stockJson.getString("Symbol")));
                    MyStockSymbol = stockJson.getString("Symbol");
                    DecimalFormat df = new DecimalFormat("#.##");
                    String formattedPrice = "$" + df.format(Double.parseDouble(stockJson.getString("LastPrice")));
                    MyStockPrice = formattedPrice;
                    stockDataList.add(new StockData("LASTPRICE", formattedPrice ));
                    stockDataList.add(new StockData("Change", mapValues(stockJson.getString("Change"), stockJson.getString("ChangePercent"))));
                    MyStockCaption = getCaption(formattedPrice, stockJson.getString("Change"), stockJson.getString("ChangePercent"));
                    String dateString = stockJson.getString("Timestamp");
                    String dateText = dateString;
                    SimpleDateFormat utcFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'UTC'z yyyy", Locale.US);
                    //utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date date = utcFormat.parse(dateString);
                        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy, hh:mm:ss a", Locale.US);
                        dateText = sdf.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    stockDataList.add(new StockData("Timestamp", dateText));
                    stockDataList.add(new StockData("MarketCap", mapMarketCap(Double.parseDouble(stockJson.getString("MarketCap")))));
                    stockDataList.add(new StockData("Volume", stockJson.getString("Volume")));
                    stockDataList.add(new StockData("ChangeYTD", mapValues(stockJson.getString("ChangeYTD"), stockJson.getString("ChangePercentYTD")) ));
                    stockDataList.add(new StockData("High", stockJson.getString("High")));
                    stockDataList.add(new StockData("Low", stockJson.getString("Low")));
                    stockDataList.add(new StockData("Open", stockJson.getString("Open")));

                    ListView listView = (ListView) getActivity().findViewById(R.id.stockDetailsView);
                    StockListAdapter stockListAdapter = new StockListAdapter(getActivity(), R.layout.stock_item, stockDataList);
                    listView.setAdapter(stockListAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
