package com.gunho0406.esancardnews.ui.mypage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyPageViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyPageViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}