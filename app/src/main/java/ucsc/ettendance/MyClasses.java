package ucsc.ettendance;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MyClasses extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private static final String TAG = "My Classes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // if user is null launch login activity
                    startActivity(new Intent(MyClasses.this, LoginActivity.class));
                    finish();
                }else{
                    Log.d(TAG,"HELLO: "+user.getEmail());
                }
            }
        };


        //ADD CLASS BUTTON
        Button addClass = (Button) findViewById(R.id.addClassButton);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyClasses.this, AddClass.class);
                startActivity(intent);

            }
        });
    }
}
