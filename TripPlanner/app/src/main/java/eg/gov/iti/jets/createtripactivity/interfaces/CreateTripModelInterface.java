package eg.gov.iti.jets.createtripactivity.interfaces;

import java.io.Serializable;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by esraa on 3/5/2018.
 */

public interface CreateTripModelInterface extends Serializable {

    public Trip addTrip(Trip trip);
    public void updateTrip(Trip trip);
    public void setCreateTripPresenterInterface(CreateTripPresenterInterface createTripPresenterInterface);
}
