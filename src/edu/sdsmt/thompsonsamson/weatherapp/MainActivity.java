/**
* Project Assignment3 Address Book - Main Activity
*/
package edu.sdsmt.thompsonsamson.weatherapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import edu.sdsmt.thompsonsamson.weatherapp.view.FragmentForecast;

/**
 * <p>
 * Android application to display a one hour forecast. The forecast is read from 
 * the weatherbug API.  This is done with a JSONReader to parse the JSON data.  
 * The temperature, felt temperature, humidity, chance of precipitation, and date
 * will be display along with an icon representing current conditions.  Conditions are only
 * displayed for one location.
 * </p>
 * <p>
 * Main entry to the application.  The fragment manager, bundle and view are created here.
 * We will create an instance of the fragementForecast object and set it's arguments to the
 * bundle contents.  A zip code for the location area is hard coded into the bundle.  The bundle
 * is then passed to the newly created fragment view.
 * <p>
 * <a href="https://github.com/amthompson/CSC421-Assignment3">GitHub Repository</a>
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
     * The onCreate function loads the bundle if and sets the view for the main activity.
     * We also load the zip code from an array to pass to the showForecast function.  The showForecast 
     * function is only called if there is data in the bundle to prevent null exceptions.  
     * 
     * @param savedInstanceState bundle data if exists from a previous state
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
	 * The showForecast function will instantiate a new fragment manager and 
	 * set the zip code parameter to the bundle.  It will then create the fragment 
	 * forecast view, pass in the bundle as it's arguments and start the view.
	 * 
	 * @param zipCode the zip code for the forecast
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
