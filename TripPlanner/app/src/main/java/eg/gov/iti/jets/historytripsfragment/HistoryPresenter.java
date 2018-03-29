package eg.gov.iti.jets.historytripsfragment;

import android.content.Context;

import java.util.List;

import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces.HistoryModelInterface;
import eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces.HistoryPresenterInterface;
import eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces.HistoryViewInterface;


/**
 * Created by Usama on 7/3/2018.
 */

public class HistoryPresenter implements HistoryPresenterInterface {

    private transient HistoryViewInterface view;
    private transient HistoryModelInterface model;

    public HistoryPresenter(Context context) {
        model =  DBModel.getInstance(context);
        model.setHistoryPresenterInterface(this);
    }

    public void setView(HistoryViewInterface view) {
        this.view = view;
    }

    @Override
    public void getTripListFromDB() {
        model.getAllHistoryTrips();
    }

    @Override
    public void addTripListToView(List<Trip> trip) {
        view.updateTripList(trip);

    }

    @Override
    public void deleteTripFromDB(Trip trip) {
        model.deleteTrip(trip);

    }

    @Override
    public void deleteTripFromView(Trip trip) {
        view.removeTripFromList(trip);
    }

    @Override
    public void addTripToView(Trip trip) {
        view.addTripToList(trip);
    }

    @Override
    public void updateTripInView(Trip trip) {
        view.updateTripInList(trip);
    }

    @Override
    public void updateTripInDB(Trip trip) {
        model.updateTrip(trip);
    }
}
