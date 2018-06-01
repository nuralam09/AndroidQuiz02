package com.dongsongrac.android_quiz_02;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dongsongrac.android_quiz_02.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    // for sign up
    //EditText edtNewUser, edtNewPassword, edtNewEmail;
    EditText edtUser, edtPassword;

    Button btnSignUp, btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        // activity main
        edtUser = (EditText)findViewById(R.id.edtUser);
        edtPassword = (EditText)findViewById(R.id.edtPassword);

        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);


        // set on click
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtUser.getText().toString(), edtPassword.getText().toString());
            }
        });
    }

    private void signIn(final String username, final String pwd) {

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(username).exists()){
                    if(!username.isEmpty()){
                        User login = dataSnapshot.child(username).getValue(User.class);
                        if(login.getPassword().equals(pwd))
                            Toast.makeText(MainActivity.this, "Login ok",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "Wrong password",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(MainActivity.this, "Please enter your name",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "User name is not exists",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showSignUpDialog() {
        LayoutInflater linf = LayoutInflater.from(this);
        final View inflator = linf.inflate(R.layout.sign_up_layout, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Sign Up");
        alert.setMessage("Please fill full information");
        alert.setIcon(R.drawable.ic_person_black_24dp);
        alert.setView(inflator);

        final EditText edtNewUser = (EditText) inflator.findViewById(R.id.edtNewUserName);
        final EditText edtNewPassword = (EditText) inflator.findViewById(R.id.edtNewPassword);
        final EditText edtNewEmail = (EditText) inflator.findViewById(R.id.edtNewEmail);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton)
            {
                if(!edtNewEmail.getText().toString().isEmpty()
                        && !edtNewPassword.getText().toString().isEmpty()
                        && !edtNewUser.getText().toString().isEmpty()) {

                    final User user = new User(edtNewEmail.getText().toString(),
                            edtNewPassword.getText().toString(),
                            edtNewUser.getText().toString());

                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(user.getUserName()).exists()) {
                                Toast.makeText(MainActivity.this, "User already exists !", Toast.LENGTH_SHORT).show();
                            } else {
                                users.child(user.getUserName()).setValue(user);
                                Toast.makeText(MainActivity.this, "User registration success !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this, "Please fill full information !", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
                dialog.dismiss();
            }

        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}
