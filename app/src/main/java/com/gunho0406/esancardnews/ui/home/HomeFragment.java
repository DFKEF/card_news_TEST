package com.gunho0406.esancardnews.ui.home;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.gunho0406.esancardnews.Upload;
import com.gunho0406.esancardnews.email;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    int imgnum,like_num;
    ArrayList<String> teacherlist = new ArrayList<>();
    ArrayList<String> subjectlist = new ArrayList<>();
    String isVerified;

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

        getVerify getVerify = new getVerify(result);
        getVerify.execute();

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
            like_num = jo.getInt("like_count");
            if(verify.equals("Y")){
                list.add(new Item(user,bitmap,title,date,subject,content,imgnum,getid,like_num));
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

    public class getVerify extends AsyncTask<Void, Integer, String> {
        String data;
        String sId;

        public getVerify(String sId) {
            this.sId = sId;
        }
        @Override
        protected String doInBackground(Void... unused) {
            //인풋 파라메터값 생성
            String param = "u_id=" + sId;
            try {
                // 서버연결
                URL home = new URL(url
                        +"get_verify.php");
                HttpURLConnection conn = (HttpURLConnection) home.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                // 안드로이드 -> 서버 파라메터값 전달
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                // 서버 -> 안드로이드 파라메터값 전달
                InputStream is = null;
                BufferedReader in = null;
                data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("RECV DATA",data);



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            Log.e("data",data);
            isVerified = data;
            if(isVerified.equals("S")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("알림").setMessage("이메일 주소를 갱신해야 합니다.");

                builder.setPositiveButton("갱신하기", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent in = new Intent(activity, email.class);
                        in.putExtra("ID",sId);
                        startActivity(in);
                        activity.finish();
                    }
                });

                builder.setNegativeButton("로그아웃", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences pref = activity.getSharedPreferences(PREFERENCE, MODE_PRIVATE);

                        // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                        SharedPreferences.Editor editor = pref.edit();
                        // key값에 value값을 저장한다.
                        // String, boolean, int, float, long 값 모두 저장가능하다.
                        editor.clear();
                        // 메모리에 있는 데이터를 저장장치에 저장한다.
                        editor.commit();

                        Toast.makeText(activity,"로그아웃이 완료되었습니다.",Toast.LENGTH_LONG).show();


                        Intent intent = activity.getIntent();
                        activity.finish();
                        startActivity(intent);
                    }
                });
                builder.setNeutralButton("종료하기", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        }
    }



}