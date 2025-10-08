package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Chuyển thẳng sang AddHikeActivity để test
        Intent intent = new Intent(MainActivity.this, AddHikeActivity.class);
        startActivity(intent);
        finish();
    }
}
