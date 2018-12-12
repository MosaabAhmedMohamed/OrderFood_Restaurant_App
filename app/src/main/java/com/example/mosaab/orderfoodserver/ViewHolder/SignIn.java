package com.example.mosaab.orderfoodserver.ViewHolder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;

public class SignIn extends AppCompatActivity {

    private EditText et_phone,et_password;
    private Button btnSignIN;

    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        InitUI();


        
        btnSignIN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext()))
                {
                    SignInUser(et_phone.getText().toString(), et_password.getText().toString());
                }
                else
                {
                    Toast.makeText(SignIn.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        

    }

    private void InitUI() {

        et_phone = findViewById(R.id.edtPhone);
        et_password =findViewById(R.id.edtPassword);
        btnSignIN =findViewById(R.id.btn_SingIn);

        db=FirebaseDatabase.getInstance();
        databaseReference= db.getReference("User");
    }

    private void SignInUser(String PhoneTxt, String PasswordTxt) {

        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("pleas Wait...");
        mDialog.show();

        final String localPhone =PhoneTxt;
        final String localPassword = PasswordTxt;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()) {
                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);


                     if (Boolean.parseBoolean(user.getIsStaff()))
                     {

                        if (user.getPassword().equals(localPassword)) {

                            Intent Hmoe_intent = new Intent(SignIn.this,Home.class);
                            Common.current_user=user;
                            startActivity(Hmoe_intent);
                            finish();

                        }
                        else {

                            Toast.makeText(SignIn.this, "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {

                        Toast.makeText(SignIn.this, "Please login with staff account !", Toast.LENGTH_SHORT).show();
                    }

                }else
                {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this, "user not exist", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
