package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MainActivity";
    public static final String SYMBOL = "SYMBOL";
    public static final String COMPANY_NAME = "COMPANY_NAME";
    SharedPreferences mainSharedPreferences;
    //ProgressDialog pDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private void loadFavouriteStocks(){
        mainSharedPreferences = getSharedPreferences(ResultActivity.FAVSTOCKS, Context.MODE_PRIVATE);
        String existingJsonStocks = mainSharedPreferences.getString(ResultActivity.FAVSTOCKS, "['0']");
        Log.d(LOG_TAG, "loadFavouriteStocks : existingJsonStocks : " + existingJsonStocks);
        new GetFavStocks().execute(existingJsonStocks, "get_quotes");
    }
    private void deleteFavStock(String symbol) {
        mainSharedPreferences = getSharedPreferences(ResultActivity.FAVSTOCKS, Context.MODE_PRIVATE);
        String existingJsonStocks = mainSharedPreferences.getString(ResultActivity.FAVSTOCKS, "['0']");
        try {
            JSONArray jsonArray = new JSONArray(existingJsonStocks);
            int no_of_stored_symbols = jsonArray.length();
            if (no_of_stored_symbols > 0) {
                JSONArray newJsonArray = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String stored_symbol = jsonArray.getString(i);
                    if (stored_symbol.equalsIgnoreCase(symbol))
                        continue;
                    else
                        newJsonArray.put(stored_symbol);
                }
                SharedPreferences.Editor editor = mainSharedPreferences.edit();
                editor.putString(ResultActivity.FAVSTOCKS, newJsonArray.toString());
                editor.commit();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "deleteFavStock : Deleted symbol : " + symbol);
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
    class GetFavStocks extends GetAsync {
        //ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pDialog = new ProgressDialog(getApplicationContext());
            //pDialog.setMessage("Loading ....");
            //pDialog.show();

        }
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            Log.i(ResultActivity.LOG_TAG, "GetFavStocks : jsonData : " + jsonData);
            List<FavStock> favStockList = new ArrayList<FavStock>();
            if (!jsonData.equalsIgnoreCase("null")) {
                try {
                    JSONObject mainObj = new JSONObject(jsonData);
                    Iterator itr = mainObj.keys();
                    while(itr.hasNext()){
                        String symbol_key = (String) itr.next();
                        if (symbol_key.equalsIgnoreCase("0"))
                            continue;
                        JSONObject tempObj = (JSONObject) mainObj.get(symbol_key);
                        //Log.i(ResultActivity.LOG_TAG, "GetFavStocks : tempObj : " + tempObj);
                        String company_name = tempObj.getString("Name");
                        //Log.i(ResultActivity.LOG_TAG, "GetFavStocks : company_name : " + company_name);
                        DecimalFormat df = new DecimalFormat("##.##");
                        String formattedPrice = "$ " + df.format(Double.parseDouble(tempObj.getString("LastPrice")));
                        double changePercent = Double.parseDouble(tempObj.getString("ChangePercent"));
                        //String changePercent = df.format(val) + "%";
                        String marketCap = "Market Cap : " + mapMarketCap(Double.parseDouble(tempObj.getString("MarketCap")));
                        FavStock favStock = new FavStock(symbol_key, formattedPrice, changePercent, company_name, marketCap);
                        favStockList.add(favStock);
                    }
                    //ListView listView = (ListView) findViewById(R.id.favListView);
                    DynamicListView dynamicListView = (DynamicListView) findViewById(R.id.dynamicListView);
                    final FavStockListAdapter favStockListAdapter = new FavStockListAdapter(getApplicationContext(), R.layout.fav_item, favStockList);
                    //AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(favStockListAdapter);
                    //animationAdapter.setAbsListView(listView);
                    //listView.setAdapter(animationAdapter);

                    dynamicListView.enableSwipeToDismiss(
                            new OnDismissCallback() {
                                @Override
                                public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                                    for (int position : reverseSortedPositions) {
                                        String symbol_deleted = favStockListAdapter.remove(position);
                                        deleteFavStock(symbol_deleted);
                                    }
                                }
                            }
                    );
                    dynamicListView.setAdapter(favStockListAdapter);
                    dynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String symbol_clicked = favStockListAdapter.getSymbol(position);
                            String company = favStockListAdapter.getCompany(position);
                            //Toast.makeText(MainActivity.this,"symbol_clicked : " + symbol_clicked, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                            intent.putExtra(SYMBOL, symbol_clicked);
                            intent.putExtra(COMPANY_NAME, company);
                            startActivity(intent);
                        }
                    });

                    //listView.setAdapter(favStockListAdapter);
                }catch (JSONException e) {
                    e.printStackTrace();

                }
            }
            //pDialog.dismiss();
        }
    }

    private Handler handler = new Handler();
    private Runnable timedTask = new Runnable(){
        @Override
        public void run() {
            handler.postDelayed(timedTask, 10000);
            Log.d(LOG_TAG,"AutoRefresh every 10 sec");
            loadFavouriteStocks();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.stock);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.main_activity);

        Log.d(LOG_TAG, "onCreate");

        /*String[] languages={"Android ","java","IOS","SQL","JDBC","Web services","And","IOA", "IOB","IOA1", "IOB1","IOA2", "IOB2"};
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,languages);
        actv.setAdapter(adapter);*/

        Button clearButton = (Button) findViewById(R.id.clearbutton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "To clear");
                AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
                autoCompleteTextView.setText("");
            }
        });

        Switch switchButton = (Switch) findViewById(R.id.autoRefSwitch);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    Log.d(LOG_TAG, "Auto Refresh On");
                    handler.post(timedTask);
                } else {
                    Log.d(LOG_TAG, "Auto Refresh Off");
                    handler.removeCallbacks(timedTask);
                }
            }
        });


        AutoCompleteTextView autoComplTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        if (autoComplTextView != null) {
            autoComplTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String symbol = s.toString();
                    if(symbol.length() > 0) {
                        Log.i("symbol", symbol);
                        new GetLookUp().execute(symbol);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
        loadFavouriteStocks();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(MainActivity.this,
                    "Your orientation is portrait", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this,
                    "Your orientation is landscape", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Log.d(LOG_TAG, "onStart");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "onResume");
        loadFavouriteStocks();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

        Log.d(LOG_TAG, "onStop");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    public void getQuoteEventHandler(View view) {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        String user_symbol = autoCompleteTextView.getText().toString();
        if(user_symbol.length() == 0){
            /*Toast.makeText(MainActivity.this,
                    "No symbol!!",
                    Toast.LENGTH_SHORT).show();*/
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Please enter a Stock Name/Symbol");
            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            //display dialog
            dialog.show();
            return;
        }
        Log.i(LOG_TAG, "Symbol info : " + user_symbol);

        //new GetAsync().execute("AAPL");
        //new GetAsync().execute("AAPL", "get_quote");
        new GetAsync().execute(user_symbol);
        Log.i("test_validity of symbol",GetAsync.result);
        String comp_name="Company Name";
        try {
            JSONArray jsonArray = new JSONArray(GetAsync.result);
            boolean symbolValid = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                if(user_symbol.equalsIgnoreCase(jsonObj.getString("Symbol"))){
                    symbolValid = true;
                    comp_name = jsonObj.getString("Name");
                    break;
                }
            }
            if(symbolValid){
                Log.i(LOG_TAG, "VALID : " + user_symbol);
                /*new GetAsync().execute(user_symbol, "get_quote");
                Log.i("test_validof quote info",GetAsync.result);
                JSONObject stockJson = new JSONObject(GetAsync.result);
                String status = stockJson.getString("Status");
                if(status.equalsIgnoreCase("Failure|APP_SPECIFIC_ERROR")){
                    Log.i(LOG_TAG, "invalid current stock info for : " + user_symbol);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Unable to retrieve stock info, try any other");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {*/
                    Log.i(LOG_TAG, "ALL VALIDATIONS PASSED : " + user_symbol);
                    //Intent intent = new Intent(this, DetailsActivity.class);
                    Intent intent = new Intent(this, ResultActivity.class);
                    intent.putExtra(SYMBOL, user_symbol);
                    intent.putExtra(COMPANY_NAME, comp_name);
                    startActivity(intent);

            }
            else{
                Log.i(LOG_TAG, "invalid : " + user_symbol);
                /*Toast.makeText(MainActivity.this,
                        "invalid symbol : " + user_symbol,
                        Toast.LENGTH_SHORT).show();*/

                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Invalid Symbol");
                // Add the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                //display dialog
                dialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void autoRefreshEventHandler(View view) {

    }

    public void manualRefreshEventHandler(View view) {
        Log.d(LOG_TAG, "manualRefreshEventHandler : existingJsonStocks ");
        loadFavouriteStocks();
    }


    class GetLookUp extends GetAsync {
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            List<LookUpStock> stocks = new ArrayList<LookUpStock>();
            if (jsonData != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonData);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        Log.i("stockInfo : ", jsonObj.getString("Symbol") + "  " + jsonObj.getString("Name") + " (" + jsonObj.getString("Exchange")+ ")");
                        LookUpStock stock = new LookUpStock(jsonObj.getString("Symbol"), jsonObj.getString("Name"), jsonObj.getString("Exchange"));
                        stocks.add(stock);
                    }
                    final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
                    LookUpStockListAdapter lookUpStockListAdapter = new LookUpStockListAdapter(getApplicationContext(), R.layout.drop_down_item, stocks);
                    autoCompleteTextView.setAdapter(lookUpStockListAdapter);
                    autoCompleteTextView.showDropDown();
                    lookUpStockListAdapter.notifyDataSetChanged();
                    autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id){
                            LookUpStockListAdapter adapter = (LookUpStockListAdapter) adapterView.getAdapter();
                            LookUpStock stock = (LookUpStock) adapterView.getItemAtPosition(position);
                            /*Toast.makeText(MainActivity.this,
                                    stock.getSymbol(),
                                    Toast.LENGTH_SHORT).show();*/
                            autoCompleteTextView.setText(stock.getSymbol());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

