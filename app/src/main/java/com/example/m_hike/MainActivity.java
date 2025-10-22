package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.database.Cursor;
import android.content.Intent;
import android.app.AlertDialog;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ListView listView;
    Button btnAdd, btnReset, btnSearch;
    ArrayAdapter<String> adapter;
    ArrayList<Integer> hikeIds; // dùng để nhớ ID từng hike khi click

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 Khởi tạo database và view
        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);
        btnAdd = findViewById(R.id.btnAdd);
        btnReset = findViewById(R.id.btnReset);
        btnSearch = findViewById(R.id.btnSearch);

        // 🔹 Load danh sách ban đầu
        loadData();

        // ➕ Add hike
        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddHikeActivity.class)));

        // 🔍 Search hike
        btnSearch.setOnClickListener(v ->
                startActivity(new Intent(this, SearchHikeActivity.class)));

        // 🗑 Reset DB
        btnReset.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm reset")
                    .setMessage("Are you sure you want to delete all hikes and observations?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.resetDatabase();
                        loadData();
                        Toast.makeText(this, "🗑 Database reset!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 👆 Click 1 hike → mở danh sách observations
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position < hikeIds.size()) { // tránh lỗi nếu list chỉ có dòng "No hikes recorded yet."
                int hikeId = hikeIds.get(position);
                Intent i = new Intent(MainActivity.this, ViewObservationsActivity.class);
                i.putExtra("hike_id", hikeId);
                startActivity(i);
            }
        });

        // ✋ Giữ lâu để xóa 1 hike
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position < hikeIds.size()) {
                int hikeId = hikeIds.get(position);
                new AlertDialog.Builder(this)
                        .setTitle("Delete Hike")
                        .setMessage("Are you sure you want to delete this hike?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            dbHelper.deleteHike(hikeId);
                            loadData();
                            Toast.makeText(this, "❌ Hike deleted!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            return true;
        });
    }

    // 📋 Hàm load dữ liệu hiển thị danh sách hikes
    private void loadData() {
        Cursor cursor = dbHelper.getAllHikes();
        ArrayList<String> list = new ArrayList<>();
        hikeIds = new ArrayList<>();

        if (cursor.getCount() == 0) {
            list.add("No hikes recorded yet.");
        } else {
            while (cursor.moveToNext()) {
                int hikeId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                int obsCount = dbHelper.getObservationCount(hikeId);

                hikeIds.add(hikeId);
                list.add("🏔 " + name + " | " + location + " (" + date + ") — " +
                        obsCount + " observations");
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        cursor.close();
    }

    // 🔁 Reload khi quay lại
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
