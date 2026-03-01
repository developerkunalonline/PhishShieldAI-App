package com.backdoorz.phishshieldai;

public class Constants {

    // Base URL
    public static final String BASE_URL = "http://192.168.1.49:3000";

    // API Endpoints
    public static final String LOGIN_URL       = BASE_URL + "/users/login";
    public static final String SIGNUP_URL      = BASE_URL + "/users/signup";
    public static final String QUIZ_URL        = BASE_URL + "/quiz";
    public static final String PREDICTION_URL  = BASE_URL + "/api/scan-url";
    public static final String DASHBOARD_URL   = BASE_URL + "/dashboard/summary";

    private Constants() {
        // Prevent instantiation
    }
}

