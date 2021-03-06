package com.gunho0406.esancardnews;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class uploadlistadapter extends RecyclerView.Adapter<uploadlistadapter.ViewHolder> {
    Context context;
    ArrayList<String> filelist = new ArrayList<>();
    ArrayList<String > file_cur = new ArrayList<>();
    public uploadlistadapter(Context context, ArrayList filelist) {
        this.context = context;
        this.filelist = filelist;
    }


    @NonNull
    @Override
    public uploadlistadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.upload_list, parent, false);
        return new uploadlistadapter.ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull uploadlistadapter.ViewHolder holder, final int position) {
        holder.filename.setText(filelist.get(position).toString());
        Uri home = Uri.parse("/storage/emulated/0/"+filelist.get(position));
        holder.preview.setImageURI(home);
        holder.clearImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filelist.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, filelist.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filelist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView filename;
        ImageButton clearImg;
        ImageView preview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = (TextView) itemView.findViewById(R.id.filename);
            clearImg = (ImageButton) itemView.findViewById(R.id.clearbtn);
            preview = (ImageView) itemView.findViewById(R.id.uploadImg);
        }
    }
}
