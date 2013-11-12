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

/**
 * 
 * @author Andrew Thompson
 * @author Scott Samson
 *
 */
public class ForecastLocation implements Parcelable
{
	private static final String TAG = "Assignment3:ForecastLocation";
	
	public String ZipCode;
	public String City;
	public String State;
	public String Country;
	
	private String _URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
			             "&api_key=zhbc4u58vr5y5zfgpwwd3rfu";
		
	/**
	 * 
	 */
	public ForecastLocation()
	{
		ZipCode = null;
		City = null;
		State = null;
		Country = null;
	}
	
	/**
	 * 
	 * @param parcel
	 */
	public ForecastLocation(Parcel parcel)
	{
		ZipCode = parcel.readString();
		City = parcel.readString();
		State = parcel.readString();
		Country = parcel.readString();
	}
	
	/**
	 * 
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * 
	 */
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(ZipCode);
		parcel.writeString(City);
		parcel.writeString(State);
		parcel.writeString(Country);
	}

	/**
	 * 
	 */
	public static final Parcelable.Creator<ForecastLocation> CREATOR = new Parcelable.Creator<ForecastLocation>() 
	{
		@Override
		public ForecastLocation createFromParcel(Parcel source) {
			return new ForecastLocation(source);
		}

		@Override
		public ForecastLocation[] newArray(int size) {
			return new ForecastLocation[size];
		}
	};
	
	/**
	 * 
	 * @author Scott Samson
	 *
	 */
	public class LoadForecastLocation extends AsyncTask<String, Void, ForecastLocation>
	{
		//private Context _context;
		private IListeners _listener;
		
		/**
		 * 
		 * @param context
		 * @param listener
		 */
		public LoadForecastLocation(Context context, IListeners listener)
		{
			//_context = context;
			_listener = listener;
		}
		
		/**
		 * 
		 * @param params
		 * 
		 */
		@Override
		protected ForecastLocation doInBackground(String... params) {
			ForecastLocation forecastLocation = null;
			URL url = null;
			Reader streamReader = null;
			
			//try catch for url
			try 
			{
				url = new URL(String.format(_URL, (Object[]) params));
			} 
			catch (MalformedURLException e) 
			{
				Log.e(TAG, "MalformedURLException: " + e.toString());
			}
			
			//try catch for reader
			try 
			{
				streamReader = new InputStreamReader(url.openStream());
			} 
			catch (IOException e)
			{
				Log.e(TAG, "IOException: " + e.toString());
			}
			catch (Exception e)
			{
				Log.e(TAG, "Exception: " + e.toString());
			}
			
			// set the reader to the stream
			JsonReader jsonReader = new JsonReader(streamReader);
			
			// try catch to read json
			try 
			{
				// start at the first object
				jsonReader.beginObject();
				
				// get the first node name
				String name = jsonReader.nextName();
				
				// if the node is location, get the data
				if (name.equals("location") == true)
				{
					// start at the first object in location
					jsonReader.beginObject();
					
					// fill the class member with json data
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
					
					// end location object
					jsonReader.endObject();
				}
				
				// end parent object
				jsonReader.endObject();
				
				// close the reader
				jsonReader.close();
			}
			catch (IllegalStateException e)
			{
				Log.e(TAG, "IllegalStateException: " + e.toString());
			}
			catch (Exception e)
			{
				Log.e(TAG, "Exception: " + e.toString());
			}
			
			// return the object to parent class			
			return forecastLocation;
		}
		
		/**
		 * Returns the forecast location to the listener
		 * @param forecastLocation the location returned from the API call
		 */
		@Override
		protected void onPostExecute(ForecastLocation forecastLocation)
		{
			super.onPostExecute(forecastLocation);
			_listener.onLocationLoaded(forecastLocation);
		}
	}
}