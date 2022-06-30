package com.example.budgetplanapplication;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CreateFutureSavingActivity extends AppCompatActivity {

    private Button btnusercreatesaving;
    private TextView targetdate;
    private EditText savingName, savingAmount;
    DatePickerDialog.OnDateSetListener setListener;
    public Double savingPerMonth;
    private ArrayList<Saving> savings = new ArrayList<>();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, reference, userreference;
    Saving saving;

    Double userincome=0.0;
    String uid;
    int monthdiff=0;

    Calendar cal = Calendar.getInstance();
    final int day = cal.get(Calendar.DAY_OF_MONTH);
    final int month = cal.get(Calendar.MONTH);
    final int year = cal.get(Calendar.YEAR);
    final int firstD = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    final int currentM = cal.get(Calendar.MONTH)+1;
    final int currentY = cal.get(Calendar.YEAR);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_future_saving);

        btnusercreatesaving = findViewById(R.id.btnUsrcreateSaving);
        savingName = findViewById(R.id.SavingName);
        savingAmount = findViewById(R.id.AmountofMoney);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Saving");
        reference = firebaseDatabase.getReference("Saving");
        userreference = firebaseDatabase.getReference("User");

        targetdate = findViewById(R.id.TargetDate);

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();

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

        targetdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateFutureSavingActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        ,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = day+"/"+month+"/"+year;
                targetdate.setText(date);
            }
        };


        btnusercreatesaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                caculation();
            }
        });


    }

    private void caculation() {

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


                String amountSaving = savingAmount.getText().toString();
                String date = targetdate.getText().toString();

        String savingname = savingName.getText().toString();
        String savingamount = savingAmount.getText().toString();
        String Targetdate = targetdate.getText().toString();


        if (TextUtils.isEmpty(savingname)||TextUtils.isEmpty(savingamount)||TextUtils.isEmpty(Targetdate)){
            Toast.makeText(CreateFutureSavingActivity.this, "Information missing", Toast.LENGTH_SHORT).show();
        }
        else{
            try {
                Date target = sdf.parse(date);
                Date today = cal.getTime();
                if (today.before(target)){
                    cal.setTime(today);
                }else
                {
                    monthdiff =0;
                }
                while (cal.getTime().before(target)){
                    cal.add(Calendar.MONTH, 1);
                    monthdiff++;
                }
                if (monthdiff == 0){
                    savingPerMonth = Double.parseDouble(amountSaving);
                    savingPerMonth = Double.parseDouble(new DecimalFormat("##.##").format(savingPerMonth));

                }
                else
                {
                    savingPerMonth = Double.parseDouble(amountSaving) / Double.valueOf(monthdiff+1);
                    savingPerMonth = Double.parseDouble(new DecimalFormat("##.##").format(savingPerMonth));
                }

                if(userincome < savingPerMonth){
                    Toast.makeText(CreateFutureSavingActivity.this, "Insufficient balance!", Toast.LENGTH_SHORT).show();
                }
                else {

                    addDatatoFirebase(savingname,savingamount,Targetdate, month, year);
                }


            } catch (ParseException e) {
                Toast.makeText(CreateFutureSavingActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                // Handle error condition.
            }

        }

    }


    private void addDatatoFirebase(String savingname, String savingamount, String Targetdate, int month, int year) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        UUID uuid = UUID.randomUUID();
        saving = new Saving(savingname,savingamount,Targetdate,uid,uuid.toString(),savingPerMonth.toString(),String.valueOf(monthdiff),savingPerMonth.toString(),String.valueOf(firstD+"/"+currentM+"/"+currentY));
        databaseReference.child(uuid.toString()).setValue(saving);
        userreference.child(uid).child("userincome").setValue(Double.toString(userincome - savingPerMonth));
        Toast.makeText(CreateFutureSavingActivity.this, "Create successfully", Toast.LENGTH_SHORT).show();
        finish();


    }

}