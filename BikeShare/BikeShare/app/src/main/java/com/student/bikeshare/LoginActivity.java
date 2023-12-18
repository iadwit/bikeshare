package com.student.bikeshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText inputUsername, inputPassword;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Boolean flag_username = false;
    String email = "";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);


        findViewById(R.id.btn_login).setOnClickListener(v -> {
            if (inputUsername.getText().toString().matches(emailPattern) && inputUsername.getText().toString().length() > 0) {
                flag_username = true;
                if (!inputPassword.getText().toString().equals("")) {
                    email = inputUsername.getText().toString();
                    doLogin(email, inputPassword.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter password.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Invalid email address.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Username or password is invalid", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}