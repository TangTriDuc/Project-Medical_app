package com.example.covidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;


public class CovidStatistics extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvCases, tvRecovered, tvCritical, tvActive, tvTodayCases, tvTotalDeaths, tvTodayDeaths, tvAffectedCountries;

    SimpleArcLoader simpleArcLoader;
    ScrollView scrollView;
    PieChart pieChart;

    //Make navigation drawer
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, global, ChatAI, about, logout;

    // Views for profile data
    private LinearLayout profileLayout;
    //private TextView profileName;
    private TextView profileEmail;

    //Make logout
    SharedPreferences sharedPreferences;

    private GoogleApiClient googleApiClient;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coivd_statistics);

        //TextView
        tvCases = findViewById(R.id.tvCases);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvCritical = findViewById(R.id.tvCritical);
        tvActive = findViewById(R.id.tvActive);
        tvTodayCases = findViewById(R.id.tvTodayCases);
        tvTotalDeaths = findViewById(R.id.tvTotalDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);
        tvAffectedCountries = findViewById(R.id.tvAffectedCountries);

        simpleArcLoader = findViewById(R.id.loader);
        scrollView = findViewById(R.id.scrollStats);
        pieChart = findViewById(R.id.piechart);

        //Process show navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);
        global = findViewById(R.id.global);
        ChatAI = findViewById(R.id.chatAi);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(CovidStatistics.this, MainActivity.class);
            }
        });

        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        ChatAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(CovidStatistics.this, ChatGPTActivity.class));
                redirectActivity(CovidStatistics.this, SplashChatActivity.class);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(CovidStatistics.this, AboutActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        //Process show profile in nav_drawer
        // Find profile views
        profileLayout = findViewById(R.id.profileLayout);
        //profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);

        // Check if the user is logged in
        boolean isLoggedIn = checkLoggedIn();

        // Show/hide the profile section based on login status
        if (isLoggedIn) {
            showProfileData();
        } else {
            hideProfileData();
        }

        //Make process to call api
        fetchData();
    }

    //Process show profile in nav_drawer
    // Method to check if the user is logged in
    private boolean checkLoggedIn() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.contains("username") && sharedPreferences.contains("password");
    }

    // Method to show profile data
    private void showProfileData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");

        // Update the profile views
        //profileName.setText(username);
        profileEmail.setText(email);
        profileLayout.setVisibility(View.VISIBLE);


    }

    // Method to hide profile data
    private void hideProfileData() {
        profileLayout.setVisibility(View.GONE);
    }

    //Process logout
//    private void logout() {
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("username");
//        editor.remove("password");
//        editor.apply();
//
//        Toast.makeText(CovidStatistics.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
//
//        Intent intent = new Intent(CovidStatistics.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//
//        // Hide the profile data after logout
//        hideProfileData();
//    }

    //Process logout with AlertDialog
    private void logout() {
        // Create the AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and message of the dialog
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        // Set the positive button and its callback
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Yes", perform logout
                performLogout();

                // Initialize the GoogleSignInOptions
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .build();

                // Build the GoogleSignInClient
                GoogleSignInClient signInClient = GoogleSignIn.getClient(CovidStatistics.this, gso);

                // Sign out from Google
                signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Sign out successful
                            performLogout();
                        } else {
                            // Sign out failed
                            Toast.makeText(CovidStatistics.this, "Failed to sign out from Google", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        // Set the negative button and its callback
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No", dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Process logout with AlertDialog
    private void performLogout() {
        // Remove the username, email and password from shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("email");
        editor.remove("password");
        editor.apply();

        // Show the "Logout Successful!" toast
        Toast.makeText(CovidStatistics.this, "Logout Successful!", Toast.LENGTH_SHORT).show();

        // Start the LoginActivity
        Intent intent = new Intent(CovidStatistics.this, LoginActivity.class);
        startActivity(intent);
        finish();

        // Hide the profile data after logout
        hideProfileData();
    }


    //Process to show info statistics after fetch api
    private void fetchData() {

        //add url to fetch api
        String url = "https://disease.sh/v3/covid-19/all";
        simpleArcLoader.start();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        //HTTP request có kết quả trả về là JSONObject.
                        JSONObject jsonObject = new JSONObject(response.toString());

                        tvCases.setText(jsonObject.getString("cases"));
                        tvRecovered.setText(jsonObject.getString("recovered"));
                        tvCritical.setText(jsonObject.getString("critical"));
                        tvActive.setText(jsonObject.getString("active"));
                        tvTodayCases.setText(jsonObject.getString("todayCases"));
                        tvTotalDeaths.setText(jsonObject.getString("deaths"));
                        tvTodayDeaths.setText(jsonObject.getString("todayDeaths"));
                        tvAffectedCountries.setText(jsonObject.getString("affectedCountries"));

                        //Make Piechart run with API
                        pieChart.addPieSlice(new PieModel("Cases", Integer.parseInt(tvCases.getText().toString()), Color.parseColor("#FFA726")));
                        pieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(tvRecovered.getText().toString()), Color.parseColor("#66BB6A")));
                        pieChart.addPieSlice(new PieModel("Deaths", Integer.parseInt(tvTotalDeaths.getText().toString()), Color.parseColor("#EF5350")));
                        pieChart.addPieSlice(new PieModel("Active", Integer.parseInt(tvActive.getText().toString()), Color.parseColor("#29B6F6")));
                        pieChart.startAnimation();

                        //Process Loader & Scroll to show/hide when load APi
                        simpleArcLoader.stop();
                        simpleArcLoader.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);

                        // Show the profile data after fetching API
                        showProfileData();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        simpleArcLoader.stop();
                        simpleArcLoader.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);

                        // Hide the profile data if an error occurs
                        hideProfileData();
                    }

                },
                error -> {
                    simpleArcLoader.stop();
                    simpleArcLoader.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    Toast.makeText(CovidStatistics.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    // Hide the profile data if an error occurs
                    hideProfileData();
                });

        //hằng đợi giữ các Request
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Thực thi
        requestQueue.add(request);


    }

    //Process button Track countries
    public void goTrackCountries(View view) {

        startActivity(new Intent(getApplicationContext(), AffectedCountries.class));
    }


    //Process show navigation drawer
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        return false;
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation item selection
        // ...

        return true;
    }
}
