package com.example.covid19app;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.gbuttons.GoogleSignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

public class CovidStatistics extends AppCompatActivity {

    TextView tvCases, tvRecovered, tvCritical, tvActive, tvTodayCases, tvTotalDeaths, tvTodayDeaths, tvAffectedCountries;

    SimpleArcLoader simpleArcLoader;
    ScrollView scrollView;
    PieChart pieChart;

    FirebaseAuth auth;

    //Make logout with google auth
    GoogleSignInButton googleBtn;
    GoogleSignInOptions gOptions;
    GoogleSignInClient gClient;

    //Take checkbox remember me from Login.java
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.covid_statistics);

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

        //Process checkbox remember me
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Receive auth from firebase
        auth = FirebaseAuth.getInstance();

        //Process button sign out with google account
        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);
        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);


        //Make function go back in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Go Back Home Page"); //Thiết lập tiêu đề nếu muốn
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Make process to call api
        fetchData();
    }

    //Create by alt enter of fetchData above
    private void fetchData() {

        //add url to fetch api
        String url = "https://disease.sh/v3/covid-19/all";
        simpleArcLoader.start();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                //new Response.Listener<String>()
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                        simpleArcLoader.stop();
                        simpleArcLoader.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }

                },
                // new Response.ErrorListener()
                error -> {
                    //Process Loader & Scroll to show/hide when load APi
                    simpleArcLoader.stop();
                    simpleArcLoader.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    Toast.makeText(CovidStatistics.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    //Make btn CHATGPT
    private void ChatAI() {
        startActivity(new Intent(CovidStatistics.this, SplashChatActivity.class));
    }

    //Make process logout
    private void logout() {

        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(CovidStatistics.this);
        builder.setTitle("LOGOUT");
        builder.setMessage("Are you sure you want to LOGOUT!!!");


        //Make alert dialog logout
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Sign out Firebase with google account & Remember
                        auth.signOut();

                        // Sign out Google account & Remember me
                        gClient.signOut().addOnCompleteListener(CovidStatistics.this, task -> {
                            // Handle sign-out result
                            if (task.isSuccessful()) {
                                // Sign-out successful, proceed with Firebase sign out
                                auth.signOut();

                                // Clear the saved credentials
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("email");
                                editor.remove("password");
                                editor.apply();
                                Toast.makeText(CovidStatistics.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                                // Redirect to LoginActivity
                                Intent intent = new Intent(CovidStatistics.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Sign-out failed
                                Toast.makeText(CovidStatistics.this, "Logout failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                // Set negative button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

    }

    //Process ActionBack to go back
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.logout) {
            logout();
            return true;
        } else if (id == R.id.ChatAI){
            ChatAI();
    }

        return super.onOptionsItemSelected(item);
    }

}