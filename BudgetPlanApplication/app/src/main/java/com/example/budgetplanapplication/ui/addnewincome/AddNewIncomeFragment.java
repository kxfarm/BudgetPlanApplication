package com.example.budgetplanapplication.ui.addnewincome;

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
import com.example.budgetplanapplication.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AddNewIncomeFragment extends Fragment {

    private AddNewIncomeViewModel addnewincomeViewModel;
    private FragmentAddNewIncomeBinding binding;

    private Button btn_user_income_done;
    private EditText etUserincome;
    User User;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_income,container,false);

        etUserincome = view.findViewById(R.id.user_income);
        btn_user_income_done = view.findViewById(R.id.button_user_income_done);

        // link with firebase realtime database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");

        btn_user_income_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userincome = etUserincome.getText().toString();

                if (TextUtils.isEmpty(userincome)){
                    Toast.makeText(getActivity(),"Please fill in your income",Toast.LENGTH_SHORT).show();
                }
                else{
                    addDatatoFirebase(userincome);



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


    private void addDatatoFirebase(String userincome) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        User = new User(uid,userincome);
        databaseReference.child(uid).setValue(User);
        Toast.makeText(getActivity(),"Income added!",Toast.LENGTH_SHORT).show();
            backtohome();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
