package com.sstp.androidsensorlogger;

import java.util.TimerTask;
import android.media.*;

public class ToneTimer extends TimerTask {
	
	ToneGenerator tg = null;

	@Override
	public void run() {
		tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
		tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);
	}
}
