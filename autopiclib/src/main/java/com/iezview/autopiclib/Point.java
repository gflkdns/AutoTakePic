package com.iezview.autopiclib;

/**
 * Created by 工作学习 on 2016/7/13.
 */
public class Point {
    public boolean ispic;
    private final float start;
    private final float end;

    private Point(float start, float end) {
        this.start = start;
        this.end = end;
    }

    /**
     * 根据给定的拍摄夹角和允许误差偏移的角度，得到拍摄区间
     *
     * @param angle 每两次拍摄的夹角
     * @return 偏移量
     */
    public static Point[] initPoint(float angle, float Offset) {
        Point[] result = new Point[360 / (int) angle];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Point(angle * i, angle * i + Offset);
        }
        return result;
    }

    public float getStart() {
        return start;
    }

    public float getEnd() {
        return end;
    }

    public boolean ispic() {
        return ispic;
    }

    public void setIspic(boolean ispic) {
        this.ispic = ispic;
    }
}
