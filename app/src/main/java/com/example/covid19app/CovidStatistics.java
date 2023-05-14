package com.example.covid19app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

public class CovidStatistics extends AppCompatActivity {

    TextView tvCases,tvRecovered,tvCritical,tvActive,tvTodayCases,tvTotalDeaths,tvTodayDeaths,tvAffectedCountries;

    SimpleArcLoader simpleArcLoader;
    ScrollView scrollView;
    PieChart pieChart;

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

        //Make function go back in action bar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                        pieChart.addPieSlice(new PieModel("Cases",Integer.parseInt(tvCases.getText().toString()), Color.parseColor("#FFA726")));
                        pieChart.addPieSlice(new PieModel("Recovered",Integer.parseInt(tvRecovered.getText().toString()), Color.parseColor("#66BB6A")));
                        pieChart.addPieSlice(new PieModel("Deaths",Integer.parseInt(tvTotalDeaths.getText().toString()), Color.parseColor("#EF5350")));
                        pieChart.addPieSlice(new PieModel("Active",Integer.parseInt(tvActive.getText().toString()), Color.parseColor("#29B6F6")));
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
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Process Loader & Scroll to show/hide when load APi
                        simpleArcLoader.stop();
                        simpleArcLoader.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                        Toast.makeText(CovidStatistics.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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

    //Process ActionBack to go back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }
}