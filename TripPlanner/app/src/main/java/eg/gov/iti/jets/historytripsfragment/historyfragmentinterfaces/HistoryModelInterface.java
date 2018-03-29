

package eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces;

import java.io.Serializable;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by Usama on 7/3/2018.
 */

public interface HistoryModelInterface extends Serializable {
    public void getAllHistoryTrips();
    public void deleteTrip(Trip trip);
    public void updateTrip(Trip trip);
    public void setHistoryPresenterInterface(HistoryPresenterInterface historyPresenterInterface);
}
