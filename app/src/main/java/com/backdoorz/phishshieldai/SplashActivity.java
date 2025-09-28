package com.backdoorz.phishshieldai;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefsManager = new PrefsManager(this);

        // Initialize views
        ImageView splashLogo = findViewById(R.id.splashLogo);
        ImageView securityIcon = findViewById(R.id.securityIcon);
        TextView appName = findViewById(R.id.appName);
        TextView tagline = findViewById(R.id.tagline);

        // Apply gradient to text
        appName.post(() -> {
            float width = appName.getPaint().measureText(appName.getText().toString());
            LinearGradient textShader = new LinearGradient(0, 0, width, 0,
                    new int[]{
                            ContextCompat.getColor(this, R.color.primary),
                            ContextCompat.getColor(this, R.color.secondary)
                    },
                    null, Shader.TileMode.CLAMP);
            appName.getPaint().setShader(textShader);
            appName.invalidate(); // Refresh the text view
        });

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation fadeInDelayed = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        // Configure animation durations
        fadeIn.setDuration(1000);
        slideUp.setDuration(1000);
        fadeInDelayed.setDuration(800);
        fadeInDelayed.setStartOffset(500);

        // Start animations
        splashLogo.startAnimation(fadeIn);
        appName.startAnimation(slideUp);
        securityIcon.startAnimation(fadeInDelayed);
        tagline.startAnimation(fadeInDelayed);

        // Check login state and navigate accordingly after splash duration
        new Handler().postDelayed(() -> {
            Intent intent;
            if (prefsManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DURATION);
    }
}
