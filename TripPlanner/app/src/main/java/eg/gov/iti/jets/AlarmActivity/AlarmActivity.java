package eg.gov.iti.jets.AlarmActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import eg.gov.iti.jets.AlarmActivity.alarminterfaces.AlarmViewInterface;
import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.floatingnotespkg.ShowDirectionWithPermission;
import eg.gov.iti.jets.picassopkg.ImageHelper;
import eg.gov.iti.jets.tripplanner.R;
public class AlarmActivity extends AppCompatActivity implements AlarmViewInterface {
    private Trip trip;
    private ImageView tripPhoto;
    private TextView tripName, startPoint, endPoint, schedule;
    private Button startBtn, laterBtn, cancelBtn;
    private boolean buttonPressedFlag;
    private MediaPlayer alarmTonePlayer;
    private Vibrator vibrator;
    private String fromNotification;
    private AlarmPresenter alarmPresenter;
    private Integer userID, tripID;
    private int origionalVolume;
    private AudioManager audioManger;
    private transient NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onCreate(savedInstanceState);
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
        DBModel.getInstance(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        buttonPressedFlag = false;

        alarmPresenter = new AlarmPresenter(this);
        alarmPresenter.setView(this);
        this.setFinishOnTouchOutside(false);
        userID = getIntent().getIntExtra("user_id", -1);
        tripID = getIntent().getIntExtra("trip_id", -1);
        if (userID != -1 && tripID != -1) {
            trip = alarmPresenter.getTripFromDB(userID, tripID);
        }

        fromNotification = getIntent().getStringExtra("Notification");
        if (fromNotification != null) {

            notificationManager.cancel(Integer.parseInt(userID.toString() + tripID.toString()));
        }

        if (trip != null) {
            if (getIntent().getStringExtra("cancel") != null) {

                if (trip.getGoAndReturn().equals(Trip.GO_AND_RETURN)) {
                    View checkBoxView = View.inflate(AlarmActivity.this, R.layout.alert_dialog_round_trip_cancel, null);
                    final CheckBox chkBox = checkBoxView.findViewById(R.id.chkBoxDelRoundTrip);
                    AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
                    builder.setTitle(" Cancel Trip ").setMessage(" Do you want to Cancel " + trip.getTripName() + " ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (chkBox.isChecked()) {
                                        trip.setStatus(Trip.CANCELLED);
                                        alarmPresenter.cancelTrip(trip, 2);

                                        notificationManager.cancel(Integer.parseInt(trip.getUserId().toString() + trip.getTripId().toString() + "2"));

                                        finish();
                                    } else {
                                        trip.setGoAndReturn(Trip.ONE_WAY);
                                        trip.setStatus(Trip.CANCELLED);
                                        alarmPresenter.cancelTrip(trip, 1);

                                        notificationManager.cancel(Integer.parseInt(trip.getUserId().toString() + trip.getTripId().toString() + "2"));
                                        finish();
                                    }
                                }
                            })
                            .setView(checkBoxView)
                            .setNegativeButton("No", null)
                            .setCancelable(false).show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
                    builder.setTitle(" Cancel Trip ").setMessage(" Do you want to Cancel " + trip.getTripName() + " ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    trip.setStatus(Trip.CANCELLED);
                                    alarmPresenter.cancelTrip(trip, 2);

                                    notificationManager.cancel(Integer.parseInt(trip.getUserId().toString() + trip.getTripId().toString() + "2"));
                                    finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .setCancelable(false).show();
                }
            } else if (getIntent().getStringExtra("start") != null)

            {
                notificationManager.cancel(Integer.parseInt(trip.getUserId().toString() + trip.getTripId().toString() + "3"));
                trip.setStatus(Trip.ONGOING);
                alarmPresenter.updateTripInDB(trip);
                showDirections(trip);
                finish();
            } else

