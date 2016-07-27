package com.iezview.autopicviewdemo;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Size;


public class CompareSizeByArea implements java.util.Comparator<Size> {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int compare(Size lhs, Size rhs) {
        // We cast here to ensure the multiplications won't overflow
        return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                (long) rhs.getWidth() * rhs.getHeight());
    }
}
