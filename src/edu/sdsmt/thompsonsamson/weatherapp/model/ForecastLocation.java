/**
* Project Assignment3 Weather App - Forecast Location
*/
package edu.sdsmt.thompsonsamson.weatherapp.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import edu.sdsmt.thompsonsamson.weatherapp.IListeners;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

/**
 * Model class for forcast loction.  We implement the Parcelable interface in order to be able
 * to save data in the bundle.  This class is responsible for running the async task to load the 
 * forcast data from the weatherbug api.  
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
	 * Initializes forecast location data.
	 */
	public ForecastLocation()
	{
		ZipCode = null;
		City = null;
		State = null;
		Country = null;
	}
	
	/**
	 * Reads in forecast location data from the parcel.
	 * 
	 * @param parcel parcel to read data from
	 */
	public ForecastLocation(Parcel parcel)
	{
		ZipCode = parcel.readString();
		City = parcel.readString();
		State = parcel.readString();
		Country = parcel.readString();
	}
	
	/**
	 * Unused method that must be overridden to implement the Parcelable interface.
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Override to save ForecastLocation objects to a bundle.  Requried to implement the parcelable 
	 * interface.  
	 * 
	 * @params parcel parcel to be written to
	 * @params flags optional flags to set
	 */
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(ZipCode);
		parcel.writeString(City);
		parcel.writeString(State);
		parcel.writeString(Country);
	}

	/**
	 * Anonymous inner class that creates a parcelable object.  
	 */
	public static final Parcelable.Creator<ForecastLocation> CREATOR = new Parcelable.Creator<ForecastLocation>() 
	{
		/**
		 * Returns a new ForecastLocation with data from the parcel.
		 * 
		 * @params source parcel to be loaded from
		 */
		@Override
		public ForecastLocation createFromParcel(Parcel source) {
			return new ForecastLocation(source);
		}

		/**
		 * Returns an array of ForecastLocations in length equal to the size param.
		 * 
		 * @params size size of the array to be returned
		 */
		@Override
		public ForecastLocation[] newArray(int size) {
			return new ForecastLocation[size];
		}
	};
	
	/**
	 * Class to load forecast location data.  
	 * 
	 * @author Scott Samson
	 *
	 */
	public class LoadForecastLocation extends AsyncTask<String, Integer, ForecastLocation>
	{
		private IListeners _listener;
		
		/**
		 * Loads the listener from main activity.
		 * 
		 * @param context main acivity context
		 * @param listener main activity lisetener
		 */
		public LoadForecastLocation(IListeners listener)
		{
			_listener = listener;
		}
		
		/**
		 * Anonymous inner class used to do async task.  We create a new URL object and reader
		 * to read from the URL.  The URL string is sent via params.  The next step is to parse
		 * the JSON data with a JSONReader.  
		 * 
		 * @param params set of parameters for the async task
		 * 
		 */
		@Override
		protected ForecastLocation doInBackground(String... params) {
			ForecastLocation forecastLocation = null;
			URL url = null;
			Reader streamReader = null;
			
			//try catch for url
			try {
				url = new URL(String.format(_URL, (Object[]) params));
			} 
			catch (MalformedURLException e) {
				Log.e(TAG, e.toString());
			}
				
			//try catch for reader
			try {
				streamReader = new InputStreamReader(url.openStream());
			} 
			catch (IOException e) {
				Log.e(TAG, e.toString());
			}
			catch (Exception e) {
				Log.e(TAG, e.toString());
			}

			if( streamReader == null) {
				return null;
			}
			
			// set the reader to the stream
			JsonReader jsonReader = new JsonReader(streamReader);
			forecastLocation = new ForecastLocation();
			
			// try catch to read json
			try	{
				
				// start at the first object
				jsonReader.beginObject();
				
				// get the first node name
				String name = jsonReader.nextName();
				
				// if the node is location, get the data
				if (name.equals("location") == true) {
					
					// start at the first object in location
					jsonReader.beginObject();
					
					// fill the class member with json data
					while (jsonReader.hasNext())
					{
						name = jsonReader.nextName();
						
						if (name.equals("city") == true) {
							forecastLocation.City = jsonReader.nextString();
						}
						else if (name.equals("state") == true) {
							forecastLocation.State = jsonReader.nextString();
						}
						else if (name.equals("country") == true) {
							forecastLocation.Country = jsonReader.nextString();
						}
						else if (name.equals("zipCode") == true) {
							forecastLocation.ZipCode = jsonReader.nextString();
						}
						else {
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
			catch (IllegalStateException e) {
				Log.e(TAG, e.toString());
			}
			catch (Exception e)	{
				Log.e(TAG, e.toString());
			}
			
			// return the object to parent class			
			return forecastLocation;
		}
		
		/**
		 * Returns the forecast location to the main activity listener
		 * 
		 * @param forecastLocation the location returned from the API call
		 */
		@Override
		protected void onPostExecute(ForecastLocation forecastLocation)
		{
			super.onPostExecute(forecastLocation);
			
			if( forecastLocation == null )
			{
				_listener.onLocationNotLoaded();
			}
			else 
			{
				_listener.onLocationLoaded(forecastLocation);	
			}
			
		}
	}
}
