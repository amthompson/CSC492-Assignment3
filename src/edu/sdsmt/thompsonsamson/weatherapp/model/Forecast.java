package edu.sdsmt.thompsonsamson.weatherapp.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;
import edu.sdsmt.thompsonsamson.weatherapp.IListeners;

/**
 * <p>This is the forecast model object for the weather application. Contained in
 * this object are the current forecast data including temperature, what it
 * feels like (with wind chill, etc), humidity, chance of precipitation, current
 * conditions and an icon depicting the current weather.</p> 
 * 
 * <p>The AsyncTask is included here that will fetch the JSON data from Weatherbug's
 * API, then parse the needed data from it. Since we are using the GetHourlyForecast
 * from Weatherbug, it returns all of the hourly data for the next 7 days. We are
 * only interested in the first one which is the most recent forecast for the current hour.
 * This is done in the background and won't affect the main ui thread.</p>
 * 
 * <p>This object also implements parcelable so it can be placed into an activity's
 * bundle for state retention.</p>
 * 
 * @author Andrew Thompson
 * @author Scott Samson
 */
public class Forecast implements Parcelable
{
	// logging tag
	private static final String TAG = "Assignment3:Forecast";
	
	// class members
	public String Icon;			// the string representing what weather icon to use
	public String Conditions;	// the current conditions
	public String Temperature;	// the temperature
	public String FeelsLike;	// what the temperature actually feels like
	public String Humidity;		// the humidity
	public String ChancePrecip;	// the chance of precipitation
	public String ForecastDate;	// the date (within the hour) of the forecast
	public Bitmap Image;		// the bitmap image representing current conditions
	
	// the URL to Weatherbug's hourly forecast data
	private String _URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" + 
	                      "&ht=t&ht=i&ht=cp&ht=fl&ht=h" + 
	                      "&api_key=zhbc4u58vr5y5zfgpwwd3rfu";
	
	// the URL to Weatherbug's forecast images
	private String _imageURL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";
	
	/**
	 * Initializes forecast data.
	 */
	public Forecast()
	{
		Icon = null;
		Conditions = null;
		Temperature = null;
		FeelsLike = null;
		Humidity = null;
		ChancePrecip = null;
		ForecastDate = null;
		Image = null;
	}

	/**
	 * Reads in forecast data from the parcel object.
	 *  
	 * @param parcel
	 */
	public Forecast(Parcel parcel)
	{
		Icon = parcel.readString();
		Conditions = parcel.readString();
		Temperature = parcel.readString();
		FeelsLike = parcel.readString();
		Humidity = parcel.readString();
		ChancePrecip = parcel.readString();
		ForecastDate = parcel.readString();
		Image = parcel.readParcelable(Bitmap.class.getClassLoader());
	}

