package org.greatfree.testing.data;

import java.io.Serializable;
import java.util.Date;

/*
 * The class keeps weather data. 02/11/2011, Bing Li
 */

// Created: 02/11/2016, Bing Li
public class Weather implements Serializable
{
	private static final long serialVersionUID = -444619864165469902L;

	private float temperature;
	private String forecast;
	private boolean rain;
	private float howMuchRain;
	private Date time;

	public Weather(float temperature, String forecast, boolean rain, float howMuchRain, Date time)
	{
		this.temperature = temperature;
		this.forecast = forecast;
		this.rain = rain;
		this.howMuchRain = howMuchRain;
		this.time = time;
	}
	
	public void setTemperature(float temperature)
	{
		this.temperature = temperature;
	}

	public float getTemperature()
	{
		return this.temperature;
	}
	
	public void setForecast(String forecast)
	{
		this.forecast = forecast;
	}
	
	public String getForecast()
	{
		return this.forecast;
	}
	
	public void setRain(boolean isRain)
	{
		this.rain = isRain;
	}
	
	public boolean isRain()
	{
		return this.rain;
	}
	
	public void setHowMuchRain(float howMuchRain)
	{
		this.howMuchRain = howMuchRain;
	}
	
	public float getHowMuchRain()
	{
		return this.howMuchRain;
	}
	
	public void setTime(Date time)
	{
		this.time = time;
	}
	
	public Date getTime()
	{
		return this.time;
	}
}
