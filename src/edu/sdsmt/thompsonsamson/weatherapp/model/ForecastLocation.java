package edu.sdsmt.thompsonsamson.weatherapp.model;

import android.os.Parcel;
import android.os.Parcelable;


public class ForecastLocation implements Parcelable
{

	private static final String TAG = "";
	
	// http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
	// NOTE:  See example JSON in doc folder.
	private String _URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
			             "&api_key=zhbc4u58vr5y5zfgpwwd3rfu";
	
	// - needs own asynch task
	// - implement parceable interface
	
	public ForecastLocation(Parcel parcel)
	{
		ZipCode = null;
		City = null;
		State = null;
		Country = null;
	}

	public String ZipCode;
	public String City;
	public String State;
	public String Country;

	@Override
	public int describeContents() 
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		// TODO Auto-generated method stub
		
	}
}
