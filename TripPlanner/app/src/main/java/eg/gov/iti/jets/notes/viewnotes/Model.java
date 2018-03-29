package eg.gov.iti.jets.notes.viewnotes;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import eg.gov.iti.jets.databasepkg.DatabaseAdapter;
import eg.gov.iti.jets.databasepkg.FirebaseDatabaseDAO;
import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.notes.viewnotes.interfaces.ModelInterface;

/**
 * Created by esraa on 3/5/2018.
 */

public class Model implements ModelInterface {

    private DatabaseAdapter dbAdapter;

    public Model(Context context)
    {
        dbAdapter = new DatabaseAdapter(context);
    }

    @Override
    public ArrayList<Note> getAllNotes(Trip trip) {
        return dbAdapter.getAllNotes(trip);
    }
    @Override
    public Note addNote(Note note)
    {
        note= dbAdapter.addNote(note);
        new FirebaseDatabaseDAO().createAndUpdateNotesOnFirebase(note);
        return note;
    }

    @Override
    public void deleteNote(Note note)
    {
        new FirebaseDatabaseDAO().removeNotesFromFirebase(note);
        dbAdapter.deleteNote(note);
    }

    @Override
    public void updateNote(Note note) {
        new FirebaseDatabaseDAO().createAndUpdateNotesOnFirebase(note);
        dbAdapter.updateNote(note);
        Log.i("esraa","model");
    }


}
