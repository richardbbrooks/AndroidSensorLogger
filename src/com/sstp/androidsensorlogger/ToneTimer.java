package com.sstp.androidsensorlogger;

import java.util.TimerTask;
import android.media.*;

public class ToneTimer extends TimerTask {
	
	ToneGenerator tg = null;

	public void run() {
		AndroidSensorLoggerActivity asl = new AndroidSensorLoggerActivity();
		while (true) {
	    	if (asl.getCurrentUTCTime().compareTo(asl.getUTCTime()) >= 30 || 
	    			asl.getCurrentUTCTime().compareTo(asl.getUTCTime()) == 1)
	    		break;
	    	else {}
		}
		tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
		tg.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_SLS);
	}
}
