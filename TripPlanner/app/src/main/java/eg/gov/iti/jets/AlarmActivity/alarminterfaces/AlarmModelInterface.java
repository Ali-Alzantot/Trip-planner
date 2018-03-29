

package eg.gov.iti.jets.AlarmActivity.alarminterfaces;

import java.io.Serializable;

import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by Usama on 7/3/2018.
 */

public interface AlarmModelInterface extends Serializable {
    public Trip getTripInstantly(int userID,int tripID);
    public Trip addTrip(Trip trip);
    public void updateTrip(Trip trip);
    public void cancelTrip(Trip trip,Integer flag);
    public void deleteTrip(Trip trip);
    public void setAlarmPresenterInterface(AlarmPresenterInterface alarmPresenterInterface);
}
