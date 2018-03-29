package eg.gov.iti.jets.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
 @author Usama
 */

@SuppressWarnings("serial")
public class User implements Serializable {
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("photo")
    private String photo;
    private static transient User currentUser;

    private User() {
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public static User getUser() {
        if (currentUser == null) currentUser = new User();
        return currentUser;
    }
}
