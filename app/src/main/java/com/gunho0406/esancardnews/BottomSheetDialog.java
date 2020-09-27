package com.gunho0406.esancardnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static android.content.Context.MODE_PRIVATE;

public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener{

    public static BottomSheetDialog getInstance() { return new BottomSheetDialog(); }

    private LinearLayout msgLo;
    private LinearLayout emailLo;
    private LinearLayout request;
    private LinearLayout logout;
    public final String PREFERENCE = "userinfo";



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container,false);
        msgLo = (LinearLayout) view.findViewById(R.id.settings);
        emailLo = (LinearLayout) view.findViewById(R.id.mynews);
        request = (LinearLayout) view.findViewById(R.id.request);
        logout = (LinearLayout) view.findViewById(R.id.quit);

        msgLo.setOnClickListener(this);
        emailLo.setOnClickListener(this);
        request.setOnClickListener(this);
        logout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.settings:
                Intent it = new Intent(getContext(),SettingsActivity.class);
                startActivity(it);
                break;
            case R.id.mynews:
                Intent init = new Intent(getContext(),Request.class);
                init.putExtra("code",0);
                startActivity(init);
                break;
            case R.id.request:
                Intent i = new Intent(getContext(),Request.class);
                i.putExtra("code",9);
                startActivity(i);
                break;
            case R.id.quit:
                SharedPreferences pref = view.getContext().getSharedPreferences(PREFERENCE, MODE_PRIVATE);

                // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                SharedPreferences.Editor editor = pref.edit();
                // key값에 value값을 저장한다.
                // String, boolean, int, float, long 값 모두 저장가능하다.
                editor.clear();
                // 메모리에 있는 데이터를 저장장치에 저장한다.
                editor.commit();
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
                break;
        }
        dismiss();
    }

}