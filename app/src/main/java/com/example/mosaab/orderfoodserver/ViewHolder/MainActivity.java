package com.example.mosaab.orderfoodserver.ViewHolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mosaab.orderfoodserver.R;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

     private Button btn_SingIn;

     private TextView textDesc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_SingIn=findViewById(R.id.btn_SingIn);
        textDesc=findViewById(R.id.txtSlogan);

       // Typeface typeface =Typeface.createFromAsset(getAssets(),"fonts/");
       // textDesc.setTypeface(typeface);


        btn_SingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent SIgnIn_Intent =new Intent(MainActivity.this,SignIn.class);
                startActivity(SIgnIn_Intent);
                finish();
            }
        });
    }
}
