/**
* Project Assignment3 Weather App- Fragment Forecast
*/
package edu.sdsmt.thompsonsamson.weatherapp.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import edu.sdsmt.thompsonsamson.weatherapp.IListeners;
import edu.sdsmt.thompsonsamson.weatherapp.R;
import edu.sdsmt.thompsonsamson.weatherapp.model.Forecast;
import edu.sdsmt.thompsonsamson.weatherapp.model.ForecastLocation;
import edu.sdsmt.thompsonsamson.weatherapp.model.Forecast.LoadForecast;
import edu.sdsmt.thompsonsamson.weatherapp.model.ForecastLocation.LoadForecastLocation;

/**
 * Class for displaying the forecast and forecast location.  Api calls to model objects are
 * also handled here with an inner class called HandleAPICallListener.  Fragment lifecycle 
 * operations with the bundle are also handled here.  
 * 
 * @author Andrew Thompson
 * @author Scott Samson
 */
public class FragmentForecast extends Fragment
{	
	// keys for parcelable/bundle data
	public static final String LOCATION_KEY = "key_location";
	public static final String FORECAST_KEY = "key_forecast";
	
	// view objects
	private String ZipCode = null;						// zipcode to get forecast
	private ForecastLocation _forecastLocation;			// object location of forecast
	private Forecast _forecast;							// object for weather forecast

	// listener/api objects
	private HandleWebCallListener _webRequest;			// class to handle asynctasks
	private LoadForecastLocation _loadForecastLocation;	// api call for location
	private LoadForecast _loadForecast;					// api call for forecast
	
	// ui objects
	private ScrollView _forecastData;					// ui container for forecast
	private RelativeLayout _loadingScreen;				// spinning wheel for loading	
	private ImageView _imageIcon;						// forecast image
	private TextView _textLocation;						// forecast location text
	private TextView _textConditions;					// current conditions text
	private TextView _textTemperature;					// temperature text
	private TextView _textFeelsLike;					// feels like text
	private TextView _textHumidity;						// humidity text
	private TextView _textPrecip;						// precipitation text
	private TextView _textTime;							// time of forecast test
		
	/**
	 * Handles the onCreate event for the fragment. This initializes the location 
	 * and forecast model objects. If the bundle is not null, the ZipCode is loaded 
	 * from it. Although, it shouldn't ever be null since we are passing the bundle item to
	 * the fragment from the main activity. Just handling it to be safe.
	 * 
	 * @param argumentsBundle Bundle data passed from main activity to fragment
	 */
	@Override
	public void onCreate(Bundle argumentsBundle)
	{
		super.onCreate(argumentsBundle);
		
		// get the bundle data saved to the fragment
		argumentsBundle = getArguments();
		
		// create the web api class
		_webRequest = new HandleWebCallListener();
		
		// define data models for location and forecast
		_forecastLocation = new ForecastLocation();
		_forecast = new Forecast();

		// if the bundled data isn't empty, get the zip code for the 
		// forecast that was passed in from parent activity 
		if (argumentsBundle != null) 
		{
			ZipCode = argumentsBundle.getString("ZIP_CODE");
		}
	}

	/**
	 * Creates the view and sets up the layout. The view is inflated to the host
	 * activity and ui objects are initialized. This method also checks if the
	 * device has a network enabled or will display a toast message.
	 * 
	 * @param inflater inflates fragment according to the layout specified
	 * @param container container for a group of views
	 * @param savedInstanceState bundle of data from previous instances
	 * @return the fragment forecast returned as a view object to the host activity
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the fragment to host activity
		View rootView = inflater.inflate(R.layout.fragment_forecast, null);
				
		// check network connection
		if ( !networkOnline() ) 
		{
			Toast.makeText(getActivity(), R.string.toastNetworkUnavaliable, Toast.LENGTH_LONG).show();
		}
		
		// setup ui objects
		configureTextFields(rootView);
		
		return rootView;
	}
	
	/**
	 * Saves the current forecast and forecast location data to the bundle.
	 * 
	 * @param savedInstanceStateBundle
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceStateBundle) 
	{		
		super.onSaveInstanceState(savedInstanceStateBundle);
		
		// save location and forecast to the bundle
		savedInstanceStateBundle.putParcelable(LOCATION_KEY, _forecastLocation);
		savedInstanceStateBundle.putParcelable(FORECAST_KEY, _forecast);
	}

	/**
	 * Creates the activity and checks if the forecast and location exist in the
	 * bundle as a parcelable object. If they do, set the members to the bundle data
	 * and populate the ui items. 
	 * 
	 * @param savedInstanceStateBundle bundle to load data from
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceStateBundle) 
	{
		super.onActivityCreated(savedInstanceStateBundle);
		
		// restore data from bundle and display information
		if( savedInstanceStateBundle != null ) 
		{			
			_forecastLocation = savedInstanceStateBundle.getParcelable(LOCATION_KEY);
			_forecast = savedInstanceStateBundle.getParcelable(FORECAST_KEY);
			populateLocation();
			populateForecast();
		}
	}

	/**
	 * Handle the fragment onPause event. This occurs when rotating the device.
	 * Need to cancel any asynctasks that might be running. We do this to
	 * prevent any zombie processes.
	 */
	@Override
	public void onPause() 
	{
		super.onPause();

		// stop any asynctasks
		stopTasks();
	}

