package com.iezview.autopicviewdemo.circleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.iezview.autopicviewdemo.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        ListView listView = (ListView) findViewById(R.id.list);
        String[] data = {
                "123", "456", "789",
                "123", "456", "789",
                "123", "456", "789",
                "123", "456", "789",
                "123", "456", "789",
        };
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        listView.setTranslationZ(-0.5f);
        float scale = getResources().getDisplayMetrics().density;
        listView.setCameraDistance(1280 * scale);
        listView.setCameraDistance(1280 * scale);
        listView.setPivotX(1000);
        listView.setPivotY(100);
        listView.setRotationY(10);
        listView.setRotationX(10);
        listView.setRotation(10);
    }
}
