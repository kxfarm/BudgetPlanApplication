package com.example.budgetplanapplication.ui.createExpenses;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetplanapplication.BarCodeActivity;
import com.example.budgetplanapplication.R;
import com.example.budgetplanapplication.User;
import com.example.budgetplanapplication.UserExpenses;
import com.example.budgetplanapplication.databinding.FragmentCreateExpensesBinding;
import com.example.budgetplanapplication.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CreateExpensesFragment extends Fragment {

    private FragmentCreateExpensesBinding binding;
    private Button btnbarscanner, btnexpensesdone;
    private EditText btnDescription,btnamountmoney,productDescription;
    private TextView btndatetime;
    private Spinner expensesSpinner;
    DatePickerDialog.OnDateSetListener setListener;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,dataref, userreference, usrreference;
    public UserExpenses userExpenses;

    ValueEventListener listener;
    ArrayList<String> expenses;
    ArrayAdapter<String> adapter;
    private ArrayList<UserExpenses> usrExpenses = new ArrayList<>();



    String barID;
    public int requestcode = 100;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_expenses, container, false);

        btnbarscanner = view.findViewById(R.id.bar_scanner);
        expensesSpinner = view.findViewById(R.id.expenses_spinner);
        btnamountmoney = view.findViewById(R.id.amount_money);
        btndatetime = view.findViewById(R.id.date_time);
        btnDescription = view.findViewById(R.id.description_text);
        btnexpensesdone = view.findViewById(R.id.button_done);
        productDescription = view.findViewById(R.id.productdescription);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Expenses");
        dataref = firebaseDatabase.getReference("UserExpenses");
        userreference = firebaseDatabase.getReference("User");
        usrreference = firebaseDatabase.getReference("User");


        expenses = new ArrayList<String>();
        expenses.add("Select Categories: ");
        adapter = new ArrayAdapter<>( getActivity(), android.R.layout.simple_spinner_dropdown_item, expenses);
        expensesSpinner.setAdapter(adapter);


        calldata();

        btnbarscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BarCodeActivity.class);
                startActivityForResult(i, requestcode);
                startActivity(i);

            }
        });

        Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int month = cal.get(Calendar.MONTH);
        final int year = cal.get(Calendar.YEAR);


        btndatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        ,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                if (month<10){
                String date1 = day+"/0"+month+"/"+year;
                btndatetime.setText(date1);
            }
                else if (day <10){
                    String date1 = "0" + day+"/"+month+"/"+year;
                    btndatetime.setText(date1);
                }
                else{
                String date1 = day+"/"+month+"/"+year;
                btndatetime.setText(date1);
            }

            }
        };

        btnexpensesdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String expensescategories = expensesSpinner.getSelectedItem().toString();
                String expensesamount = btnamountmoney.getText().toString();
                String expensesdate = btndatetime.getText().toString();
                String expensesdescription = btnDescription.getText().toString();
                String expensesproductdescription = productDescription.getText().toString();

                if (expensescategories.equals("Select Categories: ")){
                    Toast.makeText(getActivity(), "Please create a categories first", Toast.LENGTH_SHORT).show();
                }

                else if (TextUtils.isEmpty(expensesamount)||TextUtils.isEmpty(expensesdate)||TextUtils.isEmpty(expensesproductdescription)){
                    Toast.makeText(getActivity(), "Please fill in your information", Toast.LENGTH_SHORT).show();
                }else
                {
                    addData(expensescategories,expensesamount,expensesdate,expensesdescription,expensesproductdescription);

                }
            }
        });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        UserExpenses[] userExpenses = new UserExpenses[1];
        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK){
               barID =data.getStringExtra("barID");
               dataref.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                      for (DataSnapshot dss: snapshot.getChildren()){
                          if (dss.child("userUID").getValue().toString().equals(uid) && dss.child("barid").getValue().toString().equals(barID)){
                              userExpenses[0] = new UserExpenses(dss.child("userUID").getValue().toString(),dss.child("expensescategories").getValue().toString(),dss.child("expensesamount").getValue().toString(),
                                      dss.child("expensesdate").getValue().toString(),dss.child("expensesdescription").getValue().toString(),dss.child("barid").getValue().toString(),dss.child("productdescription").getValue().toString());
                              UserExpenses e = userExpenses[0];
                              productDescription.setText(e.getProductdescription());
                              expensesSpinner.setSelection(adapter.getPosition(e.getExpensescategories()));
                              btnamountmoney.setText(e.getExpensesamount());
                          }

                      }
                   }

                   @Override
                   public void onCancelled(@NonNull @NotNull DatabaseError error) {

                   }
               });
            }
        }
        }


    private void addData(String expensescategories, String expensesamount, String expensesdate, String expensesdescription,String productdescription) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String useruid = firebaseUser.getUid();
        UUID uuid = UUID.randomUUID();

        if (barID == null){
            userExpenses = new UserExpenses(useruid,expensescategories, expensesamount, expensesdate, expensesdescription,"*",productdescription);
        }
        else{
            userExpenses = new UserExpenses(useruid,expensescategories, expensesamount, expensesdate, expensesdescription,barID,productdescription);
        }

        dataref.child(uuid.toString()).setValue(userExpenses);

        userreference.addListenerForSingleValueEvent(new ValueEventListener() {
            User[] user = new User[1];
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dss: snapshot.getChildren()){
                    if (dss.child("userUID").getValue().toString().equals(useruid)){
                        user[0] = new User(dss.child("userUID").getValue().toString(),dss.child("userincome").getValue().toString());
                    }
                }
                Double money = 0.00;
                User u = user[0];
                money = Double.parseDouble(u.getUserincome()) - Double.parseDouble(expensesamount);
                userreference.child(useruid).child("userincome").setValue(money);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,homeFragment);
        fragmentTransaction.commit();

    }
    
    public void calldata() {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}