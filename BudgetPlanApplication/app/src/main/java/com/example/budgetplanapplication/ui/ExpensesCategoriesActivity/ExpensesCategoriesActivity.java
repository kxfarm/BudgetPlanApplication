package com.example.budgetplanapplication.ui.ExpensesCategoriesActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.budgetplanapplication.Expenses;
import com.example.budgetplanapplication.ExpensesAdapter;
import com.example.budgetplanapplication.R;
import com.example.budgetplanapplication.CreateExpensesCategoriesActivity;
import com.example.budgetplanapplication.ui.SavingActivity.SavingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExpensesCategoriesActivity extends Fragment {

    private ExpensesCategoriesViewModel mViewModel;
    private ExpensesCategoriesActivity binding;

    private Button btnSETopenexpenses,btnSETopensaving,btnCreateE;
    private RecyclerView tvExpenses;
    private ExpensesAdapter expensesAdapter;
    private RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private ArrayList<Expenses> expenses = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.expenses_categories_fragment, container, false);

        btnSETopenexpenses = view.findViewById(R.id.gotoexpensesC);
        btnSETopensaving = view.findViewById(R.id.gotosavingC);
        btnCreateE = view.findViewById(R.id.createE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Expenses");
        tvExpenses = view.findViewById(R.id.tvExpenses);

        btnSETopenexpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpensesCategoriesActivity expensesCategoriesActivity = new ExpensesCategoriesActivity();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,expensesCategoriesActivity);
                fragmentTransaction.commit();
            }
        });

        btnSETopensaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SavingActivity savingActivity = new SavingActivity();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,savingActivity);
                fragmentTransaction.commit();
            }
        });

        btnCreateE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(getActivity(), CreateExpensesCategoriesActivity.class);
            startActivity(i);
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference =firebaseDatabase.getReference("Expenses");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                expenses.clear();
                for (DataSnapshot dss: snapshot.getChildren()){
                    if (dss.child("userUID").getValue().toString().equals(uid)){
                        expenses.add(new Expenses(dss.child("expensesname").getValue().toString(),dss.child("userUID").getValue().toString(),dss.child("uuid").getValue().toString()));
                    }
                }

                tvExpenses.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getContext());
                expensesAdapter = new ExpensesAdapter(expenses);

                tvExpenses.setLayoutManager(layoutManager);
                tvExpenses.setAdapter(expensesAdapter);

                expensesAdapter.setOnItemClickListener(new ExpensesAdapter.OnItemClickListener() {
                    @Override
                    public void OnDeleteClick(int position) {
                            removeItem(position);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        return view;
    }

    private void removeItem(int position) {
        String uuid = expenses.get(position).getUuid();
        expenses.remove(position);
        expensesAdapter.notifyItemRemoved(position);
        FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataref = firedatabase.getReference();
        Query query = dataref.child("Expenses").orderByChild("uuid").equalTo(uuid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


