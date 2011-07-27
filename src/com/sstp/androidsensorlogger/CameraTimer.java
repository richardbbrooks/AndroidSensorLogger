package com.sstp.androidsensorlogger;

import java.util.TimerTask;


public class CameraTimer extends TimerTask {
	public void run()
	{
        CameraActivity ca = new CameraActivity();
        ca.takePicture();
    }
	
}
