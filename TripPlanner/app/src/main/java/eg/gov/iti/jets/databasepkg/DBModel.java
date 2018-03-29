package eg.gov.iti.jets.databasepkg;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import eg.gov.iti.jets.AlarmActivity.AlarmHelper;
import eg.gov.iti.jets.AlarmActivity.alarminterfaces.AlarmModelInterface;
import eg.gov.iti.jets.AlarmActivity.alarminterfaces.AlarmPresenterInterface;
import eg.gov.iti.jets.createtripactivity.interfaces.CreateTripModelInterface;
import eg.gov.iti.jets.createtripactivity.interfaces.CreateTripPresenterInterface;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.dtos.User;
import eg.gov.iti.jets.edittripactivity.interfaces.EditTripModelInterface;
import eg.gov.iti.jets.edittripactivity.interfaces.EditTripPresenterInterface;
import eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces.HistoryModelInterface;
import eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces.HistoryPresenterInterface;
import eg.gov.iti.jets.upcomingtripsfragment.upcomingfragmentinterfaces.UpcomingPresenterInterface;
import eg.gov.iti.jets.upcomingtripsfragment.upcomingfragmentinterfaces.UpComingModelInterface;
import eg.gov.iti.jets.viewtrip.interfaces.ViewTripModelInterface;
import eg.gov.iti.jets.viewtrip.interfaces.ViewTripPresenterInterface;

/**
 * Created by Anonymous on 15/03/2018.
 */

