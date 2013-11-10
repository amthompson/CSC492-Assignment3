package edu.sdsmt.thompsonsamson.weatherapp.view;

import android.app.Activity;
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

public class FragmentForecast extends Fragment
{
	public static final String LOCATION_KEY = "key_location";
	public static final String FORECAST_KEY = "key_forecast";
	
	private String ZipCode = null;
	private ForecastLocation _forecastLocation;
	private Forecast _forecast;
	
	
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
	
	
	@Override
	public void onCreate(Bundle argumentsBundle)
	{
		super.onCreate(argumentsBundle);
		argumentsBundle = getArguments();
		
		_forecastLocation = new ForecastLocation();
		_forecast = new Forecast();

		
		// pull location/forecast from bundle
		// if argumentsBundle != null get from bundle (frag destroyed/state saved)
		// else retrieve from host activity
		if (argumentsBundle != null)
		{
			ZipCode = argumentsBundle.getString("ZIP_CODE");
			
		}
		
		
		if( ZipCode != null )
		{
			
			LoadForecastLocation loadForecastLocation = _forecastLocation.new LoadForecastLocation(getActivity(), new HandleAPICallListener());
			loadForecastLocation.execute(ZipCode);	
			
			LoadForecast loadForecast = _forecast.new LoadForecast(getActivity(), new HandleAPICallListener());
			loadForecast.execute(ZipCode);
		}
	}

	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceStateBundle)
	{
		
		// save the location/forecast to bundle
		// savedInstanceStateBundle.putParcelable
		
		super.onSaveInstanceState(savedInstanceStateBundle);
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
	}

	@Override
	public void onDestroy()
	{
		
		super.onDestroy();
	}

	
}