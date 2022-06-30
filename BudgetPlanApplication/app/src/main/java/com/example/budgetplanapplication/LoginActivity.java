package com.example.budgetplanapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button btnsignup,btnsignin;
    private EditText useremail, userpassword;
    private FirebaseAuth firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnsignin = findViewById(R.id.sign_in_button);
        btnsignup = findViewById(R.id.sign_up_button);
        useremail = findViewById(R.id.User_mail);
        userpassword = findViewById(R.id.User_password);

        firebase = FirebaseAuth.getInstance();

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = useremail.getText().toString();
                final String password = userpassword.getText().toString();
                 if (TextUtils.isEmpty(mail)) {
                     Toast.makeText(getApplicationContext(), "Email needed...", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if (TextUtils.isEmpty(password)) {
                     Toast.makeText(getApplicationContext(), "Password needed...", Toast.LENGTH_SHORT).show();
                     return;
                 }

                 //check user account from firebase
                firebase.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                userpassword.setError("Password needed 6 characters");
                            } else {
                                Toast.makeText(LoginActivity.this, "Your account cannot be found, try to sign up", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }); }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUpPage();
            }
        });
    }

    public void openSignUpPage() {
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
        finish();
    }
}