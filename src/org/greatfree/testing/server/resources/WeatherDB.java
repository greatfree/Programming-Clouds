package org.greatfree.testing.server.resources;

import org.greatfree.testing.data.Weather;

/*
 * This is a singleton class that keeps the weather information. 02/11/2016, Bing Li
 */

// Created: 02/11/2016, Bing Li
public class WeatherDB
{
	// Declare one instance of Weather. 02/15/2016, Bing Li
	private Weather weather;

	/*
	 * A singleton implementation. 02/15/2016, Bing Li
	 */
	private WeatherDB()
	{
	}
	
	private static WeatherDB instance = new WeatherDB();
	
	public static WeatherDB SERVER()
	{
		if (instance == null)
		{
			instance = new WeatherDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the instance. 02/15/2016, Bing Li
	 */
	public void dispose()
	{
		this.weather = null;
	}

	/*
	 * Set the instance of Weather. 02/15/2016, Bing Li
	 */
	public void setWeather(Weather weather)
	{
		// Check whether the instance is existed. 02/15/2016, Bing Li
		if (this.weather == null)
		{
			// Initialize an instance if it is not existed. 02/15/2016, Bing Li
			this.weather = new Weather(weather.getTemperature(), weather.getForecast(), weather.isRain(), weather.getHowMuchRain(), weather.getTime());
		}
		else
		{
			// Reset the value if the instance is existed. 02/15/2016, Bing Li
			this.weather.setTemperature(weather.getTemperature());
			this.weather.setForecast(weather.getForecast());
			this.weather.setRain(weather.isRain());
			this.weather.setHowMuchRain(weather.getHowMuchRain());
			this.weather.setTime(weather.getTime());
		}
	}

	/*
	 * Get the value of the instance of Weather. 02/15/2016, Bing Li
	 */
	public Weather getWeather()
	{
		return this.weather;
	}
}
