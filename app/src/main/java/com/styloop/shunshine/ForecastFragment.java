package com.styloop.shunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.styloop.shunshine.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private final String LOG_TAG_FORECAST=ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> arrayAdapter;
    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_refresh){
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView= inflater.inflate(R.layout.fragment_main, container, false);

        arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview, new ArrayList<String>());
        ListView listView=(ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = rootView.getContext();
                String forecast = arrayAdapter.getItem(position);
                CharSequence text = forecast;
                Intent goDetailIntent = new Intent(rootView.getContext(), DetailActivity.class);
                goDetailIntent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(goDetailIntent);
            }
        });

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String[]>{

        private final String LOG_TAG=FetchWeatherTask.class.getSimpleName();



        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            String forecastJsonString=null;
            if(params.length<=0){
                return null;
            }
            String format="json";
            String units="metric";
            int cnt=7;
            String apiKey="b1b15e88fa797225412429c1c50c122a";

            try {
                final String FORECAST_BASE_URL="http://api.openweathermap.org/data/2.5/forecast/daily";
                final String QUERY_PARAM="q";
                final String FORMAT_PARAM="mode";
                final String UNITS_PARAM="units";
                final String DAYS_PARAM="cnt";
                final String APP_ID="appid";

                Uri buildUri= Uri.parse(FORECAST_BASE_URL).buildUpon()
                                .appendQueryParameter(QUERY_PARAM,params[0])
                                .appendQueryParameter(FORMAT_PARAM,format)
                                .appendQueryParameter(UNITS_PARAM,units)
                                .appendQueryParameter(DAYS_PARAM,String.valueOf(cnt))
                                .appendQueryParameter(APP_ID,apiKey).build();

                URL url=new URL(buildUri.toString());

                Log.v(LOG_TAG,"URL: "+buildUri.toString());

                connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream=connection.getInputStream();
                StringBuffer stringBuffer=new StringBuffer();
                if(inputStream==null){
                    return null;
                }
                reader=new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line=reader.readLine())!=null){
                    stringBuffer.append(line+"\n");
                }
                if (stringBuffer.length()==0){
                    return null;
                }
                forecastJsonString=stringBuffer.toString();
                Log.v(LOG_TAG,"forecast JSON String: "+ forecastJsonString);


            }catch (IOException e){
                Log.e(LOG_TAG, "ERROR", e);
            }finally {
                if(connection!=null){
                    connection.disconnect();
                }
                try{
                    if(reader!=null){
                        reader.close();
                    }
                }catch (final IOException e){
                    Log.e(LOG_TAG,"Error",e);
                }
            }

            try {
                String typeUnits=getUnit();
                return Util.getWeatherDataFromJson(forecastJsonString,cnt,typeUnits);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result!=null){
                arrayAdapter.clear();
                arrayAdapter.addAll(result);
            }

        }
    }

    public void updateWeather(){
        FetchWeatherTask task=new FetchWeatherTask();
        String location=getLocationPreference();
        task.execute(location);

    }
    public String getLocationPreference(){
        String location="94043";
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        location=sharedPreferences.getString(getString(R.string.pref_localtion_key),getString(R.string.pref_localtion_default));
        Log.v("VIEW LOG", location);
        return location;
    }

    public String getUnit(){
        String unit="metric";
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
        unit=sharedPreferences.getString(getString(R.string.pref_unit_key),getString(R.string.pref_unit_metric_value));
        Log.v(LOG_TAG_FORECAST,"THIS IS UNIT NOW: "+ unit);
        return unit;

    }
}
