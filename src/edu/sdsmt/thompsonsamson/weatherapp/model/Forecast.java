package edu.sdsmt.thompsonsamson.weatherapp.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.JsonReader;
import android.util.Log;
import edu.sdsmt.thompsonsamson.weatherapp.IListeners;

public class Forecast implements Parcelable
{

	private static final String TAG = "";
	public String Icon;
	public String Temperature;
	public String FeelsLike;
	public String Humidity;
	public String ChancePrecip;
	public String ForecastDate;
	
	// http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
	// NOTE:  See example JSON in doc folder.
	private String _URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" + 
	                      "&ht=t&ht=i&ht=cp&ht=fl&ht=h" + 
	                      "&api_key=zhbc4u58vr5y5zfgpwwd3rfu";
	
	// http://developer.weatherbug.com/docs/read/List_of_Icons
		
	private String _imageURL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";
	
	
	public Bitmap Image;
	
	public Forecast()
	{
		Icon = null;
		Temperature = null;
		FeelsLike = null;
		Humidity = null;
		ChancePrecip = null;
		ForecastDate = null;
		Image = null;
	}

	public Forecast(Parcel parcel)
	{
		// pull values from parcel -- IN ORDER!
		// Zipcode = parcel.readString();
		Image = parcel.readParcelable(Bitmap.class.getClassLoader());
		Icon = null;
		Temperature = null;
		FeelsLike = null;
		Humidity = null;
		ChancePrecip = null;
		ForecastDate = null;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
	}

	public static final Parcelable.Creator<Forecast> Creator = new Parcelable.Creator<Forecast>()
	{
		@Override
		public Forecast createFromParcel(Parcel pc)
		{
			return new Forecast(pc);
		}
		
		@Override
		public Forecast[] newArray(int size)
		{
			return new Forecast[size];
		}
	};

	public class LoadForecast extends AsyncTask<String, Void, Forecast>
	{
		private IListeners _listener;
		private Context _context;

		private int bitmapSampleSize = -1;

		public LoadForecast(Context context, IListeners listener)
		{
			_context = context;
			_listener = listener;
		}

		protected Forecast doInBackground(String... params)
		{
			Forecast forecast = null;
			URL url = null;
			
			// try catch for url
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
					jsonReader.endArray();
					jsonReader.endObject();
				}
				jsonReader.endObject();
				
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

		protected void onPostExecute(Forecast forecast)
		{
			_listener.onForecastLoaded(forecast);
		}

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
