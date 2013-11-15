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
import android.util.Log;
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
	
	private String ZipCode = null;
	private ForecastLocation _forecastLocation;
	private Forecast _forecast;

	private HandleWebCallListener _webRequest;
	private LoadForecastLocation _loadForecastLocation;
	private LoadForecast _loadForecast;
	
	private ScrollView _forecastData;
	private RelativeLayout _loadingScreen;
	
	private ImageView _imageIcon;
	private TextView _textLocation;
	private TextView _textConditions;
	private TextView _textTemperature;
	private TextView _textFeelsLike;
	private TextView _textHumidity;
	private TextView _textPrecip;
	private TextView _textTime;
	
	/**
	 * 
	 * @param argumentsBundle Bundle data passed from main activity to fragment
	 */
	@Override
	public void onCreate(Bundle argumentsBundle)
	{
		super.onCreate(argumentsBundle);
		
		// get the bundle data saved to the fragment
		argumentsBundle = getArguments();
		
		_webRequest = new HandleWebCallListener();
		
		// define data models for location and forecast
		_forecastLocation = new ForecastLocation();
		_forecast = new Forecast();

		// if the bundled data isn't empty, set the class member
		if (argumentsBundle != null) {
			ZipCode = argumentsBundle.getString("ZIP_CODE");
		}
	}

	/**
	 * 
	 * @param savedInstanceStateBundle
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceStateBundle)
	{		
		super.onSaveInstanceState(savedInstanceStateBundle);
		
		// save location to the bundle
		savedInstanceStateBundle.putParcelable(LOCATION_KEY, _forecastLocation);
		savedInstanceStateBundle.putParcelable(FORECAST_KEY, _forecast);
	}

	/**
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceStateBundle
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
	 * 
	 * @param savedInstanceStateBundle
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceStateBundle)
	{
		super.onActivityCreated(savedInstanceStateBundle);
		
		// restore data from bundle
		if( savedInstanceStateBundle != null ) {			
			_forecastLocation = savedInstanceStateBundle.getParcelable(LOCATION_KEY);
			_forecast = savedInstanceStateBundle.getParcelable(FORECAST_KEY);
		}
	}

	/**
	 * 
	 */
	@Override
	public void onPause()
	{
		super.onPause();
		stopTasks();
	}

	/**
	 * 
	 */
	@Override
	public void onResume()
	{
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
	public void onDestroy()
	{
		super.onDestroy();
		stopTasks();
	}

	/**
	 * 
	 * @param v
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
	 * 
	 * @return
	 */
	private boolean networkOnline() 
	{
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
	private void stopTasks() 
	{
		// if the asynctasks are still running, kill it
		if( _loadForecastLocation.getStatus() == Status.RUNNING) {
			_loadForecastLocation.cancel(true);
		}
		
		if( _loadForecast.getStatus() == Status.RUNNING) {
			_loadForecast.cancel(true);
		}	
	}

	/**
	 * 
	 * @param timestamp
	 * @return
	 */
	private String formatDateTime(String timestamp)
	{
		Date date = new Date(Long.valueOf(timestamp)); 	
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mm a", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
		return dateFormat.format(date);
	}

	/**
	 * 
	 * @author Andrew Thompson
	 *
	 */
	public class HandleWebCallListener implements IListeners
	{
		/**
		 * 
		 * @param forecastLocation
		 */
		@Override
		public void onLocationLoaded(ForecastLocation forecastLocation) 
		{
			_forecastLocation = forecastLocation;
			
			if( forecastLocation.City != null ) {
				_textLocation.setText(_forecastLocation.City + ", " + _forecastLocation.State);
			}
		}
	
		/**
		 * 
		 * @param forecast
		 */
		@Override
		public void onForecastLoaded(Forecast forecast) 
		{
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
	
		@Override
		public void onLocationNotLoaded()
		{
			Toast.makeText(getActivity(), R.string.toastNetworkUnavaliable, Toast.LENGTH_LONG).show();
		}
	
		@Override
		public void onForecastNotLoaded()
		{
			Toast.makeText(getActivity(), R.string.toastNetworkUnavaliable, Toast.LENGTH_LONG).show();
		}
	}
}