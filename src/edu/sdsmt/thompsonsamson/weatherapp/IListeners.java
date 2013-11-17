/**
* Project Assignment3 Address Book - I Listener Interface
*/
package edu.sdsmt.thompsonsamson.weatherapp;

import edu.sdsmt.thompsonsamson.weatherapp.model.Forecast;
import edu.sdsmt.thompsonsamson.weatherapp.model.ForecastLocation;
/**
* This class provides an interface for the Forecast and Forecast Location classes.
* 
* @ author Brian Butterfield
*/
public interface IListeners
{
	public void onLocationLoaded(ForecastLocation forecastLocation);
	public void onForecastLoaded(Forecast forecast);
	public void onLocationNotLoaded();
	public void onForecastNotLoaded();
}
