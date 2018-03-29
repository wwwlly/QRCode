/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.SurfaceView;

/**
 * 子类必须调用setMainView这个方法来设置surfaceView和viewfinderView
 * 调用setOrientationPortrait来设置横屏还是竖屏
 *
 * 二维码横屏转成竖屏注意事项：
 * 两方面camera配置和二维码配置
 *
 * acitivity竖屏配置
 *
 * camera竖屏配置{@link android.hardware.Camera#setDisplayOrientation(int)}
 * <p>This does not affect the order of byte array passed in {@link Camera.PreviewCallback#onPreviewFrame},
 * JPEG pictures, or recorded videos. This method is not allowed to be called during preview.
 *
 * camera设置预览大小{@link android.hardware.Camera.Parameters#setPreviewSize(int, int)}
 *
 * 由于{@link Camera.PreviewCallback#onPreviewFrame}与设置camera orientation无关，因此byte[] data是横屏的数据
 * 需要处理成竖屏的
 *
 * 参考：https://github.com/LibraZhu/Component
 */
public abstract class CaptureActivity extends Activity implements CaptureUnitCallback {

    private static final int REQUEST_PERMISSION_CAMERA = 0;

    private CaptureUnit captureUnit = new CaptureUnit(this, this, REQUEST_PERMISSION_CAMERA);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        captureUnit.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        captureUnit.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        captureUnit.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        captureUnit.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA:
                captureUnit.onRequestPermissionsResult();
                break;
            default:
                break;
        }
    }

    @Override
    public void handleDecodeText(String result) {
        finish();
    }

    @Override
    public void handleDecodeBitmap(Bitmap result) {

    }

    @Override
    public void resetStatusView() {

    }

    public void setMainView(SurfaceView surfaceView, ViewfinderView viewfinderView) {
        captureUnit.setMainView(surfaceView, viewfinderView);
    }

    public void setOrientationPortrait(boolean portrait) {
        captureUnit.setOrientationPortrait(portrait);
    }

    public void restartPreview() {
        captureUnit.restartPreviewAfterDelay(0L);
    }

}
