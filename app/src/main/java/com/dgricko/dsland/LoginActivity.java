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
import android.widget.TextView;
import android.widget.Toast;

import com.dgricko.dsland.model.User;
import com.dgricko.dsland.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputPhone, inputPassword;
    private Button loginButton;
    private ProgressDialog loadingBar;
    private TextView adminLink, userLink;

    private String parentDbName = "Users";
    private CheckBox rememberMeChk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPhone = (EditText) findViewById(R.id.login_phone_number_input);
        inputPassword = (EditText) findViewById(R.id.login_password_input);
        adminLink = (TextView) findViewById(R.id.admin_panel_link);
        userLink = (TextView) findViewById(R.id.user_panel_link);
        loginButton = (Button) findViewById(R.id.login_btn);
        loadingBar = new ProgressDialog(this);


        rememberMeChk = (CheckBox) findViewById(R.id.remember_checkbox);
        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                userLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        userLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login User");
                adminLink.setVisibility(View.VISIBLE);
                userLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }

    private void loginUser() {

        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            inputPhone.setBackgroundResource(R.drawable.error_design);
        } else if (TextUtils.isEmpty(password)) {
            inputPassword.setBackgroundResource(R.drawable.error_design);
        } else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait ,while we are checking you data");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(phone, password);
        }
    }

    private void allowAccessToAccount(final String phone, final String password) {

        if (rememberMeChk.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()) {

                    User userData = snapshot.child(parentDbName).child(phone).getValue(User.class);

                    if (userData.getPhone().equals(phone)) {
                        if (userData.getPassword().equals(password)) {

                            if (parentDbName.equals("Admins")) {
                                Toast.makeText(LoginActivity.this, "Admin panel", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            } else if (parentDbName.equals("Users")) {

                                Toast.makeText(LoginActivity.this, "logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

                                Prevalent.currentOnlineUser = userData;
                                startActivity(intent);
                            }

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password in incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + "number don't exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}