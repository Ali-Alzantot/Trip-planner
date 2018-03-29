package eg.gov.iti.jets.edittripactivity.interfaces;

import java.io.Serializable;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by esraa on 3/5/2018.
 */

public interface EditTripPresenterInterface extends Serializable {

    public void updateTrip(Trip trip);
}
