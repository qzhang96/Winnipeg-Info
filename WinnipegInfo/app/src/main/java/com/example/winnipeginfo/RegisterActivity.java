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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmailReg, etPasswordReg;
    private TextView tvLogin;
    private String email, password;
    private Button btnReg;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // check internet connection
        if (!isConnected()) {
            showDialog();
        }
        else {
            progressDialog = new ProgressDialog(RegisterActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Authenticating...");
        }


        sharedPreferences = getSharedPreferences("general_prefs", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();

        etEmailReg = findViewById(R.id.etEmailReg);
        tvLogin=findViewById(R.id.tvLogin);
        etPasswordReg = findViewById(R.id.etPasswordReg);
        btnReg = findViewById(R.id.btnReg);

        EventHandler eventHandler =new EventHandler();
        btnReg.setOnClickListener(eventHandler);
        tvLogin.setOnClickListener(eventHandler);

    }

    //check internet connection
    private boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    //show error internet dialog
    private  void showDialog()
    {
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

    //snack bar info
    private void customToast(String content) {
        Snackbar.make(findViewById(R.id.myCoordinatorLayoutReg), content, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }

    private void getValues()
    {
        email = etEmailReg.getText().toString().trim();
        password = etPasswordReg.getText().toString();
    }
    //simple vlidation
    public boolean v() {
        boolean valid = true;

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailReg.setError("enter a valid email address");
            valid = false;
        } else {
            etEmailReg.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPasswordReg.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPasswordReg.setError(null);
        }
        return valid;
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    //try register
    private void validate()
    {
        if(v())
        {
            etPasswordReg.setError(null);
            etPasswordReg.setError(null);
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful()){
                        //Sends an email to user for verification.
                        firebaseAuth.getCurrentUser().sendEmailVerification().
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //If successful , then sends email to the user.
                                        if (task.isSuccessful()) {
                                            //save reference for login page
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("email",email);
                                            editor.putString("password",password);
                                            editor.apply();
                                            progressDialog.dismiss();
                                            customToast("Registration Successful! , We have sent a verfication email. Please verify.");
                                        } else {
                                            progressDialog.dismiss();
                                            customToast(task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                    else
                    {
                        progressDialog.dismiss();
                        customToast("failed "+task.getException().getMessage());
                    }
                }
            });
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!isConnected()) {
            showDialog();
        }
    }

    class EventHandler implements  View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvLogin:
                    Intent intent =new Intent(RegisterActivity.this,SignInActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnReg:
                    getValues();
                    if (!isConnected()) {
                        showDialog();
                    }
                    else {
                        validate();
                    }
                    break;
            }
        }
    }

}
