package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class FetchJsonData {

    String charset = "UTF-8";
    HttpURLConnection connection;
    DataOutputStream wr;
    StringBuilder result;
    URL urlObj;
    StringBuilder sbParams;
    String paramsString;

    public String makeHttpRequest(String url, String method, HashMap<String, String> params) {

        sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0){
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }

        if (method.equals("POST")) {
            // request method is POST
            try {
                urlObj = new URL(url);
                connection = (HttpURLConnection) urlObj.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();

                paramsString = sbParams.toString();
                wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("GET")){
            // request method is GET
            if (sbParams.length() != 0) {
                url += "?" + sbParams.toString();
            }
            try {
                urlObj = new URL(url);
                connection = (HttpURLConnection) urlObj.openConnection();
                connection.setDoOutput(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setConnectTimeout(15000);
                connection.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("FetchJsonData", "result: " + result.toString());
            if(reader != null) {
                reader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(connection != null)
            connection.disconnect();

        // return JSON string
        if(result != null)
            return result.toString();
        else
            return "";
    }
}

class GetAsync extends AsyncTask<String, String, String> {

    public static String result = null;

    FetchJsonData fetchJson = new FetchJsonData();
    private static final String URL = "http://ravirajustocksearch.us-west-2.elasticbeanstalk.com/server.php";

    @Override
    protected String doInBackground(String... args) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("symbol", args[0]);
            if(args.length == 2)
                params.put("search", args[1]);
            Log.d("request", "starting");
            String jsonData = fetchJson.makeHttpRequest(URL, "GET", params);
            if (jsonData != null) {
                Log.d("JSON result", jsonData);
                return jsonData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        Log.i("response", jsonData);
        result = jsonData;
    }
}