package eg.gov.iti.jets.historytripsfragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eg.gov.iti.jets.databasepkg.FirebaseDatabaseDAO;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.tripplanner.R;
import eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces.HistoryPresenterInterface;
import eg.gov.iti.jets.historytripsfragment.historyfragmentinterfaces.HistoryViewInterface;


public class HistoryFragment extends Fragment implements HistoryViewInterface {

    private static transient final String ARG_PARAM1 = "historyPresenter";
    private static transient HistoryFragment fragment;
    private TextView no_past_trips_tv;
    // TODO: Rename and change types of parameters
    private static HistoryPresenterInterface presenter;
    private transient RecyclerView recyclerView;
    private HistoryTripAdapter adapter;
    private List<Trip> tripList = new ArrayList<Trip>(0);
    private int position = 0;

    private FirebaseDatabaseDAO firebaseDatabaseDAO;
    public HistoryFragment() {
        super();
        setArguments(new Bundle());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param presenter Parameter 1.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(HistoryPresenter presenter) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, presenter);
        fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        firebaseDatabaseDAO=new FirebaseDatabaseDAO();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            presenter = (HistoryPresenterInterface) getArguments().getSerializable(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
            presenter = (HistoryPresenter) savedInstanceState.getSerializable("presenter");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        no_past_trips_tv=view.findViewById(R.id.no_past_trips_tv);
        adapter = new HistoryTripAdapter(getActivity().getSupportFragmentManager(),getActivity(), tripList, presenter);
        recyclerView = (RecyclerView) view.findViewById(R.id.history_trip_recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        presenter.getTripListFromDB();
        Log.i("esraa","size"+tripList);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(position);
        recyclerView.scrollToPosition(position);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("position", ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        savedInstanceState.putSerializable("presenter", presenter);
    }


    @Override

    public void addTripToList(Trip trip) {
        adapter.addItem(trip);
    }

    @Override
    public void updateTripInList(Trip trip) {
        int position = tripList.indexOf(trip);
        if (position != -1) {
            adapter.updateItem(trip);
        }
    }

    @Override
    public void removeTripFromList(Trip trip) {
        int position = adapter.deleteItem(trip);
        firebaseDatabaseDAO.removeTripFromFirebase(trip);
        if((tripList==null)||(tripList!=null&&tripList.size()==0))
        {
            no_past_trips_tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateTripList(List<Trip> trip) {
        adapter.updateList(trip);
        tripList = trip;
        Log.i("esraa","size"+tripList.size());
        if((tripList==null)||(tripList!=null&&tripList.size()==0))
        {
            no_past_trips_tv.setVisibility(View.VISIBLE);
        }
    }


}
