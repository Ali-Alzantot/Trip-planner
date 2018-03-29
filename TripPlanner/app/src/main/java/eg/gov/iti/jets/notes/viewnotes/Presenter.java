package eg.gov.iti.jets.notes.viewnotes;

import android.util.Log;

import java.util.ArrayList;

import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.notes.viewnotes.interfaces.ModelInterface;
import eg.gov.iti.jets.notes.viewnotes.interfaces.PresenterInterface;
import eg.gov.iti.jets.notes.viewnotes.interfaces.ViewInterface;

/**
 * Created by esraa on 3/5/2018.
 */

public class Presenter implements PresenterInterface {

    private ViewInterface viewInterface;
    private ModelInterface modelInterface;

    public Presenter(ViewInterface viewInterface, ModelInterface modelInterface) {
        this.viewInterface = viewInterface;
        this.modelInterface = modelInterface;
    }

    @Override
    public void getAllNotes(Trip trip) {
        ArrayList<Note> noteList=modelInterface.getAllNotes(trip);
        viewInterface.setNoteList(noteList);
    }
    @Override
    public void addNote(Note note) {
        note=modelInterface.addNote(note);
        viewInterface.addToNoteList(note);
    }

    @Override
    public void deleteNote(Note note, int position) {
        modelInterface.deleteNote(note);
        viewInterface.deleteNoteFromList_UpdateUI(position);
    }

    @Override
    public void updateNote(Note note, int position) {
        modelInterface.updateNote(note);
        Log.i("esraa","pospreee="+position);
        viewInterface.updateNoteInList_UpdateUI(note,position);
    }


}
