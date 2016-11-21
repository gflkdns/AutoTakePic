package com.iezview.autopicviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class AngleTriggerView extends View {

    //角度 默认15
    private float angle = 15;
    private float offset = 4;
    private Point[] OUTPOINTS;
    //这个值大于0小于1
    private static final float smile_circle_radius = 1.5f;
    private boolean idcanpic;
    private SensorManager mSensorManager;
    //view的高宽
    private int mViewWidth;
    private int mViewHeight;
    //圆的画笔宽度
    private float paintwidth;
    //圆的画笔
    private Paint circlePaint;
    //外圈坐标点的画笔
    private Paint outPointPaint;
    //十字的画笔
    private Paint linePaint;
    //中间的实心圆
    private Paint sxPaint;
    private float mChaildcx;
    private float mChaildcy;
    private AngleTriggerView.TakePicListener mPicListener = new AngleTriggerView
            .TakePicListener() {
        @Override
        public boolean canTakePic(float angle) {
            return false;
        }

        @Override
        public void yourPhonePerfect() {

        }

        @Override
        public void placeAdjustYourPhone() {

        }
    };
    private int circle_index;
    private Sensor mSensor;
    private float x;
    private long lasttime;
    /**
     * 重力感应的监听器，可用来实时得到手机当前的位置信息
     */
    SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            x = sensorEvent.values[SensorManager.DATA_X];
            float y = sensorEvent.values[SensorManager.DATA_Y];
            float z = sensorEvent.values[SensorManager.DATA_Z];

            setRotation(-x - 180);
            setRotationX(-y);
            setRotationY(z);

            mChaildcy = mViewHeight / 2 + (mViewHeight / 2 / 180) * y;
            mChaildcx = mViewWidth / 2 + (mViewWidth / 2 / 180) * z;
            if (mPicListener != null) {
                //手机与地平面夹角 外圈  70 内圈 45 偏移量 上下偏移量±5
                if ((endangle) > y && y > (startangle)) {
                    //在可拍摄区间内，且，没有拍摄过
                    mPicListener.yourPhonePerfect();
                    circle_index = 2;
                    for (int i = 0; i < OUTPOINTS.length; i++) {
                        if (x < OUTPOINTS[i].getEnd() && x > OUTPOINTS[i].getStart() //角度合适
                                && !OUTPOINTS[i].ispic()) {                            //没有拍过
                            if (idcanpic) {
                                if (System.currentTimeMillis() - lasttime > 1000) {
                                    OUTPOINTS[i].setIspic(  mPicListener.canTakePic(x));
                                    float a=OUTPOINTS[i].getEnd();
                                    float b=OUTPOINTS[i].getStart();
                                    a=a+b;
                                    lasttime = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                } else {
                    mPicListener.placeAdjustYourPhone();
                    circle_index = 0;
                }
            }
            postInvalidate();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
    //俯仰角的范围
    private float startangle;
    private float endangle;

    public AngleTriggerView(Context context) {
        this(context, null, 0);
    }

    public AngleTriggerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleTriggerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAutoPic(attrs, defStyleAttr);
        //得到SensorManager对象
        mSensorManager = (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager
                .SENSOR_DELAY_UI);
        //注册监听器
        //  mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor
        // (Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    /**
     * 初始化画笔
     */
    private void initAutoPic(AttributeSet attrs, int defStyleAttr) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.AngleTriggerView, defStyleAttr, 0);
        angle = a.getFloat(
                R.styleable.AngleTriggerView_angle, angle);
        offset = a.getFloat(
                R.styleable.AngleTriggerView_offset, offset);
        startangle = a.getFloat(
                R.styleable.AngleTriggerView_startangle, 0);
        endangle = a.getFloat(
                R.styleable.AngleTriggerView_endangle, 0);
        a.recycle();
        OUTPOINTS = Point.initPoint(angle, offset);

        circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#f7f7f7"));
        circlePaint.setStyle(Paint.Style.STROKE);
        paintwidth = 1;
        circlePaint.setStrokeWidth(paintwidth);

        outPointPaint = new Paint();
        outPointPaint.setColor(Color.RED);
        outPointPaint.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setColor(Color.YELLOW);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(5);

        sxPaint = new Paint();
        sxPaint.setColor(Color.parseColor("#55000000"));
        sxPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画十字
//        {
//            //小点1
//            canvas.drawCircle(mViewWidth / 2, mViewHeight / 4, 12, linePaint);
//            //小点2
//            canvas.drawCircle(mViewWidth / 2, mViewHeight / 8 * 3, 8, linePaint);
//            //小点3
//            canvas.drawCircle(mViewWidth / 2, mViewHeight / 2, 12, linePaint);
//            //横线
//            canvas.drawLine(mViewWidth / 4, mViewHeight / 2, mViewWidth / 4 * 3, mViewHeight /
// 2, linePaint);
//            //竖线
//            canvas.drawLine(mViewWidth / 2, mViewHeight / 4, mViewWidth / 2, mViewHeight / 4 *
// 3, linePaint);
//        }
        float pointradius = paintwidth * 10;
        //画一个透明的实心圆
//        {
//            float sxRadius = ((mViewHeight > mViewWidth ? mViewWidth : mViewHeight) -
// pointradius * 2) / 16;
//            canvas.drawCircle(mChaildcx, mChaildcy, sxRadius, sxPaint);
//        }
        //画一个圆
        float cx;
        float cy;
        float radius;
        {
            cx = mViewWidth / 2;
            cy = mViewHeight / 2;
            radius = ((mViewHeight > mViewWidth ? mViewWidth : mViewHeight) - pointradius * 2) / 2;
            canvas.drawCircle(cx, cy, radius, circlePaint);
        }
        //画其他的点
        {
            for (int i = 0; i < OUTPOINTS.length; i++) {
                if (OUTPOINTS[i].ispic()) {
                    outPointPaint.setColor(Color.GREEN);
                } else {
                    outPointPaint.setColor(Color.RED);
                }
                canvas.save();
                //画一个圆上的点
                canvas.rotate(angle * i, cx, cy);
                canvas.drawCircle(cx, pointradius, pointradius, outPointPaint);
                canvas.restore();
            }
        }
        {
            canvas.save();
            canvas.rotate(x, cx, cy);
            //画一个不安分的小球

            switch (circle_index) {
                case 0: {
                    //not draw
                    break;
                }
                case 1: {
                    // draw in
                    canvas.drawCircle(mViewWidth / 2, mViewHeight / 2 - (radius /
                            smile_circle_radius), pointradius, linePaint);
                    break;
                }
                case 2: {
                    // draw out
                    canvas.drawCircle(mViewWidth / 2, mViewHeight / 2 - radius, pointradius,
                            linePaint);
                    break;
                }
            }

            canvas.restore();
        }
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

    public void resetPoint() {
        for (Point point : OUTPOINTS) {
            point.setIspic(false);
        }
    }

    public void pause() {
        idcanpic = false;
    }

    public void start() {
        idcanpic = true;
    }

    public void setTakePicListener(AngleTriggerView.TakePicListener takePicListener) {
        mPicListener = takePicListener;
    }

    interface TakePicListener {
        /**
         * @param angle 角度
         */
        boolean canTakePic(float angle);

        void yourPhonePerfect();

        void placeAdjustYourPhone();
    }
}
