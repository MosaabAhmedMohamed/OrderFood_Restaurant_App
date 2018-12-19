package com.example.mosaab.orderfoodserver.ViewHolder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.R;
import com.example.mosaab.orderfoodserver.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {

     private Button btn_SingIn;

     private TextView textDesc;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference table_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     InitUI();
     check_remember_me();
    }

    private void InitUI() {

        btn_SingIn=findViewById(R.id.btn_SingIn);
        textDesc=findViewById(R.id.txtSlogan);

        // Typeface typeface =Typeface.createFromAsset(getAssets(),"fonts/");
        // textDesc.setTypeface(typeface);

        Paper.init(this);

        firebaseDatabase =FirebaseDatabase.getInstance();
        table_user = firebaseDatabase.getReference("User");

        btn_SingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent SIgnIn_Intent =new Intent(MainActivity.this,SignIn.class);
                startActivity(SIgnIn_Intent);
                finish();
            }
        });
    }

    //Check Remember me if the user saved his sign in
    private void check_remember_me()
    {
        String user =Paper.book().read(Common.USER_KEY);
        String password =Paper.book().read(Common.PWD_KEY);

        if(user != null && password !=null)
        {
            if(!user.isEmpty()&&!password.isEmpty())
            {
                remember_me_login(user,password);
            }
        }
    }


    private void remember_me_login(final String phone, final String password) {

        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please Wait...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //check if user is exist in the Database
                    if (dataSnapshot.child(phone).exists()) {

                        mDialog.dismiss();

                        //Get user information
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);

                        if (user.getPassword().equals(password)) {


                            Intent Home_intent = new Intent(MainActivity.this, Home.class);
                            Common.current_user = user;
                            startActivity(Home_intent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Wrong password !", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exist", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }



    }

}
