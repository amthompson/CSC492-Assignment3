package edu.sdsmt.thompsonsamson.weatherapp;

import edu.sdsmt.thompsonsamson.weatherapp.model.Forecast;
import edu.sdsmt.thompsonsamson.weatherapp.model.ForecastLocation;

public interface IListeners
{
	public void onLocationLoaded(ForecastLocation forecastLocation);
	public void onForecastLoaded(Forecast forecast);
	public void onLocationNotLoaded();
	public void onForecastNotLoaded();
}