	/**
	 * Handle the fragment onResume event. This occurs right after creating the
	 * view and whenever the device is rotated. This is where we decided to do
	 * the api web calls to ensure the newest data is loaded. If the location
	 * is null, the asynctask will be executed to get that data. If the forecast
	 * time is null or the system time is greater than the forecast time, a 
	 * new asynctask will be executed for the forecast.
	 */
	@Override
	public void onResume() 
	{
		super.onResume();
		
		// if there isn't a forecast location, execute the asynctask to get it
		if( _forecastLocation.City == null ) 
		{	
			// make the api call to get the location data
			_loadForecastLocation = _forecastLocation.new LoadForecastLocation(_webRequest);
			_loadForecastLocation.execute(ZipCode);
		}

		// need to check the timestamp of the forecast. if it is null, or
		// the current time is greater than the forecast time, then a new
		// forecast needs to get generated
		if( checkForecastTimestamp() == true )
		{	
			// make the api call to get the forecast data
			_loadForecast = _forecast.new LoadForecast(_webRequest);
			_loadForecast.execute(ZipCode);
		}
	}

	/**
	 * Handle the fragment onDestroy event. This occurs when the app is closed.
	 * Need to cancel any asynctasks that might be running. We do this to
	 * prevent any zombie processes.
	 */
	@Override
	public void onDestroy() 
	{
		super.onDestroy();

		// stop any asynctasks
		stopTasks();
	}

	/**
	 * Sets up the textViews for the view.  This hides the view while loading data and sets 
	 * up the objects. We wanted to hide any ui objects until the data was loaded to 
	 * prevent the pending text from displaying. We think it looked a little more clean.
	 * 
	 * @param v The view to display the objects in
	 */
	private void configureTextFields(View v) 
	{
		// loading screen
		_loadingScreen = (RelativeLayout) v.findViewById(R.id.layoutProgress);
				
		// hide the data while loading
		_forecastData = (ScrollView) v.findViewById(R.id.scrollView);
		_forecastData.setVisibility(View.INVISIBLE);
		
		// setup the ui objects
		_imageIcon = (ImageView) v.findViewById(R.id.imageForecast);
		_textLocation = (TextView) v.findViewById(R.id.textViewLocation);
		_textConditions = (TextView) v.findViewById(R.id.textViewConditions);
		_textTemperature = (TextView) v.findViewById(R.id.textViewTemp);
		_textFeelsLike = (TextView) v.findViewById(R.id.textViewFeelsLikeTemp);
		_textHumidity = (TextView) v.findViewById(R.id.textViewHumidity);
		_textPrecip = (TextView) v.findViewById(R.id.textViewChanceOfPrecip);
		_textTime = (TextView) v.findViewById(R.id.textViewAsOfTime);
	}

	/**
	 * Helper function to set the location. Used in a couple areas, so
	 * easier to set it here
	 * @see FragmentForecast.HandleWebCallListener#onLocationLoaded(ForecastLocation)
	 * @see #onActivityCreated(Bundle)
	 */
	private void populateLocation()
	{
		_textLocation.setText(_forecastLocation.City + ", " + _forecastLocation.State);
	}
	
	/**
	 * Helper function to set the forecast and display it. Used in a couple
	 * areas, so used it as a seperate method.
	 * @see FragmentForecast.HandleWebCallListener#onForecastLoaded(Forecast)
	 * @see #onActivityCreated(Bundle)
	 */
	private void populateForecast()
	{
		// turn the loading screen off
		_loadingScreen.setVisibility(View.GONE);
		
		// set the image
		_imageIcon.setImageBitmap(_forecast.Image);
		
		// populate the text fields
		_textConditions.setText(_forecast.Conditions);
		_textTemperature.setText(_forecast.Temperature + "\u00B0 F");
		_textFeelsLike.setText(_forecast.FeelsLike + "\u00B0 F");
		_textHumidity.setText(_forecast.Humidity + "%");
		_textPrecip.setText(_forecast.ChancePrecip + "%");
		_textTime.setText(formatDateTime(_forecast.ForecastDate));
		
		// turn the forecast data on
		_forecastData.setVisibility(View.VISIBLE);
	}

