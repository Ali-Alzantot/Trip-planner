package eg.gov.iti.jets.floatingnotespkg;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.tripplanner.R;
/**
 * Created by Ali Alzantot on 13/03/2018.
 */

public class NotesCustomAdatper extends ArrayAdapter {
    ArrayList<Note> notes;
    Context myConext;
    public NotesCustomAdatper(@NonNull Context context, int layout, int resource, @NonNull ArrayList<Note> objects) {
        super(context,layout, resource, objects);
        notes=objects;
        myConext=context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row=null;
        LayoutInflater layoutInflater= (LayoutInflater) myConext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row=layoutInflater.inflate(R.layout.single_row,parent,false);
        CheckBox checkBox=row.findViewById(R.id.noteCheckBox);
        checkBox.setText(notes.get(position).getNote());
        if(!notes.get(position).getStatus().equals("STATUS_CHECKED"))
        checkBox.setTextColor(Color.RED);
        TextView noteId = row.findViewById(R.id.noteId);
        noteId.setText(notes.get(position).getNoteId()+"");
        TextView tripId = row.findViewById(R.id.tripId);
        tripId.setText(notes.get(position).getTripId()+"");
        TextView userId = row.findViewById(R.id.userId);
        userId.setText(notes.get(position).getUserId()+"");
        TextView floatingNoteTitle = row.findViewById(R.id.floatingnoteTitle);
        floatingNoteTitle.setText(notes.get(position).getNoteTitle()+"");
        TextView floatingNoteStatus = row.findViewById(R.id.floatingNoteStatus);
        floatingNoteStatus.setText(notes.get(position).getStatus()+"");
        return row;
    }



}
