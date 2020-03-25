package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.testing.data.Weather;

/*
 * The response contains an instance of Weather. It is returned to a client after it sends out a request, WeatherRequest. 02/15/2016, Bing Li
 */

// Created: 02/15/2016, Bing Li
public class WeatherResponse extends ServerMessage
{
	private static final long serialVersionUID = -5680951714528104272L;
	
	// Declare the instance of Weather. 02/15/2016, Bing Li
	private Weather weather;

	/*
	 * Initialize the response. 02/15/2016, Bing Li
	 */
	public WeatherResponse(Weather weather)
	{
		super(MessageType.WEATHER_RESPONSE);
		this.weather = weather;
	}

	/*
	 * Expose the instance of Weather. 02/15/2016, Bing Li
	 */
	public Weather getWeather()
	{
		return this.weather;
	}
}
