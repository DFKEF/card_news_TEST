package com.gunho0406.esancardnews.ui.popular;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PopularViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PopularViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}