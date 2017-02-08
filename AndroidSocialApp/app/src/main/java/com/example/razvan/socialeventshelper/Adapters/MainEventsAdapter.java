package com.example.razvan.socialeventshelper.Adapters;

/**
 * Created by Razvan on 2/7/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.R;
import com.example.razvan.socialeventshelper.Utils.ImageLoadTask;

import org.w3c.dom.Text;

import java.util.List;

public class MainEventsAdapter extends RecyclerView.Adapter<MainEventsAdapter.MyViewHolder> {

    private List<MainEventsModel> eventsList;

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


    public MainEventsAdapter(List<MainEventsModel> eventsList) {
        this.eventsList = eventsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.each_event_main, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MainEventsModel event = eventsList.get(position);
        holder.title.setText(event.getTitle());
        holder.takingPlace.setText(event.getTakingPlace());
        holder.eventDay.setText(event.getEventDay());
        holder.eventMonth.setText(event.getEventMonth());
        holder.eventHour.setText("Start time: "+event.getEventHour());
        new ImageLoadTask(event.getCoverPhoto(), holder.coverPhoto).execute();
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
