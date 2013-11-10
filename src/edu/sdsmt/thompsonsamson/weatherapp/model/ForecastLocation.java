package edu.sdsmt.thompsonsamson.weatherapp.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import edu.sdsmt.thompsonsamson.weatherapp.IListeners;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;


public class ForecastLocation 
{

	private static final String TAG = "";
	
	// http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
	// NOTE:  See example JSON in doc folder.
	private String _URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
			             "&api_key=zhbc4u58vr5y5zfgpwwd3rfu";
	
	// - needs own asynch task
	// - implement parceable interface
	
	public ForecastLocation()
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
	
	public class LoadForecastLocation extends AsyncTask<String, Void, ForecastLocation>
	{
		private IListeners _listener;
		private Context _context;
		
		
		public LoadForecastLocation(Context context, IListeners listener)
		{
			_context = context;
			_listener = listener;
			
		}
		
		@Override
		protected ForecastLocation doInBackground(String... params) {
			ForecastLocation forecastLocation = null;
			URL url = null;
			
			//try catchfor url
			try 
			{
				url = new URL(String.format(_URL, params));
			} 
			catch (MalformedURLException e1) 
			{
				e1.printStackTrace();
			}
			
			Reader streamReader = null;
			//try catch for reader
			try 
			{
				streamReader = new InputStreamReader(url.openStream());
			} 
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (Exception e2)
			{
				Log.d("Assignment3", "general error");
				e2.printStackTrace();
			}
			JsonReader jsonReader = new JsonReader(streamReader);
			
			// try catch to read json
			try 
			{
				jsonReader.beginObject();
				
				String name = jsonReader.nextName();
				
				if (name.equals("location") == true)
				{
					jsonReader.beginObject();
					
					while (jsonReader.hasNext())
					{
						name = jsonReader.nextName();
						
						if (name.equals("city") == true)
						{
							City = jsonReader.nextString();
						}
						else if (name.equals("state") == true)
						{
							State = jsonReader.nextString();
						}
						else if (name.equals("country") == true)
						{
							Country = jsonReader.nextString();
						}else if (name.equals("zipCode") == true)
						{
							ZipCode = jsonReader.nextString();
						}else 
						{
							jsonReader.skipValue();
						}
					}
				}
			}
			catch (IllegalStateException e)
			{
				Log.e(TAG, e.toString() + params[0]);
			}
			catch (Exception e)
			{
				Log.e(TAG, e.toString());
			}
			
			return forecastLocation;
			
		}
		
		@Override
		protected void onPostExecute(ForecastLocation forecastLocation)
		{
			super.onPostExecute(forecastLocation);
			
			Log.d("Assignemet3", "onPostExecute, ForecastLocation");
			
			_listener.onLocationLoaded(forecastLocation);
		}
		
	}
}
