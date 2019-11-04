package com.example.appvente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

// parametrage pour une connexion a la base de donne firebase

public class RegistrationActivity extends AppCompatActivity
{

    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        CreateAccountButton = (Button) findViewById (R.id.Registration_btn);
        InputName = (EditText) findViewById(R.id.Registration_username_input);
        InputPassword = (EditText) findViewById(R.id.Registration_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.Registration_phone_number_input);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreateAccount();
            }
        });

    }

    private void CreateAccount() {
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(name))

        {
            Toast.makeText(this, "please write your name...", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(phone))

        {
            Toast.makeText(this, "please write your phone number...", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password))

        {
            Toast.makeText(this, "please write your password...", Toast.LENGTH_SHORT).show();
        }

        else
            {

            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("please wait, while we are checking credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name, phone, password);

            }

    }

    private void ValidatePhoneNumber (String name, String phone, String password)

    {

      final DatabaseReference RootRef;
      RootRef = FirebaseDatabase.getInstance(). getReference();


      RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot)
          {
              if (!(dataSnapshot.child("Users").child(phone).exists()));

              {
                  HashMap <String, Object> userdataMap = new  HashMap <>();
                  userdataMap.put("phone", phone);
                  userdataMap.put("password", password);
                  userdataMap.put("name", name);
                  RootRef.child("users").child(phone).updateChildren(userdataMap)
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task)

                              {
                                  if (task.isSuccessful())

                                  {
                                      Toast.makeText(RegistrationActivity.this, "your account has been create", Toast.LENGTH_SHORT).show();
                                      loadingBar.dismiss();

                                      Intent intent = new Intent(RegistrationActivity.this, loginActivity.class);
                                      startActivity(intent);
                                  }

                                  else

                                  {
                                      loadingBar.dismiss();
                                      Toast.makeText(RegistrationActivity.this, "Network Error: please try again", Toast.LENGTH_SHORT).show();
                                  }

                              }
                          });



              }

              else
              {
                 Toast.makeText(RegistrationActivity.this, "This" + phone + "Already exists.", Toast.LENGTH_SHORT).show();
                 loadingBar.dismiss();
                 Toast.makeText(RegistrationActivity.this, "please try again using another phone number.",Toast.LENGTH_SHORT).show();

                  Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                  startActivity(intent);


              }

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError)
          {

          }
      });
    }

}

