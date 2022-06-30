package com.example.budgetplanapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SavingAdapter extends RecyclerView.Adapter<SavingAdapter.SavingViewHolder> {
    private ArrayList<Saving> mSaving;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {

        void OnDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class SavingViewHolder extends RecyclerView.ViewHolder {
        public TextView targetDate, savingName, amountSaving, savingpermonth, savingtargetmonth, cumulativesaving;
        public Button deleteSaving;

        public SavingViewHolder(View v, final OnItemClickListener listener) {
            super(v);
            savingName = v.findViewById(R.id.dataSavingName);
            amountSaving = v.findViewById(R.id.dataAmountSaving);
            deleteSaving = v.findViewById(R.id.deleteSaving);
            targetDate = v.findViewById(R.id.dataSavingTargetDate);
            savingpermonth = v.findViewById(R.id.dataSavingPerMonth);
            savingtargetmonth = v.findViewById(R.id.dataSavingTargetMonth);
            cumulativesaving = v.findViewById(R.id.dataCumulativeSaving);



            deleteSaving.setOnClickListener(new View.OnClickListener() {
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



        public SavingAdapter(ArrayList<Saving> savings) {
            mSaving = savings;
        }

        @NonNull
        @Override
        public SavingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saving_recycler_view, parent, false);
            SavingViewHolder mvh = new SavingViewHolder(view, mListener);
            return mvh;
        }

        @Override
        public void onBindViewHolder(@NonNull SavingViewHolder holder, int position) {
            Saving currentItem = mSaving.get(position);
            holder.savingName.setText(currentItem.getSavingname());
            holder.amountSaving.setText(currentItem.getSavingamount());
            holder.targetDate.setText(currentItem.getTargetdate());
            holder.savingpermonth.setText(currentItem.getSavingpermonth());
            holder.savingtargetmonth.setText(currentItem.getTargetmonth());
            holder.cumulativesaving.setText(currentItem.getCumulativesaving());
        }

        @Override
        public int getItemCount() {
            return mSaving.size();
        }
    }

