package org.greatfree.testing.client;

import java.util.Calendar;

import org.greatfree.data.ClientConfig;
import org.greatfree.testing.data.Weather;
import org.greatfree.testing.message.SignUpResponse;
import org.greatfree.testing.message.WeatherResponse;

/*
 * The class aims to print a menu list on the screen for users to interact with the client and communicate with the polling server. The menu is unique in the client such that it is implemented in the pattern of a singleton. 09/21/2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class ClientUI
{
	/*
	 * Initialize. 09/21/2014, Bing Li
	 */
	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 09/21/2014, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI FACE()
	{
		if (instance == null)
		{
			instance = new ClientUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Print the menu list on the screen. 09/21/2014, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.SIGN_UP);
		System.out.println(ClientMenu.SET_WEATHER);
		System.out.println(ClientMenu.GET_WEATHER);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	/*
	 * Send the users' option to the polling server. 09/21/2014, Bing Li
	 */
	public void send(int option)
	{
		SignUpResponse signUpResponse;
		WeatherResponse weatherResponse;
		Weather weather;

		// Check the option to interact with the polling server. 09/21/2014, Bing Li
		switch (option)
		{
			// If the SIGN_UP option is selected, send the request message to the remote server. 09/21/2014, Bing Li
			case MenuOptions.SIGN_UP:
				signUpResponse = ClientReader.signUp(ClientConfig.USERNAME, ClientConfig.PASSWORD);
				System.out.println(signUpResponse.isSucceeded());
				break;

			// If the SET_WEATHER option is selected, send the notification to the remote server. 02/18/2016, Bing Li
			case MenuOptions.SET_WEATHER:
				ClientEventer.NOTIFY().notifyWeather(new Weather(20.4f, "Sunshine", false, 10.0f, Calendar.getInstance().getTime()));
				break;
				
			// If the GET_WEATHER option is selected, send the request message to the remote server. 02/18/2016, Bing Li
			case MenuOptions.GET_WEATHER:
				weatherResponse = ClientReader.getWeather();
				weather = weatherResponse.getWeather();
				System.out.println("Temperature: " + weather.getTemperature());
				System.out.println("Forcast: " + weather.getForecast());
				System.out.println("Rain: " + weather.isRain());
				System.out.println("How much rain: " + weather.getHowMuchRain());
				System.out.println("Time: " + weather.getTime());
				break;

			// If the quit option is selected, send the notification message to the remote server. 09/21/2014, Bing Li
			case MenuOptions.QUIT:
				break;
		}
	}
}
