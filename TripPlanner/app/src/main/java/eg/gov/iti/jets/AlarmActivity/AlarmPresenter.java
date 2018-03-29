package eg.gov.iti.jets.AlarmActivity;

import android.content.Context;

import eg.gov.iti.jets.AlarmActivity.alarminterfaces.AlarmModelInterface;
import eg.gov.iti.jets.AlarmActivity.alarminterfaces.AlarmPresenterInterface;
import eg.gov.iti.jets.AlarmActivity.alarminterfaces.AlarmViewInterface;
import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.dtos.Trip;


/**
 * Created by Usama on 7/3/2018.
 */

public class AlarmPresenter implements AlarmPresenterInterface {

    private AlarmViewInterface view;
    private AlarmModelInterface model;

    public void setView(AlarmViewInterface view) {
        this.view = view;
    }

    public AlarmPresenter(Context context) {
        model = DBModel.getInstance(context);
        model.setAlarmPresenterInterface(this);
    }

    @Override
    public Trip getTripFromDB(int userID, int tripID) {
        return model.getTripInstantly(userID, tripID);
    }

    @Override
    public void deleteTrip(Trip trip) {
        model.deleteTrip(trip);
    }

    @Override
    public void cancelTrip(Trip trip,Integer flag) {
        model.cancelTrip(trip,flag);
    }

    @Override
    public void updateTripInDB(Trip trip) {
         model.updateTrip(trip);
    }

    @Override
    public Trip addTrip(Trip trip) {
        return model.addTrip(trip);
    }
}
