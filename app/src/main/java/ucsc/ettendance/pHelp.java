package ucsc.ettendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class pHelp extends AppCompatActivity {
    private ArrayAdapter<String> aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_help);

        ListView help = (ListView) findViewById(R.id.listview);
        final ArrayList<String> steps = new ArrayList<>();

        steps.add("STEP 1: \nCreate the class with the recommended fields.");
        steps.add("STEP 2: \nSelect the preferred class and navigate to the day to which you would like to take attendance.");
        steps.add("STEP 3: \nSet an attendance code for the students to input for that certain day.");
        steps.add("STEP 4: \nSet the location of the classroom on the map.");
        steps.add("STEP 5: (Optional) \nClick “Present Students” to view students who were present for the selected day.");
        steps.add("STEP 6: (Optional) \nClick “Absent Students” to view students who were absent for the selected day.");
        steps.add("STEP 7: (Optional) \nClick “Announcements” to view your announcements.");
        steps.add("STEP 8: (Optional) \nClick “Create Announcement” to add a new announcement.");



        aa = new ArrayAdapter<String>(pHelp.this, R.layout.studentlistblue, steps);
        help.setAdapter(aa);
    }
}
