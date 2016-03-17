package com.styloop.shunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG=MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent goSettings=new Intent(this,SettingsActivity.class);
            startActivity(goSettings);
            return true;
        }
        if(id==R.id.action_map){
            openPreferredLocationinMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openPreferredLocationinMap(){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String location=sharedPreferences.getString(getString(R.string.pref_localtion_key), getString(R.string.pref_localtion_default));
        Uri geoLocation=Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",location).build();
        Intent goMap=new Intent(Intent.ACTION_VIEW);
        goMap.setData(geoLocation);
        if(goMap.resolveActivity(getPackageManager())!=null){
            startActivity(goMap);

        }else{
            Log.d(LOG_TAG,"Couldn't call "+location);
        }


    }
}
