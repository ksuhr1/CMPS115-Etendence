package ucsc.ettendance;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaddClass extends AppCompatActivity

{
    private static final String TAG = "PaddClass";

    private EditText mClassNameView;
    private EditText mQuarterTermView;
    private EditText mClassCodeView;
    private EditText mClassPINView;
    private ProgressBar progressBar;


    private static FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference databaseClasses;
    private DatabaseReference codeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_padd_class);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //initializing firebase authentication object
        databaseClasses = FirebaseDatabase.getInstance().getReference();
        codeRef = databaseClasses.child("classes");


        mClassNameView = (EditText) findViewById(R.id.className);
        mQuarterTermView = (EditText) findViewById(R.id.classQuarter);
        mClassCodeView = (EditText) findViewById(R.id.classCode);
        mClassPINView = (EditText) findViewById(R.id.pin);
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

    private void checkValid() {
        progressBar.setVisibility(View.VISIBLE);

        // Reset errors.
        mClassNameView.setError(null);
        mQuarterTermView.setError(null);
        mClassCodeView.setError(null);
        mClassPINView.setError(null);

        // Store values at the time of the login attempt.
        final String name = mClassNameView.getText().toString();
        final String quarter = mQuarterTermView.getText().toString().trim();
        final String code = mClassCodeView.getText().toString().trim();
        final String pin = mClassPINView.getText().toString().trim();

        //A What is boolean cancel
        boolean invalidField = false;
        View focusView = null;


        // Checks for a valid name
        if (TextUtils.isEmpty(name))
        {
            mClassNameView.setError(getString(R.string.error_field_required));
            focusView = mClassNameView;
            invalidField = true;
        }

        // Checks for a valid class code, if the user entered one.
        if (TextUtils.isEmpty(code))
        {
            mClassCodeView.setError(getString(R.string.error_field_required));
            focusView = mClassCodeView;
            invalidField = true;
        }

        //Checks to see if the code entered fits the length constraint
        if (isCodeShort(pin))
        {
            mClassCodeView.setError("The code must be at least 4 characters");
            focusView = mClassCodeView;
            invalidField = true;
        }

        //Checks if Quarter Term was entered
        if (TextUtils.isEmpty(quarter))
        {
            mQuarterTermView.setError(getString(R.string.error_field_required));
            focusView = mQuarterTermView;
            invalidField = true;
        }

        //Checks if a pin is entered, if not, sets an error msg
        if (TextUtils.isEmpty(pin))
        {
            mClassPINView.setError(getString(R.string.error_field_required));
            focusView = mClassPINView;
            invalidField = true;
        }

        //Checks to see if pin fits the correct length constraints
        if (isPinShort(pin))
        {
            mClassPINView.setError("The pin must be at least 4 numbers");
            focusView = mClassPINView;
            invalidField = true;
        }


        if (invalidField)
        {
            // There was an error; don't attempt login and focus the first form field with an error.

            focusView.requestFocus();
            progressBar.setVisibility(View.GONE);
        }
        else
        {

            codeRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    // Counter to iterate through all the childs in classes
                    int counter = 1;
                    for (DataSnapshot data : dataSnapshot.getChildren())
                    {

                        // gets all classcodes inside class child
                        String classKeys = data.getKey();

                        if (classKeys.equals(code))
                        {
                           DatabaseReference userKeyDatabase = codeRef.child(classKeys);

                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot.getKey().equals(code))
                                    {
                                        mClassCodeView.setError("This class code is already taken");
                                        mClassCodeView.requestFocus();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            userKeyDatabase.addListenerForSingleValueEvent(eventListener);


                        }
                        else
                        {

                            if(counter >= dataSnapshot.getChildrenCount())
                            {
                                addCourseToDataBase(name, quarter, code, pin, 0.0, 0.0);
                                Log.d(TAG, "Class code doesn't exist so this works");
                                progressBar.setVisibility(View.GONE);
                                finish();

                            }
                            counter++;
                        }

                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    //Helper function to add courses to Firebase
    private void addCourseToDataBase(String className, String classQuarter, String classCode, String classPin, double classLat, double classLong)
    {
        String fullname = mFirebaseUser.getDisplayName();
        PclassInformation classInformation = new PclassInformation(className,classQuarter,classCode, classPin, fullname, classLat, classLong);
        databaseClasses.child("classes").child(classCode).setValue(classInformation);
        databaseClasses.child("classes").child(classCode).child("Days of Attendance").child("NULL").setValue("NULL");
        databaseClasses.child("teachers").child(mFirebaseUser.getUid()).child("Created Classes").child(classCode).setValue(classCode);
    }

    //the pin length must be last least 4 characters
    private static boolean isPinShort(String pin)
    {
        boolean tooShort = true;
        if(pin.length() >= 4)
            tooShort = false;

        return tooShort;
    }


    // the code length must be at least 4 characters
    private static boolean isCodeShort(String code)
    {
        boolean tooShort = true;
        if(code.length() >= 4)
            tooShort = false;

        return tooShort;
    }


    //log out button logic
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //more log out button logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }
        if(id == R.id.action_help)
        {
            loadHelpView();
        }


        return super.onOptionsItemSelected(item);
    }

    //takes user to log in screen when log out button is pressed
    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loadHelpView()
    {
        //PROFESSOR HELP
        Intent intent = new Intent(this, pHelp.class);
        startActivity(intent);
    }
}