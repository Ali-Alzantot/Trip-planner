package eg.gov.iti.jets.notes.viewnotes.interfaces;

import java.util.ArrayList;

import eg.gov.iti.jets.dtos.Note;

/**
 * Created by esraa on 3/5/2018.
 */

public interface ViewInterface {
    public void setNoteList(ArrayList<Note> noteList);
    public  void addToNoteList(Note note);
    public void deleteNoteFromList_UpdateUI(int position);
    public void updateNoteInList_UpdateUI(Note note,int position);
}
