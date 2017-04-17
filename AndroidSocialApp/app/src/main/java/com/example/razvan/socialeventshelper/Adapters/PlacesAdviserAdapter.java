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
import com.example.razvan.socialeventshelper.Models.PlacesAdviserModel;
import com.example.razvan.socialeventshelper.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlacesAdviserAdapter extends RecyclerView.Adapter<PlacesAdviserAdapter.MyViewHolder> {

    private List<PlacesAdviserModel> eventsList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTitle;

        public MyViewHolder(View view) {
            super(view);
            eventTitle = (TextView) view.findViewById(R.id.event_title);
        }
    }

    public PlacesAdviserAdapter(List<PlacesAdviserModel> eventsList, Context context) {
        this.eventsList = eventsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.each_place, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final PlacesAdviserModel event = eventsList.get(position);
        holder.eventTitle.setText(event.getPlaceName());
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
