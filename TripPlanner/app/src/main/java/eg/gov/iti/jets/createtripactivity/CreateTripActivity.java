package eg.gov.iti.jets.createtripactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import eg.gov.iti.jets.createtripactivity.interfaces.CreateTripPresenterInterface;
import eg.gov.iti.jets.createtripactivity.interfaces.CreateTripViewInterface;
import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.databasepkg.FirebaseDatabaseDAO;
import eg.gov.iti.jets.databasepkg.SaveImageCallBack;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.dtos.User;
import eg.gov.iti.jets.helperclasses.MyDatePicker;
import eg.gov.iti.jets.helperclasses.MyTimePicker;
import eg.gov.iti.jets.helperinterfaces.MyDialogInterface;
import eg.gov.iti.jets.tripplanner.NavigationDrawerActivity;
import eg.gov.iti.jets.tripplanner.R;

public class CreateTripActivity extends AppCompatActivity implements CreateTripViewInterface, MyDialogInterface, Serializable, SaveImageCallBack {

    private Place from_place;
    private Place to_place;
    private User user;
    private Bitmap to_placeBitmap;

    private CreateTripPresenterInterface presenterInterfaceRef;
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
    private transient ImageView tripImage;
    private String dateString;
    private String timeString;

    private byte[] imageBytes;
    private Trip trip;
    private int flagDonePressed;
    private String isRoundedTripChecked = Trip.ONE_WAY;

    private static String GOOGLE_API_FRAG_FROM = "FROM_FRAGMENT";

    private static String GOOGLE_API_FRAG_TO = "TO_FRAGMENT";
    private String endPointPrev;

    private FirebaseDatabaseDAO firebaseDatabaseDAO;

    private transient ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBModel.getInstance(this);
        setContentView(R.layout.activity_create_trip);
        firebaseDatabaseDAO = new FirebaseDatabaseDAO();

        progressDialog = new ProgressDialog(this,
                R.style.com_facebook_auth_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving Trip...");


        //intialize interfaces ref
        if (savedInstanceState != null && savedInstanceState.getSerializable("presenterInterfaceRef") != null) {
            presenterInterfaceRef = (CreateTripPresenterInterface) savedInstanceState.getSerializable("presenterInterfaceRef");
        } else {
            presenterInterfaceRef = new CreateTripPresenter(this, this);
        }


        myDialogInterface = this;
        //  tripImage = (ImageView) findViewById(R.id.create_trip_image);
        //get data from prev activity
        if (savedInstanceState != null && savedInstanceState.getParcelable("to_place") != null) {
            to_place = (Place) savedInstanceState.getParcelable("to_place");
        } else if (getIntent().getParcelableExtra("to_place") != null) {
            to_place = (Place) getIntent().getParcelableExtra("to_place");
        }

        if (savedInstanceState != null && savedInstanceState.getByteArray("imageBytes") != null) {
            imageBytes = savedInstanceState.getByteArray("imageBytes");
        } else if (getIntent().getByteArrayExtra("imageBytes") != null) {
            imageBytes = getIntent().getByteArrayExtra("imageBytes");
        }
        if (imageBytes != null) {
            to_placeBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            //tripImage.setImageBitmap(to_placeBitmap);
        }

        user = User.getUser();

        if (to_place != null) {
            endPointPrev = to_place.getName().toString();
        }

        //intialize Btns
        createTripDone_btn = (Button) findViewById(R.id.createTripDone_btn);
        date_EditText = (EditText) findViewById(R.id.date_EditText);
        time_EditText = (EditText) findViewById(R.id.time_EditText);

        roundedTrip = (SwitchCompat) findViewById(R.id.roundedTrip);
        roundedTrip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    isRoundedTripChecked = Trip.GO_AND_RETURN;
                } else {
                    isRoundedTripChecked = Trip.ONE_WAY;
                }
            }
        });


        //place fragments
        place_autocomplete_fragment_from = new SupportPlaceAutocompleteFragment();
        place_autocomplete_fragment_to = new SupportPlaceAutocompleteFragment();
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.place_autocomplete_fragment_from, place_autocomplete_fragment_from, GOOGLE_API_FRAG_FROM);
        tr.replace(R.id.place_autocomplete_fragment_to, place_autocomplete_fragment_to, GOOGLE_API_FRAG_TO);
        tr.commit();


    }


    @Override
    protected void onStart() {
        super.onStart();

        //intialize dest
        tripName_input = (EditText) findViewById(R.id.tripName_input);

        if (to_place != null) {
            place_autocomplete_fragment_to.setText(to_place.getName());//1
            tripName_input.setText("Trip to " + to_place.getName().toString());
        }


        //place fragments liseners
        place_autocomplete_fragment_from.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                from_place = place;
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
                    if (tripName_input != null && !tripName_input.getText().toString().trim().equals("")
                            && from_place != null && to_place != null && dateString != null && timeString != null) {
                        if (checkDate(dateString, timeString)) {
                            flagDonePressed = 1;
                            trip = new Trip(user.getUserId(),
                                    tripName_input.getText().toString(),
                                    from_place.getName().toString(),
                                    to_place.getName().toString(),
                                    timeString,
                                    dateString,
                                    isRoundedTripChecked,
                                    "1",
                                    Trip.UPCOMING,
                                    from_place.getLatLng().longitude, from_place.getLatLng().latitude,
                                    to_place.getLatLng().longitude, to_place.getLatLng().latitude, null);
                            if (trip != null) {
                                progressDialog.show();
                                trip = presenterInterfaceRef.addTrip(trip);
                                getPlacePicAndSave();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Enter valid date", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Please complete trip data", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        date_EditText.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                DialogFragment dateDialogFragment = new MyDatePicker();
                Bundle bundle = new Bundle();
                bundle.putSerializable("myDialogInterface", (Serializable) myDialogInterface);
                dateDialogFragment.setArguments(bundle);
                dateDialogFragment.show(getSupportFragmentManager(), "dateDialogFragment");

            }
        });

        time_EditText.setOnClickListener(new View.OnClickListener()

        {
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
    }

    @Override
    public void setTimeString(int hourOfDay, int min) {

        this.timeString = hourOfDay + ":" + min;
        time_EditText.setText(timeString);
    }


    public void gotoUpcommingActivity() {
        firebaseDatabaseDAO.createAndUpdateTripOnFirebase(trip);
        AlarmHelper.setAlarm(CreateTripActivity.this, trip);
        Toast.makeText(getApplicationContext(), "Trip added.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CreateTripActivity.this, NavigationDrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        progressDialog.dismiss();
        finish();

    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("presenterInterfaceRef", presenterInterfaceRef);
        outState.putParcelable("to_place", (Parcelable) to_place);
        outState.putByteArray("imageBytes", imageBytes);
    }

    public void getPlacePicAndSave() {
        final GeoDataClient geoDataClient = Places.getGeoDataClient(CreateTripActivity.this, null);
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
                                    firebaseDatabaseDAO.uploadTripImage(CreateTripActivity.this, to_placeBitmap, to_place.getName().toString());
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

    public boolean checkDate(String date, String time) {
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

