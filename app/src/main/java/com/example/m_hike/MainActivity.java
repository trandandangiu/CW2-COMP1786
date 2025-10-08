package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.database.Cursor;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ListView listView;
    Button btnAdd, btnReset;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 Khởi tạo database và các view
        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);
        btnAdd = findViewById(R.id.btnAdd);
        btnReset = findViewById(R.id.btnReset);

        // 🔹 Load danh sách ban đầu
        loadData();

        // ➕ Nút Add Hike → mở AddHikeActivity
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddHikeActivity.class));
        });

        // 🗑 Nút Reset Database → xóa hết dữ liệu
        btnReset.setOnClickListener(v -> {
            dbHelper.resetDatabase();
            loadData();
            Toast.makeText(this, "🗑 Database reset!", Toast.LENGTH_SHORT).show();
        });

        // 👆 Nhấn 1 hike → mở AddObservationActivity
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = dbHelper.getAllHikes();
            if (cursor.moveToPosition(position)) {
                int hikeId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String hikeName = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                Intent intent = new Intent(MainActivity.this, AddObservationActivity.class);
                intent.putExtra("HIKE_ID", hikeId);
                intent.putExtra("HIKE_NAME", hikeName);
                startActivity(intent);
            }
        });

        // ✋ Giữ lâu để xóa 1 hike
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Cursor cursor = dbHelper.getAllHikes();
            if (cursor.moveToPosition(position)) {
                int hikeId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                dbHelper.deleteHike(hikeId);
                loadData();
                Toast.makeText(this, "❌ Hike deleted!", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    // 📋 Hàm load dữ liệu hiển thị danh sách hikes
    private void loadData() {
        Cursor cursor = dbHelper.getAllHikes();
        java.util.ArrayList<String> list = new java.util.ArrayList<>();

        if (cursor.getCount() == 0) {
            list.add("No hikes recorded yet.");
        } else {
            while (cursor.moveToNext()) {
                int hikeId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                // 🟣 Gợi ý nâng cao: hiển thị thêm số lượng observations
                int obsCount = dbHelper.getObservationCount(hikeId);

                list.add("🏔 " + name + " | " + location + " (" + date + ") — "
                        + obsCount + " observations");
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

    // 🔁 Khi quay lại màn hình chính → reload lại danh sách
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
