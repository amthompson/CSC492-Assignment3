package edu.sdsmt.thompsonsamson.weatherapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import edu.sdsmt.thompsonsamson.weatherapp.view.FragmentForecast;

/**
 * 
 * @author Andrew Thompson
 * @author Scott Samson
 */
public class MainActivity extends Activity
{
	// class members
	private final static String FORECAST_TAG = "Forecast";	// tag for forecast view
    private String[] _citiesArray;							// list of zip codes
    
    /**
     * 
     */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get City array from resources.
        _citiesArray = getResources().getStringArray(R.array.cityArray);
        
        // show the forecast
        if( savedInstanceState == null )
        {
            showForecast(TextUtils.split(_citiesArray[0], "\\|")[0]);        	
        }
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
