package com.example.tipcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class WebLookup extends Activity {

	private static final String tag = "TipCalculator";
	
	private EditText urlBox;
	private Button goButton;
	private WebView webview;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_lookup);
		urlBox = (EditText)findViewById(R.id.urlText);
		goButton = (Button)findViewById(R.id.goButton);
		webview = (WebView)findViewById(R.id.webView);
				
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient(){  	  
    	  public boolean shouldOverrideUrlLoading(WebView view, String url){
    		  view.loadUrl(url);
    		  return true;
    	  }  	  
		});
		
		//Modifies url entered if necessary and loads it
		goButton.setOnClickListener(new OnClickListener(){			 
			public void onClick(View v) {
				try {
					url = urlBox.getText().toString();
					if(!(url.substring(0,7).equals("http://")))
					{
						url="http://"+url;
					}
					webview.loadUrl(url);
				}
				catch(Exception e){
					Log.e(tag, "Failed to load webpage:" + e.getMessage());
				}
	}});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
	        webview.goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_lookup, menu);
		
		return true;
	}

}


