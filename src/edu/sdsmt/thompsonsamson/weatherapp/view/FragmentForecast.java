package edu.sdsmt.thompsonsamson.weatherapp.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
	private static final String TAG = "Assignment3:FragmentForecast";
	
	// keys for parcelable/bundle data
	public static final String LOCATION_KEY = "key_location";
	public static final String FORECAST_KEY = "key_forecast";
	
	private String ZipCode = null;
	private ForecastLocation _forecastLocation;
	private Forecast _forecast;
	
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
	 * @author Andrew Thompson
	 *
	 */
	public class HandleAPICallListener implements IListeners
	{
		@Override
		public void onLocationLoaded(ForecastLocation forecastLocation) {
			_forecastLocation = forecastLocation;
		}

		@Override
		public void onForecastLoaded(Forecast forecast) {
			_forecast = forecast;
		}
	}
	
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
		
		// define data models for location and forecast
		_forecastLocation = new ForecastLocation();
		_forecast = new Forecast();

		// if the bundled data isn't empty, set the class member
		if (argumentsBundle != null)
		{
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

		// get the location and forecast from api calls
		if( makeAPICalls() )
		{
			populateTextFields();
			Log.d(TAG, "onActivityCreated: " + _forecast.ForecastDate);
		}
		
		// restore data from bundle
		if( savedInstanceStateBundle != null )
		{			
			_forecastLocation = savedInstanceStateBundle.getParcelable(LOCATION_KEY);
			_forecast = savedInstanceStateBundle.getParcelable(FORECAST_KEY);
		}
	}

	/**
	 * 
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * 
	 * @return
	 */
	public boolean makeAPICalls()
	{
		if( ZipCode == null)
		{
			return false;
		}
				
		// make the api call to get the location data
		LoadForecastLocation loadForecastLocation = _forecastLocation.new LoadForecastLocation(getActivity(), new HandleAPICallListener());
		loadForecastLocation.execute(ZipCode);
		
		// wait for the task to complete to set the location
		try 
		{
			loadForecastLocation.get();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		
		// make the api call to get the forecast data
		LoadForecast loadForecast = _forecast.new LoadForecast(getActivity(), new HandleAPICallListener());
		loadForecast.execute(ZipCode);

		// wait for the task to complete to set the forecast
		try 
		{
			loadForecast.get();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param v
	 */
	public void configureTextFields(View v)
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
	 */
	public void populateTextFields()
	{
		// turn the loading screen off
		_loadingScreen.setVisibility(View.GONE);
		
		// set the image
		_imageIcon.setImageBitmap(_forecast.Image);
		
		// populate the text fields
		_textLocation.setText(_forecastLocation.City + ", " + _forecastLocation.State);
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
	 * 
	 * @param timestamp
	 * @return
	 */
	public String formatDateTime(String timestamp)
	{
		Date date = new Date(Long.valueOf(timestamp)); 		
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
		return df.format(date);
	}
}