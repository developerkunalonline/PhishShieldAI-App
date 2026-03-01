package com.backdoorz.phishshieldai;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2500;
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

        // Apply vibrant gradient to text
        appName.post(() -> {
            float width = appName.getPaint().measureText(appName.getText().toString());
            LinearGradient textShader = new LinearGradient(0, 0, width, 0,
                    new int[] {
                            ContextCompat.getColor(this, R.color.gradient_start),
                            ContextCompat.getColor(this, R.color.gradient_mid),
                            ContextCompat.getColor(this, R.color.gradient_end)
                    },
                    null, Shader.TileMode.CLAMP);
            appName.getPaint().setShader(textShader);
            appName.invalidate();
        });

        // Load animations with smoother timing
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation fadeInDelayed = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        // Configure animation durations - smoother & more premium
        fadeIn.setDuration(1200);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        slideUp.setDuration(1000);
        slideUp.setInterpolator(new DecelerateInterpolator(1.5f));
        fadeInDelayed.setDuration(900);
        fadeInDelayed.setStartOffset(600);
        fadeInDelayed.setInterpolator(new DecelerateInterpolator());

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
