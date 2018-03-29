package eg.gov.iti.jets.notes.viewnotes.interfaces;

import java.util.ArrayList;

import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by esraa on 3/5/2018.
 */

public interface ModelInterface {
    public ArrayList<Note> getAllNotes(Trip trip);
    public Note addNote(Note note);
    public void deleteNote(Note note);
    public void updateNote(Note note);
}
