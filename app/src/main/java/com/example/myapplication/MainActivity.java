package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalAmount;
    private ExpenseAdapter adapter;
    private final List<Expense> expenseList = new ArrayList<>();
    private final List<Expense> displayedList = new ArrayList<>();
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    private final ActivityResultLauncher<Intent> addExpenseLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Expense newExpense = (Expense) result.getData().getSerializableExtra("NEW_EXPENSE");
                    if (newExpense != null) {
                        expenseList.add(0, newExpense);
                        filterList("");
                        updateTotalAmount();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> detailExpenseLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    int deletePosition = result.getData().getIntExtra("DELETE_POSITION", -1);
                    if (deletePosition >= 0 && deletePosition < displayedList.size()) {
                        Expense target = displayedList.get(deletePosition);
                        expenseList.remove(target);
                        displayedList.remove(deletePosition);
                        adapter.notifyItemRemoved(deletePosition);
                        updateTotalAmount();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        EditText edtSearch = findViewById(R.id.edtSearch);
        RecyclerView rvExpenses = findViewById(R.id.rvExpenses);
        FloatingActionButton btnAddExpense = findViewById(R.id.btnAddExpense);

        initSampleData();
        filterList("");

        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseAdapter(displayedList, (expense, position) -> {
            Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
            intent.putExtra("EXPENSE_DATA", expense);
            intent.putExtra("EXPENSE_POSITION", position);
            detailExpenseLauncher.launch(intent);
        });
        rvExpenses.setAdapter(adapter);

        updateTotalAmount();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            addExpenseLauncher.launch(intent);
        });
    }

    private void initSampleData() {
        if (expenseList.isEmpty()) {
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            expenseList.add(new Expense(UUID.randomUUID().toString(), "Ăn sáng", 30000, "Ăn uống", currentDate, "Phở bò"));
            expenseList.add(new Expense(UUID.randomUUID().toString(), "Đổ xăng", 50000, "Đi lại", currentDate, "Xăng RON 95"));
            expenseList.add(new Expense(UUID.randomUUID().toString(), "Mua sách", 120000, "Học tập", currentDate, "Sách Lập trình Android"));
        }
    }

    private void filterList(String query) {
        displayedList.clear();
        if (query.trim().isEmpty()) {
            displayedList.addAll(expenseList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Expense item : expenseList) {
                if (item.getTitle().toLowerCase().contains(lowerQuery) ||
                    item.getCategory().toLowerCase().contains(lowerQuery)) {
                    displayedList.add(item);
                }
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void updateTotalAmount() {
        double total = 0;
        for (Expense expense : expenseList) {
            total += expense.getAmount();
        }
        tvTotalAmount.setText(formatter.format(total));
    }
}