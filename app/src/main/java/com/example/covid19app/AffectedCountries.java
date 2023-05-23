package com.example.covid19app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AffectedCountries extends AppCompatActivity {

    EditText edtSearch;
    ListView listView;

    SimpleArcLoader simpleArcLoader;

    //Process show list flag & country
    public static List<Country> countryList = new ArrayList<>();
    Country country;
    MyCustomAdapter myCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affected_countries);

        edtSearch = findViewById(R.id.edtSearch);
        listView = findViewById(R.id.listView);
        //Make loader
        simpleArcLoader = findViewById(R.id.loader);

        //Make function go back in action bar
        getSupportActionBar().setTitle("Go Back Global Statistics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fetchData();

        //Make with DetailActivity (AdapterView hiển thị các mục được tải vào trong một trình chuyển đổi)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), DetailActivity.class).putExtra("position", position)); //position lấy từ public static List<Country> countryList = new ArrayList<>();
            }
        });

        //Make function search with keyword
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                myCustomAdapter.getFilter().filter(s);
                myCustomAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    //Process ActionBack to go back in action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void fetchData() {

        //add url to fetch api
        String url = "https://disease.sh/v3/covid-19/countries";
        simpleArcLoader.start();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                //new Response.Listener<String>()
                response -> {

                    try {
                        //HTTP request có kết quả trả về là JSONArray
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            //Khai báo data từ api
                            String countryName = jsonObject.getString("country");
                            String cases = jsonObject.getString("cases");
                            String todayCases = jsonObject.getString("todayCases");
                            String deaths = jsonObject.getString("deaths");
                            String todayDeaths = jsonObject.getString("todayDeaths");
                            String recovered = jsonObject.getString("recovered");
                            String active = jsonObject.getString("active");
                            String critical = jsonObject.getString("critical");

                            //Call object from json
                            JSONObject object = jsonObject.getJSONObject("countryInfo");
                            //Process flag
                            String flagUrl = object.getString("flag");

                            //Get Data from Country.java
                            country = new  Country(flagUrl, countryName, cases, todayCases, deaths, todayDeaths, recovered, active, critical);

                            //Add countrylist to country
                            countryList.add(country);
                        }

                        //Process active MyCustomAdapter.java

                        myCustomAdapter = new MyCustomAdapter(AffectedCountries.this, countryList);

                        //Process to show country in listview
                        listView.setAdapter(myCustomAdapter);
                        simpleArcLoader.stop();
                        simpleArcLoader.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        simpleArcLoader.stop();
                        simpleArcLoader.setVisibility(View.GONE);
                    }

                },
                // new Response.ErrorListener()
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        simpleArcLoader.stop();
                        simpleArcLoader.setVisibility(View.GONE);
                        //Process Loader & Scroll to show/hide when load APi
                        Toast.makeText(AffectedCountries.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //hằng đợi giữ các Request
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Thực thi
        requestQueue.add(request);

    }


}