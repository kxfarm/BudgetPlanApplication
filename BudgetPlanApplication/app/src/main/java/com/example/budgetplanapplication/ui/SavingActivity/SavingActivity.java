package com.example.budgetplanapplication.ui.SavingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.budgetplanapplication.R;
import com.example.budgetplanapplication.Saving;
import com.example.budgetplanapplication.SavingAdapter;
import com.example.budgetplanapplication.CreateFutureSavingActivity;
import com.example.budgetplanapplication.ui.ExpensesCategoriesActivity.ExpensesCategoriesActivity;
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

public class SavingActivity extends Fragment {

    private SavingViewModel mViewModel;
    private SavingActivity binding;

    private Button btnSETopensaving, btnSETopenexpenses, btnCreateS;
    private RecyclerView tvSaving;
    private SavingAdapter savingAdapter;
    private RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference, userreference, savingreference;
    FirebaseDatabase firebaseDatabase;
    private ArrayList<Saving> savings = new ArrayList<>();

    Double cumulativesaving = 0.0;
    Double userincome=0.0;
    String uid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.saving_fragment, container, false);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();

        btnSETopenexpenses = view.findViewById(R.id.gotoexpensesC);
        btnSETopensaving = view.findViewById(R.id.gotosavingC);
        btnCreateS = view.findViewById(R.id.createS);

        firebaseDatabase = FirebaseDatabase.getInstance();
        tvSaving = view.findViewById(R.id.tvSaving);
        userreference = firebaseDatabase.getReference("User");
        savingreference = firebaseDatabase.getReference("Saving");

        userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dss : snapshot.getChildren()) {
                    // check if whether inside "saving" realtime have data or not and get the same data of userUID from firebase
                    if (dss.child("userUID").getValue().toString().equals(uid)) {
                        userincome = Double.parseDouble(dss.child("userincome").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        savingreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dss : snapshot.getChildren()) {
                    // check if whether inside "saving" realtime have data or not and get the same data of userUID from firebase
                    if (dss.child("userUID").getValue().toString().equals(uid)) {
                        cumulativesaving = Double.parseDouble(dss.child("cumulativesaving").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

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

        btnCreateS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CreateFutureSavingActivity.class);
                startActivity(i);
            }
        });

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference =firebaseDatabase.getReference("Saving");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //to clear. Make sure that data wont duplicate into recyclerview.
                savings.clear();
                for (DataSnapshot dss: snapshot.getChildren()){
                    if (dss.child("userUID").getValue().toString().equals(uid)){
                        savings.add(new Saving(dss.child("savingname").getValue().toString(),dss.child("savingamount").getValue().toString(),
                                dss.child("targetdate").getValue().toString(),dss.child("userUID").getValue().toString(),dss.child("uuid").getValue().toString(),
                                dss.child("savingpermonth").getValue().toString(),dss.child("targetmonth").getValue().toString(),dss.child("cumulativesaving").getValue().toString(),
                                dss.child("cumulativemonth").getValue().toString()));
                    }
                }
                tvSaving.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getContext());
                savingAdapter = new SavingAdapter(savings);

                tvSaving.setLayoutManager(layoutManager);
                tvSaving.setAdapter(savingAdapter);

                savingAdapter.setOnItemClickListener(new SavingAdapter.OnItemClickListener() {
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void removeItem(int position){
        String uuid = savings.get(position).getUuid();
        savings.remove(position);
        savingAdapter.notifyItemRemoved(position);
        FirebaseDatabase firedatabase = FirebaseDatabase.getInstance();
        DatabaseReference dataref = firedatabase.getReference();
        Query query = dataref.child("Saving").orderByChild("uuid").equalTo(uuid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dss: snapshot.getChildren()){
                    getbackcmltsaving();
                    dss.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getbackcmltsaving() {
        userreference.child(uid).child("userincome").setValue(Double.toString(userincome+cumulativesaving));
        Toast.makeText(getActivity(), "Delete successful", Toast.LENGTH_SHORT).show();
    }
}