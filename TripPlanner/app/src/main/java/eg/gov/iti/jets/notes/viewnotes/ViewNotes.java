package eg.gov.iti.jets.notes.viewnotes;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.notes.AddNoteDialog;
import eg.gov.iti.jets.notes.EditNoteDialog;
import eg.gov.iti.jets.notes.viewnotes.interfaces.PresenterInterface;
import eg.gov.iti.jets.notes.viewnotes.interfaces.ViewInterface;
import eg.gov.iti.jets.tripplanner.R;

public class ViewNotes extends AppCompatActivity implements ViewInterface {

    private PresenterInterface presenterInterface;


    private FloatingActionButton addNote_btn;
    private Trip trip;
    private final int ADD_NOTE_CODE=505;
    private ArrayList<Note> noteList;
    private  RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnotes);
        noteList=new ArrayList<>();
        presenterInterface=new Presenter(this,new Model(this));

        if (savedInstanceState != null && savedInstanceState.getSerializable("trip") != null) {
            trip = (Trip) savedInstanceState.getSerializable("trip");
        } else if (getIntent().getSerializableExtra("trip") != null) {

            trip = (Trip) getIntent().getSerializableExtra("trip");
            Log.i("esraa",trip.getUserId().toString());
        }

        if(trip!=null)
        {
            if (savedInstanceState != null && savedInstanceState.getSerializable("noteList") != null) {
                noteList = (ArrayList<Note>) savedInstanceState.getSerializable("noteList");
            } else
            {
                presenterInterface.getAllNotes(trip);//get notes from database
            }

        }
        else
        {
            Log.i("esraa","null trip");
        }



        addNote_btn=(FloatingActionButton)findViewById(R.id.addNote_btn);

        addNote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showAddNoteDialog();
            }
        });
        Log.i("esraa","tripcc="+trip.getTripName());
    }

    private void showAddNoteDialog()
    {
        AddNoteDialog addNoteDialog=new AddNoteDialog();
        Bundle bundle=new Bundle();
        bundle.putSerializable("trip",trip);
        bundle.putSerializable("presenterInterface",presenterInterface);
        addNoteDialog.setArguments(bundle);
        addNoteDialog.show(getSupportFragmentManager(),"AddNoteDialog");
    }


    @Override
    protected void onStart() {
        super.onStart();

        notesRecyclerView=findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setHasFixedSize(true);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.i("esraa","s="+noteList.size());
        notesAdapter=new NotesAdapter(this,noteList,presenterInterface,getSupportFragmentManager());
        notesRecyclerView.setAdapter(notesAdapter);


    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case ADD_NOTE_CODE:
                if(resultCode== AppCompatActivity.RESULT_OK)
                {
                    noteList.add((Note)getIntent().getSerializableExtra("note_added"));
                    for(int i=0;i<noteList.size();i++)
                    {
                        Log.i("esraa","noteONACRES="+noteList.get(i).getNote());
                    }
                }
                break;
            default:
        }
    }*/
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("trip",trip);
        outState.putSerializable("noteList",noteList);
    }


    @Override
    public void setNoteList(ArrayList<Note> noteList) {
        if(noteList!=null)
        this.noteList=noteList;
    }

    @Override
    public void addToNoteList(Note note) {

        this.noteList.add(note);
        notesRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                notesAdapter.notifyItemInserted(noteList.size() - 1);
                notesRecyclerView.scrollToPosition(noteList.size() - 1);
            }
        });
    }

    @Override
    public void deleteNoteFromList_UpdateUI(final int position) {
        noteList.remove(position);
        notesRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                notesAdapter.notifyItemRemoved(position);
                notesAdapter.notifyItemRangeChanged(position, noteList.size());
                notesRecyclerView.scrollToPosition(position);
            }
        });
    }

    @Override
    public void updateNoteInList_UpdateUI(Note note, final int position) {
        noteList.set(position,note);
        Log.i("esraa","uii="+position);
        notesRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                Log.i("esraa","pos="+position);
               notesAdapter.notifyItemChanged(position);
              //  notesRecyclerView.scrollToPosition(position);
            }
        });


    }
}

