package ucsc.ettendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_announcement_page);
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

        list = (ListView) findViewById(R.id.listview);
        announceArray = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        announceRef = mDatabase.child("classes").child(classCode).child("Announcements");

        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String announceDate = ds.getKey(); // key of announcement, should be a date
                    String announcement = ds.getValue().toString(); // actual announcement text
                    announceArray.add(announcement); // adds the announcement date and text to the list view
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
                    Log.d("classPage", announcement);
                }
                aa = new ArrayAdapter<String>(pAnnouncementPage.this, R.layout.studentlistblue, announceArray);
                list.setAdapter(aa);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        };

        announceRef.addListenerForSingleValueEvent(eventListener);
    }

}
