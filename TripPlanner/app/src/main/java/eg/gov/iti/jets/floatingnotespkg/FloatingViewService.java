package eg.gov.iti.jets.floatingnotespkg;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.databasepkg.DatabaseAdapter;
import eg.gov.iti.jets.databasepkg.FirebaseDatabaseDAO;
import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.dtos.User;
import eg.gov.iti.jets.tripplanner.R;

public class FloatingViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private transient Trip trip;
    private transient DatabaseAdapter databaseAdapter;
    private transient FirebaseDatabaseDAO firebaseDatabaseDAO;
    private transient DBModel dbModel;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        trip=(Trip)intent.getSerializableExtra("trip");

        //Set the view while floating view is expanded.
        databaseAdapter=new DatabaseAdapter(getApplicationContext());
        firebaseDatabaseDAO=new FirebaseDatabaseDAO();
        dbModel=DBModel.getInstance(getApplicationContext());
        ArrayList<Note> notes=databaseAdapter.getAllNotes(trip);
        if (notes== null){
            notes=new ArrayList<Note>();
            Note note=new Note();
            note.setNote("there is no notes");
            note.setNoteId(-1);
            note.setUserId(-1);
            note.setTripId(-1);
            note.setNoteTitle("no title");
            note.setStatus("no status");
            notes.add(note);
        }


        Button finishTrip=(Button) mFloatingView.findViewById(R.id.finishTrip);
        finishTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip.setStatus("ENDED");
                dbModel.updateTrip(trip);
                firebaseDatabaseDAO.createAndUpdateTripOnFirebase(trip);
                FloatingViewService.this.stopSelf();
            }
        });
        NotesCustomAdatper customAdapter=new NotesCustomAdatper(this,R.layout.single_row,R.id.noteCheckBox,notes);
        ListView listView=(ListView) mFloatingView.findViewById(R.id.notesListView);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox checkBox=view.findViewById(R.id.noteCheckBox);
                TextView noteId=view.findViewById(R.id.noteId);
                TextView tripId=view.findViewById(R.id.tripId);
                TextView userId=view.findViewById(R.id.userId);
                TextView floatingNoteTitle = view.findViewById(R.id.floatingnoteTitle);
                TextView floatingNoteStatus = view.findViewById(R.id.floatingNoteStatus);
                if(noteId.getText().toString().equals("-1") && tripId.getText().toString().equals("-1") && userId.getText().toString().equals("-1") )
                    System.out.println("");
                else{
                    Note note=new Note();
                    note.setNoteId(Integer.parseInt(noteId.getText().toString()));
                    note.setTripId(Integer.parseInt(tripId.getText().toString()));
                    note.setUserId(Integer.parseInt(userId.getText().toString()));
                    note.setStatus(floatingNoteStatus.getText().toString());
                    note.setNoteTitle(floatingNoteTitle.getText().toString());
                    note.setNote(checkBox.getText().toString());

                    if((floatingNoteStatus.getText().toString().equals("STATUS_CHECKED"))) {
                        checkBox.setTextColor(Color.RED);
                        note.setStatus("STATUS_UNCHECKED");
                        floatingNoteStatus.setText("STATUS_UNCHECKED");
                    }
                    else{
                        checkBox.setTextColor(Color.BLACK);
                        note.setStatus("STATUS_CHECKED");
                        floatingNoteStatus.setText("STATUS_CHECKED");
                    }
                    databaseAdapter.updateNote(note);
                    firebaseDatabaseDAO.createAndUpdateNotesOnFirebase(note);

                }
            }
        });






        return START_STICKY;
    }

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }


        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);

        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopSelf();
            }
        });

        //Set the close button
        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });



        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }


    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);

    }
}