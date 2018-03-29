package eg.gov.iti.jets.upcomingtripsfragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import eg.gov.iti.jets.AlarmActivity.AlarmHelper;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.edittripactivity.EditTripActivity;
import eg.gov.iti.jets.floatingnotespkg.ShowDirectionWithPermission;
import eg.gov.iti.jets.picassopkg.ImageHelper;
import eg.gov.iti.jets.tripplanner.R;
import eg.gov.iti.jets.upcomingtripsfragment.upcomingfragmentinterfaces.UpcomingPresenterInterface;
import eg.gov.iti.jets.viewtrip.ViewTripActivity;


/**
 * Created by USAMA on 07/03/2018.
 */

public class UpcomingTripAdapter extends RecyclerView.Adapter<UpcomingTripAdapter.TripViewHolder> implements Serializable {


    private List<Trip> tripList;
    private Context context;
    private transient UpcomingPresenterInterface presenter;

    public UpcomingTripAdapter(Context Context, List<Trip> tripList, UpcomingPresenterInterface presenter) {
        this.context = Context;
        this.tripList = tripList;
        this.presenter = presenter;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_trip_card, parent, false);
        return new TripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TripViewHolder holder, int position) {
        final Trip trip = tripList.get(position);
        Bitmap photo = null;
        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToViewTripActivity(trip);
            }
        });
        holder.getTripPhoto().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToViewTripActivity(trip);
            }
        });
        holder.getTripName().setText(trip.getTripName());
        holder.getSchedule().setText(trip.getStartDate() + "  At  " + trip.getStartTime());
        holder.getStatus().setText(trip.getStatus());
        switch (trip.getStatus()) {
            case Trip.UPCOMING:
                holder.getStatus().setTextColor(Color.BLUE);
                break;
            case Trip.ONGOING:
                holder.getStatus().setTextColor(Color.GREEN);
                break;

        }
        holder.getStatus().setText(trip.getStatus());
        if (trip.getPhoto() != null && !trip.getPhoto().trim().equals("")) {
            ImageHelper.getImage(context, holder.getTripPhoto(), trip.getPhoto(), R.drawable.default_trip_photo);
        } else {
            holder.getTripPhoto().setImageResource(R.drawable.default_trip_photo);
        }


        holder.getThreeDotMenu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.getThreeDotMenu(), trip);
            }
        });

        if (trip.getStatus().equals(Trip.ONGOING)) {
            holder.getGoButton().setVisibility(View.INVISIBLE);
            holder.getFinishBtn().setVisibility(View.VISIBLE);
        } else {
            holder.getGoButton().setVisibility(View.VISIBLE);
            holder.getFinishBtn().setVisibility(View.INVISIBLE);
        }

        holder.getGoButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!trip.getStatus().equals(Trip.ONGOING)) {
                    trip.setStatus(Trip.ONGOING);
                    presenter.updateTripInDB(trip);
                }
                showDirections(trip);
            }
        });
        holder.getFinishBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    trip.setStatus(Trip.ENDED);
                    presenter.updateTripInDB(trip);
            }
        });

    }

    private void showDirections(Trip trip) {
        Intent intent = new Intent(context, ShowDirectionWithPermission.class);
        intent.putExtra("trip", trip);
        context.startActivity(intent);
    }

    private void goToViewTripActivity(Trip trip) {//esraaaaaa
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

    /**
     * Showing popup menu when tapping on 3 dots image
     */
    private void showPopupMenu(View view, final Trip trip) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.upcoming_three_dots_menu, popup.getMenu());
        // 3 dot menu
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {//esraaa
                    // edit option
                    case R.id.upcoming_edit_trip_option:
                        Toast.makeText(context, "Edit Trip" + trip.getTripId(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, EditTripActivity.class);
                        intent.putExtra("trip", trip);
                        context.startActivity(intent);
                        return true;

                    // delete option
                    case R.id.upcoming_delete_trip_option:
                        if (trip.getGoAndReturn().equals(Trip.GO_AND_RETURN)) {
                            View checkBoxView = View.inflate(context, R.layout.alert_dialog_round_trip_delete, null);
                            final CheckBox chkBox = checkBoxView.findViewById(R.id.chkBoxDelRoundTrip);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(" Delete Trip ").setMessage(" Do you want to delete " + trip.getTripName() + " ?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (chkBox.isChecked()) {
                                                presenter.deleteTripFromDB(trip);
                                                AlarmHelper.cancelAlarm(context, trip.getUserId(), trip.getTripId());
                                            } else {
                                                trip.setGoAndReturn(Trip.ONE_WAY);
                                                presenter.updateTripInDB(trip);
                                            }
                                        }
                                    })
                                    .setView(checkBoxView)
                                    .setNegativeButton("No", null)
                                    .setCancelable(false).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(" Delete Trip ").setMessage(" Do you want to delete " + trip.getTripName() + " trip ?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            presenter.deleteTripFromDB(trip);
                                            AlarmHelper.cancelAlarm(context, trip.getUserId(), trip.getTripId());
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .setCancelable(false).show();
                        }
                        return true;
                    default:
                }
                return false;
            }
        });
        popup.show();
    }


    @Override
    public int getItemCount() {
        return tripList.size();
    }


    // View Holder
    public class TripViewHolder extends RecyclerView.ViewHolder {//esraaaaa
        private ImageView tripPhoto, threeDotMenu, goButton, finishBtn;
        private TextView tripName, schedule, status;
        private CardView cardView;

        public TripViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.upcoming_card_view);
            tripPhoto = (ImageView) view.findViewById(R.id.upcoming_trip_photo);
            threeDotMenu = (ImageView) view.findViewById(R.id.upcoming_three_dots);
            goButton = (ImageView) view.findViewById(R.id.upcoming_goButton);
            finishBtn = (ImageView) view.findViewById(R.id.upcoming_finishBtn);
            tripName = (TextView) view.findViewById(R.id.upcoming_trip_title);
            schedule = (TextView) view.findViewById(R.id.upcoming_date_time);
            status = (TextView) view.findViewById(R.id.upcoming_tripStatus);
        }

        public CardView getCardView() {
            return cardView;
        }

        public ImageView getTripPhoto() {
            return tripPhoto;
        }

        public ImageView getThreeDotMenu() {
            return threeDotMenu;
        }

        public ImageView getGoButton() {
            return goButton;
        }

        public TextView getTripName() {
            return tripName;
        }

        public TextView getSchedule() {
            return schedule;
        }

        public TextView getStatus() {
            return status;
        }

        public ImageView getFinishBtn() {
            return finishBtn;
        }
    }


}
