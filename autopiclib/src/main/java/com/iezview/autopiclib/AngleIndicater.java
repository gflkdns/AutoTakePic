package com.iezview.autopiclib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * 角度指示器
 */
public class AngleIndicater extends View {
    private int mBallColor = Color.WHITE; //不安分的小球的颜色
    private int mIndicaterColor = Color.WHITE; // 框框的颜色
    private int mBallSelectColor = Color.WHITE; //不安分的小球的颜色
    private int mIndiSelectcaterColor = Color.WHITE; // 框框的颜色
    private int mWidth;//控件宽高
    private int mHeight;
    private float y;//俯仰角，来自传感器
    private int selectIndex = -1;//当前小球经过的index
    //指示范围数组
    private List<Indicater> mIndicaters;
    /**
     * 重力感应的监听器，可用来实时得到手机当前的位置信息
     */
    SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[SensorManager.DATA_X];
            y = sensorEvent.values[SensorManager.DATA_Y];
            float z = sensorEvent.values[SensorManager.DATA_Z];
            boolean flog = false;
            for (Indicater indicater : mIndicaters) {
                if (y <= indicater.getEnd() && y >= indicater.getStart()) {
                    selectIndex = mIndicaters.indexOf(indicater);
                    flog = true;
                }
            }
            if (!flog) {
                selectIndex = -1;
            }
            postInvalidate();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Paint ballPaint, indicaterPaint;

    public AngleIndicater(Context context) {
        super(context);
        init(null, 0);
    }

    public AngleIndicater(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AngleIndicater(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mIndicaters = new ArrayList<>();
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.AngleIndicater, defStyle, 0);
        mBallColor = a.getColor(
                R.styleable.AngleIndicater_ballColor, Color.WHITE);
        mIndicaterColor = a.getColor(
                R.styleable.AngleIndicater_indicaterColor, Color.WHITE);
        mBallSelectColor = a.getColor(
                R.styleable.AngleIndicater_ballSelectColor, Color.WHITE);
        mIndiSelectcaterColor = a.getColor(
                R.styleable.AngleIndicater_indicaterSelectColor, Color.WHITE);
        a.recycle();

        mSensorManager = (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        //初始化画笔
        {
            ballPaint = new Paint();
            ballPaint.setColor(mBallColor);
            ballPaint.setStyle(Paint.Style.FILL);

            indicaterPaint = new Paint();
            indicaterPaint.setColor(mIndicaterColor);
            indicaterPaint.setAntiAlias(true);
            indicaterPaint.setStrokeWidth(3);
            indicaterPaint.setStyle(Paint.Style.STROKE);
        }
    }

    /**
     * 添加一个范围
     *
     * @param indicater
     */
    public void addIndicater(Indicater indicater) {
        mIndicaters.add(indicater);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        setMeasuredDimension(widthSpec, heightSpec);
        mWidth = widthSpec;
        mHeight = heightSpec;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = mWidth / 2;
        float cy = mHeight * (1 + 1 / 90f * y);
        for (Indicater indicater : mIndicaters) {
            float left = 0;
            float top = mHeight * (1 + 1 / 90f * indicater.getStart());
            float right = mWidth;
            float bottom = mHeight * (1 + 1 / 90f * indicater.getEnd());
            if (selectIndex == mIndicaters.indexOf(indicater))
                indicaterPaint.setColor(mIndiSelectcaterColor);
            else
                indicaterPaint.setColor(mIndicaterColor);
            canvas.drawRoundRect(left, top, right, bottom, cx, cx, indicaterPaint);
        }
        if (selectIndex != -1)
            ballPaint.setColor(mBallSelectColor);
        else
            ballPaint.setColor(mBallColor);
        canvas.drawCircle(cx, cy, cx, ballPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //取消重力感应的监听
        mSensorManager.unregisterListener(mSensorEventListener, mSensor);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (mSensorManager == null)
            return;
        if (visibility == GONE) {
            //取消重力感应的监听
            mSensorManager.unregisterListener(mSensorEventListener, mSensor);
        } else {
            mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager
                    .SENSOR_DELAY_UI);
        }
    }

    public static class Indicater {
        float start;
        float end;

        public Indicater(
                @FloatRange(from = -90f, to = 0f) float start,
                @FloatRange(from = -90f, to = 0f) float end) {
            this.start = start;
            this.end = end;
        }

        public float getStart() {
            return start;
        }

        public float getEnd() {
            return end;
        }
    }
}
