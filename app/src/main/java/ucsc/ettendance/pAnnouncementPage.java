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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class pAnnouncementPage extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String day, classCode;
    private static final String TAG = "pAnnouncementPage";
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
        setContentView(R.layout.activity_p_announcement_page);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();
        updateListView();

        //CREATE ANNOUNCEMENT BUTTON
        Button create = (Button) findViewById(R.id.createAnnouncementButton);
        create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(pAnnouncementPage.this, pSetAnnouncement.class);
                intent.putExtra("day", day);
                intent.putExtra("classCode", classCode);
                startActivity(intent);


            }
        });
    }
    public void updateListView()
    {
        day = getIntent().getExtras().getString("day");
        classCode = getIntent().getExtras().getString("classCode");

        ArrayList<AnnouncementModel> arrayOfAnnouncements = new ArrayList<AnnouncementModel>();
        final pAnnouncementAdapter adapter2 = new pAnnouncementAdapter(this, arrayOfAnnouncements);
        final ListView listView2 = (ListView) findViewById(R.id.listview);

       // list = (ListView) findViewById(R.id.listview);
        announceArray = new ArrayList<>();
      //  Log.d("announceArray", announceArray)
        mDatabase = FirebaseDatabase.getInstance().getReference();
        announceRef = mDatabase.child("classes").child(classCode).child("Announcements");

        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                   // String announceDate = ds.getKey(); // key of announcement, should be a date
                  //  String announcement = ds.getValue().toString(); // actual announcement text
                    String announcement = ds.getValue().toString(); // actual announcement text
                    String lines[] = announcement.split("\n");
                    for(int i = 0; i <lines.length; i++){
                        int remainder = i%2;
                        if(remainder == 0){
                            date = lines[i];
                            Log.d("date", date);
                        }
                        if(remainder == 1){
                            announce = lines[i];
                            Log.d("announce",announce);
                        }
                    }
                }
                AnnouncementModel newUser = new AnnouncementModel(date,announce);
                adapter2.add(newUser);
                listView2.setAdapter(adapter2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        };

        announceRef.addListenerForSingleValueEvent(eventListener);
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
