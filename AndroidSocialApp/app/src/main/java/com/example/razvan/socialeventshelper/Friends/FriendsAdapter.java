package com.example.razvan.socialeventshelper.Friends;

/**
 * Created by Razvan on 2/7/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.razvan.socialeventshelper.R;
import com.example.razvan.socialeventshelper.Utils.RoundedTransformation;
import com.squareup.picasso.Picasso;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

    private List<FriendsModel> friendsList;
    private final OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(FriendsModel item);
        void onItemClickChat(FriendsModel item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView friendAvatar;
        public TextView friendName;
        public TextView friendStatus;
        public ImageView deleteFriend;
        public ImageView chat;

        public MyViewHolder(View view) {
            super(view);
            friendAvatar = (ImageView) view.findViewById(R.id.friend_avatar);
            friendName = (TextView) view.findViewById(R.id.friend_name);
            friendStatus = (TextView) view.findViewById(R.id.friend_status);
            deleteFriend = (ImageView) view.findViewById(R.id.delete_friend);
            chat = (ImageView) view.findViewById(R.id.chat);
        }
    }

    public FriendsAdapter(List<FriendsModel> friendsList, Context context, OnItemClickListener listener) {
        this.friendsList = friendsList;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.each_friend, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FriendsModel friend = friendsList.get(position);
        holder.friendName.setText(friend.getFriendName());
        if(friend.getFriendStatus() == 0) {
            holder.friendStatus.setText("Offline");
            holder.friendStatus.setTextColor(Color.parseColor("#F44336"));
        }
        else{
            holder.friendStatus.setText("Online");
            holder.friendStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
        if(friend.getFriendAvatar() == null)
            holder.friendAvatar.setImageResource(R.drawable.ic_face_black_24dp);
        else
            Picasso.with(context).load(friend.getFriendAvatar()).transform(new RoundedTransformation()).into(holder.friendAvatar);

        holder.deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(friend);
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClickChat(friend);
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}
