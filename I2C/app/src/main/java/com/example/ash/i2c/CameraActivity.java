package com.example.ash.i2c;

import android.app.Activity;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

/**
 * Created by Ash on 29/11/2017.
 */

public class CameraActivity extends AppCompatActivity {

    //private Camera mCamera = getCameraInstance();
    //private CameraPreview mPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);

        // Create an instance of Camera

        // Create our Preview view and set it as the content of our activity.
        //mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        //preview.addView(mPreview);
    }
}
