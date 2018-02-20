package ucsc.ettendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddClass extends AppCompatActivity
{
    private static final String TAG = "AddClass";
    private EditText mClassCodeView;
    private EditText mClassPINView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference codeRef;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();

        //initializing firebase authentication object
        mDatabase = FirebaseDatabase.getInstance().getReference();
        codeRef = mDatabase.child("classes");

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
    private void checkValid() {
        // Reset errors.
        mClassCodeView.setError(null);

        mClassPINView.setError(null);

        // Store values at the time of the class creation attempt.
        final String code = mClassCodeView.getText().toString();
        final String pin = mClassPINView.getText().toString();

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
            mClassCodeView.setError("The pin must be at least 4 numbers");
            focusView = mClassCodeView;
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
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String userKey = ds.getKey(); //gets all of classCodes
                        DatabaseReference userKeyDatabase = codeRef.child(userKey);
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot == null) {
                                    Toast.makeText(getApplicationContext(), "There are no classes", Toast.LENGTH_LONG).show();
                                }
                                else if(dataSnapshot.child("classCode").getValue().equals(code))
                                {
                                    if (dataSnapshot.child("classPin").getValue().equals(pin))
                                    {
                                        addStudentToClass(code);
                                        Toast.makeText(getApplicationContext(), "You're enrolled in " + code, Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        mClassPINView.setError("Pin Number is incorrect");
                                        mClassPINView.requestFocus();
                                        //Toast.makeText(getApplicationContext(), "Pin Number is incorrect", Toast.LENGTH_LONG).show();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        };
                        userKeyDatabase.addListenerForSingleValueEvent(eventListener);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            codeRef.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    //Helper function to add courses to Firebase
    private void addStudentToClass(String classCode)
    {
        EnrolledStudents student = new EnrolledStudents(  );
       // mDatabase.child("classes").child(mFirebaseUser.getUid()).child(classCode).setValue(student);
        mDatabase.child("classes").child(classCode).child("Enrolled Students").setValue(mFirebaseUser.getUid());
        Toast.makeText(getApplicationContext(), "Course " +classCode+" has been added", Toast.LENGTH_SHORT).show();

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
        return super.onOptionsItemSelected(item);
    }

    //code to transfer user to login screen when logged out
    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //the id length should be  6 characters
    private static boolean isPinShort(String pin)
    {
        return (pin.length() < 4);
    }


    // the password length must be  5 characters
    private static boolean isCodeShort(String code)
    {
        return (code.length() < 4);
    }

}

