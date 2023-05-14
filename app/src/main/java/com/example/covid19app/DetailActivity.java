package com.example.covid19app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;


public class DetailActivity extends AppCompatActivity {

    private int positionCountry;

    TextView tvCountry,tvCases,tvRecovered,tvCritical,tvActive,tvTodayCases,tvTotalDeaths,tvTodayDeaths;

    PieChart pieChart1;


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

        pieChart1= findViewById(R.id.piechart1);
        setupPieChart();


        tvCountry.setText(AffectedCountries.countryList.get(positionCountry).getCountry());
        tvCases.setText(AffectedCountries.countryList.get(positionCountry).getCases());
        tvRecovered.setText(AffectedCountries.countryList.get(positionCountry).getRecovered());
        tvCritical.setText(AffectedCountries.countryList.get(positionCountry).getCritical());
        tvActive.setText(AffectedCountries.countryList.get(positionCountry).getActive());
        tvTodayCases.setText(AffectedCountries.countryList.get(positionCountry).getTodayCases());
        tvTotalDeaths.setText(AffectedCountries.countryList.get(positionCountry).getDeaths());
        tvTodayDeaths.setText(AffectedCountries.countryList.get(positionCountry).getTodayDeaths());

    }

    //Process PieChart
    private void setupPieChart() {

        // Set up the data for the pie chart
        pieChart1.addPieSlice(new PieModel("Cases", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getCases()), Color.parseColor("#FFA726")));
        pieChart1.addPieSlice(new PieModel("Recovered", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getRecovered()), Color.parseColor("#66BB6A")));
        pieChart1.addPieSlice(new PieModel("Critical", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getCritical()), Color.parseColor("#654E92")));
        pieChart1.addPieSlice(new PieModel("Active", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getActive()), Color.parseColor("#29B6F6")));
        pieChart1.addPieSlice(new PieModel("Deaths", Integer.parseInt(AffectedCountries.countryList.get(positionCountry).getDeaths()), Color.parseColor("#EF5350")));

        //Customize the pie chart
        pieChart1.setInnerValueString("");  // Remove the inner value string (optional)
        pieChart1.startAnimation();  // Animate the pie chart
    }

    //Process ActionBack to go back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }
}