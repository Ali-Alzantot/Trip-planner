package eg.gov.iti.jets.sharedprefernces;

import android.content.Context;

import eg.gov.iti.jets.dtos.User;

/**
 * Created by Anonymous on 16/03/2018.
 */

public class SharedPreferencesHelperClass {
    private static final String SHARED_PREFERENCES = "Trip_Planner";
    public static final Integer INSERT_UPDATE_USER = 1;
    public static final Integer DELETE_USER = 0;
    public static final Integer APPLICATION_LOGIN = 0;
    public static final Integer FACEBOOK_LOGIN = 1;


    public static void manageUser(Context context, User user, Integer flag, Integer loginMethod) {
        android.content.SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES, 0);
        if (flag == INSERT_UPDATE_USER) {
            android.content.SharedPreferences.Editor editor = sp.edit();
            editor.putString("email", user.getEmail());
            editor.putString("password", user.getPassword());
            editor.putString("photo", user.getPhoto());
            editor.putString("userName", user.getUserName());
            editor.putInt("id", user.getUserId());
            if (loginMethod != null) {
                editor.putInt("facebookLogin", loginMethod);
            }
            editor.commit();
        } else if (flag == DELETE_USER) {
            sp.edit().clear().commit();
            User.getUser().setUserId(null);
            User.getUser().setPhoto(null);
            User.getUser().setUserName(null);
            User.getUser().setEmail(null);
        }
    }

    public static User getUser(Context context) {
        User user = User.getUser();
        android.content.SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES, 0);
        if (sp.getInt("id", -1) != -1) {
            user.setUserId(sp.getInt("id", 0));
            user.setEmail(sp.getString("email", null));
            user.setPassword(sp.getString("password", null));
            user.setUserName(sp.getString("userName", null));
            user.setPhoto(sp.getString("photo", null));

        }
        return user;
    }

    public static boolean isFacebookUser(Context context) {
        android.content.SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES, 0);
        int res = sp.getInt("facebookLogin", -1);
        if (res == -1) return false;
        else if (res == 1) return true;
        else return false;

    }

}
