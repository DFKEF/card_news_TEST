package com.gunho0406.imagecash;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {
    String sId,sPw, sPw_chk,sName, sEmail;
    EditText et_id, et_pw, et_pw_chk,et_email, et_name;
    String url = "http://192.168.2.2/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        tedPermission();

        et_id = (EditText) findViewById(R.id.createid);
        et_pw = (EditText) findViewById(R.id.createpw);
        et_pw_chk = (EditText) findViewById(R.id.checkpw);
        et_email = (EditText) findViewById(R.id.email);
        et_name = (EditText) findViewById(R.id.name);
        Button joinBtn = (Button) findViewById(R.id.join);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_Join(v);
            }
        });


    }

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

    }

    public void bt_Join(View view)
    {
        /* 버튼을 눌렀을 때 동작하는 소스 */
        sId = et_id.getText().toString();
        sPw = et_pw.getText().toString();
        sPw_chk = et_pw_chk.getText().toString();
        sEmail = et_email.getText().toString();
        sName = et_name.getText().toString();

        if(sId.isEmpty()||sPw.isEmpty()||sEmail.isEmpty()||sName.isEmpty()) {
            Toast.makeText(this,"모든 정보를 입력해주세요",Toast.LENGTH_LONG).show();

        }else{
            if(sPw.equals(sPw_chk))
            {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    Toast.makeText(SignupActivity.this,"이메일 형식이 아닙니다",Toast.LENGTH_SHORT).show();
                }else{
                    if(sEmail.contains("@esan.hs.kr")){
                        Log.e("qq","dd");
                        registDB registDB = new registDB(sId,sPw,sEmail,sName);
                        registDB.execute();
                    }else{
                        Toast.makeText(SignupActivity.this,"학교에서 받은 이메일을 입력해 주세요\n(형식: esan_xx_xxxxx@esan.hs.kr)",Toast.LENGTH_SHORT).show();
                    }
                }


            }
            else
            {
                Toast.makeText(this,"패스워드가 일치하지 않습니다",Toast.LENGTH_LONG).show();

            }
        }

    }

    public class registDB extends AsyncTask<Void, Integer, Void> {
        String sId, sPw, sEmail, sName;
        public registDB(String sId, String sPw, String sEmail, String sName) {
            this.sId = sId;
            this.sPw = sPw;
            this.sEmail = sEmail;
            this.sName = sName;
        }

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + sId + "&u_pw=" + sPw + "" + "&u_email" + sEmail + "&u_name" + sName;
            try {
                /* 서버연결 */
                URL home = new URL(
                        url+"join.php");
                HttpURLConnection conn = (HttpURLConnection) home.openConnection();
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

                Log.e("RECV DATA",data);

                if(data.equals("0000")) {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                    Toast.makeText(getApplicationContext(),"회원이 되신 것을 환영합니다!",Toast.LENGTH_LONG).show();
                }
                else {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    Toast.makeText(getApplicationContext(),"에러 발생!",Toast.LENGTH_LONG).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }



}