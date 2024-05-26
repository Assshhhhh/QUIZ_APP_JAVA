package com.example.quiz_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.quiz_app.R;

public class SplashScreen extends AppCompatActivity {

    TextView tv_splash,tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tv_splash = findViewById(R.id.tv_splash);
        tv_version = findViewById(R.id.tv_version);

        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(tv_splash);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(tv_version);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,FirstActivity.class);
                startActivity(intent);
                finish();
            }
        },5000);

    }
}