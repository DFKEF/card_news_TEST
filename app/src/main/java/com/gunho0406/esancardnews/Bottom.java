package com.gunho0406.esancardnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Bottom extends BottomSheetDialogFragment implements View.OnClickListener{

    public static Bottom getInstance() { return new Bottom(); }

    private LinearLayout msgLo;
    private LinearLayout login;
    public final String PREFERENCE = "userinfo";



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom, container,false);
        msgLo = (LinearLayout) view.findViewById(R.id.settings);
        login = (LinearLayout) view.findViewById(R.id.quit);
        msgLo.setOnClickListener(this);
        login.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.settings:
                Intent it = new Intent(getContext(),SettingsActivity.class);
                startActivity(it);
                break;
            case R.id.quit:
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                getActivity().finish();
                startActivity(intent);
                break;
        }
        dismiss();
    }

}
