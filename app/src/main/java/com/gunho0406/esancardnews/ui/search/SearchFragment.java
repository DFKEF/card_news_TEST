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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gunho0406.esancardnews.Item;
import com.gunho0406.esancardnews.ItemDecoration;
import com.gunho0406.esancardnews.R;
import com.gunho0406.esancardnews.RecyclerAdapter;
import com.gunho0406.esancardnews.URLConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private SearchViewModel searchViewModel;
    Activity activity;
    Button[] btn = new Button[9];
    View root;
    ArrayList<String> sublist = new ArrayList<>();

    RecyclerAdapter adapter;
    RecyclerView rview;

    ArrayList<Item> list = new ArrayList<Item>();
    private ArrayList<Item> searchlist = new ArrayList<>();


    String url = "http://13.209.232.72/";
    URLConnector task;
    String userID;
    public final String PREFERENCE = "userinfo";
    String user,bitmap,title,date,verify,subject, content, getid;
    int imgnum,like_num;
    ArrayList<String> titlelist = new ArrayList<>();
    ArrayList<String> subject_name = new ArrayList<>();
    ArrayList<String> namelist = new ArrayList<>();
    SearchView sv;

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
        startTask();


        init(root,searchlist);
        Search("");

        sv= (SearchView)  root.findViewById(R.id.searchview);
        //확인버튼 활성화
        //SearchView의 검색 이벤트
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //검색버튼을 눌렀을 경우
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //텍스트가 바뀔때마다 호출
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("new",newText + "를 검색합니다.");
                Search(newText);
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
            subject_name.add(btn[i].getText().toString());
        }

        return root;
    }

    public void Search(String query) {
        searchlist.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (query.length() == 0) {
            searchlist.addAll(list);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < list.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                String sub = sublist.get(i);
                String tit = titlelist.get(i);
                String nam = namelist.get(i);
                Log.e("nam",nam);
                if (sub.toLowerCase().contains(query)||tit.toLowerCase().contains(query)||nam.toLowerCase().contains(query))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    searchlist.add(list.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
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
                String query = subject_name.get(position);
                // 태그로 가져온 포지션을 이용해 리스트에서 출력할 데이터를 꺼내서 토스트 메시지 출력
                sv.setQuery(query,false);
                Search(query);
            }
        }
    }

    private void init(View v, ArrayList<Item> list){
        rview = (RecyclerView) v.findViewById(R.id.search_rview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity,2);
        rview.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(activity, list);
        rview.addItemDecoration(new ItemDecoration(activity));
        rview.setAdapter(adapter);
    }

    private void Parse(String result) throws JSONException {
        JSONObject root = new JSONObject(result);

        JSONArray ja = root.getJSONArray("result");

        for(int i = 0; i < ja.length();i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            user = jo.getString("user");
            bitmap = url+"cards/"+jo.getString("bitmap");
            title = jo.getString("title");
            date = jo.getString("date");
            verify = jo.getString("verify");
            subject = jo.getString("subject");
            content = jo.getString("content");
            imgnum = jo.getInt("imgnum");
            getid = jo.getString("userID");
            like_num = jo.getInt("like_count");
            if(verify.equals("Y")){
                list.add(new Item(user,bitmap,title,date,subject,content,imgnum,getid,like_num));
                namelist.add(user);
                titlelist.add(title);
                sublist.add(subject);
            }
        }
    }

    private void startTask(){
        task = new URLConnector(url+"parselist.php");
        task.start();
        try{
            task.join();
        }
        catch(InterruptedException e){

        }
        String result = task.getResult();

        try {
            Parse(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}