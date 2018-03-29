package eg.gov.iti.jets.notes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.dtos.User;
import eg.gov.iti.jets.notes.viewnotes.interfaces.PresenterInterface;
import eg.gov.iti.jets.tripplanner.R;

/**
 * Created by esraa on 3/17/2018.
 */

public class EditNoteDialog extends DialogFragment {

    private EditText noteTitle;
    private EditText noteInput;
    //private Button addNote_doneBtn;

    //private Trip trip;
    private PresenterInterface presenterInterface;
    private Note note;
    private int position;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         super.onCreateDialog(savedInstanceState);

//         if(getArguments()!=null &&getArguments().getSerializable("trip")!=null)
//         {
//             trip=(Trip)getArguments().getSerializable("trip");
//         }
        if(getArguments()!=null &&getArguments().getSerializable("presenterInterface")!=null)
        {
            presenterInterface=(PresenterInterface)getArguments().getSerializable("presenterInterface");
        }
        if(getArguments()!=null &&getArguments().getSerializable("note")!=null)
        {
            note=(Note)getArguments().getSerializable("note");
        }

        if(getArguments()!=null &&getArguments().get("position")!=null)
        {
            position=(int)getArguments().getSerializable("position");
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        View v =(View)LayoutInflater.from(getContext()).inflate(R.layout.activity_addnote,null);
        builder.setView(v);
        builder.setPositiveButton(R.string.addNote, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if((noteTitle.getText().toString().trim().equals("")==false)||(noteInput.getText().toString().trim().equals("")==false))
                        {
                            // tripId, Integer userId, String note, String status
                            //Note note=new Note(trip.getTripId(), User.getUser().getUserId(),noteTitle.getText().toString(),noteInput.getText().toString(),Note.STATUS_UNCHECKED);
                             note.setNoteTitle(noteTitle.getText().toString());
                             note.setNote(noteInput.getText().toString());
                             presenterInterface.updateNote(note,position);
                        }

                    }
                });

        noteTitle = (EditText) v.findViewById(R.id.noteTitle);
        noteInput=(EditText)v.findViewById(R.id.noteInput);
        noteTitle.setText(note.getNoteTitle());
        noteInput.setText(note.getNote());
        //addNote_doneBtn=(Button)v.findViewById(R.id.addNote_doneBtn);

        return builder.create();

    }
}
