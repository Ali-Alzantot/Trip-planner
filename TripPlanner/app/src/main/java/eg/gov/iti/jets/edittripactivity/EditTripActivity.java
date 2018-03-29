package eg.gov.iti.jets.edittripactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import eg.gov.iti.jets.AlarmActivity.AlarmHelper;
import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.databasepkg.FirebaseDatabaseDAO;
import eg.gov.iti.jets.databasepkg.SaveImageCallBack;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.edittripactivity.interfaces.EditTripPresenterInterface;
import eg.gov.iti.jets.edittripactivity.interfaces.EditTripViewInterface;
import eg.gov.iti.jets.helperclasses.MyDatePicker;
import eg.gov.iti.jets.helperclasses.MyTimePicker;
import eg.gov.iti.jets.helperinterfaces.MyDialogInterface;
import eg.gov.iti.jets.tripplanner.NavigationDrawerActivity;
import eg.gov.iti.jets.tripplanner.R;

public class EditTripActivity extends AppCompatActivity implements EditTripViewInterface, MyDialogInterface, Serializable, SaveImageCallBack {

    private Place to_place;
    private Bitmap to_placeBitmap;
    private String endPointPrev;

    private EditTripPresenterInterface presenterInterfaceRef;
    private MyDialogInterface myDialogInterface;
    private final int MY_External_Strorage_PERMISSION_CODE = 53;

    //components
    private transient EditText tripName_input;
    private transient SupportPlaceAutocompleteFragment place_autocomplete_fragment_from;
    private transient SupportPlaceAutocompleteFragment place_autocomplete_fragment_to;
    private transient EditText date_EditText;
    private transient EditText time_EditText;
    private transient Button createTripDone_btn;
    private transient SwitchCompat roundedTrip;

    private String dateString;
    private String timeString;
    private Trip trip;

    private int flagDonePressed;
    private static String GOOGLE_API_FRAG_FROM = "FROM_FRAGMENT";
    private static String GOOGLE_API_FRAG_TO = "TO_FRAGMENT";


    private FirebaseDatabaseDAO firebaseDatabaseDAO;

    private transient ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBModel.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        progressDialog = new ProgressDialog(this,
                R.style.com_facebook_auth_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating Trip...");


        firebaseDatabaseDAO = new FirebaseDatabaseDAO();
        //intialize interfaces ref
        presenterInterfaceRef = new Presenter(this, this);
        myDialogInterface = this;

        //get ref of objs
        tripName_input = (EditText) findViewById(R.id.tripName_input);
        //place fragments
        place_autocomplete_fragment_from = new SupportPlaceAutocompleteFragment();
        place_autocomplete_fragment_to = new SupportPlaceAutocompleteFragment();
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.place_autocomplete_fragment_from, place_autocomplete_fragment_from, GOOGLE_API_FRAG_FROM);
        tr.replace(R.id.place_autocomplete_fragment_to, place_autocomplete_fragment_to, GOOGLE_API_FRAG_TO);
        tr.commit();

        date_EditText = (EditText) findViewById(R.id.date_EditText);
        time_EditText = (EditText) findViewById(R.id.time_EditText);


        //intialize Btns
        createTripDone_btn = (Button) findViewById(R.id.createTripDone_btn);

