package com.gunho0406.esancardnews.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.gunho0406.esancardnews.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private SearchViewModel searchViewModel;
    Activity activity;
    Button[] btn = new Button[9];
    View root;
    ArrayList<String> sublist = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar_);
        toolbar.setTitle("검색");
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        root = inflater.inflate(R.layout.fragment_search, container, false);

        SearchView sv= (SearchView)  root.findViewById(R.id.searchview);
        //확인버튼 활성화

        //SearchView의 검색 이벤트
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //검색버튼을 눌렀을 경우
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("query",query + "를 검색합니다.");
                Search(query);
                return false;
            }

            //텍스트가 바뀔때마다 호출
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        btn[0] = (Button) root.findViewById(R.id.sub1);
        btn[1] = (Button) root.findViewById(R.id.sub2);
        btn[2] = (Button) root.findViewById(R.id.sub3);
        btn[3] = (Button) root.findViewById(R.id.sub4);
        btn[4] = (Button) root.findViewById(R.id.sub5);
        btn[5] = (Button) root.findViewById(R.id.sub6);
        btn[6] = (Button) root.findViewById(R.id.sub7);
        btn[7] = (Button) root.findViewById(R.id.sub8);
        btn[8] = (Button) root.findViewById(R.id.sub9);


        for(int i=0; i<9; i++) {
            btn[i].setTag(i);
            btn[i].setOnClickListener(this);
            sublist.add(btn[i].getText().toString());
        }

        return root;
    }

    public void Search(String query) {
        Intent intent = new Intent(activity,SearchResult.class);
        intent.putExtra("result",query);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;

        for(Button tempButton : btn)
        {
            // 클릭된 버튼을 찾았으면
            if(tempButton == button)
            {
                // 위에서 저장한 버튼의 포지션을 태그로 가져옴
                int position = (Integer) v.getTag();
                String query = sublist.get(position);
                // 태그로 가져온 포지션을 이용해 리스트에서 출력할 데이터를 꺼내서 토스트 메시지 출력
                Search(query);
            }
        }
    }
}