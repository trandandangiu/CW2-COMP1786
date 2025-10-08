package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddObservationActivity extends AppCompatActivity {

    private EditText etObservation, etComment;
    private TextView tvTime;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int hikeId;
    private String hikeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        initializeViews();
        getIntentData();
        setupCurrentTime();
        setupButtonListener();
    }

    private void initializeViews() {
        etObservation = findViewById(R.id.etObservation);
        etComment = findViewById(R.id.etComment);
        tvTime = findViewById(R.id.tvTime);
        btnSave = findViewById(R.id.btnSaveObservation);
        dbHelper = new DatabaseHelper(this); // Sửa: bỏ "context:"
    }

    private void getIntentData() {
        hikeId = getIntent().getIntExtra("HIKE_ID", -1);
        hikeName = getIntent().getStringExtra("HIKE_NAME");

        if (hikeId == -1) {
            Toast.makeText(this, "Error: No hike selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị tên hike nếu có
        if (hikeName != null && !hikeName.isEmpty()) {
            setTitle("Add Observation for: " + hikeName);
        }
    }

    private void setupCurrentTime() {
        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        tvTime.setText(currentTime);

        // Cho phép chỉnh sửa thời gian
        tvTime.setOnClickListener(v -> showTimeEditDialog());
    }

    private void setupButtonListener() {
        btnSave.setOnClickListener(v -> saveObservation());
    }

    private void saveObservation() {
        String observation = etObservation.getText().toString().trim();
        String time = tvTime.getText().toString().trim();
        String comment = etComment.getText().toString().trim();

        // Validation
        if (observation.isEmpty()) {
            etObservation.setError("Observation is required");
            etObservation.requestFocus();
            return;
        }

        if (time.isEmpty()) {
            tvTime.setError("Time is required");
            return;
        }

        // Insert vào database
        boolean inserted = dbHelper.insertObservation(hikeId, observation, time, comment);
        if (inserted) {
            Toast.makeText(this, "✅ Observation saved successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "❌ Failed to save observation!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimeEditDialog() {

        Toast.makeText(this, "Tap to change date/time", Toast.LENGTH_SHORT).show();
        // TODO: Implement date/time picker dialog
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}