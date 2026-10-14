package com.example.myapplication;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Expense expense, int position);
    }

    private final List<Expense> expenseList;
    private final OnItemClickListener listener;
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    public ExpenseAdapter(List<Expense> expenseList, OnItemClickListener listener) {
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.tvTitle.setText(expense.getTitle());
        holder.tvAmount.setText(formatter.format(expense.getAmount()));
        holder.tvCategory.setText(expense.getCategory());
        holder.tvDate.setText(expense.getDate());

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

        holder.tvCategoryIcon.setText(icon);

        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(bgColor);
        holder.tvCategoryIcon.setBackground(circleBg);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(expense, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvCategory, tvDate, tvCategoryIcon;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvAmount = itemView.findViewById(R.id.tvItemAmount);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvDate = itemView.findViewById(R.id.tvItemDate);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
        }
    }
}