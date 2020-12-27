package com.gunho0406.esancardnews;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.GONE;

public class Account extends AppCompatActivity {
    String url = "http://13.209.232.72/";
    int re;
    int h = 3;
    public final String PREFERENCE = "userinfo";
    ArrayList<Integer> resultlist = new ArrayList<>();
    int serverResponseCode = 0;
    File tempFile;
    String fileurl;
    ArrayList<String> file = new ArrayList<>();
    private static final int PICK_FROM_ALBUM = 1;
    ArrayList<String> idlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("계정 설정");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        toolbar.setSubtitleTextColor(Color.parseColor("#FAFAFA"));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        LinearLayout layout = (LinearLayout) findViewById(R.id.body);
        layout.setVisibility(GONE);
        Intent i = getIntent();
        String id = i.getStringExtra("ID");
        idlist.add(id);
        final EditText txtEdit = new EditText(this);
        txtEdit.setInputType(0x00000081);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle( "비밀번호 입력" );
        dialog.setView(txtEdit);
        dialog.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int which) {
                        String strText = txtEdit.getText().toString();
                        loginDB loginDB = new loginDB(id,strText);
                        loginDB.execute();
                    }
                });
        dialog.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
         dialog.show();

        Button profileImg = (Button) findViewById(R.id.imageButton);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();

            }
        });
        Button deleteBtn = (Button) findViewById(R.id.delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);

                builder.setTitle("계정 삭제").setMessage("계정을 삭제하시겠습니까?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        deleteDB deleteDB = new deleteDB(idlist.get(0));
                        deleteDB.execute();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home://toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            case R.id.ok :
                Log.e("hi","hi");
                    Toast.makeText(this,"성공적으로 처리되었습니다",Toast.LENGTH_LONG).show();
                    finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class deleteDB extends AsyncTask<Void, Integer, String> {

        String data = "";
        String sId;

        public deleteDB(String sId) {
            this.sId = sId;
        }


        @Override
        protected String doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "id=" + sId;
            Log.e("POST",param);
            try {
                Log.e("제발","되어라");
                /* 서버연결 */
                URL home = new URL(
                        url+"deleteID.php");
                HttpURLConnection conn = (HttpURLConnection) home
                        .openConnection();
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

                if(data.equals("0000"))
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

            if (data.equals("0000")) {
                Toast.makeText(getApplicationContext(),"완료",Toast.LENGTH_LONG).show();
                SharedPreferences pref = getApplicationContext().getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                SharedPreferences.Editor editor = pref.edit();
                // key값에 value값을 저장한다.
                // String, boolean, int, float, long 값 모두 저장가능하다.
                editor.clear();
                // 메모리에 있는 데이터를 저장장치에 저장한다.
                editor.commit();
                ActivityCompat.finishAffinity(Account.this);
                System.exit(0);
            }
            else {
                Log.e("RESULT", "에러ㅅㅂ 발생! ERRCODE = " + data);
                finish();
                Toast.makeText(getApplicationContext(),"에러 발생! 다시 시도해 주세요.",Toast.LENGTH_LONG).show();
            }



        }
    }

    public class loginDB extends AsyncTask<Void, Integer, String> {

        String data = "";
        String sId, sPw;

        public loginDB(String sId, String sPw) {
            this.sId = sId;
            this.sPw = sPw;
        }


        @Override
        protected String doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + sId + ""+ "&u_pw=" + sPw + "";
            Log.e("POST",param);
            try {
                Log.e("제발","되어라");
                /* 서버연결 */
                URL home = new URL(
                        url+"login.php");
                HttpURLConnection conn = (HttpURLConnection) home
                .openConnection();
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

                if(data.equals("0000"))
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
                resultlist.add(9);
                LinearLayout layout = (LinearLayout) findViewById(R.id.body);
                layout.setVisibility(View.VISIBLE);
            } else if (data.equals("0")) {
                Log.e("RESULT", "비밀번호가 일치하지 않습니다.");
                resultlist.add(0);
                finish();
                Toast.makeText(getApplicationContext(),"아이디나 비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
            }
            else {
                Log.e("RESULT", "에러ㅅㅂ 발생! ERRCODE = " + data);
                resultlist.add(0);
                finish();
                Toast.makeText(getApplicationContext(),"아이디나 비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
            }


        }
    }

    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private String getRealPathFromURI(Uri contentURI) {

        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();

        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        Log.e("resultdd",result);

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) {
            if(data==null) {

            }else {

                Uri photoUri = data.getData();

                Cursor cursor = null;

                try {

                    /*
                     *  Uri 스키마를
                     *  content:/// 에서 file:/// 로  변경한다.
                     */
                    String[] proj = {MediaStore.Images.Media.DATA};

                    assert photoUri != null;
                    cursor = getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();

                    tempFile = new File(cursor.getString(column_index));
                    fileurl = getRealPathFromURI(photoUri);
                    file.add(fileurl);
                    Button button = (Button) findViewById(R.id.imageButton);
                    button.setText("선택 완료");
                    SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                    final String result = pref.getString("userID","");
                    new Thread(new Runnable() {
                        public void run() {
                            uploadFile(fileurl,result);
                        }}).start();


                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }

        }
    }

    public int uploadFile(String sourceFileUri, String sId) {


        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1000 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {


            runOnUiThread(new Runnable() {
                public void run() {
                    Log.e("제발제발","asdfasdf");
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL home = new URL(url+"profileupload.php");
                String img = sId+"_profile"+".jpg";
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) home.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                Log.e("hello",fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + img + "" + lineEnd);


                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(Account.this, "사진 업로드가 완료되었습니다.",
                                    Toast.LENGTH_SHORT).show();
                            Button button = (Button) findViewById(R.id.imageButton);
                            button.setText("선택 완료");
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {


                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Account.this, "에러 발생!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Account.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }


}