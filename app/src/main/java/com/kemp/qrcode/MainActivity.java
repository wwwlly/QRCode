package com.kemp.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.ViewfinderView;

public class MainActivity extends CaptureActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setOrientationPortrait(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        ViewfinderView viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        setMainView(surfaceView, viewfinderView);
    }

    @Override
    public void handleDecodeText(String result) {
        Log.d("MainActivity", result);
        finish();
    }

    @Override
    public void handleDecodeBitmap(Bitmap result) {
        ImageView iv = (ImageView) findViewById(R.id.iv);
        iv.setImageBitmap(result);
    }
}
