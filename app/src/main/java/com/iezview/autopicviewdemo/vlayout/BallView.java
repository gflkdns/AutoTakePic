package com.iezview.autopicviewdemo.vlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.iezview.autopicviewdemo.AngleIndicater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miqt on 2016/11/17.
 */

public class BallView extends View implements IBallView {
    private Paint paint;

    //<editor-fold desc="...">
    public BallView(Context context) {
        super(context);
        init(null, 0);
    }

    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    //</editor-fold>
    private OnCanPicListener mOnCanPicListener;
    private List<Point> mPoints;
    private boolean isRuning;
    SensorManager mSensorManager;
    Sensor mOrientation;

    private final void init(AttributeSet attrs, int defStyleAttr) {
        mPoints = new ArrayList<>();
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION, true);

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (mSensorManager == null)
            return;
        if (visibility == GONE) {
            //取消重力感应的监听
            mSensorManager.unregisterListener(mSensorEventListener, mOrientation);
        } else {
            mSensorManager.registerListener(mSensorEventListener, mOrientation, SensorManager
                    .SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(pointX, pointY, 20, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //取消重力感应的监听
        mSensorManager.unregisterListener(mSensorEventListener, mOrientation);
    }

    @Override
    public void start() {
        isRuning = true;
    }

    @Override
    public void pause() {
        isRuning = false;
    }

    @Override
    public void stop() {
        isRuning = false;
    }

    @Override
    public void reset() {
        mPoints.clear();
    }

    @Override
    public void remove(Point point) {
        mPoints.remove(point);
    }

    @Override
    public void addPoint(Point point) {
        mPoints.add(point);
    }

    @Override
    public void setPicListener(@NonNull OnCanPicListener listener) {
        mOnCanPicListener = listener;
    }

    private int pointY;
    private int pointX;
    /**
     * 重力感应的监听器，可用来实时得到手机当前的位置信息
     */
    SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[SensorManager.DATA_X];
            float y = sensorEvent.values[SensorManager.DATA_Y];
            float z = sensorEvent.values[SensorManager.DATA_Z];
            //求出经纬度来
            double α = Math.PI * x / 180;
            double β = Math.PI * y / 180;
            //半径是控件的宽度/2
            double R = getWidth() / 2;
            pointY = (int) (R * Math.cos(α) * Math.cos(β));
            pointX = (int) (R * Math.cos(α) * Math.sin(β));
            //  pointY = (int) (R * Math.sin(α));
            postInvalidate();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
}
