package eg.gov.iti.jets.AlarmActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by Anonymous on 15/03/2018.
 */

public class AlarmHelper {
    public static void setAlarm(Context context, Trip trip) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra("user_id", trip.getUserId());
        intent.putExtra("trip_id", trip.getTripId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(trip.getUserId().toString()+trip.getTripId().toString()),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SimpleDateFormat sdformat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        Date date = null;
        try {
            date = sdformat.parse(trip.getStartTime() + " " + trip.getStartDate());
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
    }

    public static void cancelAlarm(Context context,Integer userId, Integer tripId) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("trip_id", tripId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(userId.toString() + tripId.toString()),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
