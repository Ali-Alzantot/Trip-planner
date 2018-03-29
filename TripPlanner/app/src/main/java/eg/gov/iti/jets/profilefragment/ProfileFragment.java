package eg.gov.iti.jets.profilefragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import eg.gov.iti.jets.databasepkg.DatabaseAdapter;
import eg.gov.iti.jets.databasepkg.FirebaseDatabaseDAO;
import eg.gov.iti.jets.dtos.User;
import eg.gov.iti.jets.picassopkg.ImageHelper;
import eg.gov.iti.jets.sharedprefernces.SharedPreferencesHelperClass;
import eg.gov.iti.jets.tripplanner.R;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ProfileFragment extends Fragment implements View.OnClickListener, Serializable {

    private transient TextView saveProfileButton;
    private transient EditText emailEditText, oldPasswordEditText, userNameEditText, newPasswordEditText;
    String email, oldPassword, newPassword, userName;
    private transient View view;
    private transient ImageView profileImageView, editBtn;
    private User user;
    private boolean editFlag = false;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        saveProfileButton = view.findViewById(R.id.btn_save_profile);
        saveProfileButton.setOnClickListener(this);
        saveProfileButton.setVisibility(View.INVISIBLE);
        emailEditText = view.findViewById(R.id.profile_email_input);
        emailEditText.setFocusable(false);
        oldPasswordEditText = view.findViewById(R.id.profile_pass_input);
        userNameEditText = view.findViewById(R.id.profile_username_input);
        newPasswordEditText = view.findViewById(R.id.profile_repass_input);
        profileImageView = view.findViewById(R.id.profile_user_photo);
      //  editBtn = view.findViewById(R.id.profile_edit_btn);
        user = User.getUser();
        emailEditText.setText(user.getEmail());
        emailEditText.setFocusable(false);
        userNameEditText.setText(user.getUserName());
        if (user.getPhoto() != null && !user.getPhoto().trim().equals("")) {

            ImageHelper.getImage(getContext(), profileImageView, user.getPhoto(),R.drawable.profile_default_photo);
        }


        saveProfileButton.setVisibility(View.VISIBLE);

        return view;
    }

    private void enableEditText(boolean flag) {
        userNameEditText.setFocusable(flag);
        oldPasswordEditText.setFocusable(flag);
        newPasswordEditText.setFocusable(flag);

    }


    public void editProfile() {

        boolean chkError = false;
        userName = userNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        oldPassword = oldPasswordEditText.getText().toString();
        newPassword = newPasswordEditText.getText().toString();

        if(userName.isEmpty()){
            userNameEditText.setError("please enter Your user Name");
            chkError = true;
        }


         if (!oldPassword.isEmpty() ) {
             if(!oldPassword.equals(user.getPassword()) ) {
                 oldPasswordEditText.setError("Worng Password ");
                 chkError=true;
             }
             else if(newPassword.isEmpty()){
                 newPasswordEditText.setError("please enter your new Password ");
                 chkError=true;
             }
        }
        if(!newPassword.isEmpty()){
            if(newPassword.length() < 6){
                newPasswordEditText.setError("please enter Password not less than 6 chars");
                chkError=true;
            }

            else if (oldPassword.isEmpty() || !oldPassword.equals(user.getPassword()) ) {
                oldPasswordEditText.setError("Worng Password ");
                chkError=true;
            }
        }

        if (chkError)
            return;
        else {
            User user = User.getUser();
            user.setEmail(email);
            if(newPassword.isEmpty() ||newPassword.equals("New Password") )
                user.setPassword(user.getPassword());
            else
            user.setPassword(newPassword);
            user.setUserName(userName);
            new DatabaseAdapter(getApplicationContext()).updateUser(user);
            new FirebaseDatabaseDAO().addUserToFirebase(user);
            SharedPreferencesHelperClass.manageUser(ProfileFragment.this.getActivity(), user,
                    SharedPreferencesHelperClass.INSERT_UPDATE_USER, null);

            Toast.makeText(getApplicationContext(),"Profile Updated Successfully",Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public void onClick(View view) {

        //if the clicked button is edit
        if (view == saveProfileButton) {
            editProfile();
        }
    }
}
