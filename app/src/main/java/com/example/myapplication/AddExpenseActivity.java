package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText edtTitle, edtAmount, edtNote;
    private Spinner spCategory;
    private TextView tvSelectedDate;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_expense);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_expense_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtTitle = findViewById(R.id.edtTitle);
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        spCategory = findViewById(R.id.spCategory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        TextView btnHeaderBack = findViewById(R.id.btnHeaderBack);
        Button btnSave = findViewById(R.id.btnSave);

        btnHeaderBack.setOnClickListener(v -> finish());

        // Categories Spinner
        String[] categories = {"Ăn uống", "Đi lại", "Học tập", "Giải trí", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spCategory.setAdapter(adapter);

        // Date selection
        updateDateText();
        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvSelectedDate.setText(sdf.format(calendar.getTime()));
    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateText();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void saveExpense() {
        String title = edtTitle.getText() != null ? edtTitle.getText().toString().trim() : "";
        String amountStr = edtAmount.getText() != null ? edtAmount.getText().toString().trim() : "";
        String note = edtNote.getText() != null ? edtNote.getText().toString().trim() : "";

        if (title.isEmpty()) {
            edtTitle.setError("Vui lòng nhập tên khoản chi");
            edtTitle.requestFocus();
            return;
        }

        if (amountStr.isEmpty()) {
            edtAmount.setError("Vui lòng nhập số tiền");
            edtAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                edtAmount.setError("Số tiền phải lớn hơn 0");
                edtAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtAmount.setError("Số tiền không hợp lệ");
            edtAmount.requestFocus();
            return;
        }

        String category = spCategory.getSelectedItem().toString();
        String date = tvSelectedDate.getText().toString();

        String id = UUID.randomUUID().toString();
        Expense expense = new Expense(id, title, amount, category, date, note);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("NEW_EXPENSE", expense);
        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, "Thêm khoản chi thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}