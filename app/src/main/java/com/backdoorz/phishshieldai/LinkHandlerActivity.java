package com.backdoorz.phishshieldai;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class LinkHandlerActivity extends AppCompatActivity {
    private String currentUrl;
    private TextView resultText;
    private ProgressBar progressBar;
    private Button openBrowserButton;
    private PrefsManager prefsManager;
    private static final String PREDICTION_URL = "https://phishshield-backend-15e6.onrender.com/api/scan-url";
    private WebView webView;
    private MaterialCardView webViewContainer;
    private CircularProgressIndicator phishingChanceIndicator;
    private TextView percentageText;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_handler);

        prefsManager = new PrefsManager(this);

        // Initialize views
        TextView urlText = findViewById(R.id.urlText);
        resultText = findViewById(R.id.resultText);
        progressBar = findViewById(R.id.progressBar);
        openBrowserButton = findViewById(R.id.openBrowserButton);
        webView = findViewById(R.id.webView);
        webViewContainer = findViewById(R.id.webViewContainer);
        phishingChanceIndicator = findViewById(R.id.phishingChanceIndicator);
        percentageText = findViewById(R.id.percentageText);
        statusText = findViewById(R.id.statusText);

        // Setup WebView
        setupWebView();

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            currentUrl = data.toString();
            urlText.setText(getString(R.string.received_url, currentUrl));

            // Initially hide the browser button until we get prediction
            openBrowserButton.setVisibility(View.GONE);
            openBrowserButton.setOnClickListener(v -> openInBrowser());

            // Make prediction request
            makePredictionRequest();
        }
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();

        // Enable features for proper rendering
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);

        // Enable required features for modern web
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setDefaultTextEncodingName("utf-8");

        // Enable zoom
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LinkHandlerActivity.this,
                    "Error loading page: " + error.getDescription(),
                    Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
                // Ignore SSL errors for preview (not recommended for production)
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Allow navigation inside WebView
                return false;
            }
        });
    }

    private void makePredictionRequest() {
        progressBar.setVisibility(View.VISIBLE);
        resultText.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        webViewContainer.setVisibility(View.GONE);
        phishingChanceIndicator.setProgress(0);
        percentageText.setText("0%");
        statusText.setText("");

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("url", currentUrl);

            JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                PREDICTION_URL,
                jsonBody,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    resultText.setVisibility(View.VISIBLE);
                    try {
                        // Display full response in status text
                         // Using indent of 2 for better readability

                        // Parse response
                        double phishingChance = response.getDouble("phishing_chance");
                        String status = response.getString("status");
                        String reason = response.getString("reason");

                        // Update UI
                        int progressValue = (int) phishingChance;
                        phishingChanceIndicator.setProgress(progressValue);
                        percentageText.setText(progressValue + "%");
                        resultText.setText(reason);
                        statusText.setText(status);

                        // Update colors based on risk level
                        int colorRes;
                        if (phishingChance < 30) {
                            colorRes = android.R.color.holo_green_light;
                            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
                            percentageText.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
                            openInBrowser();
                        } else if (phishingChance < 70) {
                            colorRes = android.R.color.holo_orange_light;
                            statusText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, null));
                            percentageText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, null));
                        } else {
                            colorRes = android.R.color.holo_red_light;
                            statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
                            percentageText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
                        }
                        phishingChanceIndicator.setIndicatorColor(getResources().getColor(colorRes, null));

                        // Handle WebView visibility
                        if ("Safe".equalsIgnoreCase(status)) {
                            openBrowserButton.setText(R.string.open_in_browser);
                            webViewContainer.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.VISIBLE);
                            webView.loadUrl(currentUrl);
                        } else {
                            openBrowserButton.setText("Open Anyway (Not Recommended)");
                            webViewContainer.setVisibility(View.GONE);
                            webView.setVisibility(View.GONE);
                        }

                        openBrowserButton.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        handleError("Error parsing response: " + e.getMessage());
                    }
                },
                error -> handleError("Network error: " + error.getMessage())
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String token = prefsManager.getToken();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            // Add request to queue
            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            handleError("Error creating request: " + e.getMessage());
        }
    }

    private void handleError(String message) {
        progressBar.setVisibility(View.GONE);
        resultText.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        statusText.setText("ERROR");
        statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        percentageText.setText("!");
        percentageText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        resultText.setText(message);
        resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        openBrowserButton.setVisibility(View.VISIBLE);
        openBrowserButton.setText("Proceed Anyway");
    }

    private void openInBrowser() {
        if (currentUrl != null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            browserIntent.setPackage("com.android.chrome");

            try {
                startActivity(browserIntent);
            } catch (Exception e) {
                browserIntent.setPackage(null);
                startActivity(browserIntent);
            }
        }
    }
}
