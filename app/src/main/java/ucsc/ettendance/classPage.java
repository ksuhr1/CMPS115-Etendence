package ucsc.ettendance;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;

public class classPage extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference announceRef;
    private ListView list;
    private ArrayAdapter<String> aa;
    private ArrayList<String> announceArray;
    private DatabaseReference classRef;
    private String classTitle;
    List<String> items;
    String announce;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_page);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();

        final String className = getIntent().getExtras().getString("className");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        TextView titleBar = (TextView) findViewById(R.id.className);
        titleBar.setText(className);

        ArrayList<AnnouncementModel> arrayOfAnnouncements = new ArrayList<AnnouncementModel>();
        final AnnouncementAdapter adapter2 = new AnnouncementAdapter(this, arrayOfAnnouncements);
        final ListView listView2 = (ListView) findViewById(R.id.listview);

        final TextView classTextView = (TextView) findViewById(R.id.classtitle);

        announceArray = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        announceRef = mDatabase.child("classes").child(className).child("Announcements");

        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String announcement = ds.getValue().toString(); // actual announcement text
                    String lines[] = announcement.split("\n");
                    for(int i = 0; i <lines.length; i++)
                    {
                        int remainder = i%2;
                        if(remainder == 0)
                        {
                            date = lines[i];
                            Log.d("date", date);
                        }
                        if(remainder == 1)
                        {
                            announce = lines[i];
                            Log.d("announce",announce);
                        }
                    }
                    AnnouncementModel newUser = new AnnouncementModel(date,announce);
                    adapter2.add(newUser);
                    listView2.setAdapter(adapter2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        };

        announceRef.addListenerForSingleValueEvent(eventListener);

        //code to pull class info

        classRef = mDatabase.child("classes").child(className);

        ValueEventListener infoEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String key = ds.getKey();
                    if(key.equals("className"))
                    {
                        classTitle = (String) ds.getValue();
                        classTextView.setText(classTitle);
                        Log.d("classpage","TITLE"+classTitle);
                    }
                 }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        };
        classRef.addListenerForSingleValueEvent(infoEventListener);



        Button checkIn = (Button) findViewById(R.id.checkInButton);
        checkIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(classPage.this, CheckInPage.class);
                intent.putExtra("classCode", className);

                startActivity(intent);

            }
        });
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

        //once you logout it should be loaded back to the log in view
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

    private void loadHelpView()
    {
        //STUDENT HELP
        Intent intent = new Intent(this, studentHelp.class);
        startActivity(intent);
    }


    //Allows to load the log in view of the app
    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
