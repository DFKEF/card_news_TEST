package com.gunho0406.esancardnews;

import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity {

    String result;
    public final String PREFERENCE = "userinfo";
    String url = "http://13.209.232.72/";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_mypage)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        result = pref.getString("userID","");

        ImageView userBtn = (ImageView) findViewById(R.id.userBtn_);
        userBtn.setBackground(new ShapeDrawable(new OvalShape()));
        userBtn.setClipToOutline(true);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result == "") {
                    Bottom bottom = Bottom.getInstance();
                    bottom.show(getSupportFragmentManager(),"bottom");
                }else{
                    BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance();
                    bottomSheetDialog.show(getSupportFragmentManager(),"bottomSheet");
                }
            }
        });

        if(result == "") {
            Log.e("d","sfd");
            Glide.with(this)
                    .load(R.drawable.ic_baseline_account_circle_24)
                    .override(200,200)
                    .into(userBtn);
        }else{
            String profile = result+"_profile.jpg";
            Log.e("profile",result+"_profile.jpg");
            Glide.with(this)
                    .load(url+"profiles/"+profile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE )
                    .skipMemoryCache(true)
                    .error(R.drawable.ic_baseline_account_circle_24)
                    .fallback(R.drawable.ic_baseline_account_circle_24)
                    .centerCrop()
                    .override(200,200)
                    .into(userBtn);
        }


    }

}