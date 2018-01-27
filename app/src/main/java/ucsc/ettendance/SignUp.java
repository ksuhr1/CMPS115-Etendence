package ucsc.ettendance;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
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
    private FirebaseAuth auth;
    private EditText loginInputEmail, loginInputPassword, loginInputName, loginInputID, loginInputConfirm;
    private TextInputLayout loginInputLayoutEmail, loginInputLayoutPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();

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
        String studentId = loginInputID.getText().toString().trim();
        String confirm = loginInputConfirm.getText().toString().trim();
        String name = loginInputName.getText().toString().trim();


        if(!checkEmail()) {
            return;
        }
        if(!checkPassword()) {
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
                            Toast.makeText(getApplicationContext(), "This email is already registered!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "You are successfully Registered !!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this, MyClasses.class));
                            finish();
                        }
                    }
                });

    }

    private boolean checkEmail() {
        String email = loginInputEmail.getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {

//            loginInputLayoutEmail.setErrorEnabled(true);
//            loginInputLayoutEmail.setError(getString(R.string.err_msg_email));
//            loginInputEmail.setError(getString(R.string.err_msg_required));
//            requestFocus(loginInputEmail);
             return false;
        }
       // loginInputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {

        String password = loginInputPassword.getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {

//            loginInputLayoutPassword.setError(getString(R.string.err_msg_password));
//            loginInputPassword.setError(getString(R.string.err_msg_required));
//            requestFocus(loginInputPassword);
            return false;
        }
        //loginInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private boolean checkName() {

        String name = loginInputName.getText().toString().trim();
        if (name.isEmpty()) {

//            loginInputLayoutPassword.setError(getString(R.string.err_msg_password));
//            loginInputPassword.setError(getString(R.string.err_msg_required));
//            requestFocus(loginInputPassword);
            return false;
        }

        //loginInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private static boolean isEmailValid(String email) {
        System.out.println("GAPRRRRRR" +android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        return (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
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