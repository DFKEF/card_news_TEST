package com.gunho0406.esancardnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class SettingsActivity extends AppCompatActivity {

    int num = 0;
    public final String PREFERENCE = "userinfo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("설정");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        toolbar.setSubtitleTextColor(Color.parseColor("#FAFAFA"));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        List<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);

        ListView listview = (ListView)findViewById(R.id.listview);
        listview.setAdapter(adapter);
        FrameLayout set = (FrameLayout) findViewById(R.id.settings);
        set.setVisibility(GONE);

        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        final String result = pref.getString("userID","");

        //리스트뷰의 아이템을 클릭시 해당 아이템의 문자열을 가져오기 위한 처리
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {

                if(position==0) {
                        Log.e("ID",result);
                        if(result.isEmpty()) {
                            Toast.makeText(getApplicationContext(),"먼저 로그인해주세요", Toast.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent(getApplicationContext(),Account.class);
                            intent.putExtra("ID",result);
                            startActivity(intent);
                        }

                }

                //클릭한 아이템의 문자열을 가져옴
                if(position==1) {
                    if(num==0) {
                        set.setVisibility(View.VISIBLE);
                        Log.e("dd", String.valueOf(num));
                        num += 1;
                    }else if (num==1) {
                        set.setVisibility(GONE);
                        Log.e("dd", String.valueOf(num));
                        num = 0;
                    }

                }
            }
        });
        list.add("계정 설정");
        list.add("앱 설정");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}