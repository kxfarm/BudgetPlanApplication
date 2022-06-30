package com.example.budgetplanapplication.ui.expenses;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.budgetplanapplication.HomeAdapter;
import com.example.budgetplanapplication.R;
import com.example.budgetplanapplication.UserExpenses;
import com.example.budgetplanapplication.databinding.FragmentExpensesHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExpensesFragment extends Fragment {

    private expensesViewModel notificationsViewModel;
    private FragmentExpensesHistoryBinding binding;
    Button btncalendar;
    String date;
    Spinner btncategories;
    private RecyclerView tvexpenses;
    private HomeAdapter homeAdapter;
    private RecyclerView.LayoutManager layoutManager;
    DatePickerDialog.OnDateSetListener setListener;
    Boolean dateselected = false;

    final Calendar cal = Calendar.getInstance();

    ValueEventListener listener;
    ArrayList<String> expenses;
    ArrayAdapter<String> adapter;

    DatabaseReference databaseReference, userExpReference;
    FirebaseDatabase firebaseDatabase;

    private ArrayList<UserExpenses> userExpenses = new ArrayList<>();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = firebaseUser.getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses_history, container, false);
        btncalendar = view.findViewById(R.id.button_calendar);
        btncategories = view.findViewById(R.id.button_expensesC);
        tvexpenses = view.findViewById(R.id.expenses_recycler_view);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Expenses");
        userExpReference = firebaseDatabase.getReference("UserExpenses");

        expenses = new ArrayList<String>();
        expenses.add("Select Categories: ");
        adapter = new ArrayAdapter<>( getActivity(), android.R.layout.simple_spinner_dropdown_item, expenses);
        btncategories.setAdapter(adapter);

        callData();

        DatePickerDialog.OnDateSetListener timeDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    cal.set(Calendar.YEAR,year);
                    cal.set(Calendar.MONTH,month);
                    cal.set(Calendar.DAY_OF_MONTH,day);

                    update();
                    getExpenses();
            }

        };

        btncalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),timeDialog,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        btncalendar.setText("Calendar");
                        getExpenses();
                    }
                });
                datePickerDialog.show();

            }
        });

        return view;
    }


    public void callData() {
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String uid = firebaseUser.getUid();

            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dss: snapshot.getChildren()){
                    if (dss.child("userUID").getValue().toString().equals(uid)){
                        expenses.add(dss.child("expensesname").getValue().toString());
                    }
                }
                adapter.notifyDataSetChanged();
                adapter.setDropDownViewResource(R.layout.spinnerlist);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        btncategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getExpenses();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void update() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        btncalendar.setText(sdf.format(cal.getTime()));
       dateselected = true;
    }
    public void getExpenses(){

        userExpenses.clear();
        if (dateselected || !btncategories.getSelectedItem().toString().equals("Select Categories: ")) {
            if (dateselected){
                userExpReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                userExpenses.clear();
                                for (DataSnapshot dss: snapshot.getChildren()){
                                    if (dss.child("userUID").getValue().toString().equals(uid) && dss.child("expensesdate").getValue().toString().equals(btncalendar.getText().toString()) ){
                                        userExpenses.add( new UserExpenses(dss.child("userUID").getValue().toString(),dss.child("expensescategories").getValue().toString(),
                                                dss.child("expensesamount").getValue().toString(), dss.child("expensesdate").getValue().toString(),
                                                dss.child("expensesdescription").getValue().toString(),dss.child("barid").getValue().toString(),
                                                dss.child("productdescription").getValue().toString()));
                                    }
                                }
                                tvexpenses.setHasFixedSize(true);
                                layoutManager = new LinearLayoutManager(getContext());
                                homeAdapter = new HomeAdapter(userExpenses);

                                tvexpenses.setLayoutManager(layoutManager);
                                tvexpenses.setAdapter(homeAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
            }
            if (!btncategories.getSelectedItem().toString().equals("Select Categories: ")){
                userExpReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dss: snapshot.getChildren()){
                            if (dss.child("userUID").getValue().toString().equals(uid) && dss.child("expensescategories").getValue().toString().equals(btncategories.getSelectedItem().toString())){
                                userExpenses.add( new UserExpenses(dss.child("userUID").getValue().toString(),dss.child("expensescategories").getValue().toString(),
                                        dss.child("expensesamount").getValue().toString(), dss.child("expensesdate").getValue().toString(),
                                        dss.child("expensesdescription").getValue().toString(),dss.child("barid").getValue().toString(),
                                        dss.child("productdescription").getValue().toString()));
                            }
                        }
                        tvexpenses.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        homeAdapter = new HomeAdapter(userExpenses);

                        tvexpenses.setLayoutManager(layoutManager);
                        tvexpenses.setAdapter(homeAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
            if (!btncategories.getSelectedItem().toString().equals("Select Categories: ") && dateselected ){
                userExpReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        userExpenses.clear();
                        for (DataSnapshot dss: snapshot.getChildren()){
                            if (dss.child("userUID").getValue().toString().equals(uid) && dss.child("expensescategories").getValue().toString().equals(btncategories.getSelectedItem().toString()) && dss.child("expensesdate").getValue().toString().equals(btncalendar.getText().toString())){
                                userExpenses.add( new UserExpenses(dss.child("userUID").getValue().toString(),dss.child("expensescategories").getValue().toString(),
                                        dss.child("expensesamount").getValue().toString(), dss.child("expensesdate").getValue().toString(),
                                        dss.child("expensesdescription").getValue().toString(),dss.child("barid").getValue().toString(),
                                        dss.child("productdescription").getValue().toString()));
                            }
                        }
                        tvexpenses.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        homeAdapter = new HomeAdapter(userExpenses);

                        tvexpenses.setLayoutManager(layoutManager);
                        tvexpenses.setAdapter(homeAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}