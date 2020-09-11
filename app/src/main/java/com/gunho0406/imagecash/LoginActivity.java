package com.gunho0406.imagecash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    String sId, sPw;
    String home = "http://192.168.2.2/";
    public final String PREFERENCE = "userinfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        Button loginbtn = (Button) findViewById(R.id.login);
        final EditText etId = (EditText) findViewById(R.id.editid);
        final EditText etPw = (EditText) findViewById(R.id.editpassword);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sId = etId.getText().toString();
                sPw = etPw.getText().toString();
                bt_Login(v,sId,sPw);
            }
        });

        TextView signup = (TextView)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

    public void bt_Login(View v, String sId, String sPw) {

        try{

        }catch (NullPointerException e)
        {
            Log.e("err",e.getMessage());
        }

        loginDB lDB = new loginDB(sId,sPw,v);
        lDB.execute();



    }
    public class loginDB extends AsyncTask<Void, Integer, String> {

        String data = "";
        String sId, sPw;
        View v;

        public loginDB(String sId, String sPw, View v) {
            this.sId = sId;
            this.sPw = sPw;
            this.v = v;
        }
        @Override
        protected String doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + sId + ""+ "&u_pw=" + sPw + "";
            Log.e("POST",param);
            try {
                Log.e("제발","되어라");
                /* 서버연결 */
                URL url = new URL(
                        home+"login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
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

                /* 서버에서 응답 */
                Log.e("RECV DATA",data);

                if(data.equals("0"))
                {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                }
                else
                {
                    Log.e("RESULT","에러우 발생! ERRCODE = " + data);
                }

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
            if (data.equals("1")) {
                Log.e("RESULT", "성공적으로 처리되었습니다!");

                SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);

                // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                SharedPreferences.Editor editor = pref.edit();
                // key값에 value값을 저장한다.
                // String, boolean, int, float, long 값 모두 저장가능하다.
                editor.putString("userID",sId);
                // 메모리에 있는 데이터를 저장장치에 저장한다.
                editor.commit();


                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("userID",sId);
                startActivity(i);
                finish();
            } else if (data.equals("0")) {
                Log.e("RESULT", "비밀번호가 일치하지 않습니다.");
                Toast.makeText(getApplicationContext(),"아이디나 비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
            } else {
                Log.e("RESULT", "에러ㅅㅂ 발생! ERRCODE = " + data);
                Toast.makeText(getApplicationContext(),"아이디나 비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
            }


        }
    }


}