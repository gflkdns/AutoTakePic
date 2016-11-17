package com.iezview.autopicviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 用来测试的activity，主要用来看看自定义view的效果
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        AngleTriggerView view1 = (AngleTriggerView) findViewById(R.id.view1);
        AngleTriggerView view2 = (AngleTriggerView) findViewById(R.id.view2);
        AngleTriggerView view3 = (AngleTriggerView) findViewById(R.id.view3);

    }
}
