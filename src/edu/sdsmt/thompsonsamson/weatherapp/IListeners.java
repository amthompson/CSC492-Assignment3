/**
* Project Assignment3 Address Book - Listener Interface
*/
package edu.sdsmt.thompsonsamson.weatherapp;

import edu.sdsmt.thompsonsamson.weatherapp.model.Forecast;
import edu.sdsmt.thompsonsamson.weatherapp.model.ForecastLocation;

/**
* This class provides an interface for the Forecast and Forecast Location models.
* In particular, methods for handling asnyctask executions. If the location or
* forecast execution succeeded, the object is returned back to the implementer.
* If not, methods are called to handle that.
*/
public interface IListeners
{
	// location and forecast success
	public void onLocationLoaded(ForecastLocation forecastLocation);
	public void onForecastLoaded(Forecast forecast);

	// location and forecast failed
	public void onLocationNotLoaded();
	public void onForecastNotLoaded();
}
