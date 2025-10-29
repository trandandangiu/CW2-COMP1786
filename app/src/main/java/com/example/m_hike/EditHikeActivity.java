package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.app.DatePickerDialog;
import android.database.Cursor;
import java.util.Calendar;

public class EditHikeActivity extends AppCompatActivity {

    EditText etName, etLocation, etDate, etLength, etDescription, etElevation;
    RadioGroup rgParking;
    Spinner spDifficulty, spWeather;
    Button btnUpdate;
    DatabaseHelper dbHelper;
    int hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hike);

        // Liên kết view
        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etLength = findViewById(R.id.etLength);
        etDescription = findViewById(R.id.etDescription);
        etElevation = findViewById(R.id.etElevation);
        rgParking = findViewById(R.id.rgParking);
        spDifficulty = findViewById(R.id.spDifficulty);
        spWeather = findViewById(R.id.spWeather);
        btnUpdate = findViewById(R.id.btnUpdate);

        dbHelper = new DatabaseHelper(this);

        // Lấy ID từ Intent
        hikeId = getIntent().getIntExtra("HIKE_ID", -1);
        if (hikeId == -1) {
            Toast.makeText(this, "Invalid Hike ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị dữ liệu cũ
        loadHikeData(hikeId);

        // Chọn ngày
        etDate.setOnClickListener(v -> showDatePicker());

        // Nút Update
        btnUpdate.setOnClickListener(v -> updateHike());
    }

    private void loadHikeData(int id) {
        Cursor cursor = dbHelper.getHikeById(id);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            etLocation.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LOCATION)));
            etDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE)));
            etLength.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LENGTH)));
            etDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)));
            etElevation.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ELEVATION)));
            cursor.close();
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, y, m, d) -> etDate.setText(d + "/" + (m + 1) + "/" + y),
                year, month, day);
        datePicker.show();
    }

    private void updateHike() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String lengthStr = etLength.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String elevationStr = etElevation.getText().toString().trim();
        double length;
        int elevation = 0;

        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || lengthStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            length = Double.parseDouble(lengthStr);
            if (!elevationStr.isEmpty()) elevation = Integer.parseInt(elevationStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updated = dbHelper.updateHike(
                hikeId, name, location, date, "Yes", length, "Easy", desc, "Sunny", elevation
        );

        if (updated) {
            Toast.makeText(this, "✅ Hike updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "❌ Update failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
