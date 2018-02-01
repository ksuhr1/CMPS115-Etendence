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
public class SignUp extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Button btnLogin;
    private ProgressBar progressBar;
    private static int result = 0;
    private static FirebaseAuth auth;

    private EditText loginInputEmail, loginInputPassword, loginInputName, loginInputID, loginInputConfirm;
    private TextInputLayout loginInputLayoutEmail, loginInputLayoutPassword;
    View focusView = null;

    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initializing firebase authentication object
        auth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

     //   loginInputLayoutEmail = (TextInputLayout) findViewById(R.id.login_input_layout_email);
       // loginInputLayoutPassword = (TextInputLayout) findViewById(R.id.login_input_layout_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        loginInputEmail = (EditText) findViewById(R.id.email);
        loginInputPassword = (EditText) findViewById(R.id.password);
        loginInputName = (EditText) findViewById(R.id.name);
        loginInputID = (EditText) findViewById(R.id.studentId);
        loginInputConfirm = (EditText) findViewById(R.id.confirm);

        btnLogin = (Button) findViewById(R.id.signUpButton);


        Button signUp = (Button) findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    /**
     * Validating form
     */
    private void submitForm() {
        String email = loginInputEmail.getText().toString().trim();
        String password = loginInputPassword.getText().toString().trim();
        final String studentId = loginInputID.getText().toString().trim();
        String confirm = loginInputConfirm.getText().toString().trim();
        final String name = loginInputName.getText().toString().trim();

        final UserInformation userInformation = new UserInformation(name, studentId, email);




        if(!checkName()) {
            return;
        }
        if(!checkStudentId()) {
            return;
        }
        if(!checkEmail()) {
            return;
        }
        if(!checkPassword()) {
            return;
        }

        if(!checkConfirm()) {
            return;
        }


//        loginInputLayoutEmail.setErrorEnabled(false);
  //      loginInputLayoutPassword.setErrorEnabled(false);

        progressBar.setVisibility(View.VISIBLE);
        //authenticate user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"createUserWithEmail:onComplete:" + task.isSuccessful());
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, Log the message to the LogCat. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d(TAG,"Authentication failed." + task.getException());
                            loginInputEmail.setError(getString(R.string.error_already_registered));
                            focusView = loginInputEmail;
                            focusView.requestFocus();
                            //Toast.makeText(getApplicationContext(), "This email is already registered!", Toast.LENGTH_SHORT).show();

                        } else {
                            FirebaseUser user = auth.getCurrentUser();
                            databaseReference.child("students").child(user.getUid()).setValue(userInformation);
                            //databaseReference.child(user.getUid()).setValue(userInformation);
                            Toast.makeText(getApplicationContext(), "Information Saved..", Toast.LENGTH_LONG).show();
                            if(user!=null)
                            {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                user.updateProfile(profileUpdates);
                            }
                           // Toast.makeText(getApplicationContext(), "You are successfully Registered !!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this, MyClasses.class));
                            finish();
                        }
                    }
                });

    }

    private boolean checkEmail() {
        String email = loginInputEmail.getText().toString().trim();
        if (email.isEmpty()) {
            loginInputEmail.setError(getString(R.string.error_field_required));
            focusView = loginInputEmail;
            focusView.requestFocus();
            return false;
        }
        if (isEmailValid(email))
        {
            loginInputEmail.setError(getString(R.string.error_already_registered));
            focusView = loginInputEmail;
            focusView.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            loginInputEmail.setError(getString(R.string.error_invalid_email));
            focusView = loginInputEmail;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkPassword() {

        String password = loginInputPassword.getText().toString().trim();
        if (password.isEmpty()) {
            loginInputPassword.setError(getString(R.string.error_field_required));
            focusView = loginInputPassword;
            focusView.requestFocus();
            return false;
        }
        else if(!isPasswordValid(password))
        {
            loginInputPassword.setError(getString(R.string.error_invalid_password));
            focusView = loginInputPassword;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkName() {

        String name = loginInputName.getText().toString().trim();
        if (name.isEmpty()) {
            loginInputName.setError(getString(R.string.error_field_required));
            focusView = loginInputName;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkStudentId() {

        String studentID = loginInputID.getText().toString().trim();
        if (studentID.isEmpty()) {
            loginInputID.setError(getString(R.string.error_field_required));
            focusView = loginInputID;
            focusView.requestFocus();
            return false;
        }
        else if(!isIdValid(studentID))
        {
            loginInputID.setError(getString(R.string.error_invalid_ID));
            focusView = loginInputID;
            focusView.requestFocus();
            return false;
        }
        return true;
    }
    private boolean checkConfirm() {
        Log.d(TAG,"IM IN CONFIRM.");
        String password = loginInputPassword.getText().toString();
        String confirm = loginInputConfirm.getText().toString();
        if (confirm.isEmpty()) {
            loginInputConfirm.setError(getString(R.string.error_field_required));
            focusView = loginInputConfirm;
            focusView.requestFocus();
            return false;
        }
        if(!confirm.equals(password))
        {

            loginInputConfirm.setError(getString(R.string.error_invalid_confirm));
            focusView = loginInputConfirm;
            focusView.requestFocus();
            return false;
        }

        return true;
    }


    private static boolean isEmailValid(String email) {

        Log.d(TAG,"inside EMAIL VALID");
        auth.fetchProvidersForEmail("emailaddress@gmail.com").addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if(task.isSuccessful()){
                    ///////// getProviders().size() will return size 1. if email ID is available.
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

    private static boolean isIdValid(String id) {

        return (id.length() == 7);
    }

    private static boolean isPasswordValid(String password){
        return (password.length() >= 6);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}