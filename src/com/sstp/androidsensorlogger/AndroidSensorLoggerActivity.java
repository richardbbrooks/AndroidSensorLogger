package com.sstp.androidsensorlogger;

//import java.io.*;
import java.util.*;
//import java.text.SimpleDateFormat;
//import java.lang.Double;

import android.app.Activity;
import android.os.Bundle;
//import android.os.Environment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.TextView;

public class AndroidSensorLoggerActivity extends Activity implements LocationListener, OnInitListener{
	private LocationManager lm;
	private TextView tv;
	private TextToSpeech mTts;
    private static final String TAG = "TextToSpeechDemo";
	private ArrayList<String> chosenEncoding = new ArrayList<String>();
	ArrayList<String> data = new ArrayList<String>();
    private int speechCounter;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.gpstext);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //1000 initially
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        tv.setText("Initializing...");
        mTts = new TextToSpeech(this, this);
        speechCounter = 89;
        chosenEncoding.add("SIERRA");
        chosenEncoding.add("YANKEE");
        chosenEncoding.add("ALPHA");
        chosenEncoding.add("VICTOR");
        chosenEncoding.add("INDIA");
        chosenEncoding.add("QUEBEC");
        chosenEncoding.add("WHISKEY");
        chosenEncoding.add("NOVEMBER");
        chosenEncoding.add("FOXTROT");
        chosenEncoding.add("MIKE");
    }
    public void onLocationChanged(Location arg0) {
    	speechCounter++;
        String lat = String.valueOf(arg0.getLatitude());
        String lon = String.valueOf(arg0.getLongitude());
        String alt = String.valueOf(arg0.getAltitude());
        
        tv.setText("lat="+lat+", lon="+lon+" , alt="+alt+" , lcv="+speechCounter);
        
        if (speechCounter == 91){
        	speechCounter = 0;
        }
        
        if (speechCounter % 90 == 0) {
        	//String la = "Latitude";
    	    //String lo = "Longitude";
    	    String al = "SWAG";
    	    String SWAG = "SWAG";
            
    	    data.add(SWAG);
    	    data.add(SWAG);
    	    data.add(SWAG);
            //data.add(la);
            double latEnc = Math.round(Double.parseDouble(lat) * 100000.0)/100000.0;
	    	lat = latEnc + "";	    	
	    	
	    	for (int n = 0; n < lat.length(); n++)
	    	{
	    		if (lat.substring(n,n+1).equals(".")) {
	    			data.add("DECIMAL");
	    		}
	    		else if (lat.substring(n,n+1).equals("-")){
	    			data.add("SWAG");
	    		}
	    		else {
	    			int number = Integer.parseInt(lat.substring(n,n+1));
	    			//data.add(number + " ");
	    			data.add(chosenEncoding.get(number) + " ");
	    		}
	    	}
	    	
            //data.add(lo);
            double lonEnc = Math.round(Double.parseDouble(lon) * 100000.0)/100000.0;
	    	lon = lonEnc + "";
	    	
	    	for (int n = 0; n < lon.length(); n++)
	    	{
	    		if (lon.substring(n,n+1).equals(".")) {
	    			data.add("DECIMAL");
	    		}
	    		else if (lon.substring(n,n+1).equals("-")) {
	    			data.add("SWAG");
	    		}
	    		else {
		    		int number = Integer.parseInt(lon.substring(n,n+1));
	    			//data.add(number + " ");
		    		data.add(chosenEncoding.get(number) + " ");
	    		}
	    	}
            
            data.add(al);
            double altEnc = Math.round(Double.parseDouble(alt) * 100.0)/100.0;
	    	alt = altEnc + "";
            data.add(alt);
            
            for (String g : data)
	    	{
		    	mTts.setSpeechRate((float) .7);
	    		mTts.speak(g, TextToSpeech.QUEUE_ADD, null);
	    		mTts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
	    	}
            data.clear();
        }              
    }
    public void onDestroy() {
        // Don't forget to shutdown!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language is not available.");
            } else {
    			//mTts.speak("No success.");
            }
        } else {
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }
    public void onProviderDisabled(String arg0) {
        Log.e("GPS", "provider disabled " + arg0);
    }
    public void onProviderEnabled(String arg0) {
        Log.e("GPS", "provider enabled " + arg0);
    }
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        Log.e("GPS", "status changed to " + arg0 + " [" + arg1 + "]");
    }
}