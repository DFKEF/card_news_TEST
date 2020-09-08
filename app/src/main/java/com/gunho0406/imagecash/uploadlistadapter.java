package com.gunho0406.imagecash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class uploadlistadapter extends RecyclerView.Adapter<uploadlistadapter.ViewHolder> {
    Context context;
    ArrayList<String> filelist = new ArrayList<>();
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
    public void onBindViewHolder(@NonNull uploadlistadapter.ViewHolder holder, int position) {
        holder.filename.setText(filelist.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return filelist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView filename;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = (TextView) itemView.findViewById(R.id.filename);
        }
    }
}
