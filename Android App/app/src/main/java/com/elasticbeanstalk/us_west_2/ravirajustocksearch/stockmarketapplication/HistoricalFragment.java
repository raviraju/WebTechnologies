package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class HistoricalFragment extends Fragment {
    public static String historic_JsonData = "";
    //private WebView mWebView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(ResultActivity.LOG_TAG, "HistoricalFragment:onCreateView");
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_historical,container,false);
    }
    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public String load() {
            //return "{\"key\":\"data\"}";
            Log.i(ResultActivity.LOG_TAG, "ReturnHistoricalData : jsonData : " + HistoricalFragment.historic_JsonData);
            return HistoricalFragment.historic_JsonData;
        }

        @JavascriptInterface
        public String getSymbol(){
            Log.i(ResultActivity.LOG_TAG, "getSymbol : query_symbol : " + ResultActivity.query_symbol);
            return ResultActivity.query_symbol;
        }
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(ResultActivity.LOG_TAG, "HistoricalFragment:onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        Log.i(ResultActivity.LOG_TAG, "HistoricalFragment : query_symbol : " + ResultActivity.query_symbol);
        new GetHistoricalData().execute(ResultActivity.query_symbol, "get_chartData");

        WebView mWebView = (WebView) getActivity().findViewById(R.id.webView);
        if(mWebView != null) {
            Log.d(ResultActivity.LOG_TAG, "HistoricalFragment:mWebView is not null");
            // Enable Javascript
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.addJavascriptInterface(new WebAppInterface(getContext()), "webConnector");
            mWebView.addJavascriptInterface(new WebAppInterface(getContext()), "toaster");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Log.d(ResultActivity.LOG_TAG, "setAllowUniversalAccessFromFileURLs");
                mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            }
            mWebView.setWebViewClient(new MyBrowser());
            //mWebView.loadUrl("http://beta.html5test.com/");
            try {
                InputStream stream = getContext().getAssets().open("historicalChart.html");
                int streamSize = stream.available();
                byte[] buffer = new byte[streamSize];
                stream.read(buffer);
                stream.close();
                String html = new String(buffer);
                mWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            Log.d(ResultActivity.LOG_TAG, "HistoricalFragment:mWebView is null");
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.i("WEB_VIEW_TEST", "error code:" + errorCode);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
    class GetHistoricalData extends GetAsync {
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Log.i(ResultActivity.LOG_TAG, "GetHistoricalData : jsonData : " + jsonData);
            historic_JsonData = jsonData;
        }
    }
}
