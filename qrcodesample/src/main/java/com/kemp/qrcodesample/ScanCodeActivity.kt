package com.kemp.qrcodesample

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import com.google.zxing.client.android.CaptureActivity
import com.google.zxing.client.android.ViewfinderView

class ScanCodeActivity : CaptureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_code)

        val surfaceView: SurfaceView = findViewById(R.id.preview_view) as SurfaceView
        val viewFinderView: ViewfinderView = findViewById(R.id.viewfinder_view) as ViewfinderView
        setMainView(surfaceView, viewFinderView)

        setOrientationPortrait(true)
    }

    override fun handleDecodeText(result: String?) {
        Log.d("ScanCodeActivity", result)
        finish()
    }
}
