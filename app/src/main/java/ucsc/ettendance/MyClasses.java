package ucsc.ettendance;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class MyClasses extends AppCompatActivity
{

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    //private FirebaseDatabase mDatabase;
    private String mUserId;
    private DatabaseReference mDatabase;
    private DatabaseReference mStudentRef;
    private DatabaseReference mProfRef;
    private DatabaseReference directRef;
    private DatabaseReference mStudentID;
    private static final int LOCATION_REQUEST_CODE = 101;

    private static final String TAG = "My Classes";

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        TextView title = (TextView) findViewById(R.id.className);
        title.setText("My Classes");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStudentRef = mDatabase.child("students");
        mProfRef = mDatabase.child("teachers");
        mStudentID = mStudentRef.child(mFirebaseUser.getUid()); //gives specific UID for student logged in
        Log.d("mStudentID: ", mStudentID.toString());

        // Permissions check right when the app loads in
        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "Permission for location denied");
            makeRequest();
        }

        final ArrayList<String> list = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String userKey = ds.getKey();
                    //Looks at children userID and gets the keys
                    //such as Enrolled classes, email, firstName etc.
                    DatabaseReference userKeyDatabase = mStudentID.child(userKey);
                    ValueEventListener valueEventListener = new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                //Gets all classes in Enrolled Classes
                                String enrolledClasses = data.getKey();
                                list.add(enrolledClasses);
                                Log.d("data children: ", data.getKey());

                            }
                            final SwipeMenuListView listView2 = (SwipeMenuListView)findViewById(R.id.listView);
                            adapter = new ArrayAdapter<String>(MyClasses.this, R.layout.classlist, list);
                            listView2.setAdapter(adapter);
                            SwipeMenuCreator creator = new SwipeMenuCreator() {

                                @Override
                                public void create(SwipeMenu menu) {
                                    // create "open" item
                                    SwipeMenuItem openItem = new SwipeMenuItem(
                                            getApplicationContext());
                                    // set item background
                                    openItem.setBackground(new ColorDrawable(Color.rgb(66, 149,154)));
                                    // set item width
                                    openItem.setWidth(170);
                                    // set item title
                                    openItem.setTitle("View");
                                    // set item title fontsize
                                    openItem.setTitleSize(18);
                                    // set item title font color
                                    openItem.setTitleColor(Color.WHITE);
                                    // add to menu
                                    menu.addMenuItem(openItem);
                                    // create "delete" item
                                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                                            getApplicationContext());
                                    // set item background
                                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                                            0x3F, 0x25)));
                                    // set item width
                                    deleteItem.setWidth(170);
                                    // set a icon
                                    deleteItem.setIcon(R.drawable.delete);
//                                    deleteItem.setIcon(R.drawable.ic_trash);
                                    // add to menu
                                    menu.addMenuItem(deleteItem);
                                }
                            };
                            listView2.setMenuCreator(creator);
                            listView2.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                                    switch (index) {
                                        case 0:
                                            Log.d("view", String.valueOf(position));
                                            Intent intent = new Intent(MyClasses.this, classPage.class);
                                            String className = list.get(position);
                                            intent.putExtra("className", className);
                                            startActivity(intent);
                                            break;
                                        //When you click on trash can, deletes class from all
                                        //respectable positions
                                        case 1:
                                            className = list.get(position);
                                            directRef = mDatabase.child("classes");
                                            mStudentID.child("Enrolled Classes").child(className).removeValue();
                                            Log.d("className", className);
                                            directRef.child(className).child("Enrolled Students").child(mFirebaseUser.getUid()).removeValue();
                                            list.remove(className);
                                            adapter.notifyDataSetChanged();
                                            break;
                                    }
                                    return false;
                                }
                            });
                            /*IF ARRAY IS CLICKED*/
                            listView2.setOnItemClickListener(new AdapterView.OnItemClickListener()
                            {
                                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                                {
                                    Intent intent = new Intent(MyClasses.this, classPage.class);
                                    String className = list.get(position);
                                    intent.putExtra("className", className);
                                    startActivity(intent);
                                }
                            });

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
        mStudentID.addListenerForSingleValueEvent(eventListener);

        TextView welcome = findViewById(R.id.welcome);
        //Gets details of the logged in user
        welcome.setText("Welcome "+ mFirebaseUser.getDisplayName());

        //ADD CLASS BUTTON
        Button addClass = findViewById(R.id.addClassButton);
        addClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MyClasses.this, AddClass.class);
                startActivity(intent);

            }
        });
    }

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
        if (id == R.id.action_logout)
        {
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


    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Make request to user to ask for permissions
    protected void makeRequest()
    {
        Log.d(TAG, "permissions: makeRequest ran");
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_REQUEST_CODE);
    }
}
