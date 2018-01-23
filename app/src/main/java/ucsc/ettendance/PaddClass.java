package ucsc.ettendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PaddClass extends AppCompatActivity {
    private EditText mClassNameView;
    private EditText mClassCodeView;
    private EditText mClassPINView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_padd_class);

        mClassNameView = (EditText) findViewById(R.id.className);
        mClassCodeView = (EditText) findViewById(R.id.classCode);
        mClassPINView = (EditText) findViewById(R.id.pin);
        //ADD CLASS BUTTON
        Button addClass = (Button) findViewById(R.id.addClassButton);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValid();

            }
        });
    }

    private void checkValid() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        mClassNameView.setError(null);
        mClassCodeView.setError(null);
        mClassPINView.setError(null);


        // Store values at the time of the login attempt.
        String name = mClassNameView.getText().toString();
        String code = mClassCodeView.getText().toString();
        String pin = mClassCodeView.getText().toString();


        boolean cancel = false;
        View focusView = null;




        if (TextUtils.isEmpty(pin)) {
            mClassPINView.setError(getString(R.string.error_field_required));
            focusView = mClassPINView;
            cancel = true;
        }
        // Check for a valid class code, if the user entered one.
        if (TextUtils.isEmpty(code) ) {
            mClassCodeView.setError(getString(R.string.error_field_required));
            focusView = mClassCodeView;
            cancel = true;
        }
        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            mClassNameView.setError(getString(R.string.error_field_required));
            focusView = mClassNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            finish();
        }
//        else {
//            // Show a progress spinner, and kick off a background task to
//            // perform the user login attempt.
//            //showProgress(true);
//            mAuthTask = new LoginActivity.UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
//    }

    }
}

