package com.gunho0406.imagecash;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    String url = "http://192.168.2.2/";
    URLConnector task;
    String userID;
    public final String PREFERENCE = "userinfo";
    String user,bitmap,title,date;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        final String result = pref.getString("userID","");

        ImageView userBtn = (ImageView) findViewById(R.id.userBtn);
        userBtn.setBackground(new ShapeDrawable(new OvalShape()));
        userBtn.setClipToOutline(true);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result == "") {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Log.e("d","sfd");
                }else{
                    BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance();
                    bottomSheetDialog.show(getSupportFragmentManager(),"bottomSheet");
                }

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
                    .load(url+profile)
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
                }else {
                    Intent intent = new Intent(MainActivity.this, Upload.class);
                    startActivity(intent);
                }
            }
        });





        startTask();

        context = this;

        init();

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

    private void init(){
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
            bitmap = url+jo.getString("bitmap");
            title = jo.getString("title");
            date = jo.getString("date");
            list.add(new Item(user,bitmap,title,date));
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

    private String setuserProfile(String sId) {
        String profileImg = "";

        String result = HttpPostData(sId);

        try {
            JSONObject root = new JSONObject(result);

            JSONArray ja = root.getJSONArray("result");

            for(int i = 0; i < ja.length();i++)
            {
                JSONObject jo = ja.getJSONObject(i);
                profileImg = jo.getString("profile");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profileImg;
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