	/**
	 * Unused method that must be overridden to implement the Parcelable interface.
	 */
	@Override
	public int describeContents()
	{
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
	public void writeToParcel(Parcel parcel, int flags)
	{
		parcel.writeString(Icon);
		parcel.writeString(Conditions);
		parcel.writeString(Temperature);
		parcel.writeString(FeelsLike);
		parcel.writeString(Humidity);
		parcel.writeString(ChancePrecip);
		parcel.writeString(ForecastDate);
		parcel.writeParcelable(Image, 0);
	}

	/**
	 * Anonymous inner class that creates a parcelable object.  
	 */
	public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>()
	{
		/**
		 * Returns a new Forecast object with data from the parcel.
		 * 
		 * @params source parcel to be loaded from
		 * @return a Forecast object
		 */
		@Override
		public Forecast createFromParcel(Parcel source)	{
			return new Forecast(source);
		}

		/**
		 * Returns an array of Forecast objects of a set size.
		 * 
		 * @params size size of the array to be returned
		 * @return an array of ForecastLocation objects.
		 */
		@Override
		public Forecast[] newArray(int size) {
			return new Forecast[size];
		}
	};

	/**
	 * Anonymous inner class that handles an asynctask to load forecast data.
	 */
	public class LoadForecast extends AsyncTask<String, Void, Forecast> 
	{
		private IListeners _listener;
		private int bitmapSampleSize = -1;
		
		/**
		 * Class constructor. Sets the parent class listener object to the
		 * activity that implemented the listener.
		 * 
		 * @param listener main activity listener
		 */
		public LoadForecast(IListeners listener)
		{
			_listener = listener;
		}

		/**
		 * We create a new URL object and reader to read from the URL. The URL 
		 * string is sent via params.  The next step is to parse the JSON data 
		 * with a JSONReader.  
		 * 
		 * @param params set of parameters for the asynctask
		 * @return a Forecast object
		 */
		protected Forecast doInBackground(String... params)
		{
			Forecast forecast = null;
			URL url = null;
			Reader streamReader = null;
			
			// try catch for url
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
			catch (UnknownHostException e) {
				Log.e(TAG, e.toString());
			}
			catch (IOException e) {
				Log.e(TAG, e.toString());
			}
			catch (Exception e)	{
				Log.e(TAG, e.toString());
			}

			if( streamReader == null) {
				return null;
			}
			
			// load the stream into the json reader
			JsonReader jsonReader = new JsonReader(streamReader);
			forecast = new Forecast();
			
			// try catch for json
			try {
				
				jsonReader.beginObject();
				
				// read in the next name
				String name = jsonReader.nextName();
				
				// if the name is hourly forecast, start parsing
				if (name.equals("forecastHourlyList") == true) {
					
					// read in the next object as a json array
					jsonReader.beginArray();
					jsonReader.beginObject();
					
					// loop through the json array checking names
					while (jsonReader.hasNext()) {

						// read in the name
						name = jsonReader.nextName();
						
						// parse the data we want
						if (name.equals("icon") == true) {
							
							forecast.Icon = jsonReader.nextString();
							
							// get the bitmap
							forecast.Image = readIconBitmap(forecast.Icon, bitmapSampleSize);
						}
						else if (name.equals("desc") == true) {
							forecast.Conditions = jsonReader.nextString();
						}
						else if (name.equals("temperature") == true) {
							forecast.Temperature = jsonReader.nextString();
						}
						else if (name.equals("feelsLike") == true) {
							forecast.FeelsLike = jsonReader.nextString();
						}
						else if (name.equals("humidity") == true) {
							forecast.Humidity = jsonReader.nextString();
						}
						else if (name.equals("chancePrecip") == true) {
							forecast.ChancePrecip = jsonReader.nextString();
						}
						else if (name.equals("dateTime") == true) {
							forecast.ForecastDate = jsonReader.nextString();
						}
						else {
							jsonReader.skipValue();
						}
					}
				}
				
				// cleanup the json objects
				jsonReader.endObject();
				jsonReader.close();				
			}
			catch (IllegalStateException e)	{
				Log.e(TAG, e.toString());
				return null;
			}
			catch (IOException e) {
				Log.e(TAG, e.toString());
				return null;
			}
			catch (Exception e) {
				Log.e(TAG, e.toString());
				return null;
			}
			
			// return the forcast data (or null)
			return forecast;
		}
				
		/**
		 * After the background activity is completed, the forecast is 
		 * returned to the implemented listener.The listener will handle 
		 * if the object is null or not.
		 * 
		 * @param forecast the forecast object returned from the API call
		 */
		protected void onPostExecute(Forecast forecast)
		{
			super.onPostExecute(forecast);
			_listener.onForecastLoaded(forecast);	
		}
		
		/**
		 * This method reads a bitmap from a remote url and returns it as a
		 * bitmap object. This is used to hold the forecast image from
		 * weatherbug's forecast data.
		 * 
		 * @param conditionString the icon to get form weatherbug
		 * @param bitmapSampleSize the size of the image
		 * @return a bitmap object parsed from the url
		 */
		private Bitmap readIconBitmap(String conditionString, int bitmapSampleSize)
		{
			// create a new bitmap object
			Bitmap iconBitmap = null;
			
			// try to get the image from the weatherbug image url
			try {
				URL weatherURL = new URL(String.format(_imageURL, conditionString));

				// build the bitmap if size is greater than -1
				BitmapFactory.Options options = new BitmapFactory.Options();

				if (bitmapSampleSize != -1)
				{
					options.inSampleSize = bitmapSampleSize;
				}

				iconBitmap = BitmapFactory.decodeStream(weatherURL.openStream(), null, options);
			}
			catch (MalformedURLException e) {
				Log.e(TAG, e.toString());
			}
			catch (IOException e) {
				Log.e(TAG, e.toString());
			}
			catch (Exception e) {
				Log.e(TAG, e.toString());
			}

			return iconBitmap;
		}
	}
}
