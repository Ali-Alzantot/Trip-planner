package eg.gov.iti.jets.upcomingtripsfragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import eg.gov.iti.jets.createtripactivity.CreateTripActivity;
import eg.gov.iti.jets.databasepkg.FirebaseDatabaseDAO;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.tripplanner.R;
import eg.gov.iti.jets.upcomingtripsfragment.upcomingfragmentinterfaces.UpcomingPresenterInterface;
import eg.gov.iti.jets.upcomingtripsfragment.upcomingfragmentinterfaces.UpcomingViewInterface;
import eg.gov.iti.jets.viewplace.PlaceActivity;

import static android.content.ContentValues.TAG;


public class UpcomingFragment extends Fragment implements UpcomingViewInterface {

    private static transient final String ARG_PARAM1 = "upcomingPresenter";
    private static transient UpcomingFragment fragment;
    private TextView no_past_trips_tv;
    // TODO: Rename and change types of parameters
    private UpcomingPresenterInterface presenter;
    private transient RecyclerView recyclerView;
    private transient FloatingActionButton floatingButton;
    private UpcomingTripAdapter adapter;
    private static List<Trip> tripList = new ArrayList<Trip>(0);
    private int position = 0;
    private final String AUTO_COMPLETE_FRAGMENT_TAG = "supportPlaceAutocompleteFragment";

    private FirebaseDatabaseDAO firebaseDatabaseDAO;
    public UpcomingFragment() {
        super();
        setArguments(new Bundle());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param presenter Parameter 1.
     * @return A new instance of fragment UpcomingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpcomingFragment newInstance(UpcomingPresenter presenter) {

        fragment = new UpcomingFragment();
        presenter.setView(fragment);
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, presenter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        firebaseDatabaseDAO=new FirebaseDatabaseDAO();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            presenter = (UpcomingPresenterInterface) getArguments().getSerializable(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
            presenter = (UpcomingPresenter) savedInstanceState.getSerializable("presenter");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
       // no_past_trips_tv=view.findViewById(R.id.no_past_trips_tv);
        adapter = new UpcomingTripAdapter(getActivity(), tripList, presenter);
        recyclerView = (RecyclerView) view.findViewById(R.id.upcoming_trip_recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        floatingButton = (FloatingActionButton) view.findViewById(R.id.floatingAddTripButton);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddTripActivity();
            }
        });
        SupportPlaceAutocompleteFragment supportPlaceAutocompleteFragment =
                (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentByTag(AUTO_COMPLETE_FRAGMENT_TAG);
        if (supportPlaceAutocompleteFragment == null) {
            supportPlaceAutocompleteFragment = new SupportPlaceAutocompleteFragment();
            FragmentTransaction tr = getChildFragmentManager().beginTransaction();
            tr.add(R.id.supportPlaceAutocompleteFragment, supportPlaceAutocompleteFragment, AUTO_COMPLETE_FRAGMENT_TAG);
            tr.commit();
        }
        supportPlaceAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Intent intent = new Intent(getActivity(), PlaceActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("place", (Parcelable) place);
                startActivity(intent);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        return view;
    }


    private void goToAddTripActivity() {

        Intent intent = new Intent(UpcomingFragment.this.getActivity(), CreateTripActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.getTripListFromDB();

    }

    @Override
    public void onResume() {
        super.onResume();
        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(position);
        recyclerView.scrollToPosition(position);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("position", ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        savedInstanceState.putSerializable("presenter", presenter);
    }

    @Override
    public void addTripToList(Trip trip) {
        adapter.addItem(trip);
        recyclerView.refreshDrawableState();
    }

    @Override
    public void updateTripInList(Trip trip) {
        int position = tripList.indexOf(trip);
        if (position != -1) {
            adapter.updateItem(trip);
        }
    }

    @Override
    public void removeTripInList(Trip trip) {
        int position = adapter.deleteItem(trip);
        firebaseDatabaseDAO.removeTripFromFirebase(trip);
        /*if(tripList!=null&&tripList.size()==0)
        {
            no_past_trips_tv.setCursorVisible(true);
        }*/


    }

    @Override
    public void updateTripList(List<Trip> trip) {
        adapter.updateList(trip);
        tripList = trip;
        /*if(tripList!=null&&tripList.size()==0)
        {
            no_past_trips_tv.setCursorVisible(true);
        }*/
    }

}
