package com.example.razvan.socialeventshelper.Adapters;

/**
 * Created by Razvan on 2/7/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.R;
import com.example.razvan.socialeventshelper.Utils.ImageLoadTask;
import com.squareup.picasso.Picasso;


import java.util.List;

public class MainEventsAdapter extends RecyclerView.Adapter<MainEventsAdapter.MyViewHolder> {

    private List<MainEventsModel> eventsList;
    private final OnItemClickListener listener;
    private Context context;


    public interface OnItemClickListener {
        void onItemClick(MainEventsModel item);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView coverPhoto;
        public TextView takingPlace;
        public TextView eventDay;
        public TextView eventMonth;
        public TextView eventHour;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            coverPhoto = (ImageView) view.findViewById(R.id.event_cover);
            takingPlace = (TextView) view.findViewById(R.id.event_street_name);
            eventDay = (TextView) view.findViewById(R.id.event_day);
            eventMonth = (TextView) view.findViewById(R.id.event_month);
            eventHour = (TextView) view.findViewById(R.id.event_start_time);
        }
    }


    public MainEventsAdapter(List<MainEventsModel> eventsList,Context context,OnItemClickListener listener) {
        this.eventsList = eventsList;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.each_event_main, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MainEventsModel event = eventsList.get(position);
        holder.title.setText(event.getTitle());
        holder.takingPlace.setText(event.getTakingPlace());
        holder.eventDay.setText(event.getEventDay());
        holder.eventMonth.setText(event.getEventMonth());
        holder.eventHour.setText("Start time: "+event.getEventHour());
        Picasso.with(context).load(event.getCoverPhoto()).fit().into(holder.coverPhoto);

        holder.coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(event);
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
