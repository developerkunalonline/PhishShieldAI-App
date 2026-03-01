package com.backdoorz.phishshieldai;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private VisitedUrlAdapter urlAdapter;
    private PrefsManager prefsManager;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsManager = new PrefsManager(this);
        requestQueue = Volley.newRequestQueue(this);

        // Check if user is logged in, if not, redirect to login
        if (!prefsManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initializeViews();
    }

    private void initializeViews() {
        TextView appName = findViewById(R.id.appName);
        TextView greetingText = findViewById(R.id.greetingText);
        MaterialCardView powerButton = findViewById(R.id.powerButton);
        FloatingActionButton chatFab = findViewById(R.id.chatFab);
        FloatingActionButton quizFab = findViewById(R.id.quizFab);
        RecyclerView visitedRecyclerView = findViewById(R.id.visitedRecyclerView);

        // Set greeting with user's name
        String userName = prefsManager.getUserName();
        greetingText.setText(getString(R.string.greeting_text, userName));

        // Initialize RecyclerView
        urlAdapter = new VisitedUrlAdapter();
        visitedRecyclerView.setAdapter(urlAdapter);
        visitedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch dashboard data
        fetchDashboardData();

        // Handle edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Apply vibrant gradient to app name text
        applyGradientToText(appName);

        // Apply gradient to greeting text too
        applyGradientToText(greetingText);

        // Setup click listeners
        setupClickListeners(powerButton, chatFab, quizFab);
    }

    private void applyGradientToText(TextView textView) {
        textView.post(() -> {
            float width = textView.getPaint().measureText(textView.getText().toString());
            LinearGradient textShader = new LinearGradient(0, 0, width, 0,
                    new int[] {
                            ContextCompat.getColor(this, R.color.gradient_start),
                            ContextCompat.getColor(this, R.color.gradient_mid),
                            ContextCompat.getColor(this, R.color.gradient_end)
                    },
                    null, Shader.TileMode.CLAMP);
            textView.getPaint().setShader(textShader);
            textView.invalidate();
        });
    }

    private void setupClickListeners(MaterialCardView powerButton, FloatingActionButton chatFab,
            FloatingActionButton quizFab) {
        powerButton.setOnClickListener(v -> {
            prefsManager.clearLoginState();
            Toast.makeText(this, R.string.logging_out, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        chatFab.setOnClickListener(v -> Toast.makeText(this, R.string.chat_coming_soon, Toast.LENGTH_SHORT).show());

        quizFab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, QuizActivity.class)));
    }

    private void fetchDashboardData() {
        String token = prefsManager.getToken();

        Log.d(TAG, "Making API request to: " + Constants.DASHBOARD_URL);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Constants.DASHBOARD_URL,
                null,
                response -> {
                    Log.d(TAG, "API Response: " + response.toString());
                    try {
                        JSONArray history = response.getJSONArray("history");
                        List<VisitedUrl> urls = new ArrayList<>();

                        for (int i = 0; i < history.length(); i++) {
                            JSONObject item = history.getJSONObject(i);
                            urls.add(createVisitedUrlFromJson(item));
                        }

                        // Update the adapter with the new data
                        Collections.reverse(urls); // Show newest first
                        urlAdapter.setUrls(urls);

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                        Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage;
                    if (error.networkResponse != null) {
                        errorMessage = String.format(Locale.US, "Server returned code %d",
                                error.networkResponse.statusCode);
                        try {
                            String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e(TAG, "Error response body: " + responseBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error response", e);
                        }
                    } else {
                        errorMessage = "Network error: " + error.getMessage();
                    }
                    Log.e(TAG, errorMessage, error);
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // Set timeout to 10 seconds
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                10000,
                1,
                1.0f));

        requestQueue.add(request);
    }

    private VisitedUrl createVisitedUrlFromJson(JSONObject item) throws Exception {
        if (!item.has("url") || !item.has("status") || !item.has("visitedAt")) {
            throw new IllegalArgumentException("Missing required fields in history item");
        }

        String urlStr = item.getString("url");
        String status = item.getString("status");
        String visitedAt = item.getString("visitedAt");

        return new VisitedUrl(
                urlStr,
                status,
                formatDate(visitedAt));
    }

    private String formatDate(String isoDate) {
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat(
                    "dd MMM yyyy", Locale.getDefault());

            java.util.Date date = inputFormat.parse(isoDate);
            return date != null ? outputFormat.format(date) : "";
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date", e);
            return isoDate;
        }
    }
}