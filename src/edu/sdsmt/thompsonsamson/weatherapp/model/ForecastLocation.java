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
 * <p>Model class for forecast location.  We implement the Parcelable interface in order to be able
 * to save data in the bundle.  This class is responsible for running the async task to load the 
 * forecast data from the weatherbug api.</p>
 * 
 * @author Andrew Thompson
 * @author Scott Samson
 *
 */
public class ForecastLocation implements Parcelable
{
	// logging tag
	private static final String TAG = "Assignment3:ForecastLocation";
	
	// class members
	public String ZipCode;	// location zip code
	public String City;		// the city the location is at
	public String State;	// the state the location is as
	public String Country;	// the country the location is at
	
	// weatherbug api url
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
	 * Reads in forecast location data from the parcel object.
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
	 * Override to save ForecastLocation objects to a bundle.  Required to implement the parcelable 
	 * interface.  
	 * 
	 * @param parcel parcel to be written to
	 * @param flags optional flags to set
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
		 * Returns a new ForecastLocation object with data from the parcel.
		 * 
		 * @params source parcel to be loaded from
		 * @return a ForecastLocation object
		 */
		@Override
		public ForecastLocation createFromParcel(Parcel source) {
			return new ForecastLocation(source);
		}

		/**
		 * Returns an array of ForecastLocations of a set size.
		 * 
		 * @params size size of the array to be returned
		 * @return an array of ForecastLocation objects.
		 */
		@Override
		public ForecastLocation[] newArray(int size) {
			return new ForecastLocation[size];
		}
	};
	
	/**
	 * Anonymous inner class that handles an asynctask to load forecast location data.
	 */
	public class LoadForecastLocation extends AsyncTask<String, Void, ForecastLocation>
	{
		private IListeners _listener;
		
		/**
		 * Class constructor. Sets the parent class listener object to the
		 * activity that implemented the listener.
		 * 
		 * @param listener main activity listener
		 */
		public LoadForecastLocation(IListeners listener)
		{
			_listener = listener;
		}
		
		/**
		 * We create a new URL object and reader to read from the URL. The URL 
		 * string is sent via params.  The next step is to parse the JSON data 
		 * with a JSONReader.  
		 * 
		 * @param params set of parameters for the asynctask
		 * @return A ForecastLocation object
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
				return null;
			}
			catch (Exception e)	{
				Log.e(TAG, e.toString());
				return null;
			}
			
			// return the object to parent class			
			return forecastLocation;
		}
		
		/**
		 * After the background activity is completed, the forecast location 
		 * is returned to the implemented listener.The listener will handle 
		 * if the object is null or not.
		 * 
		 * @param forecastLocation the location object returned from the API call
		 */
		@Override
		protected void onPostExecute(ForecastLocation forecastLocation)
		{
			super.onPostExecute(forecastLocation);			
			_listener.onLocationLoaded(forecastLocation);	
		}
	}
}
