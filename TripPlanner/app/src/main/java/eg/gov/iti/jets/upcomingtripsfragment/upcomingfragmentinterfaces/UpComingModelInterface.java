

package eg.gov.iti.jets.upcomingtripsfragment.upcomingfragmentinterfaces;

import java.io.Serializable;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by Usama on 7/3/2018.
 */

public interface UpComingModelInterface extends Serializable {
    public void getAllUpcomingTrips();

    public Trip addTrip(Trip trip);
    public void deleteTrip(Trip trip);

    public void updateTrip(Trip trip);
    public void setUpcomingPresenterInterface(UpcomingPresenterInterface upcomingPresenterInterface);

}
