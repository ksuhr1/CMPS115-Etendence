package ucsc.ettendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class pDayCode extends AppCompatActivity
{
    private EditText mAttendanceCodeView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference codeRef;
    private String day, classCode;
    private static final String TAG = "pDayCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_day_code);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        codeRef = mDatabase.child("classes");

        // Strings inherited from previous activities
        day = getIntent().getExtras().getString("day");
        classCode = getIntent().getExtras().getString("classCode");

        mAttendanceCodeView = (EditText) findViewById(R.id.classCode);

        //ADD CLASS BUTTON
        Button addClass = (Button) findViewById(R.id.setDayButton);
        addClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setAttendanceCode();
            }
        });
    }

    // sets the daily attendance code in the database
    private void setAttendanceCode()
    {
        mAttendanceCodeView.setError(null);
        final String dayCode = mAttendanceCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(dayCode))
        {
            mAttendanceCodeView.setError(getString(R.string.error_field_required));
            focusView = mAttendanceCodeView;
            cancel = true;
        }
        // checks if code is too short and throws error if it is
        if(isCodeTooShort(dayCode))
        {
            mAttendanceCodeView.setError(getString(R.string.error_field_required));
            focusView = mAttendanceCodeView;
            cancel = true;
        }
        //
        if(cancel)
        {
            focusView.requestFocus();
        }
        else
        {
            codeRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot data : dataSnapshot.getChildren())
                    {
                        if (data.child(classCode).child("Days of Attendances").child(day).exists())
                        {
                            codeRef.child(classCode).child("Days of Attendance").child(day).child("Attendance Code").setValue(dayCode);
                            Log.d(TAG, "Should've done something!");
                        }
                        else
                        {
                            Log.d(TAG, "Why wouldn't it exist?");
                            Log.d(TAG, data.child(classCode).child("Days of Attendances").child(day).toString());
                            codeRef.child(classCode).child("Days of Attendance").child(day).child("Attendance Code").setValue(dayCode);
//                            progressBar.setVisibility(View.GONE);
                            finish();
                            // result = 0;
                        }

                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private boolean isCodeTooShort(String dayCode)
    {
        return (dayCode.length() < 4);
    }
}
