package com.example.appvente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.appvente.Model.Users;
import com.example.appvente.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity

{
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;  // fait appele a la barre de chargement

    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe; // pour la case souvient toi de moi


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        LoginButton = (Button) findViewById(R.id.login_btn); // ce code correspond au boutton login
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        loadingBar = new ProgressDialog(this);

        // pour la case souvient toi de moi

        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginUser();

            }
        });
    }

    private void LoginUser()

            // pour faire apparaitre la bulle en cas de nom remplissge du champs
    {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone))

        {
            Toast.makeText(this, "please write your phone number...", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "please write your password...", Toast.LENGTH_SHORT).show();
        }

        else

        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("please wait, while we are checking credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(phone, password);

        }


    }

    private void AllowAccessToAccount(final String phone, final String password)

    {
// suite sur le boutton souvient toi de moi

        if (chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance(). getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)

            {
                if (dataSnapshot.child(parentDbName).child(phone).exists())

                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                            if (usersData.getPhone().equals(phone))
                            {
                                if (usersData.getPassword().equals(password))

                                // si le mot de passe est bon voici le message
                                {
                                    Toast.makeText(loginActivity.this,"logged in successfully...",Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                    // ceci lance la page home activity si tout est okay lance Home

                                    Intent intent = new Intent(loginActivity.this, HomeActivity.class);
                                    startActivity(intent);

                                }

                                else
                                    // sort la bulle du message d'erreur

                                {
                                    loadingBar.dismiss();
                                    Toast.makeText(loginActivity.this,"password is incorrect...",Toast.LENGTH_SHORT).show();
                                }

                            }


                }

                else

                {
                    Toast.makeText(loginActivity.this, "Account with this" + phone + "Number dont exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