	/**
	 * Returns true or false based on the device's network connectivity.
	 * Sets up the textViews for the view.  This hides the view while loading data and sets 
	 * up the objects. Called from onCreateView method.
	 * 
	 * @see #onCreateView(LayoutInflater, ViewGroup, Bundle)
	 * @return true return true if network connected
	 * @return false return false if network not connected
	 */
	private boolean networkOnline() 
	{
		// get connection manager and network info objects
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		
		// true if if network doesn't exist or isn't connected
		if (netInfo != null && netInfo.isConnected()) 
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Stops any currently running asynctasks. We do this when the view is paused
	 * or destroyed to prevent zombie processes. The tasks are generally short
	 * run anyways, but this at least keeps them cleaned up.
	 * 
	 * @see #onPause()
	 * @see #onDestroy()
	 */
	private void stopTasks() 
	{
		// if the asynctask for location isn't null and still running, kill it
		if( _loadForecastLocation != null && _loadForecastLocation.getStatus() == Status.RUNNING) 
		{
			_loadForecastLocation.cancel(true);
		}

		// if the asynctask for forecast isn't null and still running, kill it
		if( _loadForecast != null && _loadForecast.getStatus() == Status.RUNNING) 
		{
			_loadForecast.cancel(true);
		}	
	}

	/**
	 * Takes a string with the number of seconds in epoch time and returns a string with the time
	 * in a date/time format based on the the timestamp parameter.  
	 * 
	 * @param timestamp number of seconds since 1/1/1970
	 * @return The current time as a string in a date/time format
	 * @see #populateForecast()
	 */
	private String formatDateTime(String timestamp) 
	{
		// handle null, just in case
		if( timestamp == null )
		{
			return "Pending";
		}
		
		// otherwise do the conversion
		Date date = new Date(Long.valueOf(timestamp)); 	
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mm a", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
		return dateFormat.format(date);
	}
	
	/**
	 * This method checks the current system timestamp and the forecast timestamp
	 * to see if a new forecast should be generated. Android system time is in 
	 * utc so need the timezone offseet before comparing. If the current time in
	 * milliseconds minus the timezone offset is greater than the timestamp in
	 * milliseconds send by weatherbug's api, then a true is returned and a
	 * new forecast should be fetched.
	 * @return true if new forecast needed, false if forecast not needed
	 * @see #onResume()
	 */
	private boolean checkForecastTimestamp()
	{
		// if the date is null - need to generate a new
		// forecast anyways so return
		if( _forecast.ForecastDate == null )
		{
			return true;
		}

		// get the current time and timezone
		TimeZone tz = TimeZone.getDefault();
		long now = new Date().getTime();
		
		// get the current time and forecast time in milliseconds
		// need to make sure that the current time adds the offset
		// from utc (+/- based on current timezone)
		long forecastTime = Long.valueOf(_forecast.ForecastDate);
		long currentTime = now + tz.getOffset(now);
		
		// if the current time (-difference) is greater than the forecast
		// time, then return true to get a new forecast
		return currentTime > forecastTime;
	}

	/**
	 * Handles calls back from the asynctasks. This will set the class members
	 * to the model objects _forecast and _forecastLocation. If an object was
	 * returned as null, we handle that as well by displaying some toast error.
	 * This implements the IListeners interface.
	 */
	public class HandleWebCallListener implements IListeners 
	{	
		/**
		 * Sets forecast location data from the api call to the class member 
		 * and populate the io location fields. If the object was returned as
		 * null, a toast error is displayed.
		 * 
		 * @param forecastLocation
		 */
		@Override
		public void onLocationLoaded(ForecastLocation forecastLocation) 
		{
			// if the location returns with a valid object, set it to the class
			// member and populate the location fields. Otherwise toast error.
			if( forecastLocation != null)
			{
				_forecastLocation = forecastLocation;
				populateLocation();
			}
			else
			{
				Toast.makeText(getActivity(), R.string.toastNetworkUnavaliable, Toast.LENGTH_LONG).show();
			}
		}
	
		/**
		 * Sets forecast data from the api call to the class member and 
		 * populate the ui forecast fields. If the object was returned
		 * as null, a toast error is displayed.
		 * 
		 * @param forecast
		 */
		@Override
		public void onForecastLoaded(Forecast forecast) 
		{
			// if the forecast returns with a valid object, set it to the class
			// member and populate the forecast fields. Otherwise toast error.
			if( forecast != null ) 
			{
				_forecast = forecast;
				populateForecast();
			}
			else
			{
				Toast.makeText(getActivity(), R.string.toastNetworkUnavaliable, Toast.LENGTH_LONG).show();				
			}
		}
	}
}
