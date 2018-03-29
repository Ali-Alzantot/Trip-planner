package eg.gov.iti.jets.databasepkg;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.dtos.User;

/**
 * Created by Ali Alzantot on 07/03/2018.
 */

public class FirebaseDatabaseDAO implements Serializable {

    private int userId = 0, noteId = 0, tripId = 0;

    public void getMaxuserIDFromFirebase(final MyCallback callback) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User singleUser = userSnapshot.getValue(User.class);
                            userId = Math.max(singleUser.getUserId(), userId);
                        }

                        callback.onMaxIdCallBack(userId);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


    public void addUserToFirebase(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(Integer.toString(user.getUserId())).setValue(user);
    }


    public void getUserFromFirebase(final String userEmail, final String password, final MyCallback myCallback) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = null;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User singleUser = userSnapshot.getValue(User.class);
                            if (singleUser.getEmail().equals(userEmail) && singleUser.getPassword().equals(password)) {
                                user = singleUser;
                                break;
                            }
                        }
                        myCallback.onGetUserCallBack(user);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    public void getUserFromFirebaseByEmail(final String userEmail, final MyCallback myCallback) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = null;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User singleUser = userSnapshot.getValue(User.class);
                            if (singleUser.getEmail().equals(userEmail)) {
                                user = singleUser;
                                break;
                            }
                        }
                        myCallback.onGetUserByEmailCallBack(user);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


    public void createAndUpdateTripOnFirebase(Trip trip) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(trip.getUserId() + "trips");
        myRef.child(Integer.toString(trip.getTripId())).setValue(trip);
    }

    public void removeTripFromFirebase(Trip trip) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(trip.getUserId() + "trips");
        myRef.child(Integer.toString(trip.getTripId())).setValue(null);
    }


    public void retrieveUserTripsFromFirebase(User user, final MyCallback myCallback) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(user.getUserId() + "trips");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Trip> trips = new ArrayList<Trip>();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Trip trip = userSnapshot.getValue(Trip.class);
                            trips.add(trip);
                        }
                        myCallback.onRetrieveUserTripsCallBack(trips);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


    public void createAndUpdateNotesOnFirebase(Note note) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(note.getUserId() + "trip" + note.getTripId() + "notes");
        myRef.child(Integer.toString(note.getNoteId())).setValue(note);
    }

    public void removeNotesFromFirebase(Note note) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(note.getUserId() + "trip" + note.getTripId() + "notes");
        myRef.child(Integer.toString(note.getNoteId())).setValue(null);
    }


    public void retrieveUserNotesFromFirebase(Trip trip, final MyCallback myCallback) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(trip.getUserId() + "trip" + trip.getTripId() + "notes");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Note> notes = new ArrayList<Note>();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Note note = userSnapshot.getValue(Note.class);
                            notes.add(note);
                        }
                        myCallback.onRetrieveUserNotesCallBack(notes);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    public void uploadTripImage(final SaveImageCallBack callBack, Bitmap tripPhoto, final String placeName) {

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("tripImages/");
        StorageReference ref = storageReference.child(placeName);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        tripPhoto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        ref.putBytes(imageBytes)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        StorageReference storageRef = storage.getReference();
                        storageRef.child("tripImages/" + placeName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                callBack.savedImageUrl(uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                callBack.savedImageUrl(null);
                            }
                        });

                    }
                });
    }
    public void uploadUserImage(final SaveImageCallBack callBack, Bitmap userPhoto, final String userId) {

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("userImages/");
        StorageReference ref = storageReference.child(userId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        userPhoto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        ref.putBytes(imageBytes)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        StorageReference storageRef = storage.getReference();
                        storageRef.child("userImages/" + userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                callBack.savedImageUrl(uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                callBack.savedImageUrl(null);
                            }
                        });

                    }
                });
    }
}





