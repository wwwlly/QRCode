package com.google.zxing.client.android;

import android.graphics.Bitmap;

/**
 * Created by wangkp on 2017/9/18.
 */

public interface CaptureUnitCallback {

    void handleDecodeText(String result);

    void handleDecodeBitmap(Bitmap result);

    void resetStatusView();
}
