package eg.gov.iti.jets.notes.viewnotes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.util.ArrayList;
import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.notes.EditNoteDialog;
import eg.gov.iti.jets.notes.viewnotes.interfaces.PresenterInterface;
import eg.gov.iti.jets.tripplanner.R;

/**
 * Created by esraa on 3/17/2018.
 */

public class NotesAdapter extends Adapter<NotesAdapter.NotesViewHolder> {

   private ArrayList<Note> noteList;
    private PresenterInterface presenterInterface;
    private Context context;
    FragmentManager fragmentManager;
    public NotesAdapter(Context context,ArrayList<Note> noteList,PresenterInterface presenterInterface,FragmentManager fragmentManager)
    {
        this.context=context;
        this.noteList = noteList;
        this.presenterInterface=presenterInterface;
        this.fragmentManager=fragmentManager;
    }



    public class NotesViewHolder extends RecyclerView.ViewHolder
    {

        public TextView noteTitleCard;
        public TextView noteDescCard;
        public ImageView note_three_dots;
        public CardView noteCardView;
        public AppCompatCheckBox checkBox;
        public NotesViewHolder(View itemView) {
            super(itemView);
            noteTitleCard=(TextView) itemView.findViewById(R.id.noteTitleCard);
            noteDescCard=(TextView) itemView.findViewById(R.id.noteDescCard);
            note_three_dots=(ImageView)itemView.findViewById(R.id.note_three_dots);
            noteCardView=(CardView)itemView.findViewById(R.id.noteCardView);
            checkBox=(AppCompatCheckBox)itemView.findViewById(R.id.noteDoneCheckBox);
        }
    }
    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notes_card,viewGroup,false);
        NotesViewHolder notesViewHolder=new NotesViewHolder(v);
        return notesViewHolder;
    }

    @Override
    public void onBindViewHolder(final NotesViewHolder viewHolder, final int position) {

        viewHolder.noteTitleCard.setText((CharSequence) noteList.get(position).getNoteTitle());
        viewHolder.noteDescCard.setText((CharSequence) noteList.get(position).getNote());
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        Log.i("esraa","bindd="+position);
        if(noteList.get(position).getStatus().equals(Note.STATUS_CHECKED))
        {
            viewHolder.noteCardView.setBackgroundColor(context.getResources().getColor(R.color.noteCheck));
            viewHolder.checkBox.setChecked(true);
        }
        else  if(noteList.get(position).getStatus().equals(Note.STATUS_UNCHECKED))
        {
            viewHolder.noteCardView.setBackgroundColor(context.getResources().getColor(R.color.noteunCheck));
             viewHolder.checkBox.setChecked(false);
        }


        viewHolder.noteCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //view note
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(noteList.get(position).getNoteTitle()).setMessage(noteList.get(position).getNote()).show();
            }
        });

        viewHolder.noteTitleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //view note
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(noteList.get(position).getNoteTitle()).setMessage(noteList.get(position).getNote()).show();
            }
        });

        viewHolder.noteDescCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //view note
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(noteList.get(position).getNoteTitle()).setMessage(noteList.get(position).getNote()).show();
            }
        });
        viewHolder.note_three_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup menu
                showPopupMenu(viewHolder.note_three_dots,position);
            }
        });

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                {
                    Log.i("esraa","poshnaa"+position);
                    noteList.get(position).setStatus(Note.STATUS_CHECKED);
                    viewHolder.checkBox.setChecked(true);
                    presenterInterface.updateNote(noteList.get(position),position);

                    //viewHolder.noteCardView.setBackgroundColor(context.getResources().getColor(R.color.noteCheck));
                }
                else
                {
                    Log.i("esraa","poshnaaun"+position);
                    noteList.get(position).setStatus(Note.STATUS_UNCHECKED);
                    viewHolder.checkBox.setChecked(false);
                    presenterInterface.updateNote(noteList.get(position),position);
                  //  viewHolder.noteCardView.setBackgroundColor(context.getResources().getColor(R.color.noteunCheck));
                }
            }
        });




    }

    public void showPopupMenu(View threeDotsView, final int position)
    {
        //3DOTS menu
        PopupMenu popupMenu=new PopupMenu(context,threeDotsView);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.note_edit_option:
                        showEditNoteDialog(noteList.get(position),position);
                        return true;

                    // delete option
                    case R.id.note_delete_option:

                     AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(" Delete note ").setMessage(" Do you want to delete note " + noteList.get(position).getNoteTitle()+"?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenterInterface.deleteNote(noteList.get(position),position);
                            }
                        })
                                .setNegativeButton("No", null).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater menuInflater=popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.note_three_dots_menu,popupMenu.getMenu());
        popupMenu.show();
    }

    private void showEditNoteDialog(Note note,int position)
    {
        EditNoteDialog editNoteDialog=new EditNoteDialog();
        Bundle bundle=new Bundle();
        bundle.putSerializable("note",note);
        bundle.putInt("position",position);
        bundle.putSerializable("presenterInterface",presenterInterface);
        editNoteDialog.setArguments(bundle);
        editNoteDialog.show(fragmentManager,"EditNoteDialog");
    }
    @Override
    public int getItemCount() {
         return noteList.size();

    }
}