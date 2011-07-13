package com.sstp.androidsensorlogger;


import java.io.BufferedWriter;
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
import android.widget.TextView;

public class AndroidSensorLoggerActivity extends Activity implements LocationListener{
	private LocationManager lm;
	private TextView tv;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.gpstext);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
        tv.setText("Initializing...");
    }
    public void onLocationChanged(Location arg0) {
        String lat = String.valueOf(arg0.getLatitude()); //lat is retrieved 
        String lon = String.valueOf(arg0.getLongitude()); //lon is retrieved
        Log.e("GPS", "location changed: lat="+lat+", lon="+lon);
        tv.setText("lat="+lat+", lon="+lon);
        try {
			// Writes the lat and lon every time a new location is observed.
        	SaveData("Lat: " + lat + " Lon: " + lon, "LocationsSaved.txt");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
     
    public void SaveData(String data, String fileLocation)
	{
    	// This function will open a file(fileLocation within the SD card and log 
    	// a stated string(data). If the file does not exist, it will create one.
    	// File will be at the root of the SD card.
    	
    	
    	// **SIDE NOTE 1: While the data logged here can be viewed with the default text reader
    	// of Windows (Notepad), it will not show up in different lines. Notepad cannot read
    	// that a new line was requested. Instead it is recommended to use WordPad. **
    	
    	// **SIDE NOTE 2: In order to write into the SD card, a permission script has to be added to the 
    	// Android Manifest file: android.permission.WRITE_EXTERNAL.STORAGE **
		try {
			// This reaches that root of the SD card and asks if it is possible to write
			// in the SD card.
			File root = Environment.getExternalStorageDirectory();
		    if (root.canWrite()){
		        File gpxfile = new File(root, fileLocation);
		        FileWriter gpxwriter = new FileWriter(gpxfile, true);
		        BufferedWriter out = new BufferedWriter(gpxwriter);
		        out.write(data);
		        out.flush();
		        out.write('\n'); //adds a line break so not all location logs are in the same
		        				//line. Makes viewing our data easier
		        out.flush();
		        out.close();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}