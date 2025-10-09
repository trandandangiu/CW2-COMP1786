package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.database.Cursor;
import java.util.ArrayList;

public class SearchHikeActivity extends AppCompatActivity {

    EditText etName, etLocation, etLength, etDate;
    Button btnSearch;
    ListView listView;
    DatabaseHelper dbHelper;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hike);

        etName = findViewById(R.id.etNameSearch);
        etLocation = findViewById(R.id.etLocationSearch);
        etLength = findViewById(R.id.etLengthSearch);
        etDate = findViewById(R.id.etDateSearch);
        btnSearch = findViewById(R.id.btnSearch);
        listView = findViewById(R.id.listViewResults);
        dbHelper = new DatabaseHelper(this);

        btnSearch.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String length = etLength.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        Cursor cursor = dbHelper.searchHikes(name, location, length, date);
        ArrayList<String> results = new ArrayList<>();

        if (cursor.getCount() == 0) {
            results.add("No matching hikes found.");
        } else {
            while (cursor.moveToNext()) {
                String n = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String l = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String d = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                results.add("🏔 " + n + " | " + l + " (" + d + ")");
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        listView.setAdapter(adapter);
    }
}
