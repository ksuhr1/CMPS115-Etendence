package ucsc.ettendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class studentHelp extends AppCompatActivity
{
    private ArrayAdapter<String> aa;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_help);

        ListView help = (ListView) findViewById(R.id.listview);
        final ArrayList<String> steps = new ArrayList<>();

        steps.add("STEP 1: \nAdd any class for the quarter by pressing the “Add Class” button.");
        steps.add("STEP 2: \nEnter the class code and class pin given by the professor.");
        steps.add("STEP 3: \nClick on a class to navigate to the class page.");
        steps.add("STEP 4: \nTo check in for the day, click the “Check In” button.");
        steps.add("STEP 5: \nMake sure your location services are enabled in order to properly check in.");
        steps.add("STEP 6: \nInput the daily attendance code provided by the professor and click “Check In”.");

        aa = new ArrayAdapter<String>(studentHelp.this, R.layout.helplist, steps);
        help.setAdapter(aa);
    }
}
