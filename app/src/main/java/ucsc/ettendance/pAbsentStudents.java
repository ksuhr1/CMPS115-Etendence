package ucsc.ettendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by katelynsuhr on 3/4/18.
 */

public class pAbsentStudents extends AppCompatActivity {
    private static final String TAG = "pPresentStudents";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference classRef;
    private PclassInformation classInformation;
    private ListView list;
    private ArrayAdapter<String> aa;

    private String day, classCode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_absent_students);

        // Strings inherited from previous activities
        day = getIntent().getExtras().getString("day");
        classCode = getIntent().getExtras().getString("classCode");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        classRef = mDatabase.child("classes").child(classCode).child("Days of Attendance").child(day).child("Attendance List");

        list = (ListView) findViewById(R.id.listview);
        final ArrayList<String> studentArray = new ArrayList<>();


        TextView tv = (TextView)findViewById(R.id.title);
        tv.setText("Absent students for");
        TextView tv2 = (TextView)findViewById(R.id.title2);
        tv2.setText(day);

        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {

                    String className = ds.getValue(String.class);
                    String studentName = ds.getKey();
                    String status = ds.getValue().toString();
                    String isPresent = "false";
                    if(status.equals(isPresent)) {
                        studentArray.add("  "+studentName);
                    }

                }
                aa = new ArrayAdapter<String>(pAbsentStudents.this, R.layout.studentlistblue, studentArray);
                list.setAdapter(aa);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        };
        classRef.addListenerForSingleValueEvent(eventListener);
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
