package com.google.zxing.client.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.config.ConfigUnit;
import com.google.zxing.client.android.config.Constants;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

import java.io.IOException;

/**
 * activity或fragment必须调用的方法
 * onCreate、onResume、onPause、onDestroy、setMainView
 * Created by wangkp on 2017/9/15.
 */

public class CaptureUnit implements SurfaceHolder.Callback, ActivityFragmentLifecycle {

    private static final String TAG = CaptureUnit.class.getSimpleName();

    private Activity activity;
    private CaptureUnitCallback captureUnitCallback;
    private int requestPermissionCamera = 0;

    private CameraManager cameraManager;
    private CaptureHandler handler;
    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;

    ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    void setMainView(SurfaceView surfaceView, ViewfinderView viewfinderView) {
        this.surfaceView = surfaceView;
        this.viewfinderView = viewfinderView;
    }

    public void setOrientationPortrait(boolean orientationPortrait) {
        ConfigUnit.getInstance().setScreenOrientation(orientationPortrait ? Constants.SCREEN_ORIENTATION_PORTRAITE: Constants.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public boolean isOrientationPortrait() {
        return ConfigUnit.getInstance().getScreenOrientation() == Constants.SCREEN_ORIENTATION_PORTRAITE;
    }

    public Handler getHandler() {
        return handler;
    }

    CameraManager getCameraManager() {
        return cameraManager;
    }

    public CaptureUnit(Activity activity, int requestPermissionCamera) {
        this.activity = activity;
        this.requestPermissionCamera = requestPermissionCamera;
    }

    public CaptureUnit(Activity activity, CaptureUnitCallback captureUnitCallback, int requestPermissionCamera) {
        this.activity = activity;
        this.captureUnitCallback = captureUnitCallback;
        this.requestPermissionCamera = requestPermissionCamera;
    }

    void setCaptureUnitCallback(CaptureUnitCallback captureUnitCallback) {
        this.captureUnitCallback = captureUnitCallback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCameraCompat(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(activity);
        beepManager = new BeepManager(activity);
        ambientLightManager = new AmbientLightManager(activity);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onResume() {
        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        cameraManager = new CameraManager(activity);

        if (viewfinderView == null) {
            throw new NullPointerException("you must invoke setMainView method");
        }
        viewfinderView.setCameraManager(cameraManager);

        handler = null;

        activity.setRequestedOrientation(isOrientationPortrait() ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        resetStatusView();


        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        if (surfaceView == null) {
            throw new NullPointerException("you must invoke setMainView method");
        }
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCameraCompat(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    public void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();

        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            if (captureUnitCallback != null) captureUnitCallback.handleDecodeBitmap(barcode);
//            // Then not from history, so beep/vibrate and we have an image to draw on
//            beepManager.playBeepSoundAndVibrate();
//            ParsedResult parsedResult = ResultParser.parseResult(rawResult);
//            String result = parsedResult.getDisplayResult();
//            Log.d(TAG, "result:" + result);
//            if (captureUnitCallback != null) captureUnitCallback.handleDecodeText(result);

        } else {
            if (captureUnitCallback != null) captureUnitCallback.handleDecodeText(null);
        }
    }

    //兼容6.0
    private void initCameraCompat(SurfaceHolder surfaceHolder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED) {
            String permisions[] = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(activity, permisions, requestPermissionCamera);
        } else {
            initCamera(surfaceHolder);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureHandler(this, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.app_name));
        builder.setMessage(activity.getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(ConstantIds.RESTART_PREVIEW, delayMS);
        }
        resetStatusView();
    }

    void resetStatusView() {
        if (captureUnitCallback != null) captureUnitCallback.resetStatusView();
    }

    void onRequestPermissionsResult() {
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);
    }
}
