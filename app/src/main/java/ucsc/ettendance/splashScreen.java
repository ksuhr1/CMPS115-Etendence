package ucsc.ettendance;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class splashScreen extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar prog = (ProgressBar) findViewById(R.id.progressBar);
        prog.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
            }
        }, secondsDelayed * 2500);//2500);
    }
}
