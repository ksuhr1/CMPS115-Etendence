package ucsc.ettendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class DayView extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);

        mFirebaseAuth = FirebaseAuth.getInstance();


        final String day = getIntent().getExtras().getString("day");
        final String classCode = getIntent().getExtras().getString("classCode");

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(day);

        //SET CODE BUTTON
        Button addClass = findViewById(R.id.setCodeButon);
        addClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DayView.this, pDayCode.class);
                intent.putExtra("day", day);
                intent.putExtra("classCode", classCode);
                startActivity(intent);

            }
        });

        //PRESENT STUDENTS BUTTON
        Button presentButton = findViewById(R.id.presentButton);
        presentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DayView.this, pPresentStudents.class);
                intent.putExtra("day", day);
                intent.putExtra("classCode", classCode);
                startActivity(intent);

            }
        });

        //ABSENT STUDENTS BUTTON
        Button absentButton = findViewById(R.id.absentButton);
        absentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DayView.this, pAbsentStudents.class);
                intent.putExtra("day", day);
                intent.putExtra("classCode", classCode);
                startActivity(intent);

            }
        });


        //MAP BUTTON
        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DayView.this, pMap.class);
                intent.putExtra("day", day);
                intent.putExtra("classCode", classCode);
                startActivity(intent);

            }
        });

        //ANNOUCEMENT BUTTON
        Button announceButton = findViewById(R.id.announceButton);
        announceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DayView.this, pAnnouncementPage.class);
                intent.putExtra("day", day);
                intent.putExtra("classCode", classCode);
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
