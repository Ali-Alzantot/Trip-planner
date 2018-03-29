package eg.gov.iti.jets.upcomingtripsfragment;

import android.content.Context;

import java.util.List;

import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.upcomingtripsfragment.upcomingfragmentinterfaces.*;


/**
 * Created by Usama on 7/3/2018.
 */

public class UpcomingPresenter implements UpcomingPresenterInterface {

    private transient UpcomingViewInterface view;
    private transient UpComingModelInterface model;

    public void setView(UpcomingViewInterface view) {
        this.view = view;
    }

    public UpcomingPresenter(Context context) {
        model = DBModel.getInstance(context);
        model.setUpcomingPresenterInterface(this);
    }

    @Override
    public void getTripListFromDB() {
        model.getAllUpcomingTrips();
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
        view.removeTripInList(trip);
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

    @Override
    public void addTripToDB(Trip trip) {
        model.addTrip(trip);
    }
}
