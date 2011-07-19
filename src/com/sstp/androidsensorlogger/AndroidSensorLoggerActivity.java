package com.sstp.androidsensorlogger;
/*
 * Libraries Included in the class:
 * opencsv 2.3
 */

import au.com.bytecode.opencsv.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

public class AndroidSensorLoggerActivity extends Activity implements LocationListener{
	private LocationManager lm;
	private TextView tv;
	final surfaceViewer mySurfaceViewer = new surfaceViewer(this);
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	FrameLayout preview = new FrameLayout(this);
    	preview.addView(mySurfaceViewer);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.gpstext);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        tv.setText("Initializing...");
    }
    
    
    
    public void onLocationChanged(Location arg0) {
        String lat = String.valueOf(arg0.getLatitude()); //lat is retrieved 
        String lon = String.valueOf(arg0.getLongitude()); //lon is retrieved
        tv.setText("lat="+lat+", lon="+lon);
        try {
			// Writes the lat and lon every time a new location is observed.
        	SaveData(lat, lon, "LocationData.csv");
			 
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

  
}