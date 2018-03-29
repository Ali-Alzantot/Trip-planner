package eg.gov.iti.jets.databasepkg;

import java.util.ArrayList;

import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.dtos.User;

/**
 * Created by Ali Alzantot on 08/03/2018.
 */

public interface MyCallback {
    void onMaxIdCallBack(int max);
    void onGetUserCallBack(User user);
    void onGetUserByEmailCallBack(User user);
    void onRetrieveUserTripsCallBack(ArrayList<Trip> trips);
    void onRetrieveUserNotesCallBack(ArrayList<Note> notes);
}
