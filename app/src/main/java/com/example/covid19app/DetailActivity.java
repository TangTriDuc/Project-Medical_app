package com.example.covid19app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;


public class DetailActivity extends AppCompatActivity {

    private int positionCountry;

    TextView tvCountry,tvCases,tvRecovered,tvCritical,tvActive,tvTodayCases,tvTotalDeaths,tvTodayDeaths;

    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Make function go back in action bar
        getSupportActionBar().setTitle("Details of " + AffectedCountries.countryList.get(positionCountry).getCountry());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        positionCountry = intent.getIntExtra("position", 0);


        tvCountry = findViewById(R.id.tvCountry);
        tvCases = findViewById(R.id.tvCases);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvCritical = findViewById(R.id.tvCritical);
        tvActive = findViewById(R.id.tvActive);
        tvTodayCases = findViewById(R.id.tvTodayCases);
        tvTotalDeaths = findViewById(R.id.tvDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);

        barChart = findViewById(R.id.barchart1);
        setupBarChart();


        tvCountry.setText(AffectedCountries.countryList.get(positionCountry).getCountry());
        tvCases.setText(AffectedCountries.countryList.get(positionCountry).getCases());
        tvRecovered.setText(AffectedCountries.countryList.get(positionCountry).getRecovered());
        tvCritical.setText(AffectedCountries.countryList.get(positionCountry).getCritical());
        tvActive.setText(AffectedCountries.countryList.get(positionCountry).getActive());
        tvTodayCases.setText(AffectedCountries.countryList.get(positionCountry).getTodayCases());
        tvTotalDeaths.setText(AffectedCountries.countryList.get(positionCountry).getDeaths());
        tvTodayDeaths.setText(AffectedCountries.countryList.get(positionCountry).getTodayDeaths());

    }

    //Process BarChart
    private void setupBarChart() {
        // Set up the data for the bar chart
        barChart.addBar(new BarModel("Cases", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getCases()), Color.parseColor("#FFA726")));
        barChart.addBar(new BarModel("Recovered", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getRecovered()), Color.parseColor("#66BB6A")));
        barChart.addBar(new BarModel("Critical", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getCritical()), Color.parseColor("#654E92")));
        barChart.addBar(new BarModel("Active", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getActive()), Color.parseColor("#29B6F6")));
        barChart.addBar(new BarModel("Deaths", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getDeaths()), Color.parseColor("#EF5350")));

        // Customize the bar chart
        barChart.setBarWidth(8); // Adjust the bar width as per your preference
        barChart.startAnimation();

    }

    //Process ActionBack to go back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }
}