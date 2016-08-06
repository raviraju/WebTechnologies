package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;

public class ResultActivity extends AppCompatActivity {
    public static final String LOG_TAG = "ResultActivity";
    public static String query_symbol;
    public static String company_name;
    public static final String FAVSTOCKS = "FavouriteStocks";
    SharedPreferences sharedpreferences;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(ResultActivity.this,
                    "Your orientation is portrait", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ResultActivity.this,
                    "Your orientation is landscape", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //final List<Fragment> fragmentList = new ArrayList<>();
        //fragmentList.add(new CurrentFragment());
        //fragmentList.add(new HistoricalFragment());
        //fragmentList.add(new NewsFragment());

        setContentView(R.layout.result_activity);
        sharedpreferences = getSharedPreferences(FAVSTOCKS, Context.MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getApplicationContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Not Posted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Post Fail", Toast.LENGTH_SHORT).show();
            }
        });


        Log.d(LOG_TAG, "onCreate");

        query_symbol = getIntent().getStringExtra(MainActivity.SYMBOL).toUpperCase();
        company_name = getIntent().getStringExtra(MainActivity.COMPANY_NAME);
        int threshold_chars = 15;
        if(company_name.length() > threshold_chars){
            setTitle(company_name.substring(0,threshold_chars) + "...");
        }
        else {
            setTitle(company_name);
        }
        Log.d(LOG_TAG, MainActivity.SYMBOL + " : " + query_symbol + " , " + MainActivity.COMPANY_NAME + " : " + company_name);

        class MyCustomAdapter extends FragmentStatePagerAdapter {
            private String fragments [] = {"Current","Historical","News"};
            public MyCustomAdapter(FragmentManager supportFragmentManager, Context applicationContext){
                super(supportFragmentManager);
            }
            @Override
            public Fragment getItem(int position){
                Log.d(LOG_TAG, "Fragment getItem position" + " : " + position);
                //return fragmentList.get(position);
                switch(position){
                    case 0:
                        return new CurrentFragment();
                    case 1:
                        return new HistoricalFragment();
                    case 2:
                        return new NewsFragment();
                    default:
                        return null;
                }
            }
            @Override
            public int getCount(){
                //Log.d(LOG_TAG, "Fragment getCount fragments.length" + " : " + fragments.length);
                return fragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position){
                return fragments[position];
            }
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter( new MyCustomAdapter(getSupportFragmentManager(), getApplicationContext()));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        /*tabLayout.setOnTabSelectedListener( new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(LOG_TAG, "onTabSelected position" + " : " + tab.getPosition() + " viewPager.getCurrentItem : " + viewPager.getCurrentItem());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(LOG_TAG, "onTabUnselected position" + " : " + tab.getPosition());
                //viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(LOG_TAG, "onTabReselected position" + " : " + tab.getPosition());
                //viewPager.setCurrentItem(tab.getPosition());
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "Favorites : onCreateOptionsMenu : ");
        getMenuInflater().inflate(R.menu.stock_menu,menu);
        /*CheckBox checkBox= (CheckBox) menu.findItem(R.id.action_favorite).getActionView();
        checkBox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d(LOG_TAG, "Favorites : onCheckedChanged isChecked : ");
                }else{
                    Log.d(LOG_TAG, "Favorites : onCheckedChanged is NOT Checked : ");
                }
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "Favorites : onPrepareOptionsMenu : ");
        String existingJsonStocks = sharedpreferences.getString(FAVSTOCKS, "['0']");
        try {
            Log.d(LOG_TAG, "Favorites : existingJsonStocks : " + existingJsonStocks);
            JSONArray jsonArray = new JSONArray(existingJsonStocks);
            if(findSymbol_in_FavList(query_symbol, jsonArray)){
                Log.d(LOG_TAG, "Favorites : Set checked to true ");
                menu.findItem(R.id.action_favorite).setChecked(true);
            }else{
                Log.d(LOG_TAG, "Favorites : Set checked to false ");
                menu.findItem(R.id.action_favorite).setChecked(false);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return super.onPrepareOptionsMenu(menu);
    }



    public boolean findSymbol_in_FavList(String symbol, JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            String stored_symbol = jsonArray.getString(i);
            if(stored_symbol.equalsIgnoreCase(symbol)) {
                Log.d(LOG_TAG, "Favorites : Found symbol : " + symbol);
                return true;
            }
        }
        Log.d(LOG_TAG, "Favorites : NOT Found symbol : " + symbol);
        return false;
    }

    public void deleteSymbol_from_FavList(String symbol, JSONArray jsonArray) throws JSONException {
        dump_symbols_from_FavList(jsonArray);
        JSONArray newJsonArray = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            String stored_symbol = jsonArray.getString(i);
            if(stored_symbol.equalsIgnoreCase(symbol))
                continue;
            else
                newJsonArray.put(stored_symbol);
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(FAVSTOCKS, newJsonArray.toString());
        editor.commit();
        Log.d(LOG_TAG, "Favorites : deleted symbol : " + symbol);
        dump_symbols_from_FavList(newJsonArray);
    }

    public void dump_symbols_from_FavList(JSONArray jsonArray) {
        Log.d(LOG_TAG, "Favorites : ******************************* ");
        for (int i = 0; i < jsonArray.length(); i++) {
            String stored_symbol = null;
            try {
                stored_symbol = jsonArray.getString(i);
                Log.d(LOG_TAG, "Favorites : stored_symbol : " + stored_symbol);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "Favorites : onOptionsItemSelected : ");
        switch (item.getItemId()){
            case R.id.action_favorite:
                if (item.isChecked()){
                    Log.d(LOG_TAG, "Favorites : isChecked : ");
                    item.setChecked(false);
                }
                else {
                    Log.d(LOG_TAG, "Favorites : isNOTChecked : ");
                    item.setChecked(true);
                }
                Toast.makeText(getApplicationContext(), "Fav selected : " + query_symbol, Toast.LENGTH_SHORT).show();
                String existingJsonStocks = sharedpreferences.getString(FAVSTOCKS, "['0']");
                try {
                    Log.d(LOG_TAG, "Favorites : existingJsonStocks : " + existingJsonStocks);
                    JSONArray jsonArray = new JSONArray(existingJsonStocks);
                    int no_of_stored_symbols = jsonArray.length();
                    String currentStockSymbol = query_symbol;
                    //check if currentStockSymbol already in favorites
                    //yes:delete currentStockSymbol from favorites
                    //no:add stockSymbol to favorites
                    if(no_of_stored_symbols > 0) {
                        dump_symbols_from_FavList(jsonArray);
                        if(findSymbol_in_FavList(currentStockSymbol, jsonArray)){
                            deleteSymbol_from_FavList(currentStockSymbol, jsonArray);
                        }else{//append to existing favorites
                            Log.d(LOG_TAG, "Favorites : appended to existing Favourites List with : " + currentStockSymbol);
                            jsonArray.put(currentStockSymbol);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(FAVSTOCKS, jsonArray.toString());
                            editor.commit();
                        }
                    }else{
                        JSONArray newJsonArray = new JSONArray();
                        newJsonArray.put(currentStockSymbol);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(FAVSTOCKS, newJsonArray.toString());
                        editor.commit();
                        Log.d(LOG_TAG, "Favorites : created fresh Favourites List with : " + currentStockSymbol);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_fb:
                Bitmap image = CurrentFragment.bitmap;
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                //setShareContent(content);
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    Log.d(LOG_TAG, "Current Stock Price of " + CurrentFragment.MyStockCompanyName + " is " + "$ " + CurrentFragment.MyStockPrice);
                    Log.d(LOG_TAG, "Stock Information of " + CurrentFragment.MyStockCompanyName + " (" + CurrentFragment.MyStockSymbol + ")");
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Current Stock Price of " + CurrentFragment.MyStockCompanyName + " is " + "$ " + CurrentFragment.MyStockPrice )
                            .setContentDescription(
                                    "Stock Information of " + CurrentFragment.MyStockCompanyName + " (" + CurrentFragment.MyStockSymbol + ")")
                            .setImageUrl(Uri.parse("http://chart.finance.yahoo.com/t?s=" + CurrentFragment.MyStockSymbol + "&lang=en-US&width=960&height=520"))
                            .build();

                    shareDialog.show(linkContent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }
}
