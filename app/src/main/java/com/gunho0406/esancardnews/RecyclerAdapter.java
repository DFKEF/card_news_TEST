package com.gunho0406.esancardnews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Item> list = new ArrayList<Item>();
    private String sId;

    public RecyclerAdapter(Context context, ArrayList<Item> item) {
        this.context = context;
        this.list = item;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, final int position) {
        String url = list.get(position).bitmap;
        final ArrayList<Item> samplelist = new ArrayList<>();
        String home = "http://13.209.232.72/";

        Glide.with(context)
                .load(url)
                .centerCrop()
                .override(600,600)
                .into(holder.imageView);


        holder.title.setText(list.get(position).title);
        holder.user.setText(list.get(position).user);
        holder.date.setText(list.get(position).date);
        holder.subject.setText(list.get(position).subject);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                samplelist.add(list.get(position));
                Intent i = new Intent(context,Preview.class);
                i.putExtra("user",list.get(position).user);
                i.putExtra("title",list.get(position).title);
                i.putExtra("date",list.get(position).date);
                i.putExtra("bitmap",list.get(position).bitmap);
                i.putExtra("subject",list.get(position).subject);
                i.putExtra("content",list.get(position).content);
                i.putExtra("imgnum",list.get(position).imgnum);
                i.putExtra("id",list.get(position).getid);
                String profile = list.get(position).getid+"_profile.jpg";
                i.putExtra("profile",home+"profiles/"+profile);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView title, user, date, subject;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            user = (TextView) itemView.findViewById(R.id.user);
            date = (TextView) itemView.findViewById(R.id.date);
            subject = (TextView) itemView.findViewById(R.id.subject);


        }
    }
}
