
package eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces;


import java.io.Serializable;
import java.util.List;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by Usama on 7/3/2018.
 */

public interface HistoryViewInterface extends Serializable{
    public void addTripToList(Trip trip);
    public void updateTripInList(Trip trip);
    public void removeTripFromList(Trip trip);
    public void updateTripList(List<Trip> trip);
}
