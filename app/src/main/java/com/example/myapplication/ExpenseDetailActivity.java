package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class ExpenseDetailActivity extends AppCompatActivity {

    private int expensePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expense_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.expense_detail_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView btnHeaderBack = findViewById(R.id.btnHeaderBack);
        TextView btnHeaderDelete = findViewById(R.id.btnHeaderDelete);
        TextView tvDetailCategoryIcon = findViewById(R.id.tvDetailCategoryIcon);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvCategory = findViewById(R.id.tvDetailCategory);
        TextView tvAmount = findViewById(R.id.tvDetailAmount);
        TextView tvDate = findViewById(R.id.tvDetailDate);
        TextView tvNote = findViewById(R.id.tvDetailNote);
        Button btnDelete = findViewById(R.id.btnDelete);

        btnHeaderBack.setOnClickListener(v -> finish());

        DecimalFormat formatter = new DecimalFormat("#,### đ");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EXPENSE_DATA")) {
            Expense expense = (Expense) intent.getSerializableExtra("EXPENSE_DATA");
            expensePosition = intent.getIntExtra("EXPENSE_POSITION", -1);

            if (expense != null) {
                tvTitle.setText(expense.getTitle());
                tvAmount.setText(formatter.format(expense.getAmount()));
                tvCategory.setText(expense.getCategory());
                tvDate.setText("📅 " + expense.getDate());

                if (expense.getNote() != null && !expense.getNote().isEmpty()) {
                    tvNote.setText(expense.getNote());
                } else {
                    tvNote.setText("Không có ghi chú");
                }

                String cat = expense.getCategory();
                if (cat == null) cat = "Khác";

                String icon = "💬";
                int bgColor = Color.parseColor("#F3F4F6");

                switch (cat) {
                    case "Ăn uống":
                        icon = "🍽️";
                        bgColor = Color.parseColor("#FEE2E2");
                        break;
                    case "Đi lại":
                        icon = "🚗";
                        bgColor = Color.parseColor("#E0F2FE");
                        break;
                    case "Học tập":
                        icon = "📖";
                        bgColor = Color.parseColor("#DCFCE7");
                        break;
                    case "Giải trí":
                        icon = "🎮";
                        bgColor = Color.parseColor("#F3E8FF");
                        break;
                }

                tvDetailCategoryIcon.setText(icon);

                GradientDrawable circleBg = new GradientDrawable();
                circleBg.setShape(GradientDrawable.OVAL);
                circleBg.setColor(bgColor);
                tvDetailCategoryIcon.setBackground(circleBg);
            }
        }

        Runnable deleteAction = () -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("DELETE_POSITION", expensePosition);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Đã xóa khoản chi!", Toast.LENGTH_SHORT).show();
            finish();
        };

        btnDelete.setOnClickListener(v -> deleteAction.run());
        btnHeaderDelete.setOnClickListener(v -> deleteAction.run());
    }
}