package com.example.winnipeginfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText etEmailFg;
    private Button btnReset;
    private FirebaseAuth firebaseAuth;
    private String email;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        //check network connection
        if (!isConnected()) {
            showDialog();
        }
        else {
            progressDialog = new ProgressDialog(ForgetPasswordActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Authenticating...");
        }

        etEmailFg = findViewById(R.id.etEmailFg);
        btnReset = findViewById(R.id.btnReset);

        firebaseAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    showDialog();
                } else {
                    validate();
                }
            }
        });
    }

    //check network connection
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    //show error connection dialog
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


    private void customToast(String content) {
        Snackbar.make(findViewById(R.id.myCoordinatorLayoutFg), content, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }

    //simple validation
    public boolean v() {
        boolean valid = true;

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailFg.setError("enter a valid email address");
            valid = false;
        } else {
            etEmailFg.setError(null);
        }
        return valid;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isConnected()) {
            showDialog();
        }
    }

    private void validate()
    {
        email = etEmailFg.getText().toString();
        if(v()) {
            etEmailFg.setError(null);
            progressDialog.show();
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        etEmailFg.setText("");
                        customToast("Successfully sent a reset email");
                    }
                    else {
                        progressDialog.dismiss();
                        customToast(task.getException().getMessage());
                    }
                }
            });
        }
    }
}
