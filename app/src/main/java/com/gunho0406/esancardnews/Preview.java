package com.gunho0406.esancardnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class Preview extends AppCompatActivity {
    ViewPager2 viewpager2;
    Context context;
    String url = "http://13.209.232.72/";
    ArrayList<String> bitmaplist = new ArrayList<>();
    ArrayList<Item> list = new ArrayList<>();
    String bitmaprow, bitmap;
    TextView titletxt, contenttxt, contentSub, contentUser;

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
        int imgnum = i.getIntExtra("imgnum",0);
        list.add(new Item(user,bitmaprow,title,date,subject,content,imgnum, getid));
        for(int j=1; j<=imgnum; j++) {
            bitmap = bitmaprow.replace("_1.jpg","_"+j+".jpg");
            Log.e("dhjdh",bitmap);
            bitmaplist.add(bitmap);
        }

        context = this;

        titletxt = (TextView) findViewById(R.id.content_title);
        contenttxt = (TextView) findViewById(R.id.content_text);
        contentSub = (TextView) findViewById(R.id.content_subject);
        contentUser = (TextView) findViewById(R.id.content_user);

        titletxt.setText(title);
        contenttxt.setText(content);
        contentSub.setText(subject+" | "+date);
        contentUser.setText(user);

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
}