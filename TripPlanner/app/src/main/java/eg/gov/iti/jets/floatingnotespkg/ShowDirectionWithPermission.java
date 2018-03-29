package eg.gov.iti.jets.floatingnotespkg;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;

import eg.gov.iti.jets.AlarmActivity.AlarmHelper;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.tripplanner.R;

public class ShowDirectionWithPermission extends AppCompatActivity implements Serializable {
    /*  Permission request code to draw over other apps  */
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
    Trip trip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trip=(Trip)getIntent().getSerializableExtra("trip");
        createFloatingWidget();
    }

    /*  start floating widget service  */
    public void createFloatingWidget() {
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        } else
            //If permission is granted start floating widget service
            startFloatingWidgetService();

    }

    /*  Start Floating widget service and finish current activity */
    private void startFloatingWidgetService() {
        Intent intent = new Intent(getApplicationContext(), FloatingViewService.class);
        intent.putExtra("trip", trip);
        getApplicationContext().startService(intent);
        showDirection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
            //Check if the permission is granted or not.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this) )
                //If permission granted start floating widget service
                startFloatingWidgetService();
            else{
                //Permission is not available then display toast

                Toast.makeText(this,
                        "Draw over other app permission not available. you can't see you'r notes",
                        Toast.LENGTH_LONG).show();
                    showDirection();
            }


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    //open google maps and finish activity
    public void showDirection (){
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + trip.getEndLatitude() + "," + trip.getEndLongitude() + "&travelmode=driving");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(mapIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
        }

        AlarmHelper.cancelAlarm(getApplicationContext(), trip.getUserId(), trip.getTripId());
        finish();
    }

}
