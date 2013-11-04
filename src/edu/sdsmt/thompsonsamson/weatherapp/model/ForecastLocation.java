package edu.sdsmt.thompsonsamson.weatherapp.model;


public class ForecastLocation
{

	private static final String TAG = "";
	
	// http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
	// NOTE:  See example JSON in doc folder.
	private String _URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
			             "&api_key=zhbc4u58vr5y5zfgpwwd3rfu";
	

	public ForecastLocation()
	{
		ZipCode = null;
		City = null;
		State = null;
		Country = null;
		CurrentForecast = null;
	}

	public String ZipCode;
	public String City;
	public String State;
	public String Country;
	public Forecast CurrentForecast;
}
