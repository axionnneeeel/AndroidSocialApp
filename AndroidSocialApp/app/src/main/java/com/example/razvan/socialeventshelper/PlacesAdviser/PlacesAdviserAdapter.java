package com.example.razvan.socialeventshelper.PlacesAdviser;

/**
 * Created by Razvan on 2/7/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.razvan.socialeventshelper.R;
import java.util.List;

public class PlacesAdviserAdapter extends RecyclerView.Adapter<PlacesAdviserAdapter.MyViewHolder> {

    private List<PlacesAdviserModel> placesList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView placeName;
        public TextView placeStreet;
        public TextView placeOpenNow;
        public RatingBar placeRating;

        public MyViewHolder(View view) {
            super(view);
            placeName = (TextView) view.findViewById(R.id.place_name);
            placeStreet = (TextView) view.findViewById(R.id.place_street);
            placeOpenNow = (TextView) view.findViewById(R.id.place_open_now);
            placeRating = (RatingBar) view.findViewById(R.id.place_rating);
        }
    }

    public PlacesAdviserAdapter(List<PlacesAdviserModel> placesList, Context context) {
        this.placesList = placesList;
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
        final PlacesAdviserModel place = placesList.get(position);
        holder.placeName.setText(place.getPlaceName());
        holder.placeStreet.setText(place.getPlaceStreet());
        if(place.getPlaceOpenNow().isEmpty()) {
            holder.placeOpenNow.setText("Unknown");
            holder.placeOpenNow.setTextColor(Color.parseColor("#F44336"));
        }
        else if(place.getPlaceOpenNow().equals("true")) {
            holder.placeOpenNow.setText("Open now");
            holder.placeOpenNow.setTextColor(Color.parseColor("#4CAF50"));
        }
        else{
            holder.placeOpenNow.setText("Open now");
            holder.placeOpenNow.setTextColor(Color.parseColor("#F44336"));
        }
        if(!place.getPlaceRating().isEmpty())
            holder.placeRating.setRating(Float.parseFloat(place.getPlaceRating()));
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }
}
