package com.iezview.autopicviewdemo.vlayout;

/**
 * Created by miqt on 2016/11/17.
 */

public class Point {
    float x, y, z;
    boolean isComp;

    public Point() {
    }

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public boolean isComp() {
        return isComp;
    }

    public void setComp(boolean comp) {
        isComp = comp;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", isComp=" + isComp +
                '}';
    }
}
