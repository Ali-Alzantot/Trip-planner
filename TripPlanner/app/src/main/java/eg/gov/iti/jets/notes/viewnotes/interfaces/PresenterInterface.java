package eg.gov.iti.jets.notes.viewnotes.interfaces;

import java.io.Serializable;

import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;

/**
 * Created by esraa on 3/5/2018.
 */

public interface PresenterInterface extends Serializable{

    public void getAllNotes(Trip trip);
    public void addNote(Note note);
    public void deleteNote(Note note,int position);
    public void updateNote(Note note,int position);
}
