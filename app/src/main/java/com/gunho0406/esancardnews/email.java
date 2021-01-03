package com.gunho0406.esancardnews;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class email extends AppCompatActivity {

    String home = "http://13.209.232.72/";
    public final String PREFERENCE = "userinfo";
    String sId, email;
    EditText modEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        Toolbar toolbar = findViewById(R.id.toolbar_e);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        sId = intent.getStringExtra("ID");
        modEmail = (EditText) findViewById(R.id.modEmail);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(email.this);

                builder.setTitle("알림").setMessage(R.string.email_fab);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        email = modEmail.getText().toString();
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Log.e("email",email);
                            Toast.makeText(getApplicationContext(), "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                        }else{
                            if (email.contains("@esan.hs.kr")) {
                                verifyParse parse = new verifyParse(sId,email);
                                parse.execute();
                            }else{
                                Toast.makeText(getApplicationContext(), "학교에서 받은 이메일을 입력해 주세요\n(형식: esan_xx_xxxxx@esan.hs.kr)", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(),"다시 확인해주세요.",Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
    }


    public class verifyParse extends AsyncTask<Void, Integer, Integer> {

        String data = "";
        String sId;
        String email;
        int imgnum;
        int finishcode = 0;

        public verifyParse(String sId,String email) {
            this.sId = sId;
            this.email = email;
        }
        @Override
        protected Integer doInBackground(Void... unused) {
            //인풋 파라메터값 생성

            String param = "id=" + sId + "&email=" + email;
            try {
                // 서버연결
                URL url = new URL(home
                        +"mod_email.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
                    Log.e("TEST",sId+email);

                }
                else {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    Log.e("dd DATA",sId+email);
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
                SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);

                // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                SharedPreferences.Editor editor = pref.edit();
                // key값에 value값을 저장한다.
                // String, boolean, int, float, long 값 모두 저장가능하다.
                editor.clear();
                // 메모리에 있는 데이터를 저장장치에 저장한다.
                editor.commit();

                Toast.makeText(getApplicationContext(),"로그아웃이 완료되었습니다.\n앱을 다시 실행시켜 로그인해주세요.",Toast.LENGTH_LONG).show();

                Log.e("설마", String.valueOf(data));

                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                } // 액티비티 종료 + 태스크 리스트에서 지우기
                android.os.Process.killProcess(android.os.Process.myPid());
            }else{
                Log.e("dd", String.valueOf(data));
                super.onPostExecute(data);
                Toast.makeText(getApplicationContext(),"에러!",Toast.LENGTH_LONG).show();
            }

        }
    }
}