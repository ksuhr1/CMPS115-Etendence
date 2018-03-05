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

public class classPage extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference announceRef;
    private ListView list;
    private ArrayAdapter<String> aa;
    private ArrayList<String> announceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_page);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();

        final String className = getIntent().getExtras().getString("className");

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(className);

        list = (ListView) findViewById(R.id.listview);
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
                    String announceDate = ds.getKey();
                    String announcement = ds.getValue().toString();
                    announceArray.add(announceDate + "\n" + announcement);
                    //announceDate + ": " +
                    Log.d("classPage", announceDate + ": " + announcement);
                }
                aa = new ArrayAdapter<String>(classPage.this, R.layout.classlist, announceArray);
                list.setAdapter(aa);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }

        };
        announceRef.addListenerForSingleValueEvent(eventListener);

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
