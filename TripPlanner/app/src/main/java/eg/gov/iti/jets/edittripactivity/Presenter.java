package eg.gov.iti.jets.edittripactivity;


import android.content.Context;

import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.edittripactivity.interfaces.EditTripModelInterface;
import eg.gov.iti.jets.edittripactivity.interfaces.EditTripPresenterInterface;
import eg.gov.iti.jets.edittripactivity.interfaces.EditTripViewInterface;

/**
 * Created by esraa on 3/5/2018.
 */

public class Presenter implements EditTripPresenterInterface {

    private EditTripViewInterface editTripViewInterfaceRef;
    private EditTripModelInterface modelInterfaceRef;

    public Presenter(EditTripViewInterface editTripViewInterface, Context context) {
        modelInterfaceRef = DBModel.getInstance(context);
        modelInterfaceRef.setEditTripPresenterInterface(this);
        this.editTripViewInterfaceRef = editTripViewInterface;
    }

    @Override
    public void updateTrip(Trip trip) {
        modelInterfaceRef.updateTrip(trip);
    }
}
