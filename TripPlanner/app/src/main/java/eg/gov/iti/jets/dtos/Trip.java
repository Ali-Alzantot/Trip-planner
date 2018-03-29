package eg.gov.iti.jets.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
 @author Usama
 */

@SuppressWarnings("serial")
public class Trip implements Serializable {
    @SerializedName("tripId")
    private Integer tripId;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("tripName")
    private String tripName;
    @SerializedName("startPoint")
    private String startPoint;
    @SerializedName("endPoint")
    private String endPoint;
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("goAndReturn")
    private String goAndReturn = ONE_WAY;
    @SerializedName("repetition")
    private String repetition;
    @SerializedName("status")
    private String status;
    @SerializedName("startLongitude")
    private Double startLongitude;
    @SerializedName("startLatitude")
    private Double startLatitude;
    @SerializedName("endLongitude")
    private Double endLongitude;
    @SerializedName("endLatitude")
    private Double endLatitude;

    @SerializedName("photo")
    private String photo;

    public static final transient String ONE_WAY = "ONE_WAY";
    public static final transient String GO_AND_RETURN = "GO_AND_RETURN";

    public static final transient String UPCOMING = "UP_COMING";
    public static final transient String ONGOING = "ON_GOING";
    public static final transient String HANGING = "HANGING";
    public static final transient String ENDED = "ENDED";
    public static final transient String CANCELLED = "CANCELLED";

    public Trip() {
    }

    public Trip(Integer userId, String tripName, String startPoint, String endPoint, String startTime, String startDate, String goAndReturn, String repetition, String status, Double startLongitude, Double startLatitude, Double endLongitude, Double endLatitude, String photo) {
        this.userId = userId;
        this.tripName = tripName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startTime = startTime;
        this.startDate = startDate;
        this.goAndReturn = goAndReturn;
        this.repetition = repetition;
        this.status = status;
        this.startLongitude = startLongitude;
        this.startLatitude = startLatitude;
        this.endLongitude = endLongitude;
        this.endLatitude = endLatitude;
        this.photo = photo;
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

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getRepetition() {
        return repetition;
    }

    public void setRepetition(String repetition) {
        this.repetition = repetition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public Double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGoAndReturn() {
        return goAndReturn;
    }

    public void setGoAndReturn(String goAndReturn) {
        this.goAndReturn = goAndReturn;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Trip)) {
            return false;
        }
        if (obj instanceof Trip)
            if (this.tripId.equals(((Trip) obj).getTripId()) && this.userId.equals(((Trip) obj).getUserId())) {
                return true;
            }

        return false;
    }
}
