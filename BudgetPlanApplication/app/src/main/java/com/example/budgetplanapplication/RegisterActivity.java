package com.example.budgetplanapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private Button btnloginpage, btnregister;
    private EditText username, age, mail, password;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private static final String USER = "User";
    private static final String TAG = "RegisterActivity";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.register_user_id);
        age = findViewById(R.id.register_user_age);
        mail =  findViewById(R.id.register_user_mail);
        password = findViewById(R.id.register_user_password);
        btnloginpage = findViewById(R.id.already_and_login);
        btnregister = findViewById(R.id.register_button);
        progressBar =  findViewById(R.id.progressBar);

        //get firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(USER);

        //to check whether user mail and password are given correctly?
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usermail = mail.getText().toString().trim();
                String userpassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(usermail)){
                    Toast.makeText(getApplicationContext(),"Email needed...",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(userpassword)){
                    Toast.makeText(getApplicationContext(),"Password needed...",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userpassword.length()<6){
                    Toast.makeText(getApplicationContext(),"Password need at least 6 characters",Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(usermail,userpassword);

                //after all required info been fill in start to load code below
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        btnloginpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignIn();
            }
        });
    }

    public void registerUser(String usermail, String userpassword) {
        firebaseAuth.createUserWithEmailAndPassword(usermail,userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if all info is correct
                if (task.isSuccessful()){
                    Log.d(TAG,"Registered Successfully");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    updateUI(user);
                }
                //when some of the info have goes wrong
                else
                {
                    Log.w(TAG,"Registration Failure",task.getException());
                    Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateUI(FirebaseUser user) {
        String userUID =user.getUid();
        User u = new User(userUID, "0");
        databaseReference.child(userUID).setValue(u);
        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    public void openSignIn() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}