            {
                setContentView(R.layout.activity_alarm);
                tripPhoto = (ImageView) findViewById(R.id.alarm_trip_photo);
                tripName = (TextView) findViewById(R.id.alarm_trip_title);
                startPoint = (TextView) findViewById(R.id.alarm_start_point);
                endPoint = (TextView) findViewById(R.id.alarm_end_point);
                schedule = (TextView) findViewById(R.id.alarm_date_time);
                startBtn = (Button) findViewById(R.id.alarm_start_btn);
                laterBtn = (Button) findViewById(R.id.alarm_later_btn);
                cancelBtn = (Button) findViewById(R.id.alarm_cancel_btn);
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


                startBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonPressedSuccessfully();
                        if (trip != null) {
                            trip.setStatus(Trip.ONGOING);
                            alarmPresenter.updateTripInDB(trip);
                            showDirections(trip);
                        }
                        finish();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonPressedSuccessfully();
                        if (trip != null) {
                            if (trip.getGoAndReturn().equals(Trip.GO_AND_RETURN)) {
                                View checkBoxView = View.inflate(AlarmActivity.this, R.layout.alert_dialog_round_trip_cancel, null);
                                final CheckBox chkBox = checkBoxView.findViewById(R.id.chkBoxDelRoundTrip);
                                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
                                builder.setTitle(" Cancel Trip ").setMessage(" Do you want to Cancel " + trip.getTripName() + " ?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (chkBox.isChecked()) {
                                                    trip.setStatus(Trip.CANCELLED);
                                                    alarmPresenter.cancelTrip(trip, 2);
                                                    finish();
                                                } else {
                                                    trip.setGoAndReturn(Trip.ONE_WAY);
                                                    trip.setStatus(Trip.CANCELLED);
                                                    alarmPresenter.cancelTrip(trip, 1);
                                                    finish();
                                                }
                                            }
                                        })
                                        .setView(checkBoxView)
                                        .setNegativeButton("No", null)
                                        .setCancelable(false).show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
                                builder.setTitle(" Cancel Trip ").setMessage(" Do you want to Cancel " + trip.getTripName() + " ?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                trip.setStatus(Trip.CANCELLED);
                                                alarmPresenter.cancelTrip(trip, 2);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .setCancelable(false).show();
                            }
                        } else {
                            finish();
                        }
                    }
                });

                laterBtn.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        buttonPressedSuccessfully();
                        if (trip != null) {
                            // content intent
                            Intent alarmIntent = new Intent(AlarmActivity.this, AlarmActivity.class);
                            alarmIntent.putExtra("user_id", trip.getUserId());
                            alarmIntent.putExtra("trip_id", trip.getTripId());
                            alarmIntent.putExtra("Notification", "notify");
                            PendingIntent alarmPendingIntent = PendingIntent.getActivity(AlarmActivity.this, Integer.parseInt(trip.getUserId().toString() + trip.getTripId().toString()), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            // cancel intent
                            Intent cancelIntent = new Intent(AlarmActivity.this, AlarmActivity.class);
                            cancelIntent.putExtra("user_id", trip.getUserId());
                            cancelIntent.putExtra("trip_id", trip.getTripId());
                            cancelIntent.putExtra("cancel", "cancel");
                            PendingIntent cancelPendingIntent = PendingIntent.getActivity(AlarmActivity.this, Integer.parseInt(trip.getUserId().toString() + trip.getTripId().toString() + "2"), cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            // start intent
                            Intent startIntent = new Intent(AlarmActivity.this, AlarmActivity.class);
                            startIntent.putExtra("user_id", trip.getUserId());
                            startIntent.putExtra("trip_id", trip.getTripId());
                            startIntent.putExtra("start", "start");
                            PendingIntent startPendingIntent = PendingIntent.getActivity(AlarmActivity.this, Integer.parseInt(trip.getUserId().toString() + trip.getTripId().toString() + "3"), startIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(AlarmActivity.this)
                                            .setOngoing(true)
                                            .setWhen(System.currentTimeMillis())
                                            .setSmallIcon(R.drawable.go)
                                            .setTicker("Trip Alarm")
                                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                            .setContentInfo("Info")
                                            .setContentTitle("New Trip on hold")
                                            .setStyle(new NotificationCompat.BigTextStyle())
                                            .setLights(Color.BLUE, 500, 500)
                                            .setContentText(trip.getTripName() + " waiting to start ")
                                            .setPriority(NotificationCompat.PRIORITY_MAX)
                                            .setContentIntent(alarmPendingIntent)
                                            .addAction(R.drawable.cross, "Cancel", cancelPendingIntent)
                                            .addAction(R.drawable.place_holder, "Start", startPendingIntent);

                            notificationManager.notify(Integer.parseInt(trip.getUserId().toString() + trip.getTripId().toString()), mBuilder.build());
                            finish();

                        }

                    }
                });

                addTripToView();
            }
        } else {
            finish();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!buttonPressedFlag) {
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.moveTaskToFront(getTaskId(), 0);
        } else {
            stopSound();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return false;
    }


    public void addTripToView() {
        if (trip != null) {
            if (fromNotification == null) {
                startSound();
            }
            if (trip.getPhoto() != null && !trip.getPhoto().trim().equals("")) {
                ImageHelper.getImage(this, tripPhoto, trip.getPhoto(), R.drawable.default_trip_photo);
            } else {
                tripPhoto.setImageResource(R.drawable.default_trip_photo);
            }
            tripName.setText(trip.getTripName());
            startPoint.setText(trip.getStartPoint());
            endPoint.setText(trip.getEndPoint());
            schedule.setText(trip.getStartDate() + "  At  " + trip.getStartTime());
        }
    }

    private void buttonPressedSuccessfully() {
        buttonPressedFlag = true;
        stopSound();
    }

    private void startSound() {
        if (fromNotification == null) {

            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/alarm");
            audioManger = (AudioManager) getSystemService(AUDIO_SERVICE);
            origionalVolume = audioManger.getStreamVolume(AudioManager.STREAM_ALARM);
            audioManger.setStreamVolume(AudioManager.STREAM_ALARM, audioManger.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
            alarmTonePlayer = new MediaPlayer();
            try {
                alarmTonePlayer.setDataSource(AlarmActivity.this.getApplicationContext(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            alarmTonePlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            alarmTonePlayer.setLooping(true);
            alarmTonePlayer.prepareAsync();
            alarmTonePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            long[] pattern = {0, 100, 1000};
            vibrator.vibrate(pattern, 0);
        }
    }

    private void stopSound() {
        if (alarmTonePlayer != null) {
            alarmTonePlayer.stop();
            alarmTonePlayer.release();
            alarmTonePlayer = null;
        }
        if (audioManger != null) {
            audioManger.setStreamVolume(AudioManager.STREAM_ALARM, origionalVolume, 0);
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void showDirections(Trip trip) {
        Intent intent = new Intent(AlarmActivity.this, ShowDirectionWithPermission.class);
        intent.putExtra("trip", trip);
        AlarmActivity.this.startActivity(intent);
    }

}