package ucsc.ettendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DayView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);

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

        //SET CODE BUTTON
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

    }
}
