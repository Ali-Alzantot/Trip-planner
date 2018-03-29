package eg.gov.iti.jets.viewtrip;

import android.content.Context;

import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.viewtrip.interfaces.ViewTripModelInterface;
import eg.gov.iti.jets.viewtrip.interfaces.ViewTripPresenterInterface;
import eg.gov.iti.jets.viewtrip.interfaces.ViewTripViewInterface;

/**
 * Created by esraa on 3/5/2018.
 */

public class Presenter implements ViewTripPresenterInterface {

    private ViewTripViewInterface viewInterface;
    private ViewTripModelInterface modelInterface;

    public Presenter(ViewTripViewInterface viewInterface, Context context) {

        modelInterface =  DBModel.getInstance(context);
        modelInterface.setViewTripPresenterInterface(this);
        this.viewInterface = viewInterface;
    }

    @Override
    public void deleteTripFromDB(Trip trip) {
        modelInterface.deleteTrip(trip);

    }
    @Override
    public void updateTripInDB(Trip trip) {
        modelInterface.updateTrip(trip);
    }

}
