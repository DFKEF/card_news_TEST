package com.gunho0406.esancardnews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.view.View.GONE;

public class Preview extends AppCompatActivity {
    ViewPager2 viewpager2;
    Context context;
    String url = "http://13.209.232.72/";
    ArrayList<String> bitmaplist = new ArrayList<>();
    ArrayList<Item> list = new ArrayList<>();
    String bitmaprow, bitmap, sId, bool,code;
    TextView titletxt, contenttxt, contentSub, contentUser, liketxt;
    CheckBox likebox;
    ImageButton deletebtn;
    int like_count;
    public final String PREFERENCE = "user_isliked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doFullScreen();
        setContentView(R.layout.activity_preview);
        Intent i = getIntent();
        String user = i.getStringExtra("user");
        String title = i.getStringExtra("title");
        String date = i.getStringExtra("date");
        bitmaprow = i.getStringExtra("bitmap");
        String subject = i.getStringExtra("subject");
        String content = i.getStringExtra("content");
        String profile = i.getStringExtra("profile");
        String getid = i.getStringExtra("id");
        like_count = i.getIntExtra("like_count",0);
        int imgnum = i.getIntExtra("imgnum",0);
        list.add(new Item(user,bitmaprow,title,date,subject,content,imgnum, getid,like_count));
        for(int j=1; j<=imgnum; j++) {
            bitmap = bitmaprow.replace("_1.jpg","_"+j+".jpg");
            Log.e("dhjdh",bitmap);
            bitmaplist.add(bitmap);
        }

        code = bitmaprow.replace("http://13.209.232.72/cards/","");

        SharedPreferences pref = getSharedPreferences("userinfo", MODE_PRIVATE);
        sId = pref.getString("userID", "");

        context = this;

        likebox = (CheckBox) findViewById(R.id.likebox);
        titletxt = (TextView) findViewById(R.id.content_title);
        contenttxt = (TextView) findViewById(R.id.content_text);
        contentSub = (TextView) findViewById(R.id.content_subject);
        contentUser = (TextView) findViewById(R.id.content_user);
        liketxt = (TextView) findViewById(R.id.previewlike);
        deletebtn = (ImageButton) findViewById(R.id.deletebtn);

        titletxt.setText(title);
        contenttxt.setText(content);
        contentSub.setText(subject+" | "+date);
        contentUser.setText(user);
        liketxt.setText(String.valueOf(like_count));

        ImageView profileimg = (ImageView) findViewById(R.id.profileImg);
        profileimg.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileimg.setClipToOutline(true);
        }
        RequestOptions myOptions = new RequestOptions()
                .centerCrop()
                .format(DecodeFormat.PREFER_ARGB_8888);// or centerCrop



        Glide.with(this)
                .asBitmap()
                .load(profile)
                .centerCrop()
                .apply(new RequestOptions().override(200, 200))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileimg);

        likebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likebox.isChecked()) {
                    Log.e("체크테스트","hgasdfasdf");
                    SharedPreferences pref = getSharedPreferences(code, MODE_PRIVATE);

                    // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                    SharedPreferences.Editor editor = pref.edit();
                    // key값에 value값을 저장한다.
                    // String, boolean, int, float, long 값 모두 저장가능하다.
                    editor.putString("userID",sId);
                    // 메모리에 있는 데이터를 저장장치에 저장한다.
                    editor.commit();
                    Parse parse = new Parse(code,1);
                    parse.execute();
                }else{
                    Log.e("체크해제테스트","hgasdfasdf");
                    SharedPreferences pref = getSharedPreferences(code, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    Parse parse = new Parse(code,2);
                    parse.execute();
                }

            }
        });
        Log.e("who?",sId+"/"+getid);

        if(sId.equals(getid)) {
            deletebtn.setVisibility(View.VISIBLE);
        }else{
            deletebtn.setVisibility(View.GONE);
        }

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Preview.this);


                builder.setTitle("검토").setMessage("정말 삭제하시겠습니까?");

                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Delete setD = new Delete();
                        setD.execute();
                    }
                });

                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });




        SharedPreferences sP = getSharedPreferences(code,MODE_PRIVATE);
        bool = sP.getString("userID","");
        if(bool=="") {
            likebox.setChecked(false);
        }else{
            likebox.setChecked(true);
        }


        new init().execute();
    }


    private void doFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE|
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    public class init extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            viewpager2 = (ViewPager2) findViewById(R.id.image_rview);
            //viewpager2.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);
            ImageRecycler adapter = new ImageRecycler(getApplicationContext(),bitmaplist,list);
            //viewpager2.setLayoutManager(layoutManager);
            viewpager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            viewpager2.setAdapter(adapter);
        }
    }

    public class Parse extends AsyncTask<Void, Integer, Integer> {

        String data = "";
        String code;
        int finishcode = 0;
        int typecode;
        URL home;

        public Parse(String code, int typecode) {
            this.code = code;
            this.typecode = typecode;
        }


        @Override
        protected Integer doInBackground(Void... unused) {
            //인풋 파라메터값 생성

            String param = "bitmap=" + code;
            try {
                // 서버연결
                if(typecode==1) {
                    home = new URL(
                            url+"article_like.php");
                }else if(typecode==2){
                    home = new URL(
                            url+"article_unlike.php");
                }

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
                String data = "";

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

                if(data.equals("0000")) {
                    finishcode = 9;
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                    Log.e("TEST",code);

                }
                else {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    Log.e("dd DATA",code);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return finishcode;
        }

        @Override
        protected void onPostExecute(Integer data) {
            super.onPostExecute(data);
            if(data == 9) {
                if(typecode==1) {
                    Toast.makeText(getApplicationContext(), "추천하셨습니다.", Toast.LENGTH_SHORT).show();
                    like_count = like_count+1;
                    liketxt.setText(String.valueOf(like_count));
                }else if(typecode==2) {
                    Toast.makeText(getApplicationContext(), "추천 해제하셨습니다.", Toast.LENGTH_SHORT).show();
                    like_count = like_count-1;
                    liketxt.setText(String.valueOf(like_count));
                }
                Log.e("설마", String.valueOf(data));
            }else{
                Log.e("dd", String.valueOf(data));
                Toast.makeText(context,"에러 발생!",Toast.LENGTH_LONG).show();
                super.onPostExecute(data);
            }

        }
    }

    public class Delete extends AsyncTask<Void, Integer, Integer > {

        String data = "";
        String sId;
        String imgurl;
        int finishcode = 0;

        public Delete() {
        }
        @Override
        protected Integer  doInBackground(Void... unused) {
            //인풋 파라메터값 생성

            imgurl = list.get(0).bitmap.replaceAll("http://13.209.232.72/cards/","");
            String param = "id=" + list.get(0).getid+ "&bitmap=" + imgurl;
            Log.e("봐봐",param);
            try {
                // 서버연결
                URL home = new URL(url
                        +"article_delete.php");
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
                String data = "";

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


                if(data.equals("0000")) {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                    finishcode = 9;
                }
                else {
                    Log.e("RESULT","에러우 발생! ERRCODE = " + data);
                    finishcode = 0;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return finishcode;
        }

        @Override
        protected void onPostExecute(Integer finishcode) {
            if(finishcode==9) {
                Toast.makeText(getApplicationContext(),"정상적으로 처리되었습니다!",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"오류가 발생했습니다!\n계속 발생한다면 개발자에게 문의해주세요.",Toast.LENGTH_LONG).show();
            }
        }
    }
}