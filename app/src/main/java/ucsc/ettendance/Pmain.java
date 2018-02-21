package ucsc.ettendance;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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

public class Pmain extends AppCompatActivity
{

    private static final String TAG = "Pmain";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference classRef;
    private PclassInformation classInformation;
    private ListView list;
    private ArrayAdapter<String> aa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmain);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //classRef = mDatabase.child("teachers").child(mFirebaseUser.getUid()).child("Created Classes");
        classRef = mDatabase.child("teachers").child(mFirebaseUser.getUid());

        list = (ListView) findViewById(R.id.listview);
        final ArrayList<String>classArray = new ArrayList<>();

        TextView welcome = (TextView) findViewById(R.id.welcome);

        welcome.setText("Welcome "+ mFirebaseUser.getDisplayName());


        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {

                    String userKey = ds.getKey();
                    DatabaseReference userKeyDatabase = classRef.child(userKey);
                    ValueEventListener valueEventListener = new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                //String className = data.getValue(String.class);
                                String className = data.getKey();
                                classArray.add(className);
                            }
                            aa = new ArrayAdapter<String>(Pmain.this, R.layout.classlistblue, classArray);
                            list.setAdapter(aa);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    };
                    userKeyDatabase.addListenerForSingleValueEvent(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        classRef.addListenerForSingleValueEvent(eventListener);

        // Attach the adapter to a ListView
        //list.setAdapter(aa);
        //ADD CLASS BUTTON
        Button addClass = (Button) findViewById(R.id.addClassButton);
        addClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Pmain.this, PaddClass.class);
                startActivity(intent);
            }
        });


        //IF ARRAY IS CLICKED
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                    Intent intent = new Intent(Pmain.this, pClassPage.class);
                    String className = classArray.get(position);
                    intent.putExtra("className", className);
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
}
