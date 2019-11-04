package com.example.appvente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.appvente.Model.Users;
import com.example.appvente.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;




    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = (Button) findViewById(R.id.main_join_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);  // affiche la barre de chargement



        Paper.init(this);

        // code pour appeler la page de connexion login

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

        // code pour appeler la page d'enregistrement
        joinNowButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }

        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != "")

        {

            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey, UserPasswordKey);

                loadingBar.setTitle("Already logged in ");
                loadingBar.setMessage("please wait.....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        }

    }

    private void AllowAccess(final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance(). getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)

            {
                if (dataSnapshot.child("Users").child(phone).exists())

                {
                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))

                        // si le mot de passe est bon voici le message
                        {
                            Toast.makeText(MainActivity.this,"Please wait, you are already logged in...",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            // ceci lance la page home activity

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);

                        }

                        else
                        // sort la bulle du message d'erreur mot de passe incorrect

                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,"password is incorrect...",Toast.LENGTH_SHORT).show();
                        }

                    }


                }

                else
                    // sort la bulle le numero de compte nexiste pas

                {
                    Toast.makeText(MainActivity.this, "Account with this" + phone + "Number dont exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}