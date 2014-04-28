package com.example.appreader;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AppDetails extends Activity {

	private Button copyButton,shareButton,favoriteButton;
	private boolean isFavorited;
	private ContentValues values;
	
	private SQLiteDatabase db;
    private Cursor cursor;
     
    private ClipboardManager clipboard;
	
	// JSON node keys
    private static final String TAG_LABEL = "label";
    private static final String TAG_NAME = "im:name";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_ARTIST = "im:artist";
    private static final String TAG_RELEASE_DATE = "im:releaseDate";
    private static final String TAG_LINK = "id";
    
    
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_app_details);
	        
	        clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);	        
	        // getting intent data
	        Intent in = getIntent();
	        
	        // Get JSON values from previous intent
	        final String name = in.getStringExtra(TAG_NAME);
	        String summary = in.getStringExtra(TAG_SUMMARY);
	        String artist = in.getStringExtra(TAG_ARTIST);
	        String priceLabel = in.getStringExtra(TAG_LABEL);
	        String releaseDate = in.getStringExtra(TAG_RELEASE_DATE);
	        final String link = in.getStringExtra(TAG_LINK);
	        
	        // Displaying all values on the screen
	        TextView Name = (TextView) findViewById(R.id.name_label);
	        TextView Artist = (TextView) findViewById(R.id.artist_label);
	        TextView Price = (TextView) findViewById(R.id.price_label);
	        TextView ReleaseDate = (TextView) findViewById(R.id.release_date_label);
	        TextView Summary = (TextView) findViewById(R.id.summary_label);
	        
	        Name.setText(name);
	        Artist.setText(artist);
	        Price.setText(priceLabel);
	        ReleaseDate.setText(releaseDate);
	        Summary.setText(summary);	        
	        try{
		        db = openOrCreateDatabase(Constants.DATABASE_NAME, Context.MODE_PRIVATE, null);
		        cursor = db.rawQuery("SELECT * FROM "+Constants.TABLE_NAME+" WHERE "+Constants.KEY_NAME+" = '"+name+"';", null);
		        cursor.moveToFirst();
	        }catch (Exception e){
	        	e.printStackTrace();
	        }
	        int favoriteIndex = cursor.getColumnIndex(Constants.KEY_FAVORITE);
	        isFavorited = (cursor.getInt(favoriteIndex) == 1)? true:false;

	        copyButton = (Button)findViewById(R.id.copy_button);
	        copyButton.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View view){
	        		ClipData clip = ClipData.newPlainText("App name copied",name);
	        		Toast.makeText(AppDetails.this, "Copied to clipboard", Toast.LENGTH_LONG).show();
	    	        clipboard.setPrimaryClip(clip);
	        	}
	        });
	        shareButton = (Button)findViewById(R.id.share_button);
	        shareButton.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View view){
	        		Intent sendIntent = new Intent();
	        		sendIntent.setAction(Intent.ACTION_SEND);
	        		sendIntent.putExtra(Intent.EXTRA_TEXT, name+": "+link);
	        		sendIntent.setType("text/plain");
	        		startActivity(sendIntent);
	        	}
	        }); 
	        favoriteButton = (Button)findViewById(R.id.favorite_button);
	        if(isFavorited){
	        	favoriteButton.setText("Favorited");
	        }
	        favoriteButton.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View view){
	        		if(isFavorited){
	        			//Removing from favorites
	        			values = new ContentValues();
                        values.put(Constants.KEY_FAVORITE,0);
	        			db.update(Constants.TABLE_NAME, values, Constants.KEY_NAME+"= '"+name+"'", null);
	        			favoriteButton.setText("Save to favorites");
	        			isFavorited = false;
	        			Toast.makeText(getApplicationContext(), "Removed from favorites!", Toast.LENGTH_LONG).show();
	        		}else{
	        			//Adding to favorites
	        			values = new ContentValues();
                        values.put(Constants.KEY_FAVORITE,1);
	        			db.update(Constants.TABLE_NAME, values, Constants.KEY_NAME+"='"+name+"'", null);
	        			favoriteButton.setText("Favorited");
	        			isFavorited = true;
	        			Toast.makeText(getApplicationContext(), "Added to favorites!", Toast.LENGTH_LONG).show();
	        		}
	        	}
	        });
	        
	        cursor.close();
	    }

}
