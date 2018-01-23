package ucsc.ettendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

public class Pmain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmain);
        //ADD CLASS BUTTON
        Button addClass = (Button) findViewById(R.id.addClassButton);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pmain.this, PaddClass.class);
                startActivity(intent);
            }
        });

        ArrayList<String> classArray = new ArrayList<String>();
        classArray.add("gorp1");
        classArray.add("gorp2");
        classArray.add("gorp3");
        classArray.add("gorp4");
        ListView list = (ListView) findViewById(R.id.listview);
        // Create the adapter to convert the array to views
        final ArrayAdapter aa = new ArrayAdapter<String>(this, R.layout.classlist, classArray);
        //final ArrayAdapter aa = new ArrayAdapter<String>(getApplicationContext(),R.layout.whitetext,classList);
        // Attach the adapter to a ListView

        list.setAdapter(aa);
    }
}
