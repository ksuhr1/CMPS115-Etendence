package ucsc.ettendance;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaddClass extends AppCompatActivity

{
    //private static final String TAG = "ClassActivity";
    private static int result = 0;
    private EditText mClassNameView;
    private EditText mClassCodeView;
    private EditText mClassPINView;


    private static FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;



    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_padd_class);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();


        databaseReference = FirebaseDatabase.getInstance().getReference();

        mClassNameView = (EditText) findViewById(R.id.className);
        mClassCodeView = (EditText) findViewById(R.id.classCode);
        mClassPINView = (EditText) findViewById(R.id.pin);
        //ADD CLASS BUTTON
        Button addClass = (Button) findViewById(R.id.addClassButton);
        addClass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                checkValid();

            }
        });
    }

    private void checkValid()
    {


        // Reset errors.
        mClassNameView.setError(null);
        mClassCodeView.setError(null);
        mClassPINView.setError(null);

        // Store values at the time of the login attempt.
        String name = mClassNameView.getText().toString();
        String code = mClassCodeView.getText().toString().trim();
        String pin = mClassPINView.getText().toString().trim();

        //A What is boolean cancel
        boolean cancel = false;
        View focusView = null;


       //Checks if any of the fields are empty
        if (TextUtils.isEmpty(pin))
        {
            mClassPINView.setError(getString(R.string.error_field_required));
            focusView = mClassPINView;
            cancel = true;
        }

        //Checks to see if it fits the length constraint of the pin
        if(isPinShort(pin)){
            mClassPINView.setError("This PIN is too short");
            focusView = mClassPINView;
            cancel = true;
        }

        // Check for a valid class code, if the user entered one.
        if (TextUtils.isEmpty(code) )
        {
            mClassCodeView.setError(getString(R.string.error_field_required));
            focusView = mClassCodeView;
            cancel = true;
        }
        //Checks to see if the code is already taken
        if(isCodeValid(code)){
            mClassCodeView.setError("This code is already taken");
            focusView = mClassCodeView;
            cancel = true;

        }
        //Checks ot see if the code entered fits the length constraint
        if(isCodeShort(pin)){
            mClassCodeView.setError("This code is too short");
            focusView = mClassCodeView;
            cancel = true;
        }

        // Check for a valid name
        if (TextUtils.isEmpty(name))
        {
            mClassNameView.setError(getString(R.string.error_field_required));
            focusView = mClassNameView;
            cancel = true;
        }


        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            //TODO add logic to let professors add classes here
            //TODO check if class is in database
            finish();
        }
    }


    //Check if the code is already taken for the class need to look throught the database in order to do that
    //Look on stack overflow for the error
    //NEED TO FIX THIS
    private static boolean isCodeValid(String code) {
     //   Log.d(TAG,"inside CODE VALID");
       // mFirebaseAuth.fetchProvidersForCode("xxxxx").addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>()
        {
          //  @Override
     //       public void onComplete(@NonNull Task<ProviderQueryResult> task) {
       //         if(task.isSuccessful()){
         //           ///////// getProviders().size() will return size 1. if code is available.
           //         result = task.getResult().getProviders().size();
                }
           // }
       // });
        if(result ==1)
        {
            return true;
        }
        else
        {
            return false;
        }

    }


    //the id length should be  6 characters
    private static boolean isPinShort(String pin)
    {
        return (pin.length() == 6);
    }


    // the password length must be  5 characters
    private static boolean isCodeShort(String code)
    {
        return (code.length() == 5);
    }


    //log out button logic
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //log out button logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout)
        {
            mFirebaseAuth.signOut();
            loadLogInView();
        }
        return super.onOptionsItemSelected(item);
    }

    //transfers user to login page when logout button is clicked
    private void loadLogInView()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

