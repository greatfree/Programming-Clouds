package org.greatfree.testing.client;

import java.io.IOException;

import org.greatfree.client.RemoteReader;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.message.ClientForAnycastRequest;
import org.greatfree.testing.message.ClientForAnycastResponse;
import org.greatfree.testing.message.ClientForBroadcastRequest;
import org.greatfree.testing.message.ClientForBroadcastResponse;
import org.greatfree.testing.message.ClientForUnicastRequest;
import org.greatfree.testing.message.ClientForUnicastResponse;
import org.greatfree.testing.message.MessageConfig;
import org.greatfree.testing.message.SignUpRequest;
import org.greatfree.testing.message.SignUpResponse;
import org.greatfree.testing.message.TestRequest;
import org.greatfree.testing.message.TestResponse;
import org.greatfree.testing.message.WeatherRequest;
import org.greatfree.testing.message.WeatherResponse;

/*
 * The class wraps the class, RemoteReader, to send requests to the remote server and wait until relevant responses are received. 09/22/2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class ClientReader
{
	/*
	 * Send the request of SignUpRequest to the remote server and wait for the response, SignUpResponse. 09/22/2014, Bing Li
	 */
	public static SignUpResponse signUp(String userName, String password)
	{
		try
		{
			return (SignUpResponse)(RemoteReader.REMOTE().read(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new SignUpRequest(userName, password)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			// When the connection gets something wrong, a RemoteReadException and other exceptions must be raised. 09/22/2014, Bing Li
			e.printStackTrace();
		}
		// When reading gets something wrong, a null response is returned. 09/22/2014, Bing Li
		return MessageConfig.NO_SIGN_UP_RESPONSE;
	}

	/*
	 * Send the request of WeatherRequest to the remote server and wait for the response, WeatherResponse. 02/18/2016, Bing Li
	 */
	public static WeatherResponse getWeather()
	{
		try
		{
			return (WeatherResponse)(RemoteReader.REMOTE().read(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new WeatherRequest()));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			// When the connection gets something wrong, a RemoteReadException and other exceptions must be raised. 09/22/2014, Bing Li
			e.printStackTrace();
		}
		return MessageConfig.NO_WEATHER_RESPONSE;
	}

	/*
	 * Send the request of ClientForBroadcastRequest to the remote server and wait for the response, ClientForBroadcastResponse. 09/22/2014, Bing Li
	 */
	public static ClientForBroadcastResponse requestBroadcastly(String message)
	{
		try
		{
			return (ClientForBroadcastResponse)(RemoteReader.REMOTE().read(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new ClientForBroadcastRequest(message)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			// When the connection gets something wrong, a RemoteReadException and other exceptions must be raised. 09/22/2014, Bing Li
			e.printStackTrace();
		}
		// When reading gets something wrong, a null response is returned. 09/22/2014, Bing Li
		return MessageConfig.NO_CLIENT_FOR_BROADCAST_RESPONSE;
	}

	/*
	 * Send the request of ClientForUnicastRequest to the remote server and wait for the response, ClientForUnicastResponse. 09/22/2014, Bing Li
	 */
	public static ClientForUnicastResponse requestUnicastly(String message)
	{
		try
		{
			return (ClientForUnicastResponse)(RemoteReader.REMOTE().read(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new ClientForUnicastRequest(message)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			// When the connection gets something wrong, a RemoteReadException and other exceptions must be raised. 09/22/2014, Bing Li
			e.printStackTrace();
		}
		// When reading gets something wrong, a null response is returned. 09/22/2014, Bing Li
		return MessageConfig.NO_CLIENT_FOR_UNICAST_RESPONSE;
	}

	/*
	 * Send the request of ClientForUnicastRequest to the remote server and wait for the response, ClientForUnicastResponse. 09/22/2014, Bing Li
	 */
	public static ClientForAnycastResponse requestAnycastly(String message)
	{
		try
		{
			return (ClientForAnycastResponse)(RemoteReader.REMOTE().read(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new ClientForAnycastRequest(message)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			// When the connection gets something wrong, a RemoteReadException and other exceptions must be raised. 09/22/2014, Bing Li
			e.printStackTrace();
		}
		// When reading gets something wrong, a null response is returned. 09/22/2014, Bing Li
		return MessageConfig.NO_CLIENT_FOR_ANYCAST_RESPONSE;
	}
	
	public static TestResponse requestTest(String request)
	{
		try
		{
			return (TestResponse)(RemoteReader.REMOTE().read(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new TestRequest(request)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			// When the connection gets something wrong, a RemoteReadException and other exceptions must be raised. 09/22/2014, Bing Li
			e.printStackTrace();
		}
		// When reading gets something wrong, a null response is returned. 09/22/2014, Bing Li
		return MessageConfig.NO_TEST_RESPONSE;
	}
}