public class DBModel implements UpComingModelInterface, HistoryModelInterface, AlarmModelInterface, CreateTripModelInterface
        , EditTripModelInterface, ViewTripModelInterface {
    private transient AlarmPresenterInterface alarmPresenterInterface;
    private transient CreateTripPresenterInterface createTripPresenterInterface;
    private transient EditTripPresenterInterface editTripPresenterInterface;
    private transient HistoryPresenterInterface historyPresenterInterface;
    private transient UpcomingPresenterInterface upcomingPresenterInterface;
    private transient ViewTripPresenterInterface viewTripPresenterInterface;
    private DatabaseAdapter dbAdapter;
    private Integer cancelFlag;
    private static DBModel dbModel;

    private DBModel(Context context) {
        dbAdapter = new DatabaseAdapter(context);
    }

    public static DBModel getInstance(Context context) {
        if (dbModel == null) {
            dbModel = new DBModel(context);
        }
        return dbModel;
    }


    @Override
    public void getAllUpcomingTrips() {
        new GetAllUpcomingTripsTask().execute(User.getUser());
    }

    @Override
    public Trip addTrip(Trip trip) {
        Trip insertedTrip=dbAdapter.addTrip(trip);
        if (insertedTrip.getStatus().equals(Trip.UPCOMING) || insertedTrip.getStatus().equals(Trip.ONGOING) || insertedTrip.getStatus().equals(Trip.HANGING))
            if (upcomingPresenterInterface != null) {
                upcomingPresenterInterface.addTripToView(insertedTrip);
            }
        if (insertedTrip.getStatus().equals(Trip.CANCELLED) || insertedTrip.getStatus().equals(Trip.ENDED))
            if (historyPresenterInterface != null) {
                historyPresenterInterface.addTripToView(insertedTrip);
            }
        return insertedTrip;
    }


    @Override
    public void deleteTrip(Trip trip) {
        new DeleteTripTask().execute(trip);
    }

    @Override
    public Trip getTripInstantly(int userID, int tripID) {
        return dbAdapter.getTripById(tripID, userID);
    }

    @Override
    public void getAllHistoryTrips() {
        new GetAllHistoryTripsTask().execute(User.getUser());
    }


    @Override
    public void updateTrip(Trip trip) {
        new UpdateTripTask().execute(trip);
    }

    @Override
    public void cancelTrip(Trip trip, Integer flag) {
        cancelFlag = flag;
        new UpdateTripTask().execute(trip);
    }


    private class GetAllUpcomingTripsTask extends AsyncTask<User, Void, List<Trip>> {
        @Override
        protected List<Trip> doInBackground(User... users) {
            return dbAdapter.getAllUpcomingTrips(users[0]);
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            if (trips != null && trips.size() > 0 && upcomingPresenterInterface != null) {
                upcomingPresenterInterface.addTripListToView(trips);
            }
        }
    }

    private class GetAllHistoryTripsTask extends AsyncTask<User, Void, List<Trip>> {
        @Override
        protected List<Trip> doInBackground(User... users) {
            return dbAdapter.getAllHistoryTrips(users[0]);
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            if (trips != null && trips.size() > 0 && historyPresenterInterface != null) {
                historyPresenterInterface.addTripListToView(trips);
            }
        }
    }

    private class DeleteTripTask extends AsyncTask<Trip, Void, Trip> {

        @Override
        protected Trip doInBackground(Trip... trips) {
            dbAdapter.deleteTrip(trips[0]);
            return trips[0];
        }

        @Override
        protected void onPostExecute(Trip trip) {

            if (upcomingPresenterInterface != null) {
                upcomingPresenterInterface.deleteTripFromView(trip);
            }
            if (historyPresenterInterface != null) {
                historyPresenterInterface.deleteTripFromView(trip);
            }

        }
    }

    private class UpdateTripTask extends AsyncTask<Trip, Void, Trip> {

        @Override
        protected Trip doInBackground(Trip... trips) {
            dbAdapter.updateTrip(trips[0]);
            return dbAdapter.getTripById(trips[0].getTripId(), trips[0].getUserId());
        }

        @Override
        protected void onPostExecute(Trip trip) {
            if (cancelFlag != null && cancelFlag == 1) {
                if (historyPresenterInterface != null) {
                    historyPresenterInterface.addTripToView(trip);
                }
                if (upcomingPresenterInterface != null) {
                    upcomingPresenterInterface.deleteTripFromView(trip);
                }
                new AddTripTask().execute(trip);
            } else if (cancelFlag != null && cancelFlag == 2) {
                if (upcomingPresenterInterface != null) {
                    upcomingPresenterInterface.deleteTripFromView(trip);
                }
                if (historyPresenterInterface != null) {
                    historyPresenterInterface.addTripToView(trip);
                }
            } else {
                if (trip.getStatus().equals(Trip.ENDED) && upcomingPresenterInterface != null) {

                    upcomingPresenterInterface.deleteTripFromView(trip);

                } else if (trip.getStatus().equals(Trip.ENDED) && historyPresenterInterface != null) {

                    historyPresenterInterface.addTripToView(trip);

                }else if (upcomingPresenterInterface != null) {
                    upcomingPresenterInterface.updateTripInView(trip);
                }
            }
            cancelFlag = null;
        }
    }

    private class AddTripTask extends AsyncTask<Trip, Void, Trip> {

        @Override
        protected Trip doInBackground(Trip... trips) {
            trips[0].setStatus(Trip.HANGING);
            Trip trip = dbAdapter.addTrip(trips[0]);
            return trip;
        }

        @Override
        protected void onPostExecute(Trip trip) {
            if (upcomingPresenterInterface != null) {
                upcomingPresenterInterface.addTripToView(trip);
            }
        }
    }

    @Override
    public void setAlarmPresenterInterface(AlarmPresenterInterface alarmPresenterInterface) {
        this.alarmPresenterInterface = alarmPresenterInterface;
    }

    @Override
    public void setCreateTripPresenterInterface(CreateTripPresenterInterface createTripPresenterInterface) {
        this.createTripPresenterInterface = createTripPresenterInterface;
    }

    @Override
    public void setEditTripPresenterInterface(EditTripPresenterInterface editTripPresenterInterface) {
        this.editTripPresenterInterface = editTripPresenterInterface;
    }

    @Override
    public void setHistoryPresenterInterface(HistoryPresenterInterface historyPresenterInterface) {
        this.historyPresenterInterface = historyPresenterInterface;
    }

    @Override
    public void setUpcomingPresenterInterface(UpcomingPresenterInterface upcomingPresenterInterface) {
        this.upcomingPresenterInterface = upcomingPresenterInterface;
    }

    @Override
    public void setViewTripPresenterInterface(ViewTripPresenterInterface viewTripPresenterInterface) {
        this.viewTripPresenterInterface = viewTripPresenterInterface;
    }
}
