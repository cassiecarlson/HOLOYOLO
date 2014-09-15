package com.kylealar.holoyolo;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.util.Log;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements SurfaceHolder.Callback {

    public Camera camera;
    public boolean isFlashOn = false;
    public boolean hasFlash;
    Camera.Parameters params;
    public SurfaceHolder myHolder;
    public SurfaceView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if hardware flash is supported
        //show error message for now, blank white screen with max brightness in the future
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            Log.d("NO FLASH PRESENT"," " + "DEVICE DOES NOT HAVE FLASH");
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Your device does not support hardware flashlights. " +
                    "A software solution is planned for future release. " +
                    "The app will now close");
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //close the application for now
                            finish();
                        }
                    });
            alert.show();
            return;
        } else {
            Log.d("HAS FLASH = TRUE", "NOTHING TO SEE HERE, NOT THE PROBLEM");
        }

        getCamera();

        try {
            preview = (SurfaceView) findViewById(R.id.PREVIEW);
            myHolder = preview.getHolder();
            myHolder.addCallback(this);
            camera = Camera.open();
            camera.setPreviewDisplay(myHolder);
            Log.d("SURFACE HOLDER"," " + "ADDING THE CAMERA PREVIEW TO THE SURFACE HOLDER");
        }
        catch (Exception e) {
            Log.e("SURFACE HOLDER"," " + "I HAVE NO IDEA WHAT'S WRONG, BUT IT'S PROBABLY FINE");

        }

        flashOn();

        //this is where I had on on click listener, use on touch

        findViewById(R.id.mainView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isFlashOn) {
                    flashOff();
                    Log.d("CLICKING THE BUTTON", " " + "BUTTONS CALLS FLASH OFF");
                } else {
                    flashOn();
                    Log.d("CLICKING THE BUTTON", " " + "BUTTON CALLS FLASH ON");
                }
                return false;
            }
        });
    }

    private void flashOn() {
        Log.d("FLASH ON"," " + "flashOn");
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();

            isFlashOn = true;

            changeTextColor();
        } else {
            Log.d("FLASH ON FAILED", " " + "flashOn else");
        }
    }

    private void flashOff() {
        Log.d("FLASH OFF"," " + "flashOff");
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();

            isFlashOn = false;

            changeTextColor();
        } else {
            Log.d("FLASH OFF FAILED", " " + "flashOff else");
        }
    }

    public void changeTextColor() {
        if (isFlashOn) {
            TextView holo_text_view = (TextView)findViewById(R.id.holo_text_view);
            holo_text_view.setTextColor(getResources().getColor(R.color.white));
        } else {
            TextView holo_text_view = (TextView)findViewById(R.id.holo_text_view);
            holo_text_view.setTextColor(getResources().getColor(R.color.black));
        }

    }



    //call this on start, get the camera parameters
    private void getCamera() {
        Log.d("CAMERA"," " + "getCamera");
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            }
            catch (Exception e) {
                Log.e("Camera failed to Open. Error: ", e.getMessage());
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("SURFACE CHANGED ", "SURFACE CHANGED " + "SURFACE CHANGED");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("SURFACE CREATED"," " + "surfaceCreated");
        try {
            myHolder = holder;
            camera.setPreviewDisplay(myHolder);
        }
        catch (Exception e){
            Log.e("Could not create surface holder. Error: ", e.getMessage());
            finish();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("SURFACE DESTROYED"," " + "surfaceDestroyed");
        camera.stopPreview();
        myHolder = null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
