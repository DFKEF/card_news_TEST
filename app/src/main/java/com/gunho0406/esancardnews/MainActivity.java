package com.gunho0406.esancardnews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Context context;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("홈");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        final String result = pref.getString("userID","");
        startTeachers();

        ImageView userBtn = (ImageView) findViewById(R.id.userBtn);
        userBtn.setBackground(new ShapeDrawable(new OvalShape()));
        userBtn.setClipToOutline(true);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result == "") {
                    Bottom bottom = Bottom.getInstance();
                    bottom.show(getSupportFragmentManager(),"bottom");
                }else{
                    BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance();
                    bottomSheetDialog.show(getSupportFragmentManager(),"bottomSheet");
                }

            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("ㅣㅣㅣ", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.e("token",token);

                        // Log and toast
                    }
                });



        if(result == "") {
            Log.e("d","sfd");
            Glide.with(this)
                    .load(R.drawable.ic_baseline_account_circle_24)
                    .override(50,50)
                    .into(userBtn);
        }else{
            String profile = result+"_profile.jpg";
            Log.e("profile",result+"_profile.jpg");
            Glide.with(this)
                    .load(url+"profiles/"+profile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE )
                    .skipMemoryCache(true)
                    .centerCrop()
                    .override(50,50)
                    .into(userBtn);
        }


        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result=="") {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Log.e("teacher",teacherlist.get(0));
                    CustomDialog customDialog = new CustomDialog(MainActivity.this,teacherlist,subjectlist);

                    // 커스텀 다이얼로그를 호출한다.
                    // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                    customDialog.callFunction();
                }
            }
        });





        startTask();

        context = this;

        init(result);

        rview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshlayout);
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




    private void init(String sId){
        rview = (RecyclerView)findViewById(R.id.rview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,2);
        rview.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(context, list);
        rview.addItemDecoration(new ItemDecoration(this));
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

    private String HttpPostData(String sId) {
        String param = "u_id=" + sId;

        try {
            URL home = new URL(url + "user.php");
            String result = "";
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) home.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setUseCaches(false);
            try {
                conn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream outputStream = null;
            try {
                outputStream = conn.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream.write(param.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            Log.i("PHPRequest", "No Problem");
            result = conn.getInputStream().toString();
            Log.i("과연",result);

            conn.disconnect();
            return result;
        } catch (Exception ex) {
            Log.e("plz","plz");
            return null;
        }
    }
}