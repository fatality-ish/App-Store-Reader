package com.example.tipcalculator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView total_bill,total_person,total_tip,tip_person;
	private EditText check_amount,number_ppl,tip_percent;
	private Button goButton,webButton,dialButton,mapButton;
    
	private static final String tag = "TipCalculator";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Franchise-Bold.ttf");
		LinearLayout root=(LinearLayout)findViewById(R.id.root);
		root.setBackgroundColor(Color.LTGRAY);
		
		check_amount= (EditText) findViewById(R.id.check_amount);
		number_ppl= (EditText) findViewById(R.id.number_ppl);
		tip_percent= (EditText) findViewById(R.id.tip_percent);
		total_bill= (TextView) findViewById(R.id.total_bill);
		total_person= (TextView) findViewById(R.id.total_person);
		total_tip= (TextView) findViewById(R.id.total_tip);
		tip_person= (TextView) findViewById(R.id.tip_person);
		goButton = (Button) findViewById(R.id.tip_button);
		webButton = (Button) findViewById(R.id.web_button);
		dialButton = (Button) findViewById(R.id.dial_button);
		mapButton = (Button) findViewById(R.id.map_button);
		
		//demonstrating using a non-default Typeface and font color
		check_amount.setTypeface(type);
		number_ppl.setTypeface(type);
		tip_percent.setTypeface(type);
		total_bill.setTypeface(type);
		total_bill.setTextColor(Color.RED);
		total_person.setTypeface(type);
		total_tip.setTypeface(type);
		tip_person.setTypeface(type);
		
		Toast.makeText(getApplicationContext(), "Made by Amitav Khandelwal", Toast.LENGTH_LONG).show();
		
		//Tip calculator business logic
		goButton.setOnClickListener(new OnClickListener(){			 
			public void onClick(View v) {
				try {

					Log.i(tag, "Tip onClick invoked.");
					//Retrieving 
					String checkAmount = check_amount.getText().toString();
					String numberPpl = number_ppl.getText().toString();
					String tipPercent = tip_percent.getText().toString();
					
					//Setting default values for fields
					if(checkAmount.matches(""))
					{
						check_amount.setText("0");	
						checkAmount="0";
					}
					if(tipPercent.matches(""))
					{
						tip_percent.setText("15");
						tipPercent="15";
					}
						
					if(numberPpl.matches(""))
					{
						number_ppl.setText("1");
						numberPpl="1";
					}
					
					double tipPer = Float.parseFloat(tipPercent)/100;
					int numPpl = Integer.parseInt(numberPpl);
					double chkAmt = Float.parseFloat(checkAmount);
					
					//Calculating answers
					double totalTip = chkAmt*tipPer;
					double totalBill = chkAmt+totalTip;
					double totalPer = totalBill/numPpl;
					double tipPerPerson = totalTip/numPpl;
					
					//Rounding to 2 decimal places
					totalTip = Math.round(totalTip* 100.0) / 100.0;
					totalBill = Math.round(totalBill* 100.0) / 100.0;
					totalPer = Math.round(totalPer* 100.0) / 100.0;
					tipPerPerson = Math.round(tipPerPerson* 100.0) / 100.0;
					
					total_bill.setText("$"+totalBill);
					total_person.setText("$"+totalPer);
					total_tip.setText("$"+totalTip);
					tip_person.setText("$"+tipPerPerson);
					Log.i(tag, "onClick complete.");
				
				} catch (Exception e) {
					Log.e(tag, "Failed to Calculate Tip:" + e.getMessage());					
				}

		}});
		
		//Web button onClick starts a new activity 
		webButton.setOnClickListener(new OnClickListener(){			 
			public void onClick(View v) {
				Log.i(tag, "Web onClick invoked.");
				try {
					Intent intent = new Intent(getApplicationContext(),WebLookup.class);			
					startActivity(intent);
				}
				catch(Exception e){
					Log.e(tag, "Failed to launch web:" + e.getMessage());
				}
			}});
		
		//Dial button uses an intent to call a preset number
		dialButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				try {
					Log.i(tag, "Call onClick invoked.");
					Uri uri3 = Uri.parse("tel:7818912000");
		        	Intent i3 = new Intent(Intent.ACTION_CALL,uri3);
		            startActivity(i3);					
					}				
				catch(Exception e){
					Log.e(tag, "Failed to launch dialer:" + e.getMessage());
				}
			}});
		
		//Map button sends an intent that is caught by any maps application
		mapButton.setOnClickListener(new OnClickListener(){		
			public void onClick(View v) {
				try {
					Log.i(tag, "Maps onClick invoked.");
					Uri uri2 = Uri.parse("geo:0,0?q=175+forest+street+waltham+ma");		        	
		        	Intent i2 = new Intent(Intent.ACTION_VIEW,uri2);
		            startActivity(i2);
				}
				catch(Exception e){
					Log.e(tag, "Failed to launch maps:" + e.getMessage());
				}		
			}});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);		
		return true;
	}

}
