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
 * bundle contents.  A zipcode for the location area is hardcoded into the bundle.  The bundle
 * is then passed to the newley created fragment view.
 * <p>
 * <a href="https://github.com/amthompson/CSC421-Assignment3">GitHub Repository</a>
 * <p>
 * Timeline:
 * <ul>
 *                 <li>10/19/2013:        Initial project creation and repo (AT)</li>
 *                 <li>10/20/2013: Coding session (database/fragments) (AT&SS)</li>
 *                 <li>10/21/2013: Fragment detail work (AT)</li>
 *                 <li>10/23/2013: Fixed orientation bug (AT)</li>
 *                 <li>10/27/2013:        Coding Session - Fixed onResume bug, added alerts, 
 *                                                 testing and documentation (AT&SS)</li>
 *                 <li>10/28/2013:  Finalize comments and host docs and code</li>
 * </ul>
 * @author Andrew Thompson
 * @author Scott Samson
 */
public class MainActivity extends Activity
{
	// class members
	private final static String FORECAST_TAG = "Forecast";	// tag for forecast view
    private String[] _citiesArray;							// list of zip codes
    
    /**
     * The onCreate funciton loads the bundle if and sets the view for the main activity.
     * We also load the zip code from an array to pass to the showForecast function.  The showForecast 
     * function is only called if there is data in the bundle to prevent null exeptions.  
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
	 * The showForecast function will instancite a new fragementManager set the zipcode parameter to
	 * the bundle.  It will then instanciate a fragmentForecast and pass in the bundle as it's arguments.
	 * 
	 * @param zipCode the zip code for the foredcast
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
