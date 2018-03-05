package ucsc.ettendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class pSetAnnouncement extends AppCompatActivity {

    private EditText mAnnouncementTextView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase, codeRef;
    private String day, classCode;
    private static final String TAG = "pSetAnnouncement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_set_announcement);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        codeRef = mDatabase.child("classes");

        day = getIntent().getExtras().getString("day");
        classCode = getIntent().getExtras().getString("classCode");

        mAnnouncementTextView = (EditText) findViewById(R.id.announceText);

        //SEND ANNOUCEMENT BUTTON
        Button addClass = (Button) findViewById(R.id.setAnnouncementButton);
        addClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setAnnouncement();
            }
        });
    }

    public void setAnnouncement()
    {
        mAnnouncementTextView.setError(null);
        final String annoucementText = mAnnouncementTextView.getText().toString();

        // Create/traverse to Accouncements child, create or add to selected day child, and create the
        // announcement with the announcement text.
        codeRef.child(classCode).child("Announcements").child(day).setValue(annoucementText);
        Log.d(TAG,"Add announcement to Firebase");
        Toast.makeText(getApplicationContext(), "Created Annoucement for "+ day, Toast.LENGTH_LONG).show();
        finish();
    }
}
