package eg.gov.iti.jets.loginactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import eg.gov.iti.jets.AlarmActivity.AlarmHelper;
import eg.gov.iti.jets.createtripactivity.CreateTripActivity;
import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.databasepkg.DatabaseAdapter;
import eg.gov.iti.jets.databasepkg.FirebaseDatabaseDAO;
import eg.gov.iti.jets.databasepkg.MyCallback;
import eg.gov.iti.jets.dtos.Note;
import eg.gov.iti.jets.dtos.Trip;
import eg.gov.iti.jets.dtos.User;
import eg.gov.iti.jets.sharedprefernces.SharedPreferencesHelperClass;
import eg.gov.iti.jets.signupactivity.SignupActivity;
import eg.gov.iti.jets.tripplanner.NavigationDrawerActivity;
import eg.gov.iti.jets.tripplanner.R;

public class LoginActivity extends AppCompatActivity implements Serializable {
    private transient LoginButton loginButton;
    private transient CallbackManager callbackManager;
    User gettenUser, fbUser, loggedUser;
    private transient TextView signUpButton, signInButton;
    private transient EditText signInEmail, signInPassword;
    private transient DBModel dbModel;
    private transient ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbModel = DBModel.getInstance(this);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.com_facebook_auth_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");

        User user = SharedPreferencesHelperClass.getUser(this);
        if (user.getEmail() != null) {
            Intent navigationDrawerIntent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
            startActivity(navigationDrawerIntent);
            finish();
        }
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)
                findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email,public_profile,user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()

        {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog.show();


                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                facebookLogin(object);

                            }
                        });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,birthday,name,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                progressDialog.dismiss();
            }

        });

        signInPassword = findViewById(R.id.password_input);

        signInEmail = findViewById(R.id.useremail_input);

        signInButton = findViewById(R.id.btn_login);
        signInButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                signInMethod();
            }
        });

        signUpButton = findViewById(R.id.btn_signup);
        signUpButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(signUpIntent);
            }
        });


        signInPassword.setTypeface(custom_font);
        signUpButton.setTypeface(custom_font);
        signInButton.setTypeface(custom_font);
        signInEmail.setTypeface(custom_font);


    }

    private void facebookLogin(JSONObject jsonObject) {
        try {
            fbUser = User.getUser();
            fbUser.setPhoto("https://graph.facebook.com/" + jsonObject.getString("id") + "/picture?width=250&height=250");
            fbUser.setUserName(jsonObject.getString("name"));
            fbUser.setEmail(jsonObject.getString("email"));
            fbUser.setPassword("fbpassword");
            new FirebaseDatabaseDAO().getMaxuserIDFromFirebase(new MyCallback() {
                @Override
                public void onMaxIdCallBack(int max) {
                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(getApplicationContext());
                    max = Math.max(databaseAdapter.getNewUserId(), max);
                    fbUser.setUserId(max + 1);
                    loggedUser = fbUser;
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

            gettenUser = new DatabaseAdapter(getApplicationContext()).retreiveUserByEmail(fbUser.getEmail());
            if (gettenUser == null) {
                new FirebaseDatabaseDAO().getUserFromFirebaseByEmail(fbUser.getEmail(), new MyCallback() {
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
                            new DatabaseAdapter(getApplicationContext()).addUser(fbUser);
                            new FirebaseDatabaseDAO().addUserToFirebase(fbUser);
                            SharedPreferencesHelperClass.manageUser(LoginActivity.this, fbUser
                                    , SharedPreferencesHelperClass.INSERT_UPDATE_USER, SharedPreferencesHelperClass.FACEBOOK_LOGIN);
                            loginSuccess();
                        } else {
                            //found user in firebase
                            fbUser = gettenUser;
                            new DatabaseAdapter(getApplicationContext()).addUser(fbUser);
                            progressDialog = new ProgressDialog(LoginActivity.this);
                            progressDialog.setMessage("Retreiving data");
                            progressDialog.show();

                            new FirebaseDatabaseDAO().retrieveUserTripsFromFirebase(fbUser, new MyCallback() {
                                @Override
                                public void onMaxIdCallBack(int max) {

                                }

                                @Override
                                public void onGetUserCallBack(User user) {

                                }

                                @Override
                                public void onGetUserByEmailCallBack(User user) {

                                }

                                @Override
                                public void onRetrieveUserTripsCallBack(ArrayList<Trip> trips) {

                                    for (int i = 0; i < trips.size(); ++i) {
                                        dbModel.addTrip(trips.get(i));
                                        retreiveUserTripNotes(trips.get(i));
                                        if (checkDate(trips.get(i).getStartDate(), trips.get(i).getStartTime()))
                                            AlarmHelper.setAlarm(LoginActivity.this, trips.get(i));
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onRetrieveUserNotesCallBack(ArrayList<Note> notes) {

                                }
                            });


                            SharedPreferencesHelperClass.manageUser(LoginActivity.this, fbUser
                                    , SharedPreferencesHelperClass.INSERT_UPDATE_USER, SharedPreferencesHelperClass.FACEBOOK_LOGIN);

                            loggedUser = gettenUser;


                            loginSuccess();
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
                SharedPreferencesHelperClass.manageUser(LoginActivity.this, gettenUser
                        , SharedPreferencesHelperClass.INSERT_UPDATE_USER, SharedPreferencesHelperClass.FACEBOOK_LOGIN);
                loggedUser = gettenUser;
                loginSuccess();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void signInMethod() {
        Boolean chkError = false;
        String email = signInEmail.getText().toString();
        String password = signInPassword.getText().toString();

        if (email.isEmpty()) {
            signInEmail.setError("please enter Your Email");
            chkError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signInEmail.setError("please enter a Valid Email");
            chkError = true;
        }

        if (password.isEmpty()) {
            signInPassword.setError("please enter your Password ");
            chkError = true;
        }

        if (chkError) {
            return;
        } else {
            gettenUser = new DatabaseAdapter(getApplicationContext()).retreiveUser(email, password);
            if (gettenUser == null) {
                new FirebaseDatabaseDAO().getUserFromFirebase(email, password, new MyCallback() {
                    @Override
                    public void onMaxIdCallBack(int max) {
                    }

                    @Override
                    public void onGetUserCallBack(User user) {
                        gettenUser = user;
                        if (gettenUser == null) {
                            //not finding user in both loacl storgae or firebase
                            signInEmail.setError("Please check your Email and password");
                            signInPassword.setError("Please check your Email and password");
                            progressDialog.dismiss();
                        } else {
                            //found user in firebase

                            SharedPreferencesHelperClass.manageUser(LoginActivity.this, gettenUser
                                    , SharedPreferencesHelperClass.INSERT_UPDATE_USER, SharedPreferencesHelperClass.APPLICATION_LOGIN);


                            progressDialog = new ProgressDialog(LoginActivity.this);
                            progressDialog.setMessage("Retreiving data");
                            progressDialog.show();

                            new FirebaseDatabaseDAO().retrieveUserTripsFromFirebase(gettenUser, new MyCallback() {
                                @Override
                                public void onMaxIdCallBack(int max) {

                                }

                                @Override
                                public void onGetUserCallBack(User user) {

                                }

                                @Override
                                public void onGetUserByEmailCallBack(User user) {

                                }

                                @Override
                                public void onRetrieveUserTripsCallBack(ArrayList<Trip> trips) {

                                    for (int i = 0; i < trips.size(); ++i) {
                                        dbModel.addTrip(trips.get(i));
                                        if (checkDate(trips.get(i).getStartDate(), trips.get(i).getStartTime()))
                                            AlarmHelper.setAlarm(LoginActivity.this, trips.get(i));
                                        retreiveUserTripNotes(trips.get(i));
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onRetrieveUserNotesCallBack(ArrayList<Note> notes) {

                                }
                            });


                            loggedUser = gettenUser;
                            loginSuccess();
                        }
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

            } else {
                //found user in local Strogae

                SharedPreferencesHelperClass.manageUser(LoginActivity.this, gettenUser
                        , SharedPreferencesHelperClass.INSERT_UPDATE_USER, SharedPreferencesHelperClass.APPLICATION_LOGIN);
                loggedUser = gettenUser;
                loginSuccess();
            }

        }
    }

    private void loginSuccess() {
        progressDialog.dismiss();
        User.getUser().setUserId(loggedUser.getUserId());
        User.getUser().setUserName(loggedUser.getUserName());
        User.getUser().setEmail(loggedUser.getEmail());
        User.getUser().setPassword(loggedUser.getPassword());
        User.getUser().setPhoto(loggedUser.getPhoto());
        Intent navigationDrawerIntent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        startActivity(navigationDrawerIntent);
        finish();
    }

    public void retreiveUserTripNotes(Trip trip) {
        new FirebaseDatabaseDAO().retrieveUserNotesFromFirebase(trip, new MyCallback() {
            @Override
            public void onMaxIdCallBack(int max) {

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
                DatabaseAdapter databaseAdapter = new DatabaseAdapter(getApplicationContext());
                for (int i = 0; i < notes.size(); ++i) {
                    databaseAdapter.addNote(notes.get(i));
                }
            }
        });
    }

    public boolean checkDate(String date, String time) {
        boolean res = false;
        SimpleDateFormat sdformat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        Date datee = null;
        try {
            datee = sdformat.parse(time + " " + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        if (datee.getTime() > c.getTimeInMillis())
            res = true;

        return res;
    }
}


