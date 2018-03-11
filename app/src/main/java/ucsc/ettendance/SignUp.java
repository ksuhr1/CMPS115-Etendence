package ucsc.ettendance;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignUp extends AppCompatActivity
{
    private static final String TAG = "LoginActivity";
    private ProgressBar progressBar;
    private static int result = 0;
    private static FirebaseAuth auth;

    private EditText loginInputEmail, loginInputPassword, loginInputID, loginInputConfirm;
    private EditText loginInputFirstName, loginInputLastName;
    View focusView = null;
    boolean isProfessor = false;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initializing firebase authentication object
        auth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginInputEmail = (EditText) findViewById(R.id.email);
        loginInputPassword = (EditText) findViewById(R.id.password);
        loginInputID = (EditText) findViewById(R.id.studentid);
        loginInputFirstName = (EditText) findViewById(R.id.firstName);
        loginInputLastName = (EditText) findViewById(R.id.lastName);
        loginInputConfirm = (EditText) findViewById(R.id.confirm);

        //sign up button logic
        Button signUp = (Button) findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                submitForm();
            }
        });
    }

    //validating form
    private void submitForm()
    {
        String email = loginInputEmail.getText().toString().trim();
        String password = loginInputPassword.getText().toString().trim();
        final String studentId = loginInputID.getText().toString().trim();
        final String firstName = loginInputFirstName.getText().toString().trim();
        final String lastName = loginInputLastName.getText().toString().trim();
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox); // reference to checkbox value
        if(!checkFirstName())
            return;
        if(!checkLastName())
            return;
        if(!checkStudentId())
            return;
        if(!checkEmail())
            return;
        if(!checkPassword())
            return;
        if(!checkConfirm())
            return;

        // if professor checkbox is checked, user is a professor
        if (checkBox.isChecked())
            isProfessor = true;
        //Sets user info
        final UserInformation userInformation = new UserInformation(firstName,lastName,studentId, isProfessor, email);
        //displays progress bar
        progressBar.setVisibility(View.VISIBLE);
        //authenticate user
        auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                Log.d(TAG,"createUserWithEmail:onComplete:" + task.isSuccessful());
                progressBar.setVisibility(View.GONE);
                // If sign in fails, Log the message to the LogCat. If sign in succeeds
                // signed in user can be handled in the listener.

                if (!task.isSuccessful())
                {
                    Log.d(TAG,"Authentication failed." + task.getException());
                    loginInputEmail.setError(getString(R.string.error_invalid_email));
                    focusView = loginInputEmail;
                    focusView.requestFocus();
                }
                else {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String fullname = firstName + " " + lastName;
                        Log.d(TAG, "The fullname is " + fullname);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullname)
                                .build();
                        user.updateProfile(profileUpdates);
                    }
                    if (!isProfessor) {
                        sendEmailVerification();
                        databaseReference.child("students").child(user.getUid()).setValue(userInformation);
                        startActivity(new Intent(SignUp.this, LoginActivity.class));
                        finish();
                    } else {
                        sendEmailVerification();
                        databaseReference.child("teachers").child(user.getUid()).setValue(userInformation);
                        startActivity(new Intent(SignUp.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        });
    }

    //sends email verification
    private void sendEmailVerification() {
        final FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUp.this, "Email verification sent", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                    else{
                        Toast.makeText(SignUp.this, "Failed to send verification email", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private boolean checkEmail()
    {
        //Checks to see if the email is not entered and prints out message the email is require
        String email = loginInputEmail.getText().toString().trim();
        if (email.isEmpty()) {
            loginInputEmail.setError(getString(R.string.error_field_required));
            focusView = loginInputEmail;
            focusView.requestFocus();
            return false;
        }
        if (isEmailValid(email)) //check to see if the email is already used before when creating it and sends error messages
        {
            loginInputEmail.setError(getString(R.string.error_already_registered));
            focusView = loginInputEmail;
            focusView.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) //if incorrect email displays message that email is not valid
        {
            loginInputEmail.setError(getString(R.string.error_invalid_email));
            focusView = loginInputEmail;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkPassword()
    { //checks to make sure that the password section is not blank
        String password = loginInputPassword.getText().toString().trim();
        if (password.isEmpty())
        {
            loginInputPassword.setError(getString(R.string.error_field_required));
            focusView = loginInputPassword;
            focusView.requestFocus();
            return false;
        }
        else if(!isPasswordValid(password)) //Checks to makes sure that the password is a minimum length
        {
            loginInputPassword.setError(getString(R.string.error_invalid_password));
            focusView = loginInputPassword;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkFirstName()
    { //Make sure that field for the first name is not left blank
        String firstName = loginInputFirstName.getText().toString().trim();
        if (firstName.isEmpty())
        {
            loginInputFirstName.setError(getString(R.string.error_field_required));
            focusView = loginInputFirstName;
            focusView.requestFocus();
            return false;
        }
        return true;
    }
    private boolean checkLastName()
    { //Makes sure that the field for the last name is not left blank
        String lastName = loginInputLastName.getText().toString().trim();
        if (lastName.isEmpty()) {
            loginInputFirstName.setError(getString(R.string.error_field_required));
            focusView = loginInputLastName;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkStudentId()
    {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        String studentID = loginInputID.getText().toString().trim();
        if (studentID.isEmpty()&& !checkBox.isChecked())
        { //Make sure that the student id section is not left empty
            loginInputID.setError(getString(R.string.error_field_required));
            focusView = loginInputID;
            focusView.requestFocus();
            return false;
        }
        else if(!isIdValid(studentID)&& !checkBox.isChecked())
        { //Check to see that the student id is at least of length 7
            loginInputID.setError(getString(R.string.error_invalid_ID));
            focusView = loginInputID;
            focusView.requestFocus();
            return false;
        }
        return true;
    }
    private boolean checkConfirm()
    {
        Log.d(TAG,"IM IN CONFIRM.");
        String password = loginInputPassword.getText().toString();
        String confirm = loginInputConfirm.getText().toString();
        if (confirm.isEmpty())
        {
            loginInputConfirm.setError(getString(R.string.error_field_required));
            focusView = loginInputConfirm;
            focusView.requestFocus();
            return false;
        }
        if(!confirm.equals(password))
        { //check to see that the passowrds are matched for the account
            loginInputConfirm.setError(getString(R.string.error_invalid_confirm));
            focusView = loginInputConfirm;
            focusView.requestFocus();
            return false;
        }
        return true;
    }


    private static boolean isEmailValid(String email)
    {
        Log.d(TAG,"inside EMAIL VALID");
        auth.fetchProvidersForEmail("emailaddress@gmail.com").addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>()
        {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if(task.isSuccessful()){
                    // getProviders().size() will return size 1. if email ID is available.
                     result = task.getResult().getProviders().size();
                }
            }
        });
        if(result ==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //the id length should be atleast 7 characters
    private static boolean isIdValid(String id)
    {
        return (id.length() == 7);
    }

    // the password length must be atleast 6 characters
    private static boolean isPasswordValid(String password)
    {
        return (password.length() >= 6);
    }

    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //continue
    @Override
    protected void onResume()
    {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

}