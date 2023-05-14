package com.example.covid19app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MyCustomAdapter extends ArrayAdapter<Country> {

    private Context context;
    private List<Country> countryList;

    //Make function search with keyword
    private List<Country> countryListFiltered;


    //resource( là layout)
    public MyCustomAdapter( Context context, List<Country> countryList) {
        super(context, R.layout.list_custom_item, countryList);

        this.context = context;
        //Process show country list
        this.countryList = countryList;

        //Process function search filter
        this.countryListFiltered = countryList;

    }

    //Process show country
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_custom_item,null,true);
        TextView tvCountryName = view.findViewById(R.id.tvCountryName);
        ImageView imageView = view.findViewById(R.id.imageFlag);

//        Process only show list country
//        tvCountryName.setText(countryList.get(position).getCountry());
//        Glide.with(context).load(countryList.get(position).getFlag()).into(imageView);

        //Process function in searchBar
        tvCountryName.setText(countryListFiltered.get(position).getCountry());
        Glide.with(context).load(countryListFiltered.get(position).getFlag()).into(imageView);


        return view;
    }

    //process search with filter
    @Override
    public int getCount() {
        return countryListFiltered.size();
    }

    //process search with filter on Item
    @Nullable
    @Override
    public Country getItem(int position) {
        return countryListFiltered.get(position);
    }

    //Process to search from position in ItemId
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Make process to search with filtered
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    filterResults.count = countryList.size();
                    filterResults.values = countryList;

                }else{
                    List<Country> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(Country itemsModel:countryList){
                        if(itemsModel.getCountry().toLowerCase().contains(searchStr)){
                            resultsModel.add(itemsModel);

                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }


                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                countryListFiltered = (List<Country>) results.values;
                AffectedCountries.countryList = (List<Country>) results.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }
}


