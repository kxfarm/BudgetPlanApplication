package com.example.budgetplanapplication.ui.createExpenses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateExpensesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CreateExpensesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}