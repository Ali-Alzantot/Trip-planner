package eg.gov.iti.jets.viewtrip.interfaces;

import java.io.Serializable;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by esraa on 3/5/2018.
 */

public interface ViewTripModelInterface extends Serializable{

    public void deleteTrip(Trip trip);

    public void setViewTripPresenterInterface(ViewTripPresenterInterface viewTripPresenterInterface);

    public void updateTrip(Trip trip);
}