        roundedTrip = (SwitchCompat) findViewById(R.id.roundedTrip);
        roundedTrip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    trip.setGoAndReturn(Trip.GO_AND_RETURN);
                } else {
                    trip.setGoAndReturn(Trip.ONE_WAY);
                }
            }
        });


        if ((Trip) getIntent().getSerializableExtra("trip") != null) {
            trip = (Trip) getIntent().getSerializableExtra("trip");
        } else if ((Trip) savedInstanceState.getSerializable("trip") != null) {
            trip = (Trip) savedInstanceState.getSerializable("trip");
        }


    }


    @Override
    protected void onStart() {
        super.onStart();

        //intialize inputs
        if (trip != null) {
            tripName_input.setText(trip.getTripName());
            place_autocomplete_fragment_from.setText(trip.getStartPoint());
            place_autocomplete_fragment_to.setText(trip.getEndPoint());
            endPointPrev = trip.getEndPoint();
            date_EditText.setText(trip.getStartDate());
            time_EditText.setText(trip.getStartTime());
            dateString = trip.getStartDate();
            timeString = trip.getStartTime();
            if (trip.getGoAndReturn().equals(Trip.ONE_WAY)) {
                roundedTrip.setChecked(false);
            } else if (trip.getGoAndReturn().equals(Trip.GO_AND_RETURN)) {
                roundedTrip.setChecked(true);
            }
        }


        //place fragments liseners
        place_autocomplete_fragment_from.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // from_place = place;
                trip.setStartPoint(place.getName().toString());
                trip.setStartLatitude(place.getLatLng().latitude);
                trip.setStartLongitude(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(), "error in selecting place from", Toast.LENGTH_SHORT);
            }
        });

        place_autocomplete_fragment_to.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                to_place = place;
                Log.i("esraa", "toplacename==" + to_place.getName());
                trip.setEndPoint(place.getName().toString());
                trip.setEndLatitude(place.getLatLng().latitude);
                trip.setEndLongitude(place.getLatLng().longitude);

            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT);
            }
        });

        //btn listener
        createTripDone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flagDonePressed == 0) {
                    if (checkDate(date_EditText.getText().toString(), time_EditText.getText().toString())) {
                        if ((trip != null) && (trip.getEndPoint().trim().equals("") == false)) {
                            flagDonePressed = 1;
                            progressDialog.show();
                            if (trip.getStatus().equals(Trip.HANGING)||trip.getStatus().equals(Trip.ONGOING)) {
                                trip.setStatus(Trip.UPCOMING);
                            }
                            presenterInterfaceRef.updateTrip(trip);
                            if (endPointPrev.equals(trip.getEndPoint()) == false)//save pic l trip l gded
                            {

                                getPlacePicAndSave();
                                trip.setTripName(tripName_input.getText().toString());
                                presenterInterfaceRef.updateTrip(trip);

                            } else {
                                presenterInterfaceRef.updateTrip(trip);
                                gotoUpcommingActivity();
                            }


                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter destination", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), " Enter A valid date", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


        date_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateDialogFragment = new MyDatePicker();
                Bundle bundle = new Bundle();
                bundle.putSerializable("myDialogInterface", (Serializable) myDialogInterface);
                dateDialogFragment.setArguments(bundle);
                dateDialogFragment.show(getSupportFragmentManager(), "dateDialogFragment");

            }
        });

        time_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeDialogFragment = new MyTimePicker();
                Bundle bundle = new Bundle();
                bundle.putSerializable("myDialogInterface", (Serializable) myDialogInterface);
                timeDialogFragment.setArguments(bundle);
                timeDialogFragment.show(getSupportFragmentManager(), "timeDialogFragment");

            }
        });


    }


    @Override
    public void setDateString(int day, int month, int year) {
        this.dateString = day + "/" + month + "/" + year;
        date_EditText.setText(dateString);
        trip.setStartDate(dateString);
    }

    @Override
    public void setTimeString(int hourOfDay, int min) {

        this.timeString = hourOfDay + ":" + min;
        time_EditText.setText(timeString);
        trip.setStartTime(timeString);
    }

    public void gotoUpcommingActivity() {
        firebaseDatabaseDAO.createAndUpdateTripOnFirebase(trip);
        AlarmHelper.setAlarm(EditTripActivity.this, trip);
        Toast.makeText(getApplicationContext(), "Trip edited successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditTripActivity.this, NavigationDrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        progressDialog.dismiss();
        finish();

    }


    public void getPlacePicAndSave() {
        final GeoDataClient geoDataClient = Places.getGeoDataClient(EditTripActivity.this, null);
        Task<PlacePhotoMetadataResponse> placePhotoMetadataResponseTask = geoDataClient.getPlacePhotos(to_place.getId());
        Task<PlacePhotoMetadataResponse> placePhotoMetadataResponseTask1 = placePhotoMetadataResponseTask.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                PlacePhotoMetadataResponse placePhotoMetadataResponse = task.getResult();
                PlacePhotoMetadataBuffer placePhotoMetadataBuffer = placePhotoMetadataResponse.getPhotoMetadata();
                if ((placePhotoMetadataBuffer != null)) {
                    Iterator iterator = placePhotoMetadataBuffer.iterator();
                    if (iterator.hasNext()) {
                        PlacePhotoMetadata placePhotoMetadata = placePhotoMetadataBuffer.get(0);
                        if (placePhotoMetadata != null) {
                            Task<PlacePhotoResponse> placePhotoResponse = geoDataClient.getPhoto(placePhotoMetadata);
                            placePhotoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                    PlacePhotoResponse placePhotoResponse1 = task.getResult();
                                    to_placeBitmap = placePhotoResponse1.getBitmap();
                                    new FirebaseDatabaseDAO().uploadTripImage(EditTripActivity.this, to_placeBitmap, to_place.getName().toString());
                                }
                            });
                        } else {
                            gotoUpcommingActivity();
                        }

                    } else {
                        gotoUpcommingActivity();
                    }
                } else {
                    gotoUpcommingActivity();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("trip", trip);

    }

    private boolean checkDate(String date, String time) {
        boolean res = false;
        SimpleDateFormat sdformat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        Date datee = null;
        try {
            datee = sdformat.parse(time + " " + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        if (datee.getTime() > c.getTimeInMillis())
            res = true;

        return res;
    }

    @Override
    public void savedImageUrl(String url) {
        if (url != null) {
            trip.setPhoto(url);
            presenterInterfaceRef.updateTrip(trip);
        }
        gotoUpcommingActivity();
    }
}

