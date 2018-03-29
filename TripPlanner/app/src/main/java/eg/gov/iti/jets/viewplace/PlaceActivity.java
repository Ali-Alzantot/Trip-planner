package eg.gov.iti.jets.viewplace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import eg.gov.iti.jets.createtripactivity.CreateTripActivity;
import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.dtos.User;
import eg.gov.iti.jets.tripplanner.R;

public class PlaceActivity extends AppCompatActivity {

    private Place place;
    private User user;
    private transient GeoDataClient geoDataClient;
    private transient TextView createTrip_btn;
    private transient TextView placeName_view;
    private transient TextView placeAddress;
    private transient TextView placeRating;
    private Bitmap to_placeBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBModel.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewplace);

        //load data from bundle
        place = (Place) getIntent().getParcelableExtra("place");
        user = User.getUser();
        //View view= LayoutInflater.from(this).inflate(R.layout.activity_viewplace,null);
        createTrip_btn = (TextView) this.findViewById(R.id.createTrip_btn);
        if (createTrip_btn == null)
            Log.i("esraa", "null");
        else
            Log.i("esraa", createTrip_btn.toString());

        placeName_view = (TextView) this.findViewById(R.id.placeName_view);
        placeAddress = (TextView) this.findViewById(R.id.placeAddress);
        placeRating = (TextView) this.findViewById(R.id.placeRating);


    }

    @Override
    protected void onStart() {
        super.onStart();

        placeName_view.setText(place.getName());
        placeAddress.setText(place.getAddress());
        placeRating.setText(Float.toString(place.getRating()));

        final ImageView img = (ImageView) findViewById(R.id.placeImage);
        geoDataClient = Places.getGeoDataClient(this, null);
        Task<PlacePhotoMetadataResponse> placePhotoMetadataResponseTask = geoDataClient.getPlacePhotos(place.getId());
        placePhotoMetadataResponseTask.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
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
                                    img.setImageBitmap(to_placeBitmap);
                                }
                            });
                        }
                    } else
                        img.setImageResource(R.drawable.default_trip_photo);

                }

            }
        });

        //put place info

        placeName_view.setText(place.getName().toString());
        placeAddress.setText(place.getAddress().toString());
        placeRating.setText(Float.toString(place.getRating()));

        createTrip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("esraa", createTrip_btn.toString());
                Intent intent = new Intent(PlaceActivity.this, CreateTripActivity.class);
                intent.putExtra("to_place", (Parcelable) place);
                if (to_placeBitmap != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    to_placeBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();
                    intent.putExtra("imageBytes", imageBytes);
                }
                startActivity(intent);
            }
        });

    }
}

