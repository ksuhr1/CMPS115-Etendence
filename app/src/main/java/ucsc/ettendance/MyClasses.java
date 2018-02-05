package ucsc.ettendance;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class MyClasses extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    //private FirebaseDatabase mDatabase;
    private String mUserId;
    private DatabaseReference mDatabase;
    private static final String TAG = "My Classes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(mFirebaseUser == null)
        {
            //Not logged in, launch the Log in activity
            loadLogInView();
        }
        else
        {
            TextView welcome = (TextView) findViewById(R.id.title3);
            //Gets details of the logged in user
            mUserId = mFirebaseUser.getUid();
            welcome.setText("Welcome "+ mFirebaseUser.getDisplayName());
        }


        //ADD CLASS BUTTON
        Button addClass = (Button) findViewById(R.id.addClassButton);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyClasses.this, AddClass.class);
                startActivity(intent);

            }
        });

        // ARRAY LOGIC
        final ArrayList<String> classArray = new ArrayList<String>();
        classArray.add("gorp1");
        classArray.add("gorp2");
        classArray.add("gorp3");
        classArray.add("gorp4");
        ListView list = (ListView) findViewById(R.id.listview);
        // Create the adapter to convert the array to views
        final ArrayAdapter aa = new ArrayAdapter<String>(this, R.layout.classlist, classArray);
        //final ArrayAdapter aa = new ArrayAdapter<String>(getApplicationContext(),R.layout.whitetext,classList);
        // Attach the adapter to a ListView

        list.setAdapter(aa);

        /*IF ARRAY IS CLICKED*/
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent intent = new Intent(MyClasses.this, classPage.class);
                String className = classArray.get(position);
                intent.putExtra("className", className);
                startActivity(intent);
            }
        });

    }
/*
    // Retrieving data from database
    mDatabase.child(mUserId).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            UserInformation user = dataSnapshot.getValue(UserInformation.class);

            Log.d(TAG, "Name: " + user.getName() + ", ID: " + user.getId() + "isProfessor: " + user.isProf());
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    });
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
}
