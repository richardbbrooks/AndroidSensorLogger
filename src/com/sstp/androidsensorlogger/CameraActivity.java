package com.sstp.androidsensorlogger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "CameraActivity";

    private Camera camera;
    private boolean isPreviwRunning = false;
    private SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Uri targetResource = Media.EXTERNAL_CONTENT_URI;
    private Uri pictureUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setContentView(R.layout.main2);
        surfaceView = (SurfaceView) findViewById(R.id.camera_surface);		
        Log.d(TAG, "got surface view");
        surfaceHolder = surfaceView.getHolder();
        Log.d(TAG, "got surface holder");
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.d(TAG, "surface holder type was set");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    };

    protected void onSaveInstanceState(Bundle outState)	{
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    };

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        Log.d(TAG, "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            Log.d(TAG, "prepare to take picture");
            takePicture();		 
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "prepare save result");
            saveResult(RESULT_CANCELED);
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event);
        if (event.getAction() == MotionEvent.ACTION_UP) {			
            Log.d(TAG, "prepare to take picture");
            takePicture();
            return true;
        }
        return super.onTouchEvent(event);
    }

    protected void onResume() {
        Log.d(TAG, "OnResume()");		
        super.onResume();
    };

    protected void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
    };	

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        Log.d(TAG, "surfaceChanged()");
        if (isPreviwRunning) {
            Log.d(TAG, "preview is running and need to be stopped");
            camera.stopPreview();
            Log.d(TAG, "preview is stopped");
        }
        Log.d(TAG, "prepare to set camera parameters");
        Camera.Parameters p = camera.getParameters();
        Log.d(TAG, "got old parameters");
        p.setPreviewSize(width, height);
        Log.d(TAG, "set width, height");
        camera.setParameters(p);
        Log.d(TAG, "set camera parameters");
        try {
            camera.setPreviewDisplay(holder);
            Log.d(TAG, "set camera preview display");
        } catch (Exception ex) {
            Log.e(TAG, "surfaceChanged exception caught: " + ex);
        }
        camera.startPreview();
        isPreviwRunning = true;
        Log.d(TAG, "camera preview is started");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");
        camera = Camera.open();
        Log.d(TAG, "camera opened");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");
        camera.stopPreview();
        Log.d(TAG, "camera preview is stopped");
        isPreviwRunning = false;
        camera.release();
        Log.d(TAG, "camera released");
    }

    public void takePicture() {
        Log.d(TAG, "takePicture()");
        try {
            pictureUri = prepareImageFile(timeStampFormat.format(new Date()), "Android Camera Image");			
            Log.d(TAG, "picture uri: " + pictureUri.getPath());
            camera.takePicture(mShutterCallback, mPictureCallbackRaw, mPictureCallbackJpeg);
        } catch (Exception ex) {
            Log.e(TAG, "takePicture() exception caught: " + ex);
            saveResult(RESULT_CANCELED);
        }
    };

    private void saveResult(int resultCode) {
        Log.d(TAG, "saveResult()");
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "RESULT_OK");
            Intent pictureTakenIntent = new Intent();
            pictureTakenIntent.setData(pictureUri);
            Log.d(TAG, "data was set");
            setResult(resultCode, pictureTakenIntent);
            Log.d(TAG, "result is set");
        } else {
            Log.d(TAG, "resultCode != RESULT_OK");
            setResult(resultCode);
        }
        Log.d(TAG, "prepare to finish activity");
        finish();
    }

    private Uri prepareImageFile(String imageFileName, String imageDescription) {
        Log.d(TAG, "prepared file name: " + imageFileName);
        ContentValues values = new ContentValues();
        values.put(MediaColumns.TITLE, imageFileName);
        if (imageDescription != null) {
            values.put(ImageColumns.DESCRIPTION, "Android Camera Image");
        }
        Log.d(TAG, "prepared values");
        return getContentResolver().insert(targetResource, values);
    }
    
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "mShutterCallback");
        }
    };
    
    Camera.PictureCallback mPictureCallbackRaw = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "Picture taken, data = " + data);
            Log.d(TAG, "Prepare to start camera preview");
            camera.startPreview();
        }
    };

    Camera.PictureCallback mPictureCallbackJpeg = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        	Log.d(TAG, "onPictureTaken()");
        	int counter = 1;
            try {
                Log.d(TAG, "prepare to open fileOutputStream");
                OutputStream fileOutputStream = getContentResolver().openOutputStream(pictureUri);
                Log.d(TAG, "prepare to write data");
                fileOutputStream.write(data);
                Log.d(TAG, "prepare to flush stream");
                fileOutputStream.flush();
                Log.d(TAG, "prepare to close stream");
                fileOutputStream.close();
                
                saveResult(RESULT_OK);
                Bitmap bm=null;
                ByteArrayInputStream bytes = new ByteArrayInputStream(data);
                BitmapDrawable bmd = new BitmapDrawable(bytes);
                bm = bmd.getBitmap();
                Save_to_SD(bm, "ImageFile" + counter);
            }

            catch (Exception e) {
                Log.e(TAG, "mPictureCallbackJpeg exception caught: " + e);
            } 
            counter++;
    }
    };

    private static void Save_to_SD (Bitmap bm, String image_name){

    	  File extStorageDirectory = Environment.getExternalStorageDirectory();
    	  String meteoDirectory_path = extStorageDirectory + "/space";

    	  OutputStream outStream = null;
    	  File file = new File(meteoDirectory_path, "/"+ image_name);
    	  try {
    	   outStream = new FileOutputStream(file);
    	   bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
    	   outStream.flush();
    	   outStream.close();

    	   Log.i("Hub", "OK, Image Saved to SD");
    	   Log.i("Hub", "height = "+ bm.getHeight() + ", width = " + bm.getWidth());

    	  } catch (FileNotFoundException e) {

    	   e.printStackTrace();
    	   Log.i("Hub", "FileNotFoundException: "+ e.toString());

    	  } catch (IOException e) {

    	   e.printStackTrace();
    	   Log.i("Hub", "IOException: "+ e.toString());
    	  }

    	 }
}

