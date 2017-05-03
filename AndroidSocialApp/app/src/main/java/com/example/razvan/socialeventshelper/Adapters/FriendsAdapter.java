package com.example.razvan.socialeventshelper.Adapters;

/**
 * Created by Razvan on 2/7/2017.
 */

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.razvan.socialeventshelper.Models.FriendsModel;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
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
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView friendAvatar;
        public TextView friendName;
        public TextView friendStatus;

        public MyViewHolder(View view) {
            super(view);
            friendAvatar = (ImageView) view.findViewById(R.id.friend_avatar);
            friendName = (TextView) view.findViewById(R.id.friend_name);
            friendStatus = (TextView) view.findViewById(R.id.friend_status);
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
        holder.friendStatus.setText("Offline");
        Picasso.with(context).load(friend.getFriendAvatar()).transform(new RoundedTransformation()).into(holder.friendAvatar);

        holder.friendAvatar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(friend);
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}
