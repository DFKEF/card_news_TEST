package com.gunho0406.esancardnews;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

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
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    String sId,sPw, sPw_chk,sName, sEmail,lock_pw;
    EditText et_id, et_pw, et_pw_chk,et_email, et_name;
    String url = "http://13.209.232.72/";
    Context context;
    int serverResponseCode = 0;
    private static final int PICK_FROM_ALBUM = 1;
    File tempFile;
    String fileurl;
    ArrayList<String> file = new ArrayList<>();
    public final String PREFERENCE = "salt";


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
        context = this;

        Button profileImg = (Button) findViewById(R.id.imageButton);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_Join(v,context);
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

    public void bt_Join(View view, final Context context)
    {
        new Thread(new Runnable() {
            public void run() {
                /* 버튼을 눌렀을 때 동작하는 소스 */
                sId = et_id.getText().toString();
                sPw = et_pw.getText().toString();
                sPw_chk = et_pw_chk.getText().toString();
                sEmail = et_email.getText().toString();
                sName = et_name.getText().toString();



                if (sId.isEmpty() || sPw.isEmpty() || sEmail.isEmpty() || sName.isEmpty()) {
                    Toast.makeText(context, "모든 정보를 입력해주세요", Toast.LENGTH_LONG).show();

                } else {
                    if (sPw.equals(sPw_chk)) {
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                            Toast.makeText(SignupActivity.this, "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                        } else {
                            if (sEmail.contains("@esan.hs.kr")) {
                                if (sId.trim().length() < 5) {
                                    Toast.makeText(SignupActivity.this, "아이디는 최소 5자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (!Pattern.matches("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$", sPw)) {
                                        Toast.makeText(SignupActivity.this, "비밀번호 형식을 지켜주세요.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if(file.isEmpty()) {
                                            registDB registDB = new registDB(sId, sPw, sEmail, sName, context,sId+"_profile.jpg");
                                            registDB.execute();
                                        }else{
                                            String filecur = file.get(0);
                                            uploadFile(filecur, sId);
                                            Log.e("mmm", String.valueOf(serverResponseCode));
                                            if (serverResponseCode == 200) {
                                                registDB registDB = new registDB(sId, sPw, sEmail, sName, context,sId+"_profile.jpg");
                                                registDB.execute();
                                            }
                                        }

                                    }
                                }
                            } else {
                                Toast.makeText(SignupActivity.this, "학교에서 받은 이메일을 입력해 주세요\n(형식: esan_xx_xxxxx@esan.hs.kr)", Toast.LENGTH_SHORT).show();
                            }
                        }


                    } else {
                        Toast.makeText(context, "패스워드가 일치하지 않습니다", Toast.LENGTH_LONG).show();

                    }
                }
            }
        }).start();

    }

    public class registDB extends AsyncTask<Void, Integer, Integer> {
        String sId, sPw, sEmail, sName, sProfile;
        Context context;
        int finishcode = 0;
        public registDB(String sId, String sPw, String sEmail, String sName, Context context, String sProfile) {
            this.sId = sId;
            this.sPw = sPw;
            this.sEmail = sEmail;
            this.sName = sName;
            this.context = context;
            this.sProfile = sProfile;
            //this.sProfile = sProfile;
        }

        @Override
        protected Integer doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + sId + "&u_pw=" + sPw + "&u_email=" + sEmail + "&u_name=" + sName + "&u_profile=" + sProfile;
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

                if(data.equals("0000")) {
                    finishcode = 9;
                    Log.e("RESULT","성공적으로 처리되었습니다!");

                }
                else {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    Log.e("dd DATA",sId+sEmail+sName+sPw);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return finishcode;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer == 9) {
                Intent intent = new Intent(context,LoginActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
                Log.e("설마", String.valueOf(integer));
            }else{
                Log.e("dd", String.valueOf(integer));
                Toast.makeText(context,"에러 발생! 아이디 혹은 이메일이 중복되었습니다.",Toast.LENGTH_LONG).show();
                super.onPostExecute(integer);
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

                            Toast.makeText(SignupActivity.this, "사진 업로드가 완료되었습니다.",
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
                        Toast.makeText(SignupActivity.this, "에러 발생!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SignupActivity.this, "Got Exception : see logcat ",
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