package eg.gov.iti.jets.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
 @author Usama
 */
@SuppressWarnings("serial")
public class Note implements Serializable {
    @SerializedName("noteId")
    private Integer noteId;
    @SerializedName("tripId")
    private Integer tripId;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("noteTitle")
    private String noteTitle;
    @SerializedName("note")
    private String note;
    @SerializedName("status")
    private String status;

    public static final transient String STATUS_UNCHECKED="STATUS_UNCHECKED";
    public static final transient String STATUS_CHECKED="STATUS_CHECKED";

    public Note() {
    }

    public Note(Integer tripId, Integer userId,String noteTitle, String note, String status) {
        this.tripId = tripId;
        this.userId = userId;
        this.noteTitle=noteTitle;
        this.note = note;
        this.status = status;
    }



    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
