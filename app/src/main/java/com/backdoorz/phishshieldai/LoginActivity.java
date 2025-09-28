package com.backdoorz.phishshieldai;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private PrefsManager prefsManager;
    private static final String LOGIN_URL = "https://phishshield-backend-15e6.onrender.com/users/login";
    private TextInputLayout emailLayout, passwordLayout;
    private MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefsManager = new PrefsManager(this);

        // If already logged in, go to MainActivity
        if (prefsManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Initialize views
        TextView welcomeText = findViewById(R.id.welcomeText);
        loginButton = findViewById(R.id.loginButton);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        TextInputEditText emailInput = findViewById(R.id.emailInput);
        TextInputEditText passwordInput = findViewById(R.id.passwordInput);
        View forgotPasswordText = findViewById(R.id.forgotPasswordText);
        View signUpText = findViewById(R.id.signUpText);

        // Apply gradient to welcome text
        welcomeText.post(() -> {
            float width = welcomeText.getPaint().measureText(welcomeText.getText().toString());
            LinearGradient textShader = new LinearGradient(0, 0, width, 0,
                    new int[]{
                            ContextCompat.getColor(this, R.color.primary),
                            ContextCompat.getColor(this, R.color.secondary)
                    },
                    null, Shader.TileMode.CLAMP);
            welcomeText.getPaint().setShader(textShader);
            welcomeText.invalidate();
        });

        // Apply gradient to login button
        loginButton.setBackgroundTintList(null);
        loginButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_gradient));

        // Setup click listeners
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(email)) {
                emailLayout.setError("Email is required");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.setError("Please enter a valid email");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordLayout.setError("Password is required");
                return;
            }

            // Clear any previous errors
            emailLayout.setError(null);
            passwordLayout.setError(null);

            // Perform login
            performLogin(email, password);
        });

        forgotPasswordText.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot password feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin(String email, String password) {
        // Disable login button to prevent multiple requests
        loginButton.setEnabled(false);

        try {
            // Create request body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("password", password);

            // Create request
            JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_URL,
                jsonBody,
                response -> {
                    try {
                        String message = response.getString("message");
                        String token = response.getString("token");
                        JSONObject user = response.getJSONObject("user");

                        // Save user data in SharedPreferences
                        prefsManager.saveUserLoginState(
                            true,
                            token,
                            user.getString("email"),
                            user.getString("name"),
                            user.getString("id")
                        );

                        // Navigate to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        handleError("Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = "Login failed";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorResponse = new String(error.networkResponse.data);
                            JSONObject errorJson = new JSONObject(errorResponse);
                            if (errorJson.has("message")) {
                                errorMessage = errorJson.getString("message");
                            }
                        } catch (Exception e) {
                            // Use default error message
                        }
                    }
                    handleError(errorMessage);
                }
            );

            // Add request to queue
            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            handleError("Error creating request: " + e.getMessage());
        }
    }

    private void handleError(String message) {
        loginButton.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
