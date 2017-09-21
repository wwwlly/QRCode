package com.kemp.qrcode;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.ViewfinderView;

public class MainActivity extends CaptureActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        ViewfinderView viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        setMainView(surfaceView, viewfinderView);

        setOrientationPortrait(true);
    }

    @Override
    public void handleDecodeText(String result) {
        Log.d("MainActivity", result);
        finish();
    }
}
