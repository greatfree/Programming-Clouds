package com.greatfree.testing.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.greatfree.concurrency.Collaborator;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.remote.ServerIO;

/*
 * The class is actually an implementation of ServerIO, which serves for clients which access the server. 07/30/2014, Bing Li
 */

// Created: 08/10/2014, Bing Li
public class MyServerIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the client and the server. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/23/2014, Bing Li
	 */
	public MyServerIO(Socket clientSocket, Collaborator collaborator)
	{
		super(clientSocket, collaborator);
	}

	/*
	 * A concurrent method to respond the received messages asynchronously. 08/22/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			try
			{
				// Wait and read messages from a client. 08/22/2014, Bing Li
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 09/20/2014, Bing Li
				MyServerMessageProducer.SERVER().produceMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (SocketException e)
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
		}
	}
}
