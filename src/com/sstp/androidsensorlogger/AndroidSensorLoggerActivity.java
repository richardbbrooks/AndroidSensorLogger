package com.sstp.androidsensorlogger;
/*
 * Libraries Included in the class:
 * opencsv 2.3
 */

import au.com.bytecode.opencsv.*;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.lang.Double;

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
    /* NATO Phonetics picked from this array:
     * private final String[] encodedLetters = new String [] {
    "ALPHA", "BRAVO", "CHARLIE", "DELTA", "ECHO", "FOXTROT", "GOLF", "HOTEL", "INDIA",
    "JULIET", "KILO", "LIMA", "MIKE", "NOVEMBER", "OSCAR", "PAPA", "QUEBEC", "ROMEO", 
    "SIERRA", "TANGO", "UNIFORM", "VICTOR", "WHISKEY", "X-RAY", "YANKEE", "ZULU"
    };
    */
    private ArrayList<String> chosenEncoding = new ArrayList<String>();
    private int speechCounter;

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
        
        /* Used for determining which NATO Values to use
         * int choice = Integer.MIN_VALUE;
        Random rand = new Random();
        for (int n = 0; n <= 9; n++) {
        	choice = rand.nextInt(26);
        	chosenEncoding.add(encodedLetters[choice]);
        	}
         */
        // These values were chosen:
        chosenEncoding.add("SIERRA");
        chosenEncoding.add("YANKEE");
        chosenEncoding.add("ALPHA");
        chosenEncoding.add("VICTOR");
        chosenEncoding.add("INDIA");
        chosenEncoding.add("QUEBEC");
        chosenEncoding.add("WHISKEY");
        chosenEncoding.add("PAPA");
        chosenEncoding.add("KILO");
        chosenEncoding.add("MIKE");
        
        speechCounter = 0;
        
        takePic();
    }
     
    public void takePic()
    {	
        ToneTimer ct = new ToneTimer();
        new Timer().scheduleAtFixedRate(ct, 10000, 10000);
    }
    
    public void onLocationChanged(Location arg0) {
    	double lat1 = arg0.getLatitude();
    	double lon1 = arg0.getLongitude();
    	double alt1 = arg0.getAltitude();
        String lat = String.format("%.5f",lat1); //lat is retrieved and rounded to 5 places
        String lon = String.format("%.5f",lon1); //lon is retrieved and rounded to 5 places
        String alt = String.format("%.5f",alt1); //alt is retrieved and rounded to 5 places
        tv.setText("lat="+lat+", lon="+lon + ", alt="+alt);
        SimpleDateFormat utc = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        utc.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = utc.format(new Date());
        try {
			// Writes the lat, lon, and alt every time a new location is observed.
        	SaveData(lat, lon, alt, utcTime, "LocationData.csv");
        	speechCounter++;
        	if (speechCounter % 60 == 0) {
        	// Speaks out every 60th new line
        		speakData(new File(Environment.getExternalStorageDirectory()+"/LocationData.csv"));
        	}
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
     
    public void SaveData(String lat, String lon, String alt, String time, String fileLocation) throws Exception
	{
    	
    	// **SIDE NOTE 2: In order to write/read into the SD card, a permission script has to be added to the 
    	// Android Manifest file: android.permission.WRITE_EXTERNAL.STORAGE **
		try {
			
			File root = Environment.getExternalStorageDirectory();
		    if (root.canWrite()){
		        File gpxfile = new File(root, fileLocation);
		        FileWriter gpxwriter = new FileWriter(gpxfile, true);
		        CSVWriter out = new CSVWriter(gpxwriter);
		        String[] log = new String[] {lat, lon, alt, time};
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
    			//mTts.speak("The following audio is GPS coordinates " +
    				//		"from an android phone attached to a balloon made by Team SWAG!", 
    					//	TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }
    
    public void speakData(File f)
    {
    	// Speaks "Latitude", "Lat. data", "Longitude", "Lon. data", "Altitude", "Alt. data"
    	ArrayList<String> data = new ArrayList<String>();
     	Scanner in = null;
		try {
			in = new Scanner(f);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (!mTts.isSpeaking()) {	  
	    	String s = "";
	    	String la = "Latitude";
	    	String lo = "Longitude";
	    	String al = "Altitude";
	    	
	    	while (in.hasNextLine())
	    		s = in.nextLine();  	// s is always assigned to last (newest) line
	    	
	    	data.add(la);
	    	int posSpace = s.indexOf(",");
	    	String lat = s.substring(1,posSpace-1);
	    	double latEnc = Double.parseDouble(lat) + .001;
	    	lat = latEnc + "";	    	
	    	
	    	for (int n = 0; n < lat.length(); n++)
	    	{
	    		if (lat.substring(n,n+1).equals(".")) {
	    			data.add("Point");
	    		}
	    		else if (lat.substring(n,n+1).equals("-")){
	    			data.add("Minus");
	    		}
	    		else {
	    			int number = Integer.parseInt(lat.substring(n,n+1));
	    			data.add(chosenEncoding.get(number) + " ");
	    		}
	    	}
	    	
	    	data.add(lo);
	    	s = s.replace(s.substring(0,posSpace) + ",", "");
	    	posSpace = s.indexOf(",");
	    	String lon = s.substring(1, posSpace-1);
	    	double lonEnc = Double.parseDouble(lon) + .001;
	    	lon = lonEnc + "";
	    	
	    	for (int n = 0; n < lon.length(); n++)
	    	{
	    		if (lon.substring(n,n+1).equals(".")) {
	    			data.add("Point");
	    		}
	    		else if (lon.substring(n,n+1).equals("-")) {
	    			data.add("Minus");
	    		}
	    		else {
		    		int number = Integer.parseInt(lon.substring(n,n+1));
	    			data.add(chosenEncoding.get(number) + " ");
	    		}
	    	}
	    	
	    	s = s.replace(s.substring(0,posSpace) + ",", "");
	    	posSpace = s.indexOf(",");
	    	String alt = s.substring(0, posSpace);	
	    	
	    	data.add(al);
	    	data.add(alt);
	    	
	    	for (String g : data)
	    	{
		    	mTts.setSpeechRate((float) .7);
	    		mTts.speak(g, TextToSpeech.QUEUE_ADD, null);
	    		mTts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
	    	}
		}
		else {}
    }
}