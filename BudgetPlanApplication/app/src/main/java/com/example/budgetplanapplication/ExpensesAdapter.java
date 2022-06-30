package com.example.budgetplanapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpensesViewHolder> {
    private ArrayList<com.example.budgetplanapplication.Expenses> mExpenses;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void OnDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExpensesViewHolder extends RecyclerView.ViewHolder {
        public TextView expensesname;
        public Button deleteExpenses;

        public ExpensesViewHolder(View v, final OnItemClickListener listener) {
            super(v);
            expensesname = v.findViewById(R.id.dataExpensesName);
            deleteExpenses = v.findViewById(R.id.deleteExpenses);

            deleteExpenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnDeleteClick(position);
                        }
                    }
                }
            });
        }
    }



    public ExpensesAdapter(ArrayList<com.example.budgetplanapplication.Expenses> expenses) {
        mExpenses = expenses;
    }

    @NonNull
    @Override
    public ExpensesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenses_categories_recycler_view, parent, false);
        ExpensesViewHolder mvh = new ExpensesViewHolder(view, mListener);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpensesViewHolder holder, int position) {
        com.example.budgetplanapplication.Expenses currentItem = mExpenses.get(position);
        holder.expensesname.setText(currentItem.getExpensesname());
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }
}

