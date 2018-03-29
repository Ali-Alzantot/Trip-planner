package eg.gov.iti.jets.signupactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.databasepkg.DatabaseAdapter;
import eg.gov.iti.jets.databasepkg.FirebaseDatabaseDAO;
import eg.gov.iti.jets.databasepkg.MyCallback;
import eg.gov.iti.jets.databasepkg.SaveImageCallBack;
import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.dtos.User;
import eg.gov.iti.jets.sharedprefernces.SharedPreferencesHelperClass;
import eg.gov.iti.jets.tripplanner.NavigationDrawerActivity;
import eg.gov.iti.jets.tripplanner.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, SaveImageCallBack {
    private transient TextView signUpButton, imageRegValidation, alreadyUser;
    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 234;
    private transient ImageView imageView;
    Bitmap bitmap;
    private transient EditText emailRegText, passwordRegText, userNameRegTex, passwordConfirmRegText;
    String email, password, passwordConfirm, userName;
    boolean chkImageUpload = false;
    User userFromReg, gettenUser;

    private transient ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBModel.getInstance(this);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.com_facebook_auth_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering...");


        setContentView(R.layout.activity_signup);
        signUpButton = findViewById(R.id.btn_signup);
        signUpButton.setOnClickListener(this);
        imageView = findViewById(R.id.imageRegView);
        imageView.setOnClickListener(this);
        emailRegText = findViewById(R.id.signup_email_input);
        userNameRegTex = findViewById(R.id.signup_username_input);
        passwordRegText = findViewById(R.id.signup_pass_input);
        passwordConfirmRegText = findViewById(R.id.signup_repass_input);
        imageRegValidation = findViewById(R.id.s);
        alreadyUser = findViewById(R.id.already_have_account);
        alreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.this.onBackPressed();
            }
        });
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
        signUpButton.setTypeface(custom_font);
        emailRegText.setTypeface(custom_font);
        userNameRegTex.setTypeface(custom_font);
        passwordRegText.setTypeface(custom_font);
        passwordConfirmRegText.setTypeface(custom_font);
        alreadyUser.setTypeface(custom_font);

    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                chkImageUpload = true;
                imageRegValidation.setText("");


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void signUpMethod() {
        boolean chkError = false;
        userName = userNameRegTex.getText().toString();
        email = emailRegText.getText().toString();
        password = passwordRegText.getText().toString();
        passwordConfirm = passwordConfirmRegText.getText().toString();

        if (userName.isEmpty()) {
            userNameRegTex.setError("please enter Your user Name");
            chkError = true;
        }


        if (email.isEmpty()) {
            emailRegText.setError("please enter Your Email");
            chkError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegText.setError("please enter a Valid Email");
            chkError = true;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordRegText.setError("please enter Password not less than 6 chars");
            chkError = true;
        }
        if (passwordConfirm.isEmpty() || passwordConfirm.length() < 6) {
            passwordConfirmRegText.setError("please re enter Password ");
            chkError = true;
        }

        if (!password.equals(passwordConfirm)) {
            passwordConfirmRegText.setError("password dosen't match");
            chkError = true;
        }

        if (chkImageUpload == false) {
            imageRegValidation.setText("Please Upload your Photo");
            chkError = true;
        } else {
            imageRegValidation.setText("");
        }

        if (chkError)
            return;
        else {
            progressDialog.show();
            userFromReg = User.getUser();
            userFromReg.setEmail(email);
            userFromReg.setPassword(password);
            userFromReg.setUserName(userName);
            new FirebaseDatabaseDAO().getMaxuserIDFromFirebase(new MyCallback() {
                @Override
                public void onMaxIdCallBack(int max) {
                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(getApplicationContext());
                    max = Math.max(databaseAdapter.getNewUserId(), max);
                    userFromReg.setUserId(max + 1);
                }

                @Override
                public void onGetUserCallBack(User user) {
                }

                @Override
                public void onGetUserByEmailCallBack(User user) {
                }

                @Override
                public void onRetrieveUserTripsCallBack(ArrayList<Trip> trips) {

                }

                @Override
                public void onRetrieveUserNotesCallBack(ArrayList<Note> notes) {

                }
            });


            gettenUser = new DatabaseAdapter(getApplicationContext()).retreiveUserByEmail(userFromReg.getEmail());
            if (gettenUser == null) {
                new FirebaseDatabaseDAO().getUserFromFirebaseByEmail(userFromReg.getEmail(), new MyCallback() {
                    @Override
                    public void onMaxIdCallBack(int max) {
                    }

                    @Override
                    public void onGetUserCallBack(User user) {
                    }

                    @Override
                    public void onGetUserByEmailCallBack(User user) {
                        gettenUser = user;
                        if (gettenUser == null) {

                            //not finding user in both loacl storgae or firebase

                            new DatabaseAdapter(getApplicationContext()).addUser(userFromReg);

                            new FirebaseDatabaseDAO().uploadUserImage(SignupActivity.this, bitmap, userFromReg.getUserId().toString());
                            new FirebaseDatabaseDAO().addUserToFirebase(userFromReg);
                            SharedPreferencesHelperClass.manageUser(SignupActivity.this, userFromReg
                                    , SharedPreferencesHelperClass.INSERT_UPDATE_USER, SharedPreferencesHelperClass.APPLICATION_LOGIN);
                        } else {
                            //found user in firebase
                            progressDialog.dismiss();
                            emailRegText.setError("Email Already Exist");
                        }
                    }

                    @Override
                    public void onRetrieveUserTripsCallBack(ArrayList<Trip> trips) {

                    }

                    @Override
                    public void onRetrieveUserNotesCallBack(ArrayList<Note> notes) {

                    }
                });

            } else {
                //found user in local Strogae
                emailRegText.setError("Email Already Exist");
            }

        }


    }


    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == imageView) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == signUpButton) {
            signUpMethod();
        }
    }

    @Override
    public void savedImageUrl(String url) {
        userFromReg.setPhoto(url);
        new DatabaseAdapter(getApplicationContext()).updateUser(userFromReg);
        new FirebaseDatabaseDAO().addUserToFirebase(userFromReg);
        User.getUser().setUserId(userFromReg.getUserId());
        User.getUser().setUserName(userFromReg.getUserName());
        User.getUser().setEmail(userFromReg.getEmail());
        User.getUser().setPassword(userFromReg.getPassword());
        User.getUser().setPhoto(userFromReg.getPhoto());

        Intent navigationDrawerIntent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        progressDialog.dismiss();
        startActivity(navigationDrawerIntent);
        finish();
    }
}
