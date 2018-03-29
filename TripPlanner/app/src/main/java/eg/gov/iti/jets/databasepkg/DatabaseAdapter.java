package eg.gov.iti.jets.databasepkg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;

import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.dtos.User;


/*
 @author Usama
 */
public class DatabaseAdapter implements Serializable {

    private Context context;


    public DatabaseAdapter(Context context) {
        this.context = context;
    }


    ///////////////////// User Functions ///////////////////////
    public User retreiveUser(String userEmail, String password) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.T_USER_COLUMN_USER_ID,
                        DatabaseHelper.T_USER_COLUMN_USER_NAME,
                        DatabaseHelper.T_USER_COLUMN_USER_EMAIL,
                        DatabaseHelper.T_USER_COLUMN_USER_PHOTO,
                        DatabaseHelper.T_USER_COLUMN_USER_PASS}
                , DatabaseHelper.T_USER_COLUMN_USER_EMAIL + "=? AND "
                        + DatabaseHelper.T_USER_COLUMN_USER_PASS + "=?",
                new String[]{userEmail, password}, null, null, null);
        User user = null;
        cursor.moveToFirst();
        if ((cursor != null) && (cursor.getCount() > 0)) {
            user = User.getUser();
            user.setUserId(cursor.getInt(0));
            user.setUserName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setPhoto(cursor.getString(3));
            user.setPassword(cursor.getString(4));
            cursor.close();
        }
        db.close();
        return user;
    }

    public User retreiveUserByEmail(String userEmail) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.T_USER_COLUMN_USER_ID,
                        DatabaseHelper.T_USER_COLUMN_USER_NAME,
                        DatabaseHelper.T_USER_COLUMN_USER_EMAIL,
                        DatabaseHelper.T_USER_COLUMN_USER_PHOTO,
                        DatabaseHelper.T_USER_COLUMN_USER_PASS}
                , DatabaseHelper.T_USER_COLUMN_USER_EMAIL + "=?",
                new String[]{userEmail}, null, null, null);
        User user = null;
        cursor.moveToFirst();
        if ((cursor != null) && (cursor.getCount() > 0)) {
            user = User.getUser();
            user = User.getUser();
            user.setUserId(cursor.getInt(0));
            user.setUserName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setPhoto(cursor.getString(3));
            user.setPassword(cursor.getString(4));
            cursor.close();
            cursor.close();
        }
        db.close();
        return user;
    }

    public User addUser(User user) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getUserValues(user);
        db.insert(DatabaseHelper.TABLE_USER, null, values);
        db.close();
        return user;
    }

    public Integer getNewUserId() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT MAX(" + DatabaseHelper.T_USER_COLUMN_USER_ID + ") FROM " + DatabaseHelper.TABLE_USER,
                null);
        cursor.moveToFirst();

        Integer id = null;
        if ((cursor != null) && (cursor.getCount() > 0)) {
            id = cursor.getInt(0) + 1;
        } else {
            id = 1;
        }
        cursor.close();
        return id;
    }


    public void updateUser(User user) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getUserValues(user);
        db.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.T_USER_COLUMN_USER_ID + " = ?", new String[]{user.getUserId().toString()});
        db.close();
    }

    public void deleteUser(User user) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_USER, DatabaseHelper.T_USER_COLUMN_USER_ID + " = ?", new String[]{user.getUserId().toString()});
        db.close();
    }


    private ContentValues getUserValues(User user) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.T_USER_COLUMN_USER_ID, user.getUserId());
        values.put(DatabaseHelper.T_USER_COLUMN_USER_NAME, user.getUserName());
        values.put(DatabaseHelper.T_USER_COLUMN_USER_EMAIL, user.getEmail());
        values.put(DatabaseHelper.T_USER_COLUMN_USER_PHOTO, user.getPhoto());
        values.put(DatabaseHelper.T_USER_COLUMN_USER_PASS, user.getPassword());
        return values;
    }


    ////////////////// End User Functions //////////////////////


    /////////////////  Trip Functions //////////////////////////
    public Trip addTrip(Trip trip) {

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        trip.setTripId(getNewTripId());
        ContentValues values = getTripValues(trip);
        db.insert(DatabaseHelper.TABLE_TRIP, null, values);
        db.close();
        return trip;
    }

    public void updateTrip(Trip trip) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getTripValues(trip);
        db.update(DatabaseHelper.TABLE_TRIP, values,
                DatabaseHelper.T_TRIP_COLUMN_TRIP_ID + " = ? AND "
                        + DatabaseHelper.T_TRIP_COLUMN_USER_ID + " = ? ",
                new String[]{trip.getTripId().toString(), trip.getUserId().toString()});
        db.close();
    }

    public void deleteTrip(Trip trip) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_TRIP,
                DatabaseHelper.T_TRIP_COLUMN_TRIP_ID + " = ? AND "
                        + DatabaseHelper.T_TRIP_COLUMN_USER_ID + " = ? ",
                new String[]{trip.getTripId().toString(), trip.getUserId().toString()});
        db.delete(DatabaseHelper.TABLE_NOTE, DatabaseHelper.T_NOTE_COLUMN_TRIP_ID + " = ? AND " + DatabaseHelper.T_NOTE_COLUMN_USER_ID + " = ? ", new String[]{trip.getTripId().toString(), trip.getUserId().toString()});
        db.close();
    }

    public ArrayList<Trip> getAllUpcomingTrips(User user) {
        String sortOrder = DatabaseHelper.T_TRIP_COLUMN_TRIP_START_DATE + " ASC," + DatabaseHelper.T_TRIP_COLUMN_TRIP_START_TIME + " ASC";
        ArrayList<Trip> TripList = null;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_TRIP,
                new String[]{DatabaseHelper.T_TRIP_COLUMN_TRIP_ID,
                        DatabaseHelper.T_TRIP_COLUMN_USER_ID,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_NAME,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_POINT,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_END_POINT,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_LONGITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_LATITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_END_LONGITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_END_LATITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_DATE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_TIME,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_GO_AND_RETURN,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_REPETITION,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_STATUS,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_PHOTO}
                , DatabaseHelper.T_TRIP_COLUMN_USER_ID + "=? AND ("
                        + DatabaseHelper.T_TRIP_COLUMN_TRIP_STATUS + "= '" + Trip.ONGOING + "' OR "
                        + DatabaseHelper.T_TRIP_COLUMN_TRIP_STATUS + "= '" + Trip.HANGING + "' OR "
                        + DatabaseHelper.T_TRIP_COLUMN_TRIP_STATUS + "= '" + Trip.UPCOMING + "')",
                new String[]{user.getUserId().toString()}, null, null, sortOrder, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            TripList = new ArrayList<Trip>();
            while (cursor.moveToNext()) {
                Trip trip = new Trip();
                trip.setTripId(cursor.getInt(0));
                trip.setUserId(cursor.getInt(1));
                trip.setTripName(cursor.getString(2));
                trip.setStartPoint(cursor.getString(3));
                trip.setEndPoint(cursor.getString(4));
                trip.setStartLongitude(cursor.getDouble(5));
                trip.setStartLatitude(cursor.getDouble(6));
                trip.setEndLongitude(cursor.getDouble(7));
                trip.setEndLatitude(cursor.getDouble(8));
                trip.setStartDate(cursor.getString(9));
                trip.setStartTime(cursor.getString(10));
                trip.setGoAndReturn(cursor.getString(11));
                trip.setRepetition(cursor.getString(12));
                trip.setStatus(cursor.getString(13));
                trip.setPhoto(cursor.getString(14));
                TripList.add(trip);
            }
            cursor.close();
        }
        db.close();
        return TripList;
    }

    /////////////////// Get History trips
    public ArrayList<Trip> getAllHistoryTrips(User user) {
        String sortOrder = DatabaseHelper.T_TRIP_COLUMN_TRIP_START_DATE + " DESC," + DatabaseHelper.T_TRIP_COLUMN_TRIP_START_TIME + " DESC";
        ArrayList<Trip> TripList = null;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_TRIP,
                new String[]{DatabaseHelper.T_TRIP_COLUMN_TRIP_ID,
                        DatabaseHelper.T_TRIP_COLUMN_USER_ID,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_NAME,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_POINT,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_END_POINT,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_LONGITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_LATITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_END_LONGITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_END_LATITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_DATE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_TIME,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_GO_AND_RETURN,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_REPETITION,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_STATUS,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_PHOTO}
                , DatabaseHelper.T_TRIP_COLUMN_USER_ID + "=? AND ("
                        + DatabaseHelper.T_TRIP_COLUMN_TRIP_STATUS + "= '" + Trip.CANCELLED + "' OR "
                        + DatabaseHelper.T_TRIP_COLUMN_TRIP_STATUS + "= '" + Trip.ENDED + "')",
                new String[]{user.getUserId().toString()}, null, null, sortOrder, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            TripList = new ArrayList<Trip>();
            while (cursor.moveToNext()) {
                Trip trip = new Trip();
                trip.setTripId(cursor.getInt(0));
                trip.setUserId(cursor.getInt(1));
                trip.setTripName(cursor.getString(2));
                trip.setStartPoint(cursor.getString(3));
                trip.setEndPoint(cursor.getString(4));
                trip.setStartLongitude(cursor.getDouble(5));
                trip.setStartLatitude(cursor.getDouble(6));
                trip.setEndLongitude(cursor.getDouble(7));
                trip.setEndLatitude(cursor.getDouble(8));
                trip.setStartDate(cursor.getString(9));
                trip.setStartTime(cursor.getString(10));
                trip.setGoAndReturn(cursor.getString(11));
                trip.setRepetition(cursor.getString(12));
                trip.setStatus(cursor.getString(13));
                trip.setPhoto(cursor.getString(14));
                TripList.add(trip);
            }
            cursor.close();
        }
        db.close();
        return TripList;
    }

    public Trip getTripById(Integer tripId, Integer userId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRIP,
                new String[]{DatabaseHelper.T_TRIP_COLUMN_TRIP_ID,
                        DatabaseHelper.T_TRIP_COLUMN_USER_ID,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_NAME,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_POINT,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_END_POINT,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_LONGITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_LATITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_END_LONGITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_END_LATITUDE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_DATE,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_START_TIME,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_GO_AND_RETURN,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_REPETITION,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_STATUS,
                        DatabaseHelper.T_TRIP_COLUMN_TRIP_PHOTO}
                , DatabaseHelper.T_TRIP_COLUMN_USER_ID + "= ? AND " + DatabaseHelper.T_TRIP_COLUMN_TRIP_ID + "=?",
                new String[]{userId.toString(), tripId.toString()}, null, null, null);
        Trip trip = null;
        cursor.moveToFirst();
        if ((cursor != null) && (cursor.getCount() > 0)) {
            trip = new Trip();
            trip.setTripId(cursor.getInt(0));
            trip.setUserId(cursor.getInt(1));
            trip.setTripName(cursor.getString(2));
            trip.setStartPoint(cursor.getString(3));
            trip.setEndPoint(cursor.getString(4));
            trip.setStartLongitude(cursor.getDouble(5));
            trip.setStartLatitude(cursor.getDouble(6));
            trip.setEndLongitude(cursor.getDouble(7));
            trip.setEndLatitude(cursor.getDouble(8));
            trip.setStartDate(cursor.getString(9));
            trip.setStartTime(cursor.getString(10));
            trip.setGoAndReturn(cursor.getString(11));
            trip.setRepetition(cursor.getString(12));
            trip.setStatus(cursor.getString(13));
            trip.setPhoto(cursor.getString(14));
            cursor.close();
        }
        db.close();
        return trip;
    }


    private Integer getNewTripId() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT MAX(" + DatabaseHelper.T_TRIP_COLUMN_TRIP_ID + ") FROM " + DatabaseHelper.TABLE_TRIP,
                null);
        cursor.moveToFirst();
        Integer id = null;
        if ((cursor != null) && (cursor.getCount() > 0)) {
            id = cursor.getInt(0) + 1;
        } else {
            id = 1;
        }
        cursor.close();
        return id;
    }
    public boolean isTripExist(Trip trip) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRIP,
                new String[]{DatabaseHelper.T_TRIP_COLUMN_TRIP_ID,
                        DatabaseHelper.T_TRIP_COLUMN_USER_ID,}
                , DatabaseHelper.T_TRIP_COLUMN_USER_ID + "= " + trip.getUserId() + " AND "
                        + DatabaseHelper.T_TRIP_COLUMN_TRIP_ID + "= " + trip.getTripId(),
                null, null, null, null);

        if (cursor.getCount() > 0) {
            db.close(); return true;
        } else {
            db.close(); return false;
        }
    }
    private ContentValues getTripValues(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.T_TRIP_COLUMN_USER_ID, trip.getUserId());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_ID, trip.getTripId());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_NAME, trip.getTripName());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_START_POINT, trip.getStartPoint());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_END_POINT, trip.getEndPoint());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_START_LONGITUDE, trip.getStartLongitude());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_START_LATITUDE, trip.getStartLatitude());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_END_LONGITUDE, trip.getEndLongitude());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_END_LATITUDE, trip.getEndLatitude());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_START_DATE, trip.getStartDate());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_START_TIME, trip.getStartTime());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_GO_AND_RETURN, trip.getGoAndReturn());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_REPETITION, trip.getRepetition());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_STATUS, trip.getStatus());
        values.put(DatabaseHelper.T_TRIP_COLUMN_TRIP_PHOTO, trip.getPhoto());
        return values;
    }

    /////////////////  End Trip Functions /////////////////////


    /////////////////  Note Functions //////////////////////////
    public Note addNote(Note note) {

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        note.setNoteId(getNewNoteId());
        ContentValues values = getNoteValues(note);
        db.insert(DatabaseHelper.TABLE_NOTE, null, values);
        db.close();
        return note;
    }

    public void updateNote(Note note) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getNoteValues(note);
        db.update(DatabaseHelper.TABLE_NOTE, values,
                DatabaseHelper.T_NOTE_COLUMN_NOTE_ID + " = ? AND "
                        + DatabaseHelper.T_NOTE_COLUMN_TRIP_ID + " = ? AND "
                        + DatabaseHelper.T_NOTE_COLUMN_USER_ID + " = ? "

                , new String[]{note.getNoteId().toString(), note.getTripId().toString(), note.getUserId().toString()});
        db.close();
    }

    public void deleteNote(Note note) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NOTE,
                DatabaseHelper.T_NOTE_COLUMN_TRIP_ID
                        + " = ? AND " + DatabaseHelper.T_NOTE_COLUMN_USER_ID +
                        " = ? AND " + DatabaseHelper.T_NOTE_COLUMN_NOTE_ID + " = ? ",
                new String[]{note.getTripId().toString(),
                        note.getUserId().toString(), note.getNoteId().toString()});
        db.close();
    }

    public ArrayList<Note> getAllNotes(Trip trip) {
        String sortOrder = DatabaseHelper.T_NOTE_COLUMN_TRIP_ID + " ASC";
        ArrayList<Note> NoteList = null;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTE,
                new String[]{DatabaseHelper.T_NOTE_COLUMN_NOTE_ID,
                        DatabaseHelper.T_NOTE_COLUMN_TRIP_ID,
                        DatabaseHelper.T_NOTE_COLUMN_USER_ID,
                        DatabaseHelper.T_NOTE_COLUMN_NOTE_TITLE,
                        DatabaseHelper.T_NOTE_COLUMN_NOTE,
                        DatabaseHelper.T_NOTE_COLUMN_STATUS}
                , DatabaseHelper.T_NOTE_COLUMN_USER_ID + "= ? AND " + DatabaseHelper.T_NOTE_COLUMN_TRIP_ID + "=?",
                new String[]{trip.getUserId().toString(), trip.getTripId().toString()}, null, null, sortOrder, null);

        if ((cursor != null) && (cursor.getCount() > 0)) {
            NoteList = new ArrayList<Note>();
            while (cursor.moveToNext()) {
                Note note = new Note();
                note.setNoteId(cursor.getInt(0));
                note.setTripId(cursor.getInt(1));
                note.setUserId(cursor.getInt(2));
                note.setNoteTitle(cursor.getString(3));
                note.setNote(cursor.getString(4));
                note.setStatus(cursor.getString(5));
                NoteList.add(note);
            }
            cursor.close();
        }
        db.close();
        return NoteList;
    }

    public Note getNoteById(Integer noteId, Trip trip) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTE,
                new String[]{DatabaseHelper.T_NOTE_COLUMN_NOTE_ID,
                        DatabaseHelper.T_NOTE_COLUMN_TRIP_ID,
                        DatabaseHelper.T_NOTE_COLUMN_USER_ID,
                        DatabaseHelper.T_NOTE_COLUMN_NOTE_TITLE,
                        DatabaseHelper.T_NOTE_COLUMN_NOTE,
                        DatabaseHelper.T_NOTE_COLUMN_STATUS}
                , DatabaseHelper.T_NOTE_COLUMN_NOTE_ID + "= ? AND " + DatabaseHelper.T_NOTE_COLUMN_TRIP_ID + "=? AND " + DatabaseHelper.T_NOTE_COLUMN_USER_ID + "=?",
                new String[]{noteId.toString(), trip.getTripId().toString(), trip.getUserId().toString()}, null, null, null, null);
        ;

        Note note = null;
        cursor.moveToFirst();
        if ((cursor != null) && (cursor.getCount() > 0)) {
            note = new Note();
            note.setNoteId(cursor.getInt(0));
            note.setTripId(cursor.getInt(1));
            note.setUserId(cursor.getInt(2));
            note.setNoteTitle(cursor.getString(3));
            note.setNote(cursor.getString(4));
            note.setStatus(cursor.getString(5));
            cursor.close();
        }
        db.close();
        return note;
    }

    public Integer getNewNoteId() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT MAX(" + DatabaseHelper.T_NOTE_COLUMN_NOTE_ID + ") FROM " + DatabaseHelper.TABLE_NOTE,
                null);
        cursor.moveToFirst();

        Integer id = null;
        if ((cursor != null) && (cursor.getCount() > 0)) {
            id = cursor.getInt(0) + 1;
        } else {
            id = 1;
        }
        cursor.close();
        return id;
    }

    private ContentValues getNoteValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.T_NOTE_COLUMN_NOTE_ID, note.getNoteId());
        values.put(DatabaseHelper.T_NOTE_COLUMN_TRIP_ID, note.getTripId());
        values.put(DatabaseHelper.T_NOTE_COLUMN_USER_ID, note.getUserId());
        values.put(DatabaseHelper.T_NOTE_COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(DatabaseHelper.T_NOTE_COLUMN_NOTE, note.getNote());
        values.put(DatabaseHelper.T_NOTE_COLUMN_STATUS, note.getStatus());
        return values;
    }

    /////////////////  End Note Functions /////////////////////


    ///////////////   Helper Class ////////////////////////////
    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "TripPlannerApp.db";
        private static final String TABLE_USER = "user";
        private static final String TABLE_TRIP = "trip";
        private static final String TABLE_NOTE = "note";

        private static final String T_USER_COLUMN_USER_ID = "user_id",
                T_USER_COLUMN_USER_NAME = "user_name",
                T_USER_COLUMN_USER_PHOTO = "photo",
                T_USER_COLUMN_USER_EMAIL = "user_email",
                T_USER_COLUMN_USER_PASS = "user_password";

        private static final String T_TRIP_COLUMN_TRIP_ID = "trip_id",
                T_TRIP_COLUMN_USER_ID = "user_id",
                T_TRIP_COLUMN_TRIP_NAME = "trip_name",
                T_TRIP_COLUMN_TRIP_START_POINT = "start_point",
                T_TRIP_COLUMN_TRIP_END_POINT = "end_point",
                T_TRIP_COLUMN_TRIP_START_LONGITUDE = "start_longitude",
                T_TRIP_COLUMN_TRIP_START_LATITUDE = "start_latitude",
                T_TRIP_COLUMN_TRIP_END_LONGITUDE = "destination_longitude",
                T_TRIP_COLUMN_TRIP_END_LATITUDE = "destination_latitude",
                T_TRIP_COLUMN_TRIP_START_DATE = "start_date",
                T_TRIP_COLUMN_TRIP_START_TIME = "start_time",
                T_TRIP_COLUMN_TRIP_GO_AND_RETURN = "go_return",
                T_TRIP_COLUMN_TRIP_REPETITION = "repetition",
                T_TRIP_COLUMN_TRIP_STATUS = "status",
                T_TRIP_COLUMN_TRIP_PHOTO = "trip_photo";


        private static final String T_NOTE_COLUMN_NOTE_ID = "note_id",
                T_NOTE_COLUMN_TRIP_ID = "trip_id",
                T_NOTE_COLUMN_USER_ID = "user_id",
                T_NOTE_COLUMN_NOTE = "note",
                T_NOTE_COLUMN_NOTE_TITLE = "note_title",
                T_NOTE_COLUMN_STATUS = "status";


        // create tables
        private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + T_USER_COLUMN_USER_ID + " INTEGER PRIMARY KEY ,"
                + T_USER_COLUMN_USER_NAME + " TEXT,"
                + T_USER_COLUMN_USER_EMAIL + " TEXT,"
                + T_USER_COLUMN_USER_PHOTO + " TEXT,"
                + T_USER_COLUMN_USER_PASS + " TEXT,"
                + "  UNIQUE (" + T_USER_COLUMN_USER_EMAIL + "))";

        private static final String CREATE_TRIP_TABLE = "CREATE TABLE " + TABLE_TRIP + "("
                + T_TRIP_COLUMN_TRIP_ID + " INTEGER ,"
                + T_TRIP_COLUMN_USER_ID + " INTEGER,"
                + T_TRIP_COLUMN_TRIP_NAME + " TEXT,"
                + T_TRIP_COLUMN_TRIP_START_POINT + " TEXT,"
                + T_TRIP_COLUMN_TRIP_END_POINT + " TEXT,"
                + T_TRIP_COLUMN_TRIP_START_LONGITUDE + " TEXT,"
                + T_TRIP_COLUMN_TRIP_START_LATITUDE + " TEXT,"
                + T_TRIP_COLUMN_TRIP_END_LONGITUDE + " TEXT,"
                + T_TRIP_COLUMN_TRIP_END_LATITUDE + " TEXT,"
                + T_TRIP_COLUMN_TRIP_START_TIME + " TEXT,"
                + T_TRIP_COLUMN_TRIP_START_DATE + " TEXT,"
                + T_TRIP_COLUMN_TRIP_GO_AND_RETURN + " TEXT,"
                + T_TRIP_COLUMN_TRIP_REPETITION + " TEXT,"
                + T_TRIP_COLUMN_TRIP_STATUS + " TEXT,"
                + T_TRIP_COLUMN_TRIP_PHOTO + " TEXT,"
                + "PRIMARY KEY (" + T_TRIP_COLUMN_TRIP_ID + "," + T_TRIP_COLUMN_USER_ID + "),"
                + " FOREIGN KEY(" + T_TRIP_COLUMN_USER_ID + ") REFERENCES " + "TABLE_USER(" + T_USER_COLUMN_USER_ID + ") )";


        private static final String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NOTE + "("
                + T_NOTE_COLUMN_NOTE_ID + " INTEGER ,"
                + T_NOTE_COLUMN_TRIP_ID + " INTEGER,"
                + T_NOTE_COLUMN_USER_ID + " INTEGER,"
                + T_NOTE_COLUMN_NOTE + " TEXT,"
                + T_NOTE_COLUMN_NOTE_TITLE+ " TEXT,"
                + T_NOTE_COLUMN_STATUS + " TEXT,"
                + "PRIMARY KEY (" + T_NOTE_COLUMN_NOTE_ID + "," + T_NOTE_COLUMN_TRIP_ID + "," + T_NOTE_COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + T_NOTE_COLUMN_TRIP_ID + ") REFERENCES " + TABLE_TRIP + "(" + T_TRIP_COLUMN_TRIP_ID + "),"
                + "FOREIGN KEY(" + T_NOTE_COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + T_USER_COLUMN_USER_ID + ") )";

        // drop tables
        private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
        private String DROP_TRIP_TABLE = "DROP TABLE IF EXISTS " + TABLE_TRIP;
        private String DROP_NOTE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NOTE;


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_USER_TABLE);
            db.execSQL(CREATE_TRIP_TABLE);
            db.execSQL(CREATE_NOTE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_USER_TABLE);
            db.execSQL(DROP_TRIP_TABLE);
            db.execSQL(DROP_NOTE_TABLE);
            onCreate(db);
        }

    }

}

