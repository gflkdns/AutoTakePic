package com.iezview.autopicviewdemo.vlayout;


/**
 * Created by miqt on 2016/11/17.
 */

public interface IBallView {

    void start();

    void pause();

    void stop();

    void reset();

    void remove(Point point);

    void addPoint(Point point);

    void setPicListener(OnCanPicListener listener);

    interface OnCanPicListener {
        public boolean canTakePic();
    }
}