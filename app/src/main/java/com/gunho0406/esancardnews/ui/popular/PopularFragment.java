package com.gunho0406.esancardnews.ui.popular;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.gunho0406.esancardnews.Item;
import com.gunho0406.esancardnews.R;
import com.gunho0406.esancardnews.RecyclerAdapter;
import com.gunho0406.esancardnews.URLConnector;
import com.gunho0406.esancardnews.ui.home.HomeViewModel;

import java.util.ArrayList;

public class PopularFragment extends Fragment {

    private PopularViewModel popularViewModel;

    View root;
    RecyclerAdapter adapter;
    RecyclerView rview;
    ArrayList<Item> list = new ArrayList<>();
    String url = "http://13.209.232.72/";
    URLConnector task;
    String userID;
    public final String PREFERENCE = "userinfo";
    String user,bitmap,title,date,verify,subject, content, getid;
    int imgnum,like_num;
    ArrayList<String> teacherlist = new ArrayList<>();
    ArrayList<String> subjectlist = new ArrayList<>();

    String result;
    private HomeViewModel homeViewModel;

    Activity activity;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        popularViewModel =
                ViewModelProviders.of(this).get(PopularViewModel.class);
        root = inflater.inflate(R.layout.fragment_popular, container, false);
        Toolbar toolbar = activity.findViewById(R.id.toolbar_);
        toolbar.setTitle("인기글");
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        return root;
    }
}