package com.sung.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private MnScaleBar scale_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scale_bar = findViewById(R.id.scale_bar);

        scale_bar.setOnScrollListener(new MnScaleBar.OnScrollListener() {
            @Override
            public void onScrollScale(int scale) {
            }
        });
        scale_bar.smoothScrollTo(12);
    }
}
