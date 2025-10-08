package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.app.DatePickerDialog;
import java.util.Calendar;

public class AddHikeActivity extends AppCompatActivity {

    EditText etName, etLocation, etDate, etLength, etDescription, etElevation;
    RadioGroup rgParking;
    Spinner spDifficulty, spWeather;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hike);

        // Liên kết View
        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etLength = findViewById(R.id.etLength);
        etDescription = findViewById(R.id.etDescription);
        etElevation = findViewById(R.id.etElevation);
        rgParking = findViewById(R.id.rgParking);
        spDifficulty = findViewById(R.id.spDifficulty);
        spWeather = findViewById(R.id.spWeather);
        btnSave = findViewById(R.id.btnSave);

        // Spinner Difficulty
        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Easy", "Moderate", "Hard"});
        spDifficulty.setAdapter(diffAdapter);

        // Spinner Weather
        ArrayAdapter<String> weatherAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Sunny", "Cloudy", "Rainy", "Snowy"});
        spWeather.setAdapter(weatherAdapter);

        // Date Picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Button Save
        btnSave.setOnClickListener(v -> validateAndShow());
    }

    // Hiển thị lịch chọn ngày
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

    // Kiểm tra dữ liệu và hiển thị thông tin xác nhận
    private void validateAndShow() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String length = etLength.getText().toString().trim();
        int selectedParking = rgParking.getCheckedRadioButtonId();

        if (name.isEmpty() || location.isEmpty() || date.isEmpty()
                || length.isEmpty() || selectedParking == -1) {
            Toast.makeText(this, "⚠ Please fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu
        RadioButton rb = findViewById(selectedParking);
        String parking = rb.getText().toString();
        String difficulty = spDifficulty.getSelectedItem().toString();
        String weather = spWeather.getSelectedItem().toString();
        String desc = etDescription.getText().toString();
        String elevation = etElevation.getText().toString();

        // Hiển thị xác nhận
        String summary = "✅ Hike Added:\n\n" +
                "Name: " + name + "\n" +
                "Location: " + location + "\n" +
                "Date: " + date + "\n" +
                "Parking: " + parking + "\n" +
                "Length: " + length + " km\n" +
                "Difficulty: " + difficulty + "\n" +
                "Weather: " + weather + "\n" +
                "Elevation: " + elevation + " m\n" +
                "Description: " + desc;

        new android.app.AlertDialog.Builder(this)
                .setTitle("Confirm Details")
                .setMessage(summary)
                .setPositiveButton("OK", null)
                .setNegativeButton("Edit", null)
                .show();
    }
}
