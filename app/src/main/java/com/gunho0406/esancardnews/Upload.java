package com.gunho0406.esancardnews;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Upload extends AppCompatActivity {
    ArrayList<String> filelist = new ArrayList<>();
    ArrayList<File> file_cur = new ArrayList<File>();
    private static final int PICK_FROM_ALBUM = 1;
    File tempFile;
    String fileurl;
    uploadlistadapter adapter;
    RecyclerView recyclerView;
    Context context;
    String url = "http://13.209.232.72/";
    public final String PREFERENCE = "userinfo";
    int serverResponseCode = 0;
    ArrayList<String> urllist = new ArrayList<>();
    String subjectrow, subject, teacher;
    String date_text, date_row;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        tedPermission();
        Intent intent = getIntent();
        subjectrow = intent.getStringExtra("subject");
        teacher = intent.getStringExtra("teacher");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        Date date = Calendar.getInstance().getTime();
        date_text = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(date);
        date_row = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date);

        switch (subjectrow) {
            case "국어":
                subject = "korean";
                break;
            case "수학" :
                subject = "math";
                break;
            case "영어" :
                subject = "english";
                break;
            case "과학" :
                subject = "science";
                break;
            case "사회" :
                subject = "society";
                break;
            default:
                subject = "etc";
                break;

        }

        context = this;

        init();



        Button uploadbutton = (Button) findViewById(R.id.addbutton);
        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });



        ImageButton uploadbtn = (ImageButton) findViewById(R.id.uploadbutton);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                        final String sId = pref.getString("userID","");
                        for(int i=0; i<filelist.size();i++) {
                            int j = i+1;
                            uploadFile("/storage/emulated/0/"+filelist.get(i),sId,i,subject,date_text);
                            urllist.add(sId+"_"+j+".jpg");
                        }
                    }
                }).start();
            }
        });

    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.uploadview);
        uploadlistadapter adapter = new uploadlistadapter(context,filelist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        EditText editTitle = (EditText) findViewById(R.id.editTitle);
        EditText editContent = (EditText)findViewById(R.id.editContent);
        final String title = editTitle.getText().toString();
        final String content = editContent.getText().toString();
        int id = item.getItemId();




        //noinspection SimplifiableIfStatement
        if(id==R.id.next){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("검토").setMessage("제목 : "+title+"\n과목 : "+subject+"/"+teacher+"이 맞습니까?");

            builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if(title.isEmpty()||urllist.isEmpty()) {
                        Toast.makeText(getApplicationContext(),"사진 업로드 또는 제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                        final String sId = pref.getString("userID","");
                        Parse parse = new Parse(sId,title,content, subject,subjectrow,date_text,date_row,teacher);
                        parse.execute();
                    }
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

        return result;
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

    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
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
                    String title = fileurl.replace("/storage/emulated/0/", "");
                    filelist.add(title);
                    init();

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }

        }
    }



    public class Parse extends AsyncTask<Void, Integer, Integer> {

        String data = "";
        String sId, title, content,subject,bitmap,subjectrow, date_text, date_row,teacher;
        int imgnum;
        int finishcode = 0;

        public Parse(String sId, String title, String content, String subject, String subjectrow, String date_text, String date_row, String teacher) {
            this.sId = sId;
            this.title = title;
            this.content = content;
            this.subject = subject;
            this.subjectrow = subjectrow;
            this.date_text = date_text;
            this.date_row = date_row;
            this.teacher = teacher;
        }
        @Override
        protected Integer doInBackground(Void... unused) {
            //인풋 파라메터값 생성
            Date date = Calendar.getInstance().getTime();
            Log.d("webnautes", date_text);
            imgnum = urllist.size();
            Log.e("size",String.valueOf(imgnum));

            bitmap = sId+"_"+date_text+"_"+subject+"_1.jpg";

            String param = "u_id=" + sId + "&title=" + title + "&date=" + date_row + "&content=" + content + "&bitmap=" + bitmap + "&subject=" + subjectrow + "&imgnum=" + imgnum + "&teacher=" + teacher;
            try {
                // 서버연결
                URL home = new URL(
                        url+"uploadarticle.php");
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
                    Log.e("TEST",sId+title+date_text+bitmap+subject+teacher);

                }
                else {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    Log.e("dd DATA",sId+title+date_text+bitmap+subject+teacher);
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
                Toast.makeText(getApplicationContext(), "선생님께 업로드 요청되었습니다!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,Request.class);
                intent.putExtra("code",9);
                context.startActivity(intent);
                ((Activity)context).finish();
                Log.e("설마", String.valueOf(data));
            }else{
                Log.e("dd", String.valueOf(data));
                Toast.makeText(context,"에러 발생!",Toast.LENGTH_LONG).show();
                super.onPostExecute(data);
            }

        }
    }



    public int uploadFile(String sourceFileUri, String sId, int pos, String subject, String date_text) {


        String fileName = sourceFileUri;
        String num = String.valueOf(pos+1);
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {


            runOnUiThread(new Runnable() {
                public void run() {

                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL home = new URL(url+"upload.php");
                String img = sId+"_"+date_text+"_"+subject+"_"+num+".jpg";
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

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + img + "" + lineEnd);
                Log.e("hello",img);

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

                            Toast.makeText(Upload.this, "사진 업로드가 완료되었습니다.",
                                    Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Upload.this, "에러 발생!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Upload.this, "Got Exception : see logcat ",
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