/**
* Project Assignment3 Address Book - Listener Interface
*/
package edu.sdsmt.thompsonsamson.weatherapp;

import edu.sdsmt.thompsonsamson.weatherapp.model.Forecast;
import edu.sdsmt.thompsonsamson.weatherapp.model.ForecastLocation;

/**
* This class provides an interface for the Forecast and Forecast Location models.
* In particular, methods for handling asnyctask executions. 
*/
public interface IListeners
{
	// location and forecast success
	public void onLocationLoaded(ForecastLocation forecastLocation);
	public void onForecastLoaded(Forecast forecast);
}
