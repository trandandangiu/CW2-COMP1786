package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.database.Cursor;
import android.content.Intent;

public class ViewObservationsActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ListView listObservations;
    TextView tvTitle;
    Button btnAddObservation; // 🟢 thêm biến nút
    int hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_observations);

        dbHelper = new DatabaseHelper(this);
        listObservations = findViewById(R.id.listObservations);
        tvTitle = findViewById(R.id.tvTitle);
        btnAddObservation = findViewById(R.id.btnAddObservation); // 🟢 ánh xạ nút

        // 🟢 Lấy hike_id từ Intent
        hikeId = getIntent().getIntExtra("hike_id", -1);

        if (hikeId != -1) {
            loadObservations();
        }

        // 🟢 Khi nhấn nút Add → mở AddObservationActivity
        btnAddObservation.setOnClickListener(v -> {
            Intent i = new Intent(ViewObservationsActivity.this, AddObservationActivity.class);
            i.putExtra("hike_id", hikeId);
            startActivity(i);
        });

        // 🟢 Khi nhấn vào 1 dòng Observation → mở EditObservationActivity
        listObservations.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int obsId = cursor.getInt(cursor.getColumnIndexOrThrow("_id")); // ✅ _id mới đúng

            Intent i = new Intent(this, EditObservationActivity.class);
            i.putExtra("obs_id", obsId);
            startActivity(i);
        });

    }

    // 🟢 Load danh sách observation
    private void loadObservations() {
        Cursor dataCursor = dbHelper.getObservationsByHike(hikeId);

        if (dataCursor != null && dataCursor.getCount() > 0) {
            String[] from = new String[]{"time", "observation", "comment"};
            int[] to = new int[]{R.id.tvTime, R.id.tvObsText, R.id.tvComment};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.item_observation,
                    dataCursor,
                    from,
                    to,
                    0
            );
            listObservations.setAdapter(adapter);
        } else {
            // 🟡 Nếu chưa có observation, hiển thị thông báo
            Toast.makeText(this, "No observations yet.", Toast.LENGTH_SHORT).show();
        }
    }

    // 🟢 Khi quay lại màn hình này, load lại danh sách
    @Override
    protected void onResume() {
        super.onResume();
        loadObservations();
    }
}
