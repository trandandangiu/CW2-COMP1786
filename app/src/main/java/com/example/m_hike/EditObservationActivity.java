package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.database.Cursor;

public class EditObservationActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText etObservation, etTime, etComment;
    Button btnUpdate, btnDelete;
    int obsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_observation);

        // 🔹 Khởi tạo DatabaseHelper và liên kết View
        dbHelper = new DatabaseHelper(this);
        etObservation = findViewById(R.id.etObservation);
        etTime = findViewById(R.id.etTime);
        etComment = findViewById(R.id.etComment);
        btnUpdate = findViewById(R.id.btnUpdateObservation);
        btnDelete = findViewById(R.id.btnDeleteObservation);

        // 🔹 Nhận obs_id từ Intent
        obsId = getIntent().getIntExtra("obs_id", -1);

        // 🔹 Gọi hàm load dữ liệu nếu obsId hợp lệ
        if (obsId != -1) {
            loadObservation(obsId);
        } else {
            Toast.makeText(this, "Invalid Observation ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔸 Cập nhật Observation
        btnUpdate.setOnClickListener(v -> {
            String newObs = etObservation.getText().toString().trim();
            String newTime = etTime.getText().toString().trim();
            String newComment = etComment.getText().toString().trim();

            if (newObs.isEmpty() || newTime.isEmpty()) {
                Toast.makeText(this, "Observation and Time are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = dbHelper.updateObservation(obsId, newObs, newTime, newComment);
            Toast.makeText(this, updated ? "✅ Updated successfully!" : "❌ Update failed!", Toast.LENGTH_SHORT).show();
            if (updated) finish();
        });

        // 🔸 Xóa Observation
        btnDelete.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteObservation(obsId);
            Toast.makeText(this, deleted ? "🗑 Deleted successfully!" : "❌ Delete failed!", Toast.LENGTH_SHORT).show();
            if (deleted) finish();
        });
    }

    // 🔹 Hàm riêng để load dữ liệu Observation từ DB
    private void loadObservation(int id) {
        Cursor c = dbHelper.getObservationById(id);
        if (c != null && c.moveToFirst()) {
            etObservation.setText(c.getString(c.getColumnIndexOrThrow("observation")));
            etTime.setText(c.getString(c.getColumnIndexOrThrow("time")));
            etComment.setText(c.getString(c.getColumnIndexOrThrow("comment")));
            c.close();
        } else {
            Toast.makeText(this, "Observation not found!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
