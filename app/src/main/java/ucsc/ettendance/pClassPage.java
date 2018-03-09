package ucsc.ettendance;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class pClassPage extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private int day;
    private int month;
    private int year;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private DatabaseReference directRef;
    private DatabaseReference classRef;

    private static final String TAG = "pClassPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_class_page);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // className is the same as classCode here
        final String className = getIntent().getExtras().getString("className");

        classRef = mDatabase.child("classes").child(className);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        TextView title = (TextView) findViewById(R.id.className);
        title.setText(className);



        Button dayPage = (Button) findViewById(R.id.dayButton);
        dayPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                directRef = mDatabase.child("classes").child(className).child("Days of Attendance");

                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG,"The date selected is: "+getSelectedDate());

                // This ValueEventListener is specifically for making the initial days of attendance child or else the directRef one will not work.
                classRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // Counter to iterate through all the childs in classes
                        int counter = 1;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            // gets all child inside the className child
                            String classKeys = data.getKey();

                            if (classKeys.equals("Days of Attendance"))
                            {
                                DatabaseReference userKeyDatabase = directRef.child(classKeys);

                                ValueEventListener eventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.getKey().equals("Days of Attendance"))
                                        {
                                            Log.d(TAG,"Days of Attendance has already been made");
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
                                // if we checked every single child and Days of Attendance child does not exist
                                if (counter >= dataSnapshot.getChildrenCount())
                                {
                                    mDatabase.child("classes").child(className).child("Days of Attendance").child(getSelectedDate()).setValue("NULL");
                                    Toast.makeText(getApplicationContext(), "Created attendance day for "+getSelectedDate(), Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "First creation of the Days of Attendance child, date code doesn't exist");

                                }
                                counter++;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                directRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Counter to iterate through all the childs in classes
                        int counter = 1;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            // gets all dates inside Days of Attendance child
                            String dateKeys = data.getKey();

                            if (dateKeys.equals(getSelectedDate()))
                            {
                                DatabaseReference userKeyDatabase = directRef.child(dateKeys);

                                ValueEventListener eventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.getKey().equals(getSelectedDate()))
                                        {
                                            Log.d(TAG,"This date has already been made");
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

                                if (counter >= dataSnapshot.getChildrenCount())
                                {
                                    mDatabase.child("classes").child(className).child("Days of Attendance").child(getSelectedDate()).setValue("NULL");
                                    directRef.child("NULL").removeValue();
                                    Toast.makeText(getApplicationContext(), "Created attendance day for "+getSelectedDate(), Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "Date code doesn't exist so this works");

                                }
                                Log.d("counter", String.format("value = %d", counter));
                                counter++;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                progressBar.setVisibility(View.GONE);

                Intent intent = new Intent(pClassPage.this, DayView.class);
                String day = getSelectedDate();
                intent.putExtra("day", day);
                intent.putExtra("classCode", className);
                startActivity(intent);
            }
        });
    }

    //In order to get the selected date from the calendar
    public String getSelectedDate()
    {
        final DatePicker picker = (DatePicker) findViewById(R.id.datePicker);
        day = picker.getDayOfMonth();
        month = picker.getMonth() +1 ;
        year = picker.getYear();

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
    //more log out button logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout)
        {
            mFirebaseAuth.signOut();
            loadLogInView();
        }

        return super.onOptionsItemSelected(item);
    }
    //takes user to log in screen if logout button is pressed
    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
