package com.example.winnipeginfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.events.EventHandler;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmailLog, etPasswordLog;
    private TextView tvRegister, tvForget;
    private CheckBox chkRemember;
    private Button btnLogin;
    private String email,password;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private final int RESULT=0;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        toolbar=findViewById(R.id.toolbar);
        //get sharePreferences
        sharedPreferences = getSharedPreferences("general_prefs", MODE_PRIVATE);
        //get firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        //check whether connected/ if connected try login otherwise ask for checking internet
        if (!isConnected()) {
            showDialog();
        }
        else {
            //show progress
            progressDialog = new ProgressDialog(SignInActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Authenticating...");

            //check lasttime if login or not
            boolean signin=sharedPreferences.getBoolean("signin", false);
            if (signin==true) {
                progressDialog.show();
                //sigin witht current email
                firebaseAuth.signInWithEmailAndPassword(sharedPreferences.getString("email", ""), sharedPreferences.getString("password", "")).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), SuccessfulActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        }

        chkRemember = findViewById(R.id.chkRemember);
        etEmailLog = findViewById(R.id.etEmailLog);
        etPasswordLog = findViewById(R.id.etPasswordLog);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForget = findViewById(R.id.tvForget);

        EventHandler eventHandler = new EventHandler();
        btnLogin.setOnClickListener(eventHandler);
        tvRegister.setOnClickListener(eventHandler);
        tvForget.setOnClickListener(eventHandler);

        //check preference for setting up
        String email=sharedPreferences.getString("email", "");
        if (!email.equals("")) {
            etEmailLog.setText(email);
        }

        boolean checked=sharedPreferences.getBoolean("checked", false);
        chkRemember.setChecked(checked);

        String pass=sharedPreferences.getString("password", "");
        if (chkRemember.isChecked()) {
            etPasswordLog.setText(pass);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //check internet connection
        if (!isConnected()) {
            showDialog();
        }
    }

    //custom disply status
    private void customToast(String content) {
        Snackbar.make(findViewById(R.id.myCoordinatorLayout), content, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }

    //check current internet status
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    //internet error dialog
    private  void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("CONNECTION ERROR! Please check your internet conection and try again");
        alertDialogBuilder.setNeutralButton("REFRESH",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //get email and pass value
    private void getValues() {
        email = etEmailLog.getText().toString();
        password = etPasswordLog.getText().toString();
    }

    //simple validation
    public boolean v() {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailLog.setError("enter a valid email address");
            valid = false;
        } else {
            etEmailLog.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPasswordLog.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPasswordLog.setError(null);
        }

        return valid;
    }

    //validate the info and try to login
    private void validate()
    {
        if (v())
        {
            etPasswordLog.setError(null);
            etPasswordLog.setError(null);
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if(firebaseAuth.getCurrentUser().isEmailVerified())
                        {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email",email);
                            editor.putString("password",password);
                            editor.putBoolean("signin",true);
                            boolean test=editor.commit();
                            finish();
                            Intent intent = new Intent(getApplicationContext(),SuccessfulActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            progressDialog.dismiss();
                            customToast("Please verify your email to login.");
                        }
                    }
                    else
                    {
                        progressDialog.dismiss();
                        customToast("Login Failed , Please try again !");
                    }
                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        //this will recevice the data from the late activity
        super.onActivityResult(requestCode,resultCode,data);
        //if requestCode= == ??
        if (resultCode==RESULT_OK)
        {
            etEmailLog.setText(data.getStringExtra("email"));
            etPasswordLog.setText(data.getStringExtra("password"));
        }
        else
        {
            //nothing
        }
    }

    class EventHandler implements  View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.btnLogin:
                    if (!isConnected())
                    {
                        showDialog();
                    }else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (chkRemember.isChecked()) {
                            editor.putBoolean("checked", true);
                        } else {
                            editor.putBoolean("checked", false);
                        }
                        boolean test= editor.commit();
                        getValues();
                        validate();
                    }
                    break;
                case R.id.tvRegister:
                    Intent intent = new Intent(SignInActivity.this,RegisterActivity.class);
                    startActivityForResult(intent,RESULT);
                    break;
                case R.id.tvForget:
                    Intent intent2 = new Intent(SignInActivity.this,ForgetPasswordActivity.class);
                    startActivity(intent2);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }


}
