package ucsc.ettendance;

import android.content.Intent;
import android.provider.ContactsContract;
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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CheckInPage extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    //private DatabaseReference studentRef;
    private DatabaseReference directRef;
    private static final String TAG = "CheckInPage";
    private String classCode;
    private EditText dailyCodeView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_page);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        classCode = getIntent().getExtras().getString("className");

        //studentRef = mDatabase.child("students").child(mFirebaseUser.getUid());

        dailyCodeView = (EditText)findViewById(R.id.classCode);


        Button checkIn = (Button) findViewById(R.id.checkInButton);
        checkIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String attendanceCode = dailyCodeView.getText().toString();

                Log.d(TAG, "Current day is: " + getCurrentDay());
                directRef = mDatabase.child("classes").child(classCode).child("Days of Attendance");

                //PUT CHECK IN LOGIC HERE
                // Check in logic:
                // 1) Check if student is actually in enrolled classes, i.e look in Enrolled Classes child if the class exists
                //    Don't have to do this cause listview shouldn't show classes that aren't enrolled
                // 2) Check current day if Professor has made the certain day, then check the attendance code
                // 3) If successful, enroll into child called Present Students. Else, display error if the day hasn't been made, or invalid code

                dailyCodeView.setError(null);

                boolean cancel = false;
                View focusView = null;

                if(TextUtils.isEmpty(attendanceCode))
                {
                    dailyCodeView.setError(getString(R.string.error_field_required));
                    focusView = dailyCodeView;
                    cancel = true;
                }

                //Log.d(TAG,"attendancecode is: " + attendanceCode);
                // checks if code is too short and throws error if it is
                if(isCodeTooShort(attendanceCode))
                {
                    dailyCodeView.setError("Code must be at least 4 characters");
                    focusView = dailyCodeView;
                    cancel = true;
                }


                if(cancel)
                {
                    focusView.requestFocus();
                }
                else
                {
                    directRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Counter to iterate through all the childs in classes
                            int counter = 1;
                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                // gets all dates inside Days of Attendance child
                                String dateKeys = data.getKey();

                                if (dateKeys.equals(getCurrentDay()))
                                {

                                    DatabaseReference userKeyDatabase = directRef.child(dateKeys);

                                    ValueEventListener eventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.getKey().equals(getCurrentDay()))
                                            {
                                                // Attendance Code that user inputted compared with the Database entered Attendance Code
                                                Object attendanceObj = dataSnapshot.child("Attendance Code").getValue();
                                                //String attCode = dataSnapshot.child("Attendance Code").getValue().toString();
                                                String attCode = "";
                                                if(attendanceObj != null)
                                                {
                                                    attCode = attendanceObj.toString();

                                                }
                                                if(attCode == "")
                                                {
                                                    Log.d(TAG,"The day has been set, but it's currently null.");
                                                    dailyCodeView.setError("Your professor has not made today an attendance day yet.");
                                                    dailyCodeView.requestFocus();
                                                }
                                                else if(attCode.equals(attendanceCode))
                                                {
                                                    Log.d(TAG, "This date has already been made, so this student will join");
                                                    directRef.child(getCurrentDay()).child("Attendance List").child(mFirebaseUser.getDisplayName()).setValue("true");
                                                   // directRef.child(getCurrentDay()).child("Present Students").child(mFirebaseUser.getUid()).setValue(mFirebaseUser.getDisplayName());
                                                    Toast.makeText(getApplicationContext(), "Successfully checked in for " + getCurrentDay() + ".", Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                                else
                                                {
                                                    dailyCodeView.setError("Invalid Attendance Code");
                                                    dailyCodeView.requestFocus();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError)
                                        {

                                        }
                                    };
                                    userKeyDatabase.addListenerForSingleValueEvent(eventListener);
                                } else {

                                    if (counter >= dataSnapshot.getChildrenCount()) {
                                        Toast.makeText(getApplicationContext(), "Your professor has not made " + getCurrentDay() + " an attendance day.", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "Your professor has not made today an attendance day.");
                                    }
                                    //Log.d("counter", String.format("value = %d", counter));
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
        });
    }


// Returns string of current day
    public String getCurrentDay()
    {
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int day = localCalendar.get(Calendar.DATE);
        int month = localCalendar.get(Calendar.MONTH)+1;
        int year = localCalendar.get(Calendar.YEAR);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(month + "-");
        stringBuilder.append(day + "-");
        stringBuilder.append(year);

        return stringBuilder.toString();
    }

    //log out button logic
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //log out button logic
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
        return super.onOptionsItemSelected(item);
    }

    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isCodeTooShort(String dailyCode)
    {
        return (dailyCode.length() < 4);
    }

}



