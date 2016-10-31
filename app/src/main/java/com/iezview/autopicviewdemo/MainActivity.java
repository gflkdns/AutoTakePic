package com.iezview.autopicviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class MainActivity extends Activity {
    private static final String TAG = "myupload";
    @ViewInject(R.id.angleindicater)
    AngleIndicater angleIndicater;
    private MySurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        x.Ext.init(getApplication());
        x.view().inject(this);
        angleIndicater.addIndicater(new AngleIndicater.Indicater(-50, -40));
    }
}
