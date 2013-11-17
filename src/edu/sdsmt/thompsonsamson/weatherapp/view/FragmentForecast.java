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
	//private static final String TAG = "Assignment3:FragmentForecast";
	
	// keys for parcelable/bundle data
	public static final String LOCATION_KEY = "key_location";
	public static final String FORECAST_KEY = "key_forecast";
	
	private String ZipCode = null;						// zipcode to get forecast
	private ForecastLocation _forecastLocation;			// object location of forecast
	private Forecast _forecast;							// object for weather forecast

	private HandleWebCallListener _webRequest;			// class to handle asynctasks
	private LoadForecastLocation _loadForecastLocation;	// api call for location
	private LoadForecast _loadForecast;					// api call for forecast
	
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
	 * Creates the forecast and forecast location model objects.  If the bundle is not 
	 * null ZipCode is loaded from it.
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
		if (argumentsBundle != null) {
			ZipCode = argumentsBundle.getString("ZIP_CODE");
		}
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
		
		// save location nd forecast to the bundle
		savedInstanceStateBundle.putParcelable(LOCATION_KEY, _forecastLocation);
		savedInstanceStateBundle.putParcelable(FORECAST_KEY, _forecast);
	}

	/**
	 * Creates the view and sets the layout.  Objects are inflated to their position specified
	 * in the R file with the object inflator paramter.
	 * 
	 * @param inflater inflates fragment according to the layout specified
	 * @param container container for a group of views
	 * @param savedInstanceStateBundle bundle of data from previous instances
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_forecast, null);
		
		// setup ui objects
		configureTextFields(rootView);
		
		// check network connection
		if ( !networkOnline() ) {
			Toast.makeText(getActivity(), R.string.toastNetworkUnavaliable, Toast.LENGTH_LONG).show();
		}
		
		return rootView;
	}
	
	/**
	 * Creates the activity and gets forecast and forecast location data from the api.  
	 * We will notify the user if there is no network connectivity.  Data is loaded 
	 * from previous instances from the bundle here also.
	 * 
	 * @param savedInstanceStateBundle bundle to load data from
	 * @param v The view to display the objects in
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceStateBundle) {
		super.onActivityCreated(savedInstanceStateBundle);
		
		// restore data from bundle
		if( savedInstanceStateBundle != null ) {			
			_forecastLocation = savedInstanceStateBundle.getParcelable(LOCATION_KEY);
			_forecast = savedInstanceStateBundle.getParcelable(FORECAST_KEY);
		}
	}

	/**
	 * Calls onDestroy in the parent object.
	 */
	@Override
	public void onPause() {
		super.onPause();
		stopTasks();
	}

	/**
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		if( ZipCode != null) {
	
			// make the api call to get the location data
			_loadForecastLocation = _forecastLocation.new LoadForecastLocation(_webRequest);
			_loadForecastLocation.execute(ZipCode);
	
			// make the api call to get the forecast data
			_loadForecast = _forecast.new LoadForecast(_webRequest);
			_loadForecast.execute(ZipCode);		
		}
	}

	/**
	 * 
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTasks();
	}

	/**
	 * Sets up the textViews for the view.  This hides the view while loading data and sets 
	 * up the objects.
	 * 
	 * @param v The view to display the objects in
	 */
	private void configureTextFields(View v) {
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
	 * Returns true or false based on the device's network connectivity.
	 * Sets up the textViews for the view.  This hides the view while loading data and sets 
	 * up the objects.
	 * 
	 * @return true return true if network connected
	 * @return false return false if network not connected
	 */
	private boolean networkOnline() {
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 */
	private void stopTasks() {
		// if the asynctasks are still running, kill it
		if( _loadForecastLocation.getStatus() == Status.RUNNING) {
			_loadForecastLocation.cancel(true);
		}
		
		if( _loadForecast.getStatus() == Status.RUNNING) {
			_loadForecast.cancel(true);
		}	
	}

	/**
	 * Takes a string with the number of seconds in epoch time and returns a string with the time
	 * in a date/time format based on the the timestamp parameter.  
	 * 
	 * @param timestamp number of seconds since 1/1/1970
	 * @return the current time in a date/time format
	 */
	private String formatDateTime(String timestamp) {
		Date date = new Date(Long.valueOf(timestamp)); 	
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mm a", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
		return dateFormat.format(date);
	}
	
	/**
	 * Handles the api calls to the model objects _forecast and _forecastLocation.
	 * This implements the IListeners interface.
	 * 
	 * @author Andrew Thompson
	 *
	 */
	public class HandleWebCallListener implements IListeners {
		
		/**
		 * Sets forecast location data from the api call to the screen
		 * location element.
		 * @param forecastLocation
		 */
		@Override
		public void onLocationLoaded(ForecastLocation forecastLocation) {
			_forecastLocation = forecastLocation;
			
			if( forecastLocation.City != null ) {
				_textLocation.setText(_forecastLocation.City + ", " + _forecastLocation.State);
			}
		}
	
		/**
		 * Sets forecast data from the api call to the forecast screen 
		 * elements if it was loaded correctly.
		 * @param forecast
		 * @return
		 */
		@Override
		public void onForecastLoaded(Forecast forecast) {
			_forecast = forecast;
	
			if( forecast.ForecastDate != null ) {
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
		}
	
		/**
		 * 
		 */
		@Override
		public void onLocationNotLoaded() {
			Toast.makeText(getActivity(), R.string.toastNetworkUnavaliable, Toast.LENGTH_LONG).show();
		}
	
		/**
		 * 
		 */
		@Override
		public void onForecastNotLoaded() {
			Toast.makeText(getActivity(), R.string.toastNetworkUnavaliable, Toast.LENGTH_LONG).show();
		}
	}
}
