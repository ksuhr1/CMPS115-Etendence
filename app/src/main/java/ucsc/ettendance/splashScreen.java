package ucsc.ettendance;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class splashScreen extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    private DatabaseReference mDatabase;
    private DatabaseReference mStudentRef;
    private DatabaseReference mProfRef;

    private static final String TAG = "Splash Screen";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Authenticating Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStudentRef = mDatabase.child("students");
        mProfRef = mDatabase.child("teachers");


        if (mFirebaseUser == null) {
            //Not logged in, launch the Log in activity
            loadLogInView();
        }
        else
        {

            mUserId = mFirebaseUser.getUid();
            Log.d("mUserId", mUserId);

            //Checks on if the user is a professor and is trying to access the student view
            mStudentRef.child(mUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    UserInformation user = dataSnapshot.getValue(UserInformation.class);
                    if (user == null) {
                        Log.d(TAG, "This is a professor, trying to access the student side");
                        loadProfView();
                    } else {
                        Log.d("StudentRef", "First Name: " + user.getFirstName() + " Last Name: " + user.getLastName() + ", ID: " + user.getStudentId() + " isProfessor: " + user.isProfessor());
                        loadStudentView(); // load student page
                        Log.d("loadStudentView", user.getFirstName());
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            //Checks on if the user is a student is and is trying to access the professor view
            mProfRef.child(mUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    UserInformation user = dataSnapshot.getValue(UserInformation.class);
                    if (user == null) {
                        Log.d(TAG, "This is a student, trying to access the professor side");
                        loadStudentView();
                    } else {
                        Log.d("ProfRef", "First Name: " + user.getFirstName() + " Last Name: " + user.getLastName() + ", ID: " + user.getStudentId() + " isProfessor: " + user.isProfessor());
                        loadProfView(); // load professor page
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            ProgressBar prog = (ProgressBar) findViewById(R.id.progressBar);
            prog.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        }
    }


    //Allows for the loading of the log-in page
    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //Allows for loading the professor view of the app
    private void loadProfView()
    {
        Intent intent = new Intent(this, Pmain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //Allows for loading the student view of the app
    private void loadStudentView()
    {
        Intent intent = new Intent(this, MyClasses.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
