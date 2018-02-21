package ucsc.ettendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DayView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);

        final String day = getIntent().getExtras().getString("day");

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(day);
    }
}