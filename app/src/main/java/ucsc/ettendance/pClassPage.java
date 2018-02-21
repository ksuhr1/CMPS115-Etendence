package ucsc.ettendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class pClassPage extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private int day;
    private int month;
    private int year;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private DatabaseReference dayRef;

    private static final String TAG = "pClassPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_class_page);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        final String className = getIntent().getExtras().getString("className");
        dayRef = mDatabase.child("classes").child(className);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(className);



        Button dayPage = (Button) findViewById(R.id.dayButton);
        dayPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG,"The date selected is: "+getSelectedDate());

                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG,"The date selected is: "+getSelectedDate());
                //TODO create logic to create day in database if it does not exist
                mDatabase.child("classes").child(className).child("Days of Attendance").child(getSelectedDate()).setValue("NULL");

                Toast.makeText(getApplicationContext(), "Created attendance day for "+getSelectedDate(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

                Intent intent = new Intent(pClassPage.this, DayView.class);
                String day = getSelectedDate();
                intent.putExtra("day", day);
                intent.putExtra("classCode", className);
                startActivity(intent);
            }
        });
    }

    public String getSelectedDate()
    {
        final DatePicker picker = (DatePicker) findViewById(R.id.datePicker);
        day = picker.getDayOfMonth();
        month = picker.getMonth() +1 ;
        year = picker.getYear();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(month + "-");
        stringBuilder.append(day + "-");
        stringBuilder.append(year);

        return stringBuilder.toString();
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
        if (id == R.id.action_logout)
        {
            mFirebaseAuth.signOut();
            loadLogInView();
        }

        return super.onOptionsItemSelected(item);
    }
    //takes user to log in screen if logout button is pressed
    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
