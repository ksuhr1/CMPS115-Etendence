package ucsc.ettendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class pSetAnnouncement extends AppCompatActivity {

    private EditText mAnnouncementTextView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase, codeRef, directRef;
    private String day, classCode;
    private static final String TAG = "pSetAnnouncement";
    private int aCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_set_announcement);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        codeRef = mDatabase.child("classes");


        day = getIntent().getExtras().getString("day");
        classCode = getIntent().getExtras().getString("classCode");

        directRef = codeRef.child(classCode);


        mAnnouncementTextView = (EditText) findViewById(R.id.announceText);

        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String key = ds.getKey();
                    if(key .equals("Announcements"))
                    {
                        aCount = (int) dataSnapshot.child(key).getChildrenCount();
                        Log.d(TAG,""+ aCount);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        };
        directRef.addListenerForSingleValueEvent(eventListener);

        //SEND ANNOUCEMENT BUTTON
        Button addClass = (Button) findViewById(R.id.setAnnouncementButton);
        addClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setAnnouncement();
            }
        });
    }

    public void setAnnouncement()
    {


        mAnnouncementTextView.setError(null);
        final String annoucementText = mAnnouncementTextView.getText().toString();

        // Create/traverse to Accouncements child, create or add to selected day child, and create the
        // announcement with the announcement text.
        Log.d(TAG,"UPDATED COUNT"+ aCount);

        String counter =  String.valueOf(aCount+1);
        codeRef.child(classCode).child("Announcements").child(counter).setValue(day +":\n" + annoucementText);
        Log.d(TAG,"Add announcement to Firebase");
        Toast.makeText(getApplicationContext(), "Created Annoucement for "+ day, Toast.LENGTH_LONG).show();
        finish();
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
