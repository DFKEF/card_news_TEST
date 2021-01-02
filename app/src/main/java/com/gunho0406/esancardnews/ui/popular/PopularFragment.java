package com.gunho0406.esancardnews.ui.popular;

import android.app.Activity;
import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gunho0406.esancardnews.Item;
import com.gunho0406.esancardnews.PopularRecycler;
import com.gunho0406.esancardnews.R;
import com.gunho0406.esancardnews.URLConnector;
import com.gunho0406.esancardnews.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PopularFragment extends Fragment {

    private PopularViewModel popularViewModel;

    View root;
    PopularRecycler month_adapter, week_adapter, all_adapter;
    RecyclerView month_rview, week_rview, all_rview;
    ArrayList<Item> list = new ArrayList<>();
    ArrayList<Item> list_week = new ArrayList<>();
    ArrayList<Item> list_all = new ArrayList<>();
    String url = "http://13.209.232.72/";
    URLConnector task;
    String userID;
    public final String PREFERENCE = "userinfo";
    String user,bitmap,title,date,verify,subject, content, getid;
    int imgnum,like_num;
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
        popularViewModel =
                ViewModelProviders.of(this).get(PopularViewModel.class);
        root = inflater.inflate(R.layout.fragment_popular, container, false);
        Toolbar toolbar = activity.findViewById(R.id.toolbar_);
        toolbar.setTitle("인기글");
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        //list.add(new Item("황수현","http://13.209.232.72/cards/gunho0406_20200914_0403_science_1.jpg","미세먼지 싫다","2021-01-02","사회","",2,"shtngus0506",100));
        //list.add(new Item("황수현","http://13.209.232.72/cards/gunho0406_20201003_2322_etc_1.jpg","미세먼지 싫다","2021-01-02","사회","",2,"shtngus0506",90));
        //list.add(new Item("황수현","http://13.209.232.72/cards/gunho0406_20200915_0353_society_1.jpg","카드뉴스 외않함 으어어어","2021-01-02","사회","",2,"shtngus0506",87));
        startTask_Month();
        startTask_Week();
        startTask_All();

        init_month(root);
        init_week(root);
        init_all(root);
        return root;
    }

    private void init_month(View v){
        month_rview = (RecyclerView) v.findViewById(R.id.pop_month);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false);
        month_rview.setLayoutManager(layoutManager);
        month_adapter = new PopularRecycler(activity, list);
        month_rview.setAdapter(month_adapter);
    }

    private void init_week(View v){
        week_rview = (RecyclerView) v.findViewById(R.id.pop_week);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false);
        week_rview.setLayoutManager(layoutManager);
        week_adapter = new PopularRecycler(activity, list_week);
        week_rview.setAdapter(week_adapter);
    }

    private void init_all(View v){
        all_rview = (RecyclerView) v.findViewById(R.id.pop_all);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false);
        all_rview.setLayoutManager(layoutManager);
        all_adapter = new PopularRecycler(activity, list_all);
        all_rview.setAdapter(all_adapter);
    }

    private void Parse_Month(String result) throws JSONException {
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

        if(list.isEmpty()) {
            list.add(new Item("황수현","http://13.209.232.72/cards/test_img_1.png","카드뉴스를 제작해주세요!","2021-01-02","기타","제발루ㅠㅠㅠㅠ",2,"shtngus0506",100));
        }
    }

    private void startTask_Month(){
        task = new URLConnector(url+"parse_like.php");
        task.start();
        try{
            task.join();
        }
        catch(InterruptedException e){

        }
        String result = task.getResult();

        try {
            Parse_Month(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Parse_Week(String result) throws JSONException {
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
                list_week.add(new Item(user,bitmap,title,date,subject,content,imgnum,getid,like_num));
            }
        }

        if(list_week.isEmpty()) {
            Log.e("ㅎㅇ","ㅎㅎ");
            list_week.add(new Item("황수현","http://13.209.232.72/cards/test_img_1.png","카드뉴스를 제작해주세요!","2021-01-02","기타","제발루ㅠㅠㅠㅠ",2,"shtngus0506",100));
        }
    }

    private void startTask_Week(){
        task = new URLConnector(url+"parse_like_week.php");
        task.start();
        try{
            task.join();
        }
        catch(InterruptedException e){

        }
        String result = task.getResult();

        try {
            Parse_Week(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Parse_All(String result) throws JSONException {
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
                list_all.add(new Item(user,bitmap,title,date,subject,content,imgnum,getid,like_num));
            }
        }

        if(list_all.isEmpty()) {
            Log.e("ㅎㅇ","ㅎㅎ");
            list_all.add(new Item("황수현","http://13.209.232.72/cards/test_img_1.png","카드뉴스를 제작해주세요!","2021-01-02","기타","제발루ㅠㅠㅠㅠ",2,"shtngus0506",100));
        }
    }

    private void startTask_All(){
        task = new URLConnector(url+"parse_like_alltime.php");
        task.start();
        try{
            task.join();
        }
        catch(InterruptedException e){

        }
        String result = task.getResult();

        try {
            Parse_All(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}