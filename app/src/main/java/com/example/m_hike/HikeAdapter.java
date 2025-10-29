package com.example.m_hike;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class HikeAdapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;

    public HikeAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        cursor.moveToPosition(position);
        return cursor;
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_hike, parent, false);
        }

        TextView tvHikeInfo = convertView.findViewById(R.id.tvHikeInfo);
        TextView tvObservationCount = convertView.findViewById(R.id.tvObservationCount);
        Button btnEdit = convertView.findViewById(R.id.btnEditHike);

        cursor.moveToPosition(position);
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LOCATION));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));

        // Hiển thị thông tin hike
        tvHikeInfo.setText("🏞 " + name + " | " + location + " (" + date + ")");
        tvObservationCount.setText("Observations: " + new DatabaseHelper(context).getObservationCount(id));

        // 🔹 Xử lý nút Edit
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditHikeActivity.class);
            intent.putExtra("HIKE_ID", id);
            context.startActivity(intent);
        });

        return convertView;
    }
}
