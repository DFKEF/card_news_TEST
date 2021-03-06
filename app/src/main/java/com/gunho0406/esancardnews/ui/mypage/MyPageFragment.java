package com.gunho0406.esancardnews.ui.mypage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gunho0406.esancardnews.Item;
import com.gunho0406.esancardnews.ItemDecoration;
import com.gunho0406.esancardnews.LoginActivity;
import com.gunho0406.esancardnews.R;
import com.gunho0406.esancardnews.RecyclerAdapter;
import com.gunho0406.esancardnews.URLConnector;

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

public class MyPageFragment extends Fragment {

    private MyPageViewModel myPageViewModel;
    ArrayList<Item> list = new ArrayList<>();
    ArrayList<String> urllist = new ArrayList<>();
    RecyclerAdapter adapter;
    RecyclerView rview;
    String url = "http://13.209.232.72/";
    URLConnector task;
    String userID;
    public final String PREFERENCE = "userinfo";
    String user,bitmap,title,date,uId, subject,content,getid;
    int imgnum,like_count;
    String sId;
    Activity activity;
    TextView name;
    String nameres;
    View root;
    String home = "http://13.209.232.72/";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar_);
        toolbar.setTitle("마이페이지");
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        myPageViewModel =
                ViewModelProviders.of(this).get(MyPageViewModel.class);
        root = inflater.inflate(R.layout.fragment_mypage, container, false);
        SharedPreferences pref = activity.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        sId = pref.getString("userID", "");
        if (sId.isEmpty()) {
            LinearLayout layout = (LinearLayout) root.findViewById(R.id.profile_layout);
            layout.setVisibility(View.GONE);
            Intent in = new Intent(activity, LoginActivity.class);
            startActivity(in);
            activity.finish();
        } else{
            TextView myID = (TextView) root.findViewById(R.id.myID);
            myID.setText(sId);
        name = (TextView) root.findViewById(R.id.myname);
        ImageView profile = (ImageView) root.findViewById(R.id.myprofile);
        profile.setBackground(new ShapeDrawable(new OvalShape()));
        profile.setClipToOutline(true);

        startTask();
        getName getName = new getName(sId);
        getName.execute();

        //name.setText(nameres);

        init(root);

        Glide.with(activity)
                .load(home + "profiles/" + sId + "_profile.jpg").placeholder(R.drawable.ic_baseline_account_circle_24)
                .centerCrop()
                .override(500, 500)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profile);

        final SwipeRefreshLayout refreshLayout = root.findViewById(R.id.rflayout_request);
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
        return root;
    }


    private void init(View root){
        rview = (RecyclerView) root.findViewById(R.id.requestview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity,2);
        rview.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(activity, list);
        rview.addItemDecoration(new ItemDecoration(activity));
        rview.setAdapter(adapter);
    }

    private void Parse(String result,String sId) throws JSONException {
        JSONObject root = new JSONObject(result);

        JSONArray ja = root.getJSONArray("result");
        Log.e("uID" , sId);
        for(int i = 0; i < ja.length();i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            uId = jo.getString("userID");
            String valid = jo.getString("verify");
            if(valid.equals("Y")) {
                    if(uId.equals(sId)){
                        user = jo.getString("user");
                        bitmap = url+"cards/"+jo.getString("bitmap");
                        title = jo.getString("title");
                        date = jo.getString("date");
                        subject = jo.getString("subject");
                        content = jo.getString("content");
                        imgnum = jo.getInt("imgnum");
                        getid = jo.getString("userID");
                        like_count = jo.getInt("like_count");
                        list.add(new Item(user,bitmap,title,date,subject,content,imgnum, getid,like_count));
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
            Parse(result,sId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class getName extends AsyncTask<Void, Integer, String> {
        String data;
        String sId;

        public getName(String sId) {
            this.sId = sId;
        }
        @Override
        protected String doInBackground(Void... unused) {
            //인풋 파라메터값 생성
            String param = "u_id=" + sId;
            try {
                // 서버연결
                URL home = new URL(url
                        +"get_name.php");
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
                nameres = data;



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
            name.setText(data);
        }
    }
}