package com.styloop.shunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import  android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    String forecastStr="";
    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent=getActivity().getIntent();
        if(intent !=null && intent.hasExtra(Intent.EXTRA_TEXT)){
             forecastStr=intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView)rootView.findViewById(R.id.myText)).setText(forecastStr);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);
        MenuItem menuItem=menu.findItem(R.id.menu_item_share);
        ShareActionProvider mShareActionProvider= (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mShareActionProvider!=null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }else{
            Log.d("SHARE","ERROR");
        }
    }
    public Intent createShareForecastIntent(){
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,forecastStr+"#ShushineApp");
        return shareIntent;
    }
}
