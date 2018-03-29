package eg.gov.iti.jets.createtripactivity;



import android.content.Context;

import eg.gov.iti.jets.createtripactivity.interfaces.CreateTripModelInterface;
import eg.gov.iti.jets.createtripactivity.interfaces.CreateTripPresenterInterface;
import eg.gov.iti.jets.createtripactivity.interfaces.CreateTripViewInterface;
import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by esraa on 3/5/2018.
 */

public class CreateTripPresenter implements CreateTripPresenterInterface {

    private CreateTripViewInterface viewInterfaceRef;
    private CreateTripModelInterface modelInterfaceRef;


    public CreateTripPresenter(CreateTripViewInterface viewInterface, Context context) {
        modelInterfaceRef =  DBModel.getInstance(context);
        modelInterfaceRef.setCreateTripPresenterInterface(this);
        viewInterfaceRef=viewInterface;
    }


    @Override
    public Trip addTrip(Trip trip) {
         return modelInterfaceRef.addTrip(trip);
    }

    @Override
    public void updateTrip(Trip trip) {
         modelInterfaceRef.updateTrip(trip);
    }
}
