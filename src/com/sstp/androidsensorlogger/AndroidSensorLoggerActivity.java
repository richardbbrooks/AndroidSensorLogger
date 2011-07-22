package com.sstp.androidsensorlogger;
/*
 * Libraries Included in the class:
 * opencsv 2.3
 */

import au.com.bytecode.opencsv.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
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

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.gpstext);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        tv.setText("Initializing...");
        mTts = new TextToSpeech(this, this);
    }
    
    public void onLocationChanged(Location arg0) {
        String lat = String.valueOf(arg0.getLatitude()); //lat is retrieved 
        String lon = String.valueOf(arg0.getLongitude()); //lon is retrieved
        tv.setText("lat="+lat+", lon="+lon);
        try {
			// Writes the lat and lon every time a new location is observed.
        	SaveData(lat, lon, "LocationData.csv");
        	// Speaks out new line
        	speakData(new File(Environment.getExternalStorageDirectory()+"/LocationData.csv"));
        	
		} catch (Exception e) {
			e.printStackTrace();
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
     
    public void SaveData(String lat, String lon, String fileLocation) throws Exception
	{
    	
    	// **SIDE NOTE 2: In order to write/read into the SD card, a permission script has to be added to the 
    	// Android Manifest file: android.permission.WRITE_EXTERNAL.STORAGE **
		try {
			
			File root = Environment.getExternalStorageDirectory();
		    if (root.canWrite()){
		        File gpxfile = new File(root, fileLocation);
		        FileWriter gpxwriter = new FileWriter(gpxfile, true);
		        CSVWriter out = new CSVWriter(gpxwriter);
		        String[] log = new String[] {lat, lon};
		        out.writeNext(log);
		        out.flush();
		       
		    }
		} catch (IOException e) {
			e.printStackTrace();
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
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = mTts.setLanguage(Locale.US);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
               // Lanuage data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
            } else {
    			mTts.speak("The following audio is GPS coordinates " +
    						"from an android phone attached to a balloon made by Team SWAG!", 
    						TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }
    
    public void speakData(File f)
    {
    	Scanner in = null;
		try {
			in = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	String s = "";
    	while (in.hasNextLine())
    	{
    		s = in.nextLine(); // overwrites s so always speaks last line (the new data)
    	}
    	mTts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }
}