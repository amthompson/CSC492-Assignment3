package edu.sdsmt.thompsonsamson.weatherapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import edu.sdsmt.thompsonsamson.weatherapp.view.FragmentForecast;

/**
 * 
 * @author Andrew Thompson
 * @author Scott Samson
 */
public class MainActivity extends Activity
{
	// class members
	private final static String NETWORK_ERROR = "Unable to establish network connectivity.";
	private final static String FORECAST_TAG = "Forecast";	// tag for forecast view
    private String[] _citiesArray;							// list of zip codes
    
    /**
     * 
     */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		if ( !networkOnline() )
		{
			Toast.makeText(this, R.string.toastNetworkUnavaliable, Toast.LENGTH_LONG).show();
		}
		
		setContentView(R.layout.activity_main);
		
		// Get City array from resources.
        _citiesArray = getResources().getStringArray(R.array.cityArray);
        
        // show the forecast
        if( savedInstanceState == null )
        {
            showForecast(TextUtils.split(_citiesArray[0], "\\|")[0]);        	
        }
       
	}
	
	private boolean networkOnline() 
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
			return true;
		return false;
	}

	/**
	 * 
	 * @param zipCode
	 */
	private void showForecast(String zipCode)
	{	
		// initialize the fragment manager
		FragmentManager fragmentManager = getFragmentManager();
		
		// send zip code to the bundle
		Bundle bundle = new Bundle();
		bundle.putString("ZIP_CODE", zipCode);
		
		
		// initialize the forecast view fragment
		FragmentForecast fragmentForecast = (FragmentForecast) fragmentManager.findFragmentByTag(FORECAST_TAG);
		if( fragmentForecast == null )
		{
			fragmentForecast = new FragmentForecast();
			
			
		}
		
		// send the bundle to the view and call it
		fragmentForecast.setArguments(bundle);
		fragmentManager.beginTransaction()
		.replace(R.id.ViewFrameLayout, fragmentForecast, FORECAST_TAG)
		.commit();
	}	
}
