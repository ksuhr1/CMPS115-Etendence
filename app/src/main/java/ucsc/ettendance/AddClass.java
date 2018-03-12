package ucsc.ettendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddClass extends AppCompatActivity
{
    private static final String TAG = "AddClass";
    private EditText mClassCodeView;
    private EditText mClassPINView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference codeRef;
    private DatabaseReference classRef;
    private DatabaseReference mStudentRef;
    private DatabaseReference mStudentID;
    private ProgressBar progressBar;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CODE = "code";
    public static final String SWITCH1 = "switch1";
    List<String> classes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //initializing firebase authentication object
        mDatabase = FirebaseDatabase.getInstance().getReference();
        codeRef = mDatabase.child("classes");
        mStudentRef = mDatabase.child("students");
        mStudentID = mStudentRef.child(mFirebaseUser.getUid());


        mClassCodeView = (EditText) findViewById(R.id.studentCode);
        mClassPINView = (EditText) findViewById(R.id.studentPassword);


        //ADD CLASS BUTTON
        Button addClass = (Button) findViewById(R.id.addClassButton);
        addClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                checkValid();

            }
        });
    }

    //checks if user entered valid information for class creation
    private void checkValid()
    {

        progressBar.setVisibility(View.VISIBLE);
        // Reset errors.
        mClassCodeView.setError(null);

        mClassPINView.setError(null);

        // Store values at the time of the class creation attempt.
        final String code = mClassCodeView.getText().toString();
        final String pin = mClassPINView.getText().toString();
        classRef = codeRef.child(code);
        Log.d("classRef", classRef.toString());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid class code, if the user entered one.
        if (TextUtils.isEmpty(code)) {
            mClassCodeView.setError(getString(R.string.error_field_required));
            focusView = mClassCodeView;
            cancel = true;
        }
        //checks for valid pin, if user enter one
        if (TextUtils.isEmpty(pin)) {
            mClassPINView.setError(getString(R.string.error_field_required));
            focusView = mClassPINView;
            cancel = true;
        }
        //checks for valid code length
        if (isCodeShort(code)) {
            mClassCodeView.setError("The code must be at least 4 characters");
            focusView = mClassCodeView;
            cancel = true;
        }
        //checks for valid pin length
        if (isPinShort(pin)) {
            mClassPINView.setError("The PIN must be at least 4 numbers");
            focusView = mClassPINView;
            cancel = true;
        }
        // There was an error; don't attempt login and focus the first
        // form field with an error.
        if (cancel) {
            focusView.requestFocus();
        }
        else // logic for adding a user to the class
        {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 1;
                    //ds is the class key and value including all its fields
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d("DataSnapshot", ds.toString());
                        //userKey are just the classNames
                        String userKey = ds.getKey(); //gets all of classCodes
                        Log.d("UserKey", userKey);
                        //if class code in database matches
                        // with the class code the user inputted
                        if(userKey.equals(code)){
                            Log.d("KeySuccess", userKey);
                            DatabaseReference userKeyDatabase = codeRef.child(userKey);
                            Log.d("userKeyDatabaseSuccess", userKeyDatabase.toString());
                            //Start eventListener on userKeyDatabase
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot == null)
                                    {
                                        Toast.makeText(getApplicationContext(), "There are no classes", Toast.LENGTH_LONG).show();
                                    }

                                    if(dataSnapshot.getKey().equals(code)){
                                        Log.d("Get Key", dataSnapshot.getKey().toString());
                                        String classPin = dataSnapshot.child("classPin").getValue().toString();

                                         if (classPin.equals(pin))
                                         {
                                             //See if student has already enrolled
                                            addStudentToClass(code);
                                            Intent intent = new Intent(AddClass.this,MyClasses.class );
                                            startActivity(intent);
                                        }
                                        else
                                         {
                                             Log.d("Invalid Pin",pin);
                                             mClassPINView.setError("Invalid Pin");
                                             mClassPINView.requestFocus();
                                             progressBar.setVisibility(View.GONE);

                                         }
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {

                                }
                            };
                            userKeyDatabase.addListenerForSingleValueEvent(eventListener);

                        }
                        else
                        {

                            //Check if you've parsed through all the children in classes
                            if(count >=  dataSnapshot.getChildrenCount())
                            {
                                mClassCodeView.setError("Invalid Class Code");
                                mClassCodeView.requestFocus();
                                progressBar.setVisibility(View.GONE);
                                Log.d("InvalidCode", code);
                                Log.d("count", String.format("value = %d",count));

                            }
                            Log.d("count", String.format("value = %d",count));
                            count++;

                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                }
            };
            codeRef.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    // Helper function to add courses to Firebase
    private void addStudentToClass(final String classCode)
    {
        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String userKey = ds.getKey();
                    //Looks at children userID and gets the keys
                    //such as Enrolled classes, email, firstName etc.
                    DatabaseReference userKeyDatabase = mStudentID.child(userKey);
                    ValueEventListener valueEventListener = new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            // Counter to iterate through all the childs in classes
                            int counter = 1;
                            for(DataSnapshot data: dataSnapshot.getChildren())
                            {
                                //Gets all classes in Enrolled Classes
                                String enrolledClasses = data.getKey();
                                Log.d("data children", data.getKey());

                                if(enrolledClasses.equals(classCode))
                                {
                                    Log.d(TAG,"enrolledClasses, already enrolled in: " + enrolledClasses);
                                    progressBar.setVisibility(View.GONE);
                                    //Toast.makeText(getApplicationContext(), "You're already enrolled in "+classCode ,
                                    //        Toast.LENGTH_LONG).show();
                                }
                                else
                                {

                                        mDatabase.child("classes").child(classCode).child("Enrolled Students").child(mFirebaseUser.getUid()).setValue(mFirebaseUser.getDisplayName());
                                        mStudentID.child("Enrolled Classes").child(classCode).setValue("");
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Course " +classCode+" has been added", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    };
                    userKeyDatabase.addListenerForSingleValueEvent(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        };
        mStudentID.addListenerForSingleValueEvent(eventListener);

        //THIS ADDS STUDENT IN CLASS
        // Looks in Enrolled Students child and adds the logged in student child along with the display name
        mDatabase.child("classes").child(classCode).child("Enrolled Students").child(mFirebaseUser.getUid()).setValue(mFirebaseUser.getDisplayName());
        mStudentID.child("Enrolled Classes").child(classCode).setValue("");
        progressBar.setVisibility(View.GONE);
       // Toast.makeText(getApplicationContext(), "Course " +classCode+" has been added", Toast.LENGTH_SHORT).show();

    }


    //log out button code
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //more log out button code
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout)
        {
            mFirebaseAuth.signOut();
            loadLogInView();
        }
        if(id == R.id.action_help)
        {
            loadHelpView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadHelpView()
    {
        //STUDENT HELP
        Intent intent = new Intent(this, studentHelp.class);
        startActivity(intent);
    }

    //code to transfer user to login screen when logged out
    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //checks that the id length should be atleast 4 characters
    private static boolean isPinShort(String pin)
    {
        return (pin.length() < 4);
    }


    // checks that the password should be atleast 4 characters
    private static boolean isCodeShort(String code)
    {
        return (code.length() < 4);
    }

}
