package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.testing.data.Weather;

/*
 * The notification contains the data of weather. It is sent to a server such that the data on the server can be updated. 02/06/2016, Bing Li
 */

// Created: 02/06/2016, Bing Li
public class WeatherNotification extends ServerMessage
{
	private static final long serialVersionUID = 3555195575233260451L;

	private Weather weather;
	
	public WeatherNotification(Weather weather)
	{
		super(MessageType.WEATHER_NOTIFICATION);
		this.weather = weather;
	}

	public Weather getWeather()
	{
		return this.weather;
	}
}
