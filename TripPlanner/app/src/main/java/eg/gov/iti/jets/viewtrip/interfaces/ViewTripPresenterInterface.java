package eg.gov.iti.jets.viewtrip.interfaces;

import java.io.Serializable;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by esraa on 3/5/2018.
 */

public interface ViewTripPresenterInterface extends Serializable{
    public void deleteTripFromDB(Trip trip);
    public void updateTripInDB(Trip trip);
}
