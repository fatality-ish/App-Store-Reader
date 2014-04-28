package com.example.appreader;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
 
public class MainActivity extends ListActivity {
 
    private ProgressDialog pDialog;
 
    // URL to get JSON
    private static String url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topgrossingapplications/sf=143441/limit=25/json";
 
    // JSON Node names
    private SQLiteDatabase db;
    private Cursor cursor;
    private final String create = Constants.CREATE_TABLE;
    private ContentValues values;
    private static final String TAG_FEED = "feed";
    private static final String TAG_ENTRY = "entry";
    private static final String TAG_LABEL = "label";
    private static final String TAG_NAME = "im:name";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_PRICE = "im:price";    
    private static final String TAG_ATTRIBUTES = "attributes";
    private static final String TAG_ARTIST = "im:artist";
    private static final String TAG_RELEASE_DATE = "im:releaseDate";
    private static final String TAG_LINK = "id";
 
    // apps JSONArray
    JSONArray apps = null;
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> appList;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        appList = new ArrayList<HashMap<String, String>>();
 
        ListView lv = getListView();
        
        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {
        	
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {                
                // Starting single contact activity
            	HashMap<String,String> toSend = appList.get(position);
                Intent in = new Intent(getApplicationContext(),AppDetails.class);
                in.putExtra(TAG_NAME,toSend.get(TAG_NAME));
                in.putExtra(TAG_SUMMARY,toSend.get(TAG_SUMMARY));
                in.putExtra(TAG_RELEASE_DATE,toSend.get(TAG_RELEASE_DATE));
                in.putExtra(TAG_ARTIST,toSend.get(TAG_ARTIST));
                in.putExtra(TAG_LABEL,toSend.get(TAG_LABEL)); 
                in.putExtra(TAG_LINK, toSend.get(TAG_LINK));
                startActivity(in);
 
            }
        });
 		
        // Calling async task to get json
        new GetApps().execute();
        
      //create database and table
        db = openOrCreateDatabase(Constants.DATABASE_NAME, Context.MODE_PRIVATE, null);        
        db.execSQL(create);
    }
 
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetApps extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            
        	// Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject feed = jsonObj.getJSONObject(TAG_FEED); 
                    // Getting JSON Array node
                    apps = feed.getJSONArray(TAG_ENTRY);
                    // looping through All Apps
                    for (int i = 0; i < apps.length(); i++) {
                        JSONObject a = apps.getJSONObject(i);
                        
                        //Name
                        JSONObject nameobj = a.getJSONObject(TAG_NAME);
                        String name = nameobj.getString(TAG_LABEL);                        
                        
                        //Summary
                        JSONObject summobj = a.getJSONObject(TAG_SUMMARY); 
                        String summary = summobj.getString(TAG_LABEL);                                            
 
                        // Price 
                        JSONObject price = a.getJSONObject(TAG_PRICE);
                        String priceLabel = price.getString(TAG_LABEL);
                        
                        // Artist
                        JSONObject artistobj = a.getJSONObject(TAG_ARTIST);
                        String artist = artistobj.getString(TAG_LABEL);
                        
                        //Release date
                        JSONObject releaseDateobj = a.getJSONObject(TAG_RELEASE_DATE);
                        JSONObject releaseattrobj = releaseDateobj.getJSONObject(TAG_ATTRIBUTES);
                        String releaseDate = releaseattrobj.getString(TAG_LABEL);
                        
                        //Link
                        JSONObject idobj = a.getJSONObject(TAG_LINK);
                        String link = idobj.getString(TAG_LABEL);
                                                
                        // Temporary hashmap for single app
                        HashMap<String, String> app = new HashMap<String, String>();
 
                        // adding each child node to HashMap key-value
                        app.put(TAG_NAME, name);                        
                        app.put(TAG_SUMMARY, summary);
                        app.put(TAG_LABEL, priceLabel);
                        app.put(TAG_ARTIST, artist);
                        app.put(TAG_RELEASE_DATE, releaseDate);
                        app.put(TAG_LINK, link);
                        
                        // adding app to app list
                        appList.add(app);
                        
                        //adding app to database if it hasn't been added already
                        cursor = db.query(Constants.TABLE_NAME, new String[] {Constants.KEY_NAME, Constants.KEY_FAVORITE}, Constants.KEY_NAME+"= '"+name+"'", null, null, null, null);
                        if(cursor.moveToFirst()==false){
	                        values = new ContentValues();
	                        values.put(Constants.KEY_NAME, name);
	                        values.put(Constants.KEY_FAVORITE,0);
	                        db.insert(Constants.TABLE_NAME, null, values);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            
            cursor.close();
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */                        
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, appList,
                    R.layout.list_item, new String[] { TAG_NAME, TAG_ARTIST,
                            TAG_LABEL }, new int[]{R.id.name,R.id.artist,R.id.price_header} );            
            setListAdapter(adapter);
        }
 
    }
 
}