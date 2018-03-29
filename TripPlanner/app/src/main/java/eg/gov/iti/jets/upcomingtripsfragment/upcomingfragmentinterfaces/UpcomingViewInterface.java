
package eg.gov.iti.jets.upcomingtripsfragment.upcomingfragmentinterfaces;


import java.io.Serializable;
import java.util.List;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by Usama on 7/3/2018.
 */

public interface UpcomingViewInterface extends Serializable {
    public void addTripToList(Trip trip);

    public void updateTripInList(Trip trip);

    public void removeTripInList(Trip trip);

    public void updateTripList(List<Trip> trip);
}
