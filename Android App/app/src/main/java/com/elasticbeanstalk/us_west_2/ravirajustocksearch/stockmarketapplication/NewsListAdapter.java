package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsListAdapter extends ArrayAdapter<News>{

    private List<News> news;
    public NewsListAdapter(Context context, int resource, List<News> objects) {
        super(context, resource, objects);
        news = objects;
    }

    public String getUrl(int position) {
        News newsItem = news.get(position);
        String url = newsItem.getUrl();
        return url;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d("LookUpStockListAdapter", "getView()");
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.news_item, parent, false);
        }
        News newsItem = news.get(position);

        TextView titleLinkText = (TextView) convertView.findViewById(R.id.titleLinkText);
        titleLinkText.setText(Html.fromHtml("<a href=" + newsItem.getUrl() + "> " + newsItem.getTitle()) );

        TextView descText = (TextView) convertView.findViewById(R.id.descText);
        descText.setText(newsItem.getDescription());

        TextView publisherText = (TextView) convertView.findViewById(R.id.publisherText);
        publisherText.setText("Publisher : " + newsItem.getPublisher());

        TextView dateText = (TextView) convertView.findViewById(R.id.dateText);
        String dateString = newsItem.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date = format.parse(dateString);
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy, hh:mm:ss a", Locale.US);
            dateText.setText("Date : " + sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            dateText.setText("Date : " + dateString);
        }

        return convertView;
    }
}
