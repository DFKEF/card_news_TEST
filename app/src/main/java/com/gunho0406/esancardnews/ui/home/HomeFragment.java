package com.gunho0406.esancardnews.ui.home;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gunho0406.esancardnews.CustomDialog;
import com.gunho0406.esancardnews.Item;
import com.gunho0406.esancardnews.ItemDecoration;
import com.gunho0406.esancardnews.LoginActivity;
import com.gunho0406.esancardnews.R;
import com.gunho0406.esancardnews.RecyclerAdapter;
import com.gunho0406.esancardnews.URLConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment{

    RecyclerAdapter adapter;
    RecyclerView rview;
    ArrayList<Item> list = new ArrayList<>();
    String url = "http://13.209.232.72/";
    URLConnector task;
    String userID;
    public final String PREFERENCE = "userinfo";
    String user,bitmap,title,date,verify,subject, content, getid;
    int imgnum;
    ArrayList<String> teacherlist = new ArrayList<>();
    ArrayList<String> subjectlist = new ArrayList<>();

    String result;
    private HomeViewModel homeViewModel;

    Activity activity;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //list.add(new Item("신건호","https://img.khan.co.kr/news/2020/05/15/l_2020051401001770900136491.jpg","헬로","2020.04.06","과학","아아아",1,"gunho"));
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = activity.findViewById(R.id.toolbar_);
        toolbar.setTitle("홈");
        //setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        SharedPreferences pref = activity.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        result = pref.getString("userID","");
        startTeachers();

        FragmentManager fragManager = activity.getFragmentManager();




        startTask();
        init(root);


        final SwipeRefreshLayout refreshLayout = root.findViewById(R.id.refreshlayout_);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                startTask();
                adapter.notifyDataSetChanged();
                rview.setAdapter(adapter);
                refreshLayout.setRefreshing(false);
            }
        });




        return root;
    }

    private void init(View v){
        rview = (RecyclerView) v.findViewById(R.id.r_view);
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
            if(verify.equals("Y")){
                list.add(new Item(user,bitmap,title,date,subject,content,imgnum,getid));
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

    private void startTeachers(){
        task = new URLConnector(url+"teacher.php");
        task.start();
        try{
            task.join();
        }
        catch(InterruptedException e){

        }
        String result = task.getResult();

        try {
            Teachers(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Teachers(String result) throws JSONException {
        JSONObject root = new JSONObject(result);

        JSONArray ja = root.getJSONArray("result");

        for(int i = 0; i < ja.length();i++)
        {
            String ver;
            JSONObject jo = ja.getJSONObject(i);
            ver = jo.getString("verify");
            if(ver.equals("Y")){
                teacherlist.add(jo.getString("name"));
                subjectlist.add(jo.getString("subject"));
            }
        }
    }



}