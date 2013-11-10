package edu.sdsmt.thompsonsamson.weatherapp.view;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	public static final String LOCATION_KEY = "key_location";
	public static final String FORECAST_KEY = "key_forecast";
	
	private String ZipCode = null;
	private ForecastLocation _forecastLocation;
	private Forecast _forecast;
	
	/**
	 * 
	 * @author Andrew Thompson
	 *
	 */
	public class HandleAPICallListener implements IListeners
	{
		@Override
		public void onLocationLoaded(ForecastLocation forecastLocation) {
			Log.d("Assignment3", "onLocationLoaded");
			_forecastLocation = forecastLocation;		
		}

		@Override
		public void onForecastLoaded(Forecast forecast) {
			Log.d("Assignment3", "onforecastLoaded");
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
		
		// if the zip code isn't null, get the location and forecast
		if( ZipCode != null )
		{	
			// make the api call to get the location data
			LoadForecastLocation loadForecastLocation = _forecastLocation.new LoadForecastLocation(getActivity(), new HandleAPICallListener());
			loadForecastLocation.execute(ZipCode);	
			
			// make the api call to get the forecast data
			LoadForecast loadForecast = _forecast.new LoadForecast(getActivity(), new HandleAPICallListener());
			loadForecast.execute(ZipCode);
		}
	}

	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceStateBundle)
	{		
		super.onSaveInstanceState(savedInstanceStateBundle);
		
		Log.d("Assignment3","Save Parcelable");
		
		// save location to the bundle
		savedInstanceStateBundle.putParcelable(LOCATION_KEY, _forecastLocation);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_forecast, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceStateBundle)
	{
		super.onActivityCreated(savedInstanceStateBundle);
		
		if( savedInstanceStateBundle != null )
		{
			Log.d("Assignment3","Get Parcelable");
			
			_forecastLocation = savedInstanceStateBundle.getParcelable(LOCATION_KEY);
		}
		
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	
}