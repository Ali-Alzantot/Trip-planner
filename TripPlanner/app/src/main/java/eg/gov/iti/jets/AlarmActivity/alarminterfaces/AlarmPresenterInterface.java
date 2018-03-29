
package eg.gov.iti.jets.AlarmActivity.alarminterfaces;


import java.io.Serializable;
import java.util.List;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by Usama on 7/3/2018.
 */

public interface AlarmPresenterInterface extends Serializable {

    public Trip addTrip(Trip trip);
    public Trip getTripFromDB(int userID, int tripID);
    public void updateTripInDB(Trip trip);
    public void deleteTrip(Trip trip);
    void cancelTrip(Trip trip,Integer flag);
}
