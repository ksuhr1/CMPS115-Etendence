package ucsc.ettendance;

import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


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

    private static final String TAG = "My Classes";

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_classes);


       Intent intent = getIntent();
       if(intent.getExtras() == null){

       }
       else{
          Bundle extras = getIntent().getExtras();
          String code = extras.getString("classCode");
           ListView list = (ListView) findViewById(R.id.listview);
           final ArrayList<String> classArray = new ArrayList<>();
           classArray.add(code);
           adapter = new ArrayAdapter<>(this,R.layout.classlist, classArray);
           list.setAdapter(adapter);
           adapter.notifyDataSetChanged();
          // Toast.makeText(getApplicationContext(), "Course " +code +" has been added", Toast.LENGTH_SHORT).show();
       }
      //  Bundle extras = getIntent().getExtras();
      //  String classCode = extras.getString("classCode");
       // Toast.makeText(getApplicationContext(), "Course " +classCode+" has been added", Toast.LENGTH_SHORT).show();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStudentRef = mDatabase.child("students");
        mProfRef = mDatabase.child("teachers");




            TextView welcome = findViewById(R.id.welcome);
            //Gets details of the logged in user
//            mUserId = mFirebaseUser.getUid();
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


//



//        // ARRAY LOGIC
//        final ArrayList<String> classArray = new ArrayList<String>();
//        classArray.add("Class 1");
//        classArray.add("Class 2");
//        classArray.add("Class 3");
//        classArray.add("Class 4");



//        ListView list = (ListView) findViewById(R.id.listview);
//        // Create the adapter to convert the array to views
//        final ArrayAdapter aa = new ArrayAdapter<String>(this, R.layout.classlist, classArray);
//        final ArrayAdapter aa = new ArrayAdapter<String>(getApplicationContext(),R.layout.whitetext,classList);
//        // Attach the adapter to a ListView
//
//        list.setAdapter(aa);

//        /*IF ARRAY IS CLICKED*/
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
//            {
//                Intent intent = new Intent(MyClasses.this, classPage.class);
//                String className = classArray.get(position);
//                intent.putExtra("className", className);
//                startActivity(intent);
//            }
//        });

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
