package com.gunho0406.esancardnews;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class CustomDialog {

    private Context context;
    ArrayList<String> teacherlist = new ArrayList<>();
    ArrayList<String> subjectlist = new ArrayList<>();
    ArrayList<String > list = new ArrayList<>();
    String subject,teacher;

    public CustomDialog(Context context, ArrayList<String> teacherlist, ArrayList<String> subjectlist) {
        this.context = context;
        this.teacherlist = teacherlist;
        this.subjectlist = subjectlist;
    }
    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        for(int i= 0; i<subjectlist.size(); i++) {
            list.add(subjectlist.get(i)+"/"+teacherlist.get(i));
            Log.e("나나",list.get(i));
        }
        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final Button okButton = (Button) dlg.findViewById(R.id.button);
        final Spinner selectTeacher = (Spinner) dlg.findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, list);
        selectTeacher.setAdapter(adapter);
        selectTeacher.setSelection(0);
        selectTeacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = subjectlist.get(position);
                teacher = teacherlist.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> hello = new ArrayList<>();
                hello.add(subject);
                hello.add(teacher);
                dlg.dismiss();
            }
        });

    }
}
