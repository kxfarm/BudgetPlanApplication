package com.example.budgetplanapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.budgetplanapplication.ui.categories.CategoriesFragment;
import com.example.budgetplanapplication.ui.expenses.ExpensesFragment;
import com.example.budgetplanapplication.ui.home.HomeFragment;

import com.example.budgetplanapplication.ui.createExpenses.CreateExpensesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String uid;
    Double userincome = 0.0;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userreference, savingreference;
    private ArrayList<Saving> savings = new ArrayList<>();

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date gettoday;
    final int firstD = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    final int currentM = cal.get(Calendar.MONTH)+1;
    final int currentY = cal.get(Calendar.YEAR);
    Date cmlm;
    Date today;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userreference = firebaseDatabase.getReference("User");
        savingreference = firebaseDatabase.getReference("Saving");


        userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dss: snapshot.getChildren()){
                    if (dss.child("userUID").getValue().toString().equals(uid)) {
                        userincome = Double.parseDouble(dss.child("userincome").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        cal.set(Calendar.DAY_OF_MONTH,1);
        gettoday = cal.getTime();
        savingreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dss: snapshot.getChildren()) {
                    if (dss.child("userUID").getValue().toString().equals(uid)) {
                        savings.add(new Saving(dss.child("savingname").getValue().toString(), dss.child("savingamount").getValue().toString(),
                                dss.child("targetdate").getValue().toString(), dss.child("userUID").getValue().toString(), dss.child("uuid").getValue().toString(),
                                dss.child("savingpermonth").getValue().toString(), dss.child("targetmonth").getValue().toString(), dss.child("cumulativesaving").getValue().toString(),
                                dss.child("cumulativemonth").getValue().toString()));
                    }
                }
                for (int i = 0; i<savings.size(); i++){
                    Saving s = savings.get(i);
                    try {
                        cmlm = sdf.parse(s.getCumulativemonth());
                        today = sdf.parse(sdf.format(gettoday));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (cmlm.before(today) && !Integer.valueOf(s.getTargetmonth()).equals(0)){
                        s.setCumulativemonth(sdf.format(today));
                        s.setCumulativesaving(String.valueOf(Double.parseDouble(s.getCumulativesaving())+Double.parseDouble(s.getSavingpermonth())));
                        s.setTargetmonth(String.valueOf(Integer.valueOf(s.getTargetmonth())-1));
                        userincome -= Double.parseDouble(s.getSavingpermonth());
                        userreference.child(uid).child("userincome").setValue(userincome.toString());
                        savingreference.child(s.getUuid()).setValue(s);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new HomeFragment()).commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        bottomNav.setOnNavigationItemSelectedListener(navListner);

        //to make homefragment as the default page after user log in
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new HomeFragment()).commit();
    }

    // in order display different fragment pages
    private BottomNavigationView.OnNavigationItemSelectedListener navListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.navigation_home:
                    HomeFragment homeFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,homeFragment).commit();
                    break;
                case R.id.navigation_scan:
                    CreateExpensesFragment scannerFragment = new CreateExpensesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,scannerFragment).commit();
                    break;
                case R.id.navigation_expenses:
                    ExpensesFragment expensesFragment = new ExpensesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,expensesFragment).commit();
                    break;
                case R.id.navigation_categories:
                    CategoriesFragment categoriesFragment = new CategoriesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,categoriesFragment).commit();
                    break;
            }
            return true;
        }
    };



    // for user to logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_nav_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }


    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}