package com.dgricko.dsland;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dgricko.dsland.prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEdit, userPhoneEdit, addressEdit;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextBtn;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageReferencePictureRef;
    private String checker="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageReferencePictureRef = FirebaseStorage.getInstance().getReference().child("Profile photos");

        profileImageView = (CircleImageView)findViewById(R.id.settings_profile_image);
        fullNameEdit = (EditText)findViewById(R.id.settings_full_name_edit);
        userPhoneEdit = (EditText)findViewById(R.id.settings_phone_number_edit);
        addressEdit = (EditText)findViewById(R.id.settings_address_edit);
        profileChangeTextBtn = (TextView)findViewById(R.id.profile_change_btn);
        closeTextBtn = (TextView)findViewById(R.id.close_settings_btn);
        saveTextBtn = (TextView) findViewById(R.id.update_settings_btn);

        userInfoDisplay(profileImageView,fullNameEdit,userPhoneEdit, addressEdit);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    userInfoSave();
                }else{
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name",fullNameEdit.getText().toString());
        userMap.put("address",addressEdit.getText().toString());
        userMap.put("phoneOrder",userPhoneEdit.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
        Toast.makeText(this, "Profile info update", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data!=null){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }else{
            Toast.makeText(this, "Error, try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSave() {
        if (TextUtils.isEmpty(fullNameEdit.getText().toString())) {
           fullNameEdit.setBackgroundResource(R.drawable.error_design);
        } else if (TextUtils.isEmpty(addressEdit.getText().toString())) {
            addressEdit.setBackgroundResource(R.drawable.error_design);
        } else if (TextUtils.isEmpty(userPhoneEdit.getText().toString())) {
            userPhoneEdit.setBackgroundResource(R.drawable.error_design);
        } else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Your profile information updating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageReferencePictureRef
                    .child(Prevalent.currentOnlineUser.getPhone()+".jpg");

            uploadTask = fileRef.putFile(imageUri);
             uploadTask.continueWithTask(new Continuation() {
                 @Override
                 public Object then(@NonNull Task task) throws Exception {
                     if (!task.isSuccessful()) {
                         throw task.getException();
                     }
                     return fileRef.getDownloadUrl();
                 }
             })
                     .addOnCompleteListener(new OnCompleteListener<Uri>() {
                         @Override
                         public void onComplete(@NonNull Task<Uri> task) {
                             if (task.isSuccessful()) {
                                 Uri downloadUrl = task.getResult();
                                 myUrl = downloadUrl.toString();

                                 DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                                 HashMap<String, Object> userMap = new HashMap<>();
                                 userMap.put("name",fullNameEdit.getText().toString());
                                 userMap.put("address",addressEdit.getText().toString());
                                 userMap.put("phoneOrder",userPhoneEdit.getText().toString());
                                 userMap.put("image",myUrl);
                                    ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
                                 progressDialog.dismiss();

                                 startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                                 Toast.makeText(SettingsActivity.this, "Profile info update", Toast.LENGTH_SHORT).show();
                                 finish();
                             }else{
                                 progressDialog.dismiss();
                                 Toast.makeText(SettingsActivity.this, "Error update info", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
        }else{
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, EditText fullNameEdit, EditText userPhoneEdit, EditText addressEdit) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String pass = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}