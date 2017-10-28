package cs2340.gatech.edu.brodents;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.concurrent.ExecutionException;

/**
 * Class controller to manage registration for Brodents.
 * @author Rikesh Subedi
 * @version 1.0
 */
public class ResetPasswordActivity extends AppCompatActivity {
    private PasswordResetTask mAuthTask;
    private EditText mUser;
    private EditText mPassword;
    private EditText mHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mUser = (EditText) findViewById(R.id.txtUsername);
        mPassword = (EditText) findViewById(R.id.txtPassword);
        mHome = (EditText) findViewById(R.id.txtHome);
        mAuthTask = null;

        Button mResetPassword = (Button) findViewById(R.id.btnSubmit);
        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptResetPassword();
            }
        });

        Button mCancel = (Button) findViewById(R.id.btnCancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginPage = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginPage);
            }
        });
    }


    /**
     * Attempts to register the account specified by the registration form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptResetPassword() {
        // Reset errors.
        mUser.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mUser.getText().toString();
        String password = mPassword.getText().toString();
        String home = mHome.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_too_short_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUser.setError(getString(R.string.error_field_required));
            focusView = mUser;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mUser.setError(getString(R.string.error_invalid_email));
            focusView = mUser;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // kick off a background task to
            // perform the user login attempt.
            mAuthTask = new PasswordResetTask(email, password, home);
            try {
                if (mAuthTask.execute((Void) null).get()) {
                    Intent loginPage = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginPage);
                } else {
                    mHome.setError("Invalid Home Address or Username!");
                }
            } catch (Exception e) {
                Log.e("Reset Password", e.getMessage(), e);
            }
        }
    }
    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    private class PasswordResetTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmailText;
        private final String mPasswordText;
        private final String mHomeText;


        PasswordResetTask(String email, String password, String home) {
            mEmailText = email;
            mPasswordText = password;
            mHomeText = home;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            RatAppModel.checkInitialization();
            RatAppModel model = RatAppModel.getInstance();

            int regCode = model.changePassword(mEmailText, mHomeText, mPasswordText);

            switch (regCode) {
                case 0:
                    return true;
                case 1:
                    return false;
                case 2:
                    // SQL error
                    return false;
                default:
                    return false;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
