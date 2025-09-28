package com.backdoorz.phishshieldai;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        TextView titleText = findViewById(R.id.titleText);
        MaterialButton signupButton = findViewById(R.id.signupButton);
        TextInputLayout nameLayout = findViewById(R.id.nameLayout);
        TextInputLayout emailLayout = findViewById(R.id.emailLayout);
        TextInputLayout phoneLayout = findViewById(R.id.phoneLayout);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
        TextInputEditText nameInput = findViewById(R.id.nameInput);
        TextInputEditText emailInput = findViewById(R.id.emailInput);
        TextInputEditText phoneInput = findViewById(R.id.phoneInput);
        TextInputEditText passwordInput = findViewById(R.id.passwordInput);

        // Apply gradient to title text
        titleText.post(() -> {
            float width = titleText.getPaint().measureText(titleText.getText().toString());
            LinearGradient textShader = new LinearGradient(0, 0, width, 0,
                    new int[]{
                            ContextCompat.getColor(this, R.color.primary),
                            ContextCompat.getColor(this, R.color.secondary)
                    },
                    null, Shader.TileMode.CLAMP);
            titleText.getPaint().setShader(textShader);
            titleText.invalidate();
        });

        // Apply gradient to signup button
        signupButton.setBackgroundTintList(null);
        signupButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_gradient));

        // Setup click listener
        signupButton.setOnClickListener(v -> attemptSignup(
            nameLayout, emailLayout, phoneLayout, passwordLayout,
            nameInput, emailInput, phoneInput, passwordInput
        ));
    }

    private void attemptSignup(
        TextInputLayout nameLayout, TextInputLayout emailLayout,
        TextInputLayout phoneLayout, TextInputLayout passwordLayout,
        TextInputEditText nameInput, TextInputEditText emailInput,
        TextInputEditText phoneInput, TextInputEditText passwordInput
    ) {
        // Reset errors
        nameLayout.setError(null);
        emailLayout.setError(null);
        phoneLayout.setError(null);
        passwordLayout.setError(null);

        String name = nameInput != null ? nameInput.getText().toString().trim() : "";
        String email = emailInput != null ? emailInput.getText().toString().trim() : "";
        String phone = phoneInput != null ? phoneInput.getText().toString().trim() : "";
        String password = passwordInput != null ? passwordInput.getText().toString() : "";

        boolean cancel = false;
        View focusView = null;

        // Check name
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError(getString(R.string.invalid_name));
            focusView = nameInput;
            cancel = true;
        }

        // Check email
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError(getString(R.string.invalid_email));
            focusView = emailInput;
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(getString(R.string.invalid_email));
            focusView = emailInput;
            cancel = true;
        }

        // Check phone
        if (TextUtils.isEmpty(phone) || !Patterns.PHONE.matcher(phone).matches()) {
            phoneLayout.setError(getString(R.string.invalid_phone));
            focusView = phoneInput;
            cancel = true;
        }

        // Check password
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordLayout.setError(getString(R.string.invalid_password));
            focusView = passwordInput;
            cancel = true;
        }

        if (cancel && focusView != null) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            focusView.startAnimation(shake);
            focusView.requestFocus();
        } else {
            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
        }
    }
}
