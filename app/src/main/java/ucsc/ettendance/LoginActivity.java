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
import com.google.firebase.auth.ProviderQueryResult;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Button btnLogin, btnLinkToSignUp;
    private ProgressBar progressBar;
    private EditText loginInputEmail, loginInputPassword;
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static int result = 0;
    private static boolean emailInDatabase;
    private TextInputLayout loginInputLayoutEmail, loginInputLayoutPassword;
    View focusView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        //loginInputLayoutEmail = (TextInputLayout) findViewById(R.id.login_input_layout_email);
        //loginInputLayoutPassword = (TextInputLayout) findViewById(R.id.login_input_layout_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        loginInputEmail = (EditText) findViewById(R.id.email);
        loginInputPassword = (EditText) findViewById(R.id.password);

        btnLogin = (Button) findViewById(R.id.email_sign_in_button);
        btnLinkToSignUp = (Button) findViewById(R.id.email_sign_up_button);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        btnLinkToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Validating form
     */
    private void submitForm() {
        final String email = loginInputEmail.getText().toString().trim();
        String password = loginInputPassword.getText().toString().trim();

        if(!checkEmail()) {
            return;
        }
        if(!checkPassword()) {
            return;
        }
//        loginInputLayoutEmail.setErrorEnabled(false);
//        loginInputLayoutPassword.setErrorEnabled(false);

        progressBar.setVisibility(View.VISIBLE);
        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, Log a message to the LogCat. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {

                            // password incorrect message
                            //Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            loginInputPassword.setError(getString(R.string.error_incorrect_password));
                            focusView = loginInputPassword;
                            focusView.requestFocus();

                            //checks if email is in database
                            auth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if(task.isSuccessful()){
                                        ///////// getProviders().size() will return size 1. if email ID is available.
                                        result = task.getResult().getProviders().size();
                                        if(result != 1)
                                        {
                                            loginInputEmail.setError(getString(R.string.error_not_registered));
                                            focusView = loginInputEmail;
                                            focusView.requestFocus();
                                        }
                                    }
                                }
                            });

                        } else {
                            Intent intent = new Intent(LoginActivity.this, MyClasses.class);
                            startActivity(intent);
                            finish();
                            //TODO add logic to send professors to professor UI
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
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
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
//            loginInputLayoutPassword.setError(getString(R.string.err_msg_password));
//            loginInputPassword.setError(getString(R.string.err_msg_required));
//            requestFocus(loginInputPassword);
            return false;
        }
        //loginInputLayoutPassword.setErrorEnabled(false);
        return true;
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