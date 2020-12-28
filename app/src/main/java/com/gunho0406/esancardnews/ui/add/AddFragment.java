package com.gunho0406.esancardnews.ui.add;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.gunho0406.esancardnews.CustomDialog;
import com.gunho0406.esancardnews.LoginActivity;
import com.gunho0406.esancardnews.R;
import com.gunho0406.esancardnews.Request;
import com.gunho0406.esancardnews.URLConnector;
import com.gunho0406.esancardnews.Upload;
import com.gunho0406.esancardnews.uploadlistadapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import static android.content.Context.MODE_PRIVATE;

public class AddFragment extends Fragment {

    private AddViewModel addViewModel;
    ArrayList<String> filelist = new ArrayList<>();
    ArrayList<File> file_cur = new ArrayList<File>();
    private static final int PICK_FROM_ALBUM = 1;
    File tempFile;
    String fileurl;
    URLConnector task;
    uploadlistadapter adapter;
    RecyclerView recyclerView;
    String url = "http://13.209.232.72/";
    public final String PREFERENCE = "userinfo";
    int serverResponseCode = 0;
    ArrayList<String> urllist = new ArrayList<>();
    String subjectrow, subject, teacher;
    String date_text, date_row;
    Activity activity;
    ArrayList<String> teacherlist = new ArrayList<>();
    ArrayList<String> subjectlist = new ArrayList<>();
    View root;
    int isChecked = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addViewModel =
                ViewModelProviders.of(this).get(AddViewModel.class);
        root = inflater.inflate(R.layout.fragment_add, container, false);
        tedPermission();
        Intent intent = activity.getIntent();
        subjectrow = intent.getStringExtra("subject");
        teacher = intent.getStringExtra("teacher");

        SharedPreferences pref = activity.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        final String res = pref.getString("userID", "");
        Log.e("res",res);

        if (res.isEmpty()) {
            Intent in = new Intent(activity, LoginActivity.class);
            startActivity(in);
        } else {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


            Date date = Calendar.getInstance().getTime();
            date_text = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(date);
            date_row = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date);


            startTeachers();
            ArrayList<String> list = new ArrayList<>();
            for(int i= 0; i<subjectlist.size(); i++) {
                list.add(subjectlist.get(i)+"/"+teacherlist.get(i));
                Log.e("나나",list.get(i));
            }
            final Spinner selectTeacher = (Spinner) root.findViewById(R.id.spinner2);
            ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, list);
            selectTeacher.setAdapter(adapter);
            selectTeacher.setSelection(0);
            selectTeacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    subjectrow = subjectlist.get(position);
                    teacher = teacherlist.get(position);
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
                    isChecked = 9;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            init(root);





            Button uploadbutton = (Button) root.findViewById(R.id.addbutton);
            uploadbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToAlbum();
                }
            });


            Button uploadbtn = (Button) root.findViewById(R.id.uploadbutton);
            uploadbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        public void run() {
                            SharedPreferences pref = activity.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                            final String sId = pref.getString("userID", "");
                            for (int i = 0; i < filelist.size(); i++) {
                                int j = i + 1;
                                uploadFile("/storage/emulated/0/" + filelist.get(i), sId, i, subject, date_text);
                                urllist.add(sId + "_" + j + ".jpg");
                            }
                        }
                    }).start();
                }
            });

        }

        Button sendbtn = (Button) root.findViewById(R.id.sendbtn);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTitle = (EditText) root.findViewById(R.id.editTitle);
                EditText editContent = (EditText) root.findViewById(R.id.editContent);
                final String title = editTitle.getText().toString();
                final String content = editContent.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("검토").setMessage("제목 : "+title+"\n과목 : "+subjectrow+"/"+teacher+"이 맞습니까?");

                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(title.isEmpty()||urllist.isEmpty()) {
                            Toast.makeText(activity,"사진 업로드 또는 제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                        }else if(isChecked==0) {
                            Toast.makeText(activity,"선생님이 올바르지 않습니다.",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            SharedPreferences pref = activity.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                            final String sId = pref.getString("userID","");
                            AddFragment.Parse parse = new AddFragment.Parse(sId,title,content, subject,subjectrow,date_text,date_row,teacher);
                            parse.execute();
                        }
                    }
                });

                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(activity, "다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return root;
    }

    private void init(View root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.uploadview);
        uploadlistadapter adapter = new uploadlistadapter(activity,filelist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }






    private String getRealPathFromURI(Uri contentURI) {

        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);

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

        TedPermission.with(activity)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
                    cursor = activity.getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();

                    tempFile = new File(cursor.getString(column_index));
                    fileurl = getRealPathFromURI(photoUri);
                    String title = fileurl.replace("/storage/emulated/0/", "");
                    filelist.add(title);
                    init(root);

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
                Toast.makeText(activity, "선생님께 업로드 요청되었습니다!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, Request.class);
                intent.putExtra("code",9);
                activity.startActivity(intent);
                activity.finish();
                Log.e("설마", String.valueOf(data));
            }else{
                Log.e("dd", String.valueOf(data));
                Toast.makeText(activity,"에러 발생!",Toast.LENGTH_LONG).show();
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


            activity.runOnUiThread(new Runnable() {
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

                    activity.runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(activity, "사진 업로드가 완료되었습니다.",
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

                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, "에러 발생!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
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
}