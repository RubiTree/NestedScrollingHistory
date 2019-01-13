package com.rubitree.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rubitree.demo.nestedinnested.NestedInNestedActivity;
import com.rubitree.demo.suspendedlayout.SuspendedLayoutActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.vToNestedInNested).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NestedInNestedActivity.class));
            }
        });

        findViewById(R.id.vToSuspendedLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SuspendedLayoutActivity.class));
            }
        });
    }
}
