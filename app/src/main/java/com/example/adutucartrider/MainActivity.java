package com.example.adutucartrider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;
    private TextView WelcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.lottieAnimationView = findViewById(R.id.animationView);
        this.WelcomeText = findViewById(R.id.welcome_text);

        this.lottieAnimationView.animate().translationX(1000).setDuration(1000).setStartDelay(4500);
        this.WelcomeText.animate().translationX(1000).setDuration(1000).setStartDelay(4500);

        Thread thread = new Thread(){

            public void run(){

                try {

                    Thread.sleep(5800);



                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(MainActivity.this,RiderLogin.class);
                    startActivity(intent);

                }
            }
        };
        thread.start();

    }
}