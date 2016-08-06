package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(ResultActivity.LOG_TAG, "NewsFragment:onCreateView");
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_news,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(ResultActivity.LOG_TAG, "NewsFragment:onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        Log.i(ResultActivity.LOG_TAG, "NewsFragment : query_symbol : " + ResultActivity.query_symbol);
        new GetNews().execute(ResultActivity.query_symbol, "get_bingNews");
    }

    class GetNews extends GetAsync {
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Log.i(ResultActivity.LOG_TAG, "GetNews : jsonData : " + jsonData);
            List<News> newsList = new ArrayList<News>();
            if (jsonData != null) {
                try {
                    JSONObject mainObj = new JSONObject(jsonData);
                    JSONArray jsonArray = (mainObj.getJSONObject("d")).getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        Log.i("newsInfo : ", jsonObj.getString("Url") + "  " + jsonObj.getString("Title") + " " + jsonObj.getString("Description") + " "  + jsonObj.getString("Source") + " "  + jsonObj.getString("Date"));
                        News newsItem = new News(jsonObj.getString("Url"), jsonObj.getString("Title"), jsonObj.getString("Description"), jsonObj.getString("Source"),jsonObj.getString("Date")  );
                        newsList.add(newsItem);
                    }
                    ListView listView = (ListView) getActivity().findViewById(R.id.newsListView);
                    final NewsListAdapter newsListAdapter = new NewsListAdapter(getActivity(), R.layout.news_item, newsList);
                    listView.setAdapter(newsListAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String url = newsListAdapter.getUrl(position);
                            Log.d(ResultActivity.LOG_TAG, "URL to be opened : " + url);
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    }
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
