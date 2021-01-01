package com.gunho0406.esancardnews;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Modify extends AppCompatActivity {

    String url = "http://13.209.232.72/";
    ArrayList<String> bitmaplist = new ArrayList<>();
    public final String PREFERENCE = "userinfo";
    String title, content, bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent in = getIntent();
        title = in.getStringExtra("title");
        content = in.getStringExtra("content");
        bitmap = in.getStringExtra("bitmap");

        EditText titletxt = (EditText) findViewById(R.id.modTitle);
        EditText con = (EditText) findViewById(R.id.modContent);

        titletxt.setText(title);
        con.setText(content);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        EditText editTitle = (EditText) findViewById(R.id.modTitle);
        EditText editContent = (EditText)findViewById(R.id.modContent);
        final String title = editTitle.getText().toString();
        final String content = editContent.getText().toString();

        if(id==R.id.send) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("검토").setMessage("수정하시겠습니까?");

            builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                        final String sId = pref.getString("userID","");
                        Parse parse = new Parse(sId,title,content, bitmap);
                        parse.execute();
                }
            });

            builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    Toast.makeText(getApplicationContext(), "다시 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    public class Parse extends AsyncTask<Void, Integer, Integer > {

        String data = "";
        String sId;
        String title, content, bitmap;
        String imgurl;
        int finishcode = 0;

        public Parse(String sId, String title, String content, String bitmap) {
            this.sId = sId;
            this.title = title;
            this.content = content;
            this.bitmap = bitmap;
        }
        @Override
        protected Integer  doInBackground(Void... unused) {
            //인풋 파라메터값 생성
            SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
            final String id = pref.getString("userID", "");
            String param = "id=" + id+ "&title=" + title+ "&content=" + content+ "&bitmap=" + bitmap;
            try {
                // 서버연결
                URL home = new URL(url
                        +"article_modify.php");
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
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"오류가 발생했습니다!\n계속 발생한다면 개발자에게 문의해주세요.",Toast.LENGTH_LONG).show();
            }
        }
    }
}