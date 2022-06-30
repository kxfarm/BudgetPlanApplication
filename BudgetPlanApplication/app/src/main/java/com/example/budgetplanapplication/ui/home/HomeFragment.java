package com.example.budgetplanapplication.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.budgetplanapplication.HomeAdapter;
import com.example.budgetplanapplication.R;
import com.example.budgetplanapplication.User;
import com.example.budgetplanapplication.UserExpenses;
import com.example.budgetplanapplication.databinding.FragmentHomeBinding;
import com.example.budgetplanapplication.ui.addincome.AddExtraIncomeFragment;
import com.example.budgetplanapplication.ui.addnewincome.AddNewIncomeFragment;
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
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private RecyclerView tvhome;
    private HomeAdapter homeAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView currentincome, dailyexpenses;
    private Button btnAddNewIncome, btnAddIncome;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<UserExpenses> userExpenses = new ArrayList<>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        currentincome = view.findViewById(R.id.current_income);
        dailyexpenses = view.findViewById(R.id.daily_expenses);
        btnAddNewIncome = view.findViewById(R.id.add_new_income);
        btnAddIncome = view.findViewById(R.id.add_income);

        // link with firebase realtime database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");
        tvhome = view.findViewById(R.id.daily_expenses_recyclerview);


        //get current user uid
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            Calendar cal = Calendar.getInstance();
            int daily = cal.get(Calendar.DAY_OF_MONTH);
            int date = cal.getActualMaximum(Calendar.DATE);

            User[] user = new User[1];
            // check whether this user existed or not ;
            Boolean existed=false;
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
             for (DataSnapshot dss: snapshot.getChildren()){
                 if (dss.child("userUID").getValue().toString().equals(uid)){
                    existed = true;
                   user[0] = new User(dss.child("userUID").getValue().toString(),dss.child("userincome").getValue().toString());
                }
             }

             User u = user[0];
             Double daily = 0.0;
                    if (existed){
                        currentincome.setText(u.getUserincome());
                        if (Double.parseDouble(u.getUserincome())<0.0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Insufficient income");
                            builder.setMessage("Please add your income");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        //calculation for user daily expenses
                        if (this.daily < date){
                            daily = Double.parseDouble(u.getUserincome())/(date - this.daily +1);
                        }
                        else
                        {
                            daily = Double.parseDouble(u.getUserincome());
                        }
                        dailyexpenses.setText(String.format("%.2f", daily));
                    }
                    else
                    {
                        currentincome.setText("0.00");
                        dailyexpenses.setText("0.00");
                    }

                //to set visibility of two different buttons in home fragment
                if (Double.parseDouble(currentincome.getText().toString()) == 0.00){
                    btnAddNewIncome.setVisibility(view.VISIBLE);
                    btnAddIncome.setVisibility(view.GONE);
                }
                else
                {
                    btnAddNewIncome.setVisibility(view.GONE);
                    btnAddIncome.setVisibility(view.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        //home fragment recycler view
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference =firebaseDatabase.getReference("UserExpenses");

        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        String today = sdf.format(calendar);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userExpenses.clear();
                for (DataSnapshot dss: snapshot.getChildren()){
                    if (dss.child("userUID").getValue().toString().equals(uid) && dss.child("expensesdate").getValue().toString().equals(today)){
                        userExpenses.add( new UserExpenses(dss.child("userUID").getValue().toString(),dss.child("expensescategories").getValue().toString(),dss.child("expensesamount").getValue().toString(),
                                dss.child("expensesdate").getValue().toString(),dss.child("expensesdescription").getValue().toString(),dss.child("barid").getValue().toString(),dss.child("productdescription").getValue().toString()));
                    }
                }

                tvhome.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getContext());
                homeAdapter = new HomeAdapter(userExpenses);

                tvhome.setLayoutManager(layoutManager);
                tvhome.setAdapter(homeAdapter);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        btnAddNewIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewIncomeFragment addNewIncomeFragment = new AddNewIncomeFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.nav_host_fragment_activity_main,addNewIncomeFragment).commit();

            }
        });

        btnAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddExtraIncomeFragment addExtraIncomeFragment = new AddExtraIncomeFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.nav_host_fragment_activity_main,addExtraIncomeFragment).commit();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}