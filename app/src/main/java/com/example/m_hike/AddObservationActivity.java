package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddObservationActivity extends AppCompatActivity {

    EditText etObservation, etTime, etComment;
    Button btnSaveObservation;
    DatabaseHelper dbHelper;
    int hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        // 🟢 Khởi tạo View
        etObservation = findViewById(R.id.etObservation);
        etTime = findViewById(R.id.etTime);
        etComment = findViewById(R.id.etComment);
        btnSaveObservation = findViewById(R.id.btnSaveObservation);
        dbHelper = new DatabaseHelper(this);

        // 🟢 Lấy hike_id từ Intent
        hikeId = getIntent().getIntExtra("hike_id", -1);
        if (hikeId == -1) {
            Toast.makeText(this, "Error: no hike selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🕒 Gán mặc định thời gian hiện tại
        etTime.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Calendar.getInstance().getTime()));

        // 🗓 Khi click vào Time → mở Date + Time picker
        etTime.setOnClickListener(v -> showDateTimePicker());

        // 💾 Khi bấm Save
        btnSaveObservation.setOnClickListener(v -> saveObservation());
    }

    // 🗓 Hàm chọn ngày & giờ
    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(
                            this,
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                String formatted = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                        .format(calendar.getTime());
                                etTime.setText(formatted);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePicker.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    // 💾 Lưu observation vào DB
    private void saveObservation() {
        String obs = etObservation.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String comment = etComment.getText().toString().trim();

        if (obs.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = dbHelper.insertObservation(hikeId, obs, time, comment);

        if (inserted) {
            Toast.makeText(this, "✅ Observation added!", Toast.LENGTH_SHORT).show();
            finish(); // trở lại ViewObservationsActivity
        } else {
            Toast.makeText(this, "❌ Failed to add observation!", Toast.LENGTH_SHORT).show();
        }
    }
}
