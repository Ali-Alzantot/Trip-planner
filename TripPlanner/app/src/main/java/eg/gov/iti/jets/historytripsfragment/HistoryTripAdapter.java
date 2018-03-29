package eg.gov.iti.jets.historytripsfragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.Serializable;
import java.util.List;

import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.picassopkg.ImageHelper;
import eg.gov.iti.jets.tripplanner.R;
import eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces.HistoryPresenterInterface;
import eg.gov.iti.jets.viewtrip.ViewTripActivity;

/**
 * Created by USAMA on 07/03/2018.
 */

public class HistoryTripAdapter extends RecyclerView.Adapter<HistoryTripAdapter.TripViewHolder> implements Serializable {


    private List<Trip> tripList;
    private Context context;
    private HistoryPresenterInterface presenter;

    private FragmentManager supportFragmentManager;

    public HistoryTripAdapter(FragmentManager supportFragmentManager, Context Context, List<Trip> tripList, HistoryPresenterInterface presenter) {
        this.context = Context;
        this.supportFragmentManager = supportFragmentManager;
        this.tripList = tripList;
        this.presenter = presenter;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_trip_card, parent, false);
        return new TripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TripViewHolder holder, int position) {
        final Trip trip = tripList.get(position);
        holder.setTrip(trip);
//        holder.getCardView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goToDetailsActivity(trip);
//            }
//        });
        holder.getTripName().setText(trip.getTripName());
        holder.getStartPoint().setText(trip.getStartPoint());
        holder.getEndPoint().setText(trip.getEndPoint());
        holder.getSchedule().setText(trip.getStartDate() + " At " + trip.getStartTime());
        switch (trip.getStatus()) {
            case Trip.ENDED:
                holder.getStatus().setTextColor(Color.GREEN);
                break;
            case Trip.CANCELLED:
                holder.getStatus().setTextColor(Color.RED);
                break;
        }

        holder.getStatus().setText(trip.getStatus());


//        if (trip.getPhoto() != null && !trip.getPhoto().trim().equals("")) {
//            ImageHelper.getImage(context, holder.getTripPhoto(), trip.getPhoto(),R.drawable.default_trip_photo);
//        } else {
//            holder.getTripPhoto().setImageResource(R.drawable.default_trip_photo);
//        }

        holder.getDelButton().

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(" Delete Trip ").setMessage(" Do you want to delete " + trip.getTripName() + " trip ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.deleteTripFromDB(trip);
                            }
                        })
                                .setNegativeButton("No", null).show();
                    }
                });

    }

    private void goToDetailsActivity(Trip trip) {
        Intent intent = new Intent(context, ViewTripActivity.class);
        intent.putExtra("trip", (Serializable) trip);
        context.startActivity(intent);
    }


    public void addItem(Trip trip) {
        if (tripList.indexOf(trip) == -1) {
            tripList.add(trip);
            notifyItemInserted(tripList.indexOf(trip));
            notifyDataSetChanged();
        }
    }

    public int deleteItem(Trip trip) {
        int position = tripList.indexOf(trip);
        tripList.remove(trip);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tripList.size());
        return position;

    }

    public void updateItem(Trip trip) {
        int position = tripList.indexOf(trip);
        tripList.remove(trip);
        tripList.add(position, trip);
        notifyItemChanged(position);

    }

    public void updateList(List<Trip> trips) {
        tripList = trips;
        notifyItemRangeChanged(0, tripList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }


    // View Holder
    public class TripViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        //  private ImageView tripPhoto;
        private ImageView delButton;
        private TextView tripName, startPoint, endPoint, schedule, status;
        private CardView cardView;
        private SupportMapFragment pastTripMapFragment;
        private Trip trip;
        private GoogleMap googleMap;

        public TripViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.history_card_view);
            // tripPhoto = (ImageView) view.findViewById(R.id.history_trip_photo);
            delButton = (ImageView) view.findViewById(R.id.historyDelBtn);
            tripName = (TextView) view.findViewById(R.id.history_trip_title);
            startPoint = (TextView) view.findViewById(R.id.history_start_point);
            endPoint = (TextView) view.findViewById(R.id.history_end_point);
            schedule = (TextView) view.findViewById(R.id.history_date_time);
            status = (TextView) view.findViewById(R.id.history_tripStatus);
            pastTripMapFragment = SupportMapFragment.newInstance();
            supportFragmentManager.beginTransaction().replace(R.id.pastTripMapContainer, pastTripMapFragment).commit();
            pastTripMapFragment.getMapAsync(this);

        }


        public void setTrip(Trip trip) {
            this.trip = trip;
        }


        public ImageView getDelButton() {
            return delButton;
        }

        public TextView getTripName() {
            return tripName;
        }

        public TextView getStartPoint() {
            return startPoint;
        }

        public TextView getEndPoint() {
            return endPoint;
        }

        public TextView getSchedule() {
            return schedule;
        }

        public TextView getStatus() {
            return status;
        }

        public CardView getCardView() {
            return cardView;
        }

        public void drawLine(Trip trip) {
            if (googleMap != null) {
                googleMap.addPolygon(new PolygonOptions().add(new LatLng(trip.getStartLongitude(), trip.getStartLongitude()), new LatLng(trip.getEndLatitude(), trip.getEndLongitude())));
            } else {
                Log.i("esraa", "gmnull");
            }

        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.i("esraa", "hnaa1");
            this.googleMap = googleMap;
            googleMap.setPadding(100, 100, 100, 100);
            if (trip != null) {
                LatLng start = new LatLng(trip.getStartLongitude(), trip.getStartLongitude());
                googleMap.addMarker(new MarkerOptions().position(start));
                LatLng end = new LatLng(trip.getEndLatitude(), trip.getEndLongitude());
                googleMap.addMarker(new MarkerOptions().position(end));
                LatLngBounds.Builder mapBounds = new LatLngBounds.Builder();
                mapBounds.include(start);
                mapBounds.include(end);
                googleMap.addPolygon(new PolygonOptions().add(start, end).strokeColor(Color.BLUE).strokeWidth(10f));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBounds.build(), 0));

            } else {
                Log.i("esraa", "trip is null");
            }
        }
    }

}
