package com.example.budgetplanapplication.ui.addincome;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.budgetplanapplication.R;
import com.example.budgetplanapplication.User;
import com.example.budgetplanapplication.databinding.FragmentAddNewIncomeBinding;
import com.example.budgetplanapplication.ui.addnewincome.AddNewIncomeViewModel;
import com.example.budgetplanapplication.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AddExtraIncomeFragment extends Fragment {

    private AddNewIncomeViewModel addnewincomeViewModel;
    private FragmentAddNewIncomeBinding binding;

    private Button btn_user_additional_income_done;
    private EditText etUserAdditionalincome;
    User User;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_additional_income,container,false);

        etUserAdditionalincome = view.findViewById(R.id.addition_income);
        btn_user_additional_income_done = view.findViewById(R.id.button_user_additional_income_done);

        // link with firebase realtime database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");




        btn_user_additional_income_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_income = etUserAdditionalincome.getText().toString();

                if (TextUtils.isEmpty(user_income)){
                    Toast.makeText(getActivity(),"Please fill in your income",Toast.LENGTH_SHORT).show();
                }
                else{
                    addDatatoFirebase(user_income);



                }
            }

        });

        return view;
    }


    private void backtohome() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,homeFragment);
        fragmentTransaction.commit();
    }


    private void addDatatoFirebase(String user_income) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        User = new User(uid,user_income);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            User[] user = new User[1];
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dss: snapshot.getChildren()){
                    if (dss.child("userUID").getValue().toString().equals(uid)){
                        user[0] = new User(dss.child("userUID").getValue().toString(),dss.child("userincome").getValue().toString());
                    }
                }
                Double additionalI = 0.0;
                User u = user[0];
                additionalI = Double.parseDouble(u.getUserincome())+Double.parseDouble(user_income);
                databaseReference.child(uid).child("userincome").setValue(additionalI);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        Toast.makeText(getActivity(),"Additional Income added!",Toast.LENGTH_SHORT).show();
        backtohome();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
