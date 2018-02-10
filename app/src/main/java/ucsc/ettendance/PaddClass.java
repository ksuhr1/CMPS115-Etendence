package ucsc.ettendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PaddClass extends AppCompatActivity
{
    private EditText mClassNameView;
    private EditText mClassCodeView;
    private EditText mClassPINView;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_padd_class);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mFirebaseAuth.getCurrentUser();

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

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(pin))
        {
            mClassPINView.setError(getString(R.string.error_field_required));
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

