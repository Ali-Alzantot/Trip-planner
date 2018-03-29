package eg.gov.iti.jets.viewtrip;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.Serializable;

import eg.gov.iti.jets.AlarmActivity.AlarmActivity;
import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.edittripactivity.EditTripActivity;
import eg.gov.iti.jets.floatingnotespkg.ShowDirectionWithPermission;
import eg.gov.iti.jets.notes.viewnotes.ViewNotes;
import eg.gov.iti.jets.picassopkg.ImageHelper;
import eg.gov.iti.jets.tripplanner.NavigationDrawerActivity;
import eg.gov.iti.jets.tripplanner.R;
import eg.gov.iti.jets.viewtrip.interfaces.ViewTripPresenterInterface;
import eg.gov.iti.jets.viewtrip.interfaces.ViewTripViewInterface;

public class ViewTripActivity extends AppCompatActivity implements ViewTripViewInterface {

    private transient ImageView placeImage;
    private transient TextView placeName_view;
    private transient ImageView upcoming_goButton, upcoming_finishBtn;
    private transient ImageView upcoming_three_dots;
    private transient ImageView goToNotes;
    private transient TextView upcoming_tripStatus;
    private transient TextView upcoming_start_point;
    private transient TextView upcoming_end_point;
    private transient TextView upcoming_date_time;


    private Trip trip;
    private ViewTripPresenterInterface presenterInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBModel.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtrip);


        if (savedInstanceState != null && savedInstanceState.getSerializable("presenterInterface") != null) {
            presenterInterface = (ViewTripPresenterInterface) savedInstanceState.getSerializable("presenterInterface");
        } else {
            presenterInterface = new Presenter(this, this);
        }

        //load data from bundle
        if ((Trip) getIntent().getSerializableExtra("trip") != null) {
            trip = (Trip) getIntent().getSerializableExtra("trip");
        } else {
            trip = (Trip) savedInstanceState.getSerializable("trip");
        }

        //get ref of objs
        placeImage = (ImageView) findViewById(R.id.placeImage);
        placeName_view = (TextView) findViewById(R.id.placeName_view);
        upcoming_goButton = (ImageView) findViewById(R.id.upcoming_goButton);
        upcoming_finishBtn = (ImageView) findViewById(R.id.view_trip_finishBtn);
        upcoming_three_dots = (ImageView) findViewById(R.id.upcoming_three_dots);
        upcoming_tripStatus = (TextView) findViewById(R.id.upcoming_tripStatus);
        upcoming_start_point = (TextView) findViewById(R.id.upcoming_start_point);
        upcoming_end_point = (TextView) findViewById(R.id.upcoming_end_point);
        upcoming_date_time = (TextView) findViewById(R.id.upcoming_date_time);
        goToNotes = (ImageView) findViewById(R.id.goToNotes);

        if (trip.getStatus().equals(Trip.ONGOING)) {
            upcoming_goButton.setVisibility(View.INVISIBLE);
            upcoming_finishBtn.setVisibility(View.VISIBLE);
        } else if (trip.getStatus().equals(Trip.ENDED)) {
            upcoming_goButton.setVisibility(View.INVISIBLE);
            upcoming_finishBtn.setVisibility(View.INVISIBLE);
        } else {
            upcoming_goButton.setVisibility(View.VISIBLE);
            upcoming_finishBtn.setVisibility(View.INVISIBLE);
        }

        upcoming_goButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                upcoming_goButton.setVisibility(View.INVISIBLE);
                upcoming_finishBtn.setVisibility(View.VISIBLE);
                trip.setStatus(Trip.ONGOING);
                upcoming_tripStatus.setText(trip.getStatus());
                presenterInterface.updateTripInDB(trip);
                showDirections( trip);
            }
        });
        upcoming_finishBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                upcoming_goButton.setVisibility(View.INVISIBLE);
                upcoming_finishBtn.setVisibility(View.INVISIBLE);
                trip.setStatus(Trip.ENDED);
                upcoming_tripStatus.setText(trip.getStatus());
                presenterInterface.updateTripInDB(trip);
            }
        });
        upcoming_three_dots.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });
        goToNotes.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTripActivity.this, ViewNotes.class);
                intent.putExtra("trip", trip);
                startActivity(intent);
            }
        });

    }

    private void showDirections(Trip trip) {
        Intent intent = new Intent(ViewTripActivity.this, ShowDirectionWithPermission.class);
        intent.putExtra("trip", trip);
        ViewTripActivity.this.startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (trip != null) {
            placeName_view.setText(trip.getTripName());
            upcoming_tripStatus.setText(trip.getStatus());
            upcoming_start_point.setText(trip.getStartPoint());
            upcoming_end_point.setText(trip.getEndPoint());
            upcoming_date_time.setText(trip.getStartDate() + " at " + trip.getStartTime());
        }

        if (trip.getPhoto() != null && !trip.getPhoto().trim().equals("")) {
            ImageHelper.getImage(this, placeImage, trip.getPhoto(), R.drawable.default_trip_photo);

        } else {
            placeImage.setImageResource(R.drawable.default_trip_photo);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("trip", trip);
        outState.putSerializable("presenterInterface", (Serializable) presenterInterface);
    }


    public void showPopupMenu() {
        //3DOTS menu
        View threeDotsView = (ImageView) findViewById(R.id.upcoming_three_dots);
        PopupMenu popupMenu = new PopupMenu(this, threeDotsView);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.upcoming_edit_trip_option:
                        Intent intent = new Intent(ViewTripActivity.this, EditTripActivity.class);
                        intent.putExtra("trip", trip);
                        ViewTripActivity.this.startActivity(intent);
                        return true;

                    // delete option
                    case R.id.upcoming_delete_trip_option:

                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewTripActivity.this);
                        builder.setTitle(" Delete Trip ").setMessage(" Do you want to delete " + trip.getTripName() + " trip ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenterInterface.deleteTripFromDB(trip);
                                Intent intent = new Intent(ViewTripActivity.this, NavigationDrawerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                ViewTripActivity.this.startActivity(intent);

                            }
                        })
                                .setNegativeButton("No", null).show();

                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.upcoming_three_dots_menu, popupMenu.getMenu());
        popupMenu.show();
    }


}

