package com.gunho0406.imagecash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Request extends AppCompatActivity {
    ArrayList<Item> list = new ArrayList<>();
    ArrayList<String> urllist = new ArrayList<>();
    Context context;
    RecyclerAdapter adapter;
    RecyclerView rview;
    String url = "http://192.168.2.2/";
    URLConnector task;
    String userID;
    public final String PREFERENCE = "userinfo";
    String user,bitmap,title,date,uId, subject,content;
    int imgnum,code;
    String sId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        code = intent.getIntExtra("code",0);
        setContentView(R.layout.activity_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(code==9) {
            toolbar.setTitle("요청 중");
        }else{
            toolbar.setTitle("내 뉴스");
        }

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        sId = pref.getString("userID","");



        startTask();

        context = this;

        init();


        final SwipeRefreshLayout refreshLayout = findViewById(R.id.rflayout_request);
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
    }


    private void init(){
        rview = (RecyclerView)findViewById(R.id.requestview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,2);
        rview.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(context, list);
        rview.addItemDecoration(new ItemDecoration(this));
        rview.setAdapter(adapter);
    }

    private void Parse(String result,String sId, int code) throws JSONException {
        JSONObject root = new JSONObject(result);

        JSONArray ja = root.getJSONArray("result");
        Log.e("uID" , sId);
        for(int i = 0; i < ja.length();i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            uId = jo.getString("userID");
            String valid = jo.getString("verify");
            if(code==9) {
                if(valid.equals("N")) {
                    if(uId.equals(sId)){
                        user = jo.getString("user");
                        bitmap = url+"cards/"+jo.getString("bitmap");
                        title = jo.getString("title");
                        date = jo.getString("date");
                        subject = jo.getString("subject");
                        content = jo.getString("content");
                        imgnum = jo.getInt("imgnum");
                        list.add(new Item(user,bitmap,title,date,subject,content,imgnum));
                    }
                }
            }else{
                if(valid.equals("Y")) {
                    if(uId.equals(sId)){
                        user = jo.getString("user");
                        bitmap = url+"cards/"+jo.getString("bitmap");
                        title = jo.getString("title");
                        date = jo.getString("date");
                        subject = jo.getString("subject");
                        content = jo.getString("content");
                        imgnum = jo.getInt("imgnum");
                        list.add(new Item(user,bitmap,title,date,subject,content,imgnum));
                    }
                }
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
            Parse(result,sId,code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}