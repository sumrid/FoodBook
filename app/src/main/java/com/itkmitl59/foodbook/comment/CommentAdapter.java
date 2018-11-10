package com.itkmitl59.foodbook.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itkmitl59.foodbook.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private List<Comment> mComments;
    private Context mContext;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView message;
        public TextView date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.comment_user_name);
            message = itemView.findViewById(R.id.comment_message);
            date = itemView.findViewById(R.id.comment_date);
        }
    }

    public CommentAdapter(List<Comment> mComments, Context mContext) {
        this.mComments = mComments;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment item = mComments.get(position);

        holder.userName.setText(item.getUserID());
        holder.message.setText(item.getMessage());
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        holder.date.setText(format.format(item.getDate()));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }
}
