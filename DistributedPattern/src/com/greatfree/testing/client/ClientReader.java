package com.greatfree.testing.client;

import java.io.IOException;

import com.greatfree.exceptions.RemoteReadException;
import com.greatfree.remote.RemoteReader;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.MessageConfig;
import com.greatfree.testing.message.SignUpRequest;
import com.greatfree.testing.message.SignUpResponse;
import com.greatfree.util.NodeID;

/*
 * The class wraps the class, RemoteReader, to send requests to the remote server and wait until relevant responses are received. 09/22/2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class ClientReader
{
	/*
	 * Send the request of SignUpRequest to the polling server and wait for the response, SignUpResponse. 09/22/2014, Bing Li
	 */
	public static SignUpResponse signUp(String userName, String password)
	{
		// When the connection gets something wrong, a RemoteReadException is raised. 09/22/2014, Bing Li
		try
		{
			return (SignUpResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new SignUpRequest(userName, password)));
		}
		catch (RemoteReadException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		// When reading gets something wrong, a null response is returned. 09/22/2014, Bing Li
		return MessageConfig.NO_SIGN_UP_RESPONSE;
	}
}
