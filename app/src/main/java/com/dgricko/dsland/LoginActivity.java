package com.dgricko.dsland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dgricko.dsland.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText inputPhone, inputPassword;
    private Button loginButton;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPhone = (EditText)findViewById(R.id.login_phone_number_input);
        inputPassword = (EditText)findViewById(R.id.login_password_input);
        loginButton = (Button)findViewById(R.id.login_btn);
        loadingBar = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {

        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)){
            inputPhone.setBackgroundResource(R.drawable.error_design);
        }else if (TextUtils.isEmpty(password)){
            inputPassword.setBackgroundResource(R.drawable.error_design);
        }else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait ,while we are checking you data");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(phone,password);
        }
    }

    private void allowAccessToAccount(final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()){

                    User userData = snapshot.child(parentDbName).child(phone).getValue(User.class);

                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)) {

                            Toast.makeText(LoginActivity.this, "logged in Successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password in incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Account with this "+phone+"number don't exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}