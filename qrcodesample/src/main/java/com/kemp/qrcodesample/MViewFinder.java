package com.kemp.qrcodesample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.google.zxing.client.android.ViewfinderView;

/**
 * Created by wangkp on 2017/9/20.
 */

public class MViewFinder extends ViewfinderView {

    //相框 4个直角框
    private int photoFrameColor;//相框颜色
    private int photoFrameWidth;//一个直角框的外边长度
    private int photoFrameThickness;//一个直角框的厚度
    //laser
    private Bitmap laserBitmap;
    private int laserStep;
    private int laserRange;
    private int laserY = 0;
    private Rect laserRect;

    public MViewFinder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initRes(Resources resources) {
        photoFrameColor = resources.getColor(R.color.viewfinder_photo_frame);

        photoFrameWidth = 80;
        photoFrameThickness = 10;
        laserStep = 5;
        laserRange = 200;
        laserY -= laserRange;

        laserBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_qrcode_laser);
        laserRect = new Rect();
    }

    @Override
    public void onDrawLaser(Canvas canvas, Rect frame, Paint paint) {
        int frameMidX = (frame.left + frame.right) / 2;
        int frameMidY = (frame.top + frame.bottom) / 2;
        int laserWidth = laserBitmap.getWidth();
        int laserHeight = laserBitmap.getHeight();
        laserRect.set(frameMidX - laserWidth / 2, frameMidY + laserY - laserHeight / 2, frameMidX + laserWidth / 2, frameMidY + laserY + laserHeight / 2);
        canvas.drawBitmap(laserBitmap, null, laserRect, paint);
        laserY += laserStep;
        if (laserY > laserRange) {
            laserY = -laserRange;
        }
    }

    @Override
    public void onDrawForeground(Canvas canvas, Rect frame, Paint paint) {
        //画4个直角框
        paint.setColor(photoFrameColor);
        //左上
        canvas.drawRect(frame.left, frame.top, frame.left + photoFrameWidth, frame.top + photoFrameThickness, paint);
        canvas.drawRect(frame.left, frame.top + photoFrameThickness, frame.left + photoFrameThickness, frame.top + photoFrameWidth - photoFrameThickness, paint);
        //右上
        canvas.drawRect(frame.right - photoFrameWidth, frame.top, frame.right, frame.top + photoFrameThickness, paint);
        canvas.drawRect(frame.right - photoFrameThickness, frame.top + photoFrameThickness, frame.right, frame.top + photoFrameWidth - photoFrameThickness, paint);
        //左下
        canvas.drawRect(frame.left, frame.bottom + 1 - photoFrameWidth, frame.left + photoFrameThickness, frame.bottom + 1 - photoFrameThickness, paint);
        canvas.drawRect(frame.left, frame.bottom + 1 - photoFrameThickness, frame.left + photoFrameWidth, frame.bottom + 1, paint);
        //右下
        canvas.drawRect(frame.right - photoFrameThickness, frame.bottom + 1 - photoFrameWidth, frame.right, frame.bottom + 1 - photoFrameThickness, paint);
        canvas.drawRect(frame.right - photoFrameWidth, frame.bottom + 1 - photoFrameThickness, frame.right, frame.bottom + 1, paint);
    }
}
