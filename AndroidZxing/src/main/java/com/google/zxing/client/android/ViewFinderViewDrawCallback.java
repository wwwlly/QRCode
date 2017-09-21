package com.google.zxing.client.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by wangkp on 2017/9/15.
 */

interface ViewFinderViewDrawCallback {

    /**
     * 画背景
     *
     * @param canvas
     * @param frame
     * @param paint
     */
    void onDrawBackground(Canvas canvas, Rect frame, Paint paint);

    /**
     * 画镭射线
     *
     * @param canvas
     * @param paint
     */
    void onDrawLaser(Canvas canvas, Rect frame, Paint paint);

    /**
     * 画前景
     *
     * @param canvas
     * @param paint
     */
    void onDrawForeground(Canvas canvas, Rect frame, Paint paint);

}
