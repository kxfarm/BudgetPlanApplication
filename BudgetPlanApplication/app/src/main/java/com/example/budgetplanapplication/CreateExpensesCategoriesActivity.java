package com.example.budgetplanapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class CreateExpensesCategoriesActivity extends AppCompatActivity {

    private Button btnusercreateexpenses;
    private EditText expensesname;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, dataReference;
    Expenses expenses;
    String expname;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = firebaseUser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_expenses_categories);

        btnusercreateexpenses = findViewById(R.id.btnUsrcreateExpenses);
        expensesname = findViewById(R.id.expenses_name);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Expenses");
        dataReference = firebaseDatabase.getReference("Expenses");

        btnusercreateexpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expenses_name = expensesname.getText().toString();

                if (TextUtils.isEmpty(expenses_name)){
                    Toast.makeText(CreateExpensesCategoriesActivity.this, "Please fill in your expenses name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addDatatoFirebase(expenses_name);
                }
            }
        });
    }

    private void addDatatoFirebase(String expenses_name) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        UUID uuid = UUID.randomUUID();
        expenses = new Expenses(expenses_name,uid,uuid.toString());
        databaseReference.child(uuid.toString()).setValue(expenses);
        Toast.makeText(CreateExpensesCategoriesActivity.this, "Create successfully", Toast.LENGTH_SHORT).show();
        finish();

    }
}