package com.example.budgetplanapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private ArrayList<UserExpenses> usrExpenses;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        public TextView expensescategories, expensesamount, expensesdate, expensesdescription, expensesproductdescription;

        public HomeViewHolder(View v, final OnItemClickListener listener) {
            super(v);
           expensescategories = v.findViewById(R.id.categories);
           expensesamount = v.findViewById(R.id.money);
           expensesdate = v.findViewById(R.id.date);
           expensesdescription = v.findViewById(R.id.description);
           expensesproductdescription = v.findViewById(R.id.product_des);
        }
    }



    public HomeAdapter(ArrayList<UserExpenses> userExpenses) {
        usrExpenses = userExpenses;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homefragment_recyclerview, parent, false);
        HomeViewHolder mvh = new HomeViewHolder(view, mListener);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        UserExpenses currentItem = usrExpenses.get(position);
     holder.expensescategories.setText(currentItem.getExpensescategories());
        holder.expensesamount.setText(currentItem.getExpensesamount());
        holder.expensesdate.setText(currentItem.getExpensesdate());
        holder.expensesdescription.setText(currentItem.getExpensesdescription());
        holder.expensesproductdescription.setText(currentItem.getProductdescription());

    }

    @Override
    public int getItemCount() {
        return usrExpenses.size();
    }
}

