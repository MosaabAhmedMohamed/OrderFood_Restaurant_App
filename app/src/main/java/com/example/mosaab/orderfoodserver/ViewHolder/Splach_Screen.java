package com.example.mosaab.orderfoodserver.ViewHolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mosaab.orderfoodserver.R;

public class Splach_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach__screen);

        Thread Splash = new Thread(){
            @Override
            public void run(){

                try {
                    sleep(2000); // the time of holding the splash
                    Intent splash = new Intent(Splach_Screen.this,MainActivity.class);
                    startActivity(splash);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Splash.start();
    }
}
