package edu.sdsmt.thompsonsamson.weatherapp.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;
import edu.sdsmt.thompsonsamson.weatherapp.IListeners;

/**
 * 
 * @author Andrew Thompson
 * @author Scott Samson
 *
 */
public class Forecast implements Parcelable
{

	private static final String TAG = "Assignment3:Forecast";
	
	public String Icon;
	public String Conditions;
	public String Temperature;
	public String FeelsLike;
	public String Humidity;
	public String ChancePrecip;
	public String ForecastDate;
	public Bitmap Image;
	
	private String _URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" + 
	                      "&ht=t&ht=i&ht=cp&ht=fl&ht=h" + 
	                      "&api_key=zhbc4u58vr5y5zfgpwwd3rfu";
			
	private String _imageURL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";
	
	/**
	 * 
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
	 * 
	 */
	@Override
	public int describeContents()
	{
		return 0;
	}

	/**
	 * 
	 * @param parcel
	 * @param flags
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
	 * 
	 */
	public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>()
	{
		/**
		 * 
		 * @param source
		 * @return
		 */
		@Override
		public Forecast createFromParcel(Parcel source)
		{
			return new Forecast(source);
		}
		
		/**
		 * 
		 * @param size
		 * @return
		 */
		@Override
		public Forecast[] newArray(int size)
		{
			return new Forecast[size];
		}
	};

	/**
	 * 
	 * @author Scott Samson
	 *
	 */
	public class LoadForecast extends AsyncTask<String, Void, Forecast>
	{
		private IListeners _listener;
		private Context _context;
		private int bitmapSampleSize = -1;

		/**
		 * 
		 * @param context
		 * @param listener
		 */
		public LoadForecast(Context context, IListeners listener)
		{
			_context = context;
			_listener = listener;
		}

		/**
		 * 
		 * @param params
		 * @return
		 */
		protected Forecast doInBackground(String... params)
		{
			Forecast forecast = null;
			URL url = null;
			Reader streamReader = null;
			
			// try catch for url
			try 
			{
				url = new URL(String.format(_URL, (Object[]) params));
			} 
			catch (MalformedURLException e1) 
			{
				e1.printStackTrace();
			}
			
			//try catch for reader
			try 
			{
				streamReader = new InputStreamReader(url.openStream());
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

			// load the stream into the json reader
			JsonReader jsonReader = new JsonReader(streamReader);
		
			// try catch for json
			try
			{
				jsonReader.beginObject();
				
				String name = jsonReader.nextName();
				
				if (name.equals("forecastHourlyList") == true)
				{
					jsonReader.beginArray();			
					jsonReader.beginObject();
					while (jsonReader.hasNext())
					{
						//jsonReader.beginObject();
						name = jsonReader.nextName();
						
						if (name.equals("icon") == true)
						{
							Icon = jsonReader.nextString();
							
							// get the bitmap
							Image = readIconBitmap(Icon, bitmapSampleSize);
							
						}
						else if (name.equals("desc") == true)
						{
							Conditions = jsonReader.nextString();
						}
						else if (name.equals("temperature") == true)
						{
							Temperature = jsonReader.nextString();
						}
						else if (name.equals("feelsLike") == true)
						{
							FeelsLike = jsonReader.nextString();
						}else if (name.equals("humidity") == true)
						{
							Humidity = jsonReader.nextString();
						}else if (name.equals("chancePrecip") == true)
						{
							ChancePrecip = jsonReader.nextString();
						}else if (name.equals("dateTime") == true)
						{
							ForecastDate = jsonReader.nextString();
						}else 
						{
							jsonReader.skipValue();
						}
					}
				}
				
				jsonReader.endObject();
				jsonReader.close();				
			}
			catch (IllegalStateException e)
			{
				Log.e(TAG, e.toString() + params[0]);
			}
			catch (Exception e)
			{
				Log.e(TAG, e.toString());
			}
			
			return forecast;
		}
		
		/**
		 * 
		 * @param forecast
		 */
		protected void onPostExecute(Forecast forecast)
		{
			_listener.onForecastLoaded(forecast);
		}

		/**
		 * 
		 * @param conditionString
		 * @param bitmapSampleSize
		 * @return
		 */
		private Bitmap readIconBitmap(String conditionString, int bitmapSampleSize)
		{
			Bitmap iconBitmap = null;
			try
			{
				URL weatherURL = new URL(String.format(_imageURL, conditionString));

				BitmapFactory.Options options = new BitmapFactory.Options();
				if (bitmapSampleSize != -1)
				{
					options.inSampleSize = bitmapSampleSize;
				}

				iconBitmap = BitmapFactory.decodeStream(weatherURL.openStream(), null, options);
			}
			catch (MalformedURLException e)
			{
				Log.e(TAG, e.toString());
			}
			catch (IOException e)
			{
				Log.e(TAG, e.toString());
			}
			catch (Exception e)
			{
				Log.e(TAG, e.toString());
			}

			return iconBitmap;
		}
	}
}
