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

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_page);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();

        final String className = getIntent().getExtras().getString("className");
//        TextView title = (TextView) findViewById(R.id.title);
//        title.setText("Announcements");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        TextView titleBar = (TextView) findViewById(R.id.className);
        titleBar.setText(className);


        list = (ListView) findViewById(R.id.listview);

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
                    String announceDate = ds.getKey(); // key of announcement, should be a date
                    String announcement = ds.getValue().toString(); // actual announcement text
                    announceArray.add(announceDate + "\n" + announcement+"\n" ); // adds the announcement date and text to the list view
                  //  String[] items = announceArray.split(",");
//                    String temp = announceArray.toString();
//                    String choice = temp.substring(1, temp.length()-1);
//                    String[] arrayList = choice.split("/n");
//                    Log.d("choice",choice);
//                    Log.d("announceArray", temp);
//                    for(int i = 0; i < announceArray.size(); i++){
//                        int remainder = i% 4;
//
//
//                    }

                    //announceDate + ": " +
                    Log.d("classPage", announceDate + ": " + announcement);
                }
                aa = new ArrayAdapter<String>(classPage.this, R.layout.class_page_list, announceArray);
                list.setAdapter(aa);
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
        return super.onOptionsItemSelected(item);
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
