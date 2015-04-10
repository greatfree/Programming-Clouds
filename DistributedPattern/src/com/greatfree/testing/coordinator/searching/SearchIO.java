package com.greatfree.testing.coordinator.searching;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.greatfree.concurrency.Collaborator;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.remote.ServerIO;
import com.greatfree.testing.coordinator.CoordinatorMessageProducer;

/*
 * The class is actually an implementation of ServerIO, which serves for the searchers which access the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the searchers and the coordinator. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/29/2014, Bing Li
	 */
	public SearchIO(Socket clientSocket, Collaborator collaborator)
	{
		super(clientSocket, collaborator);
	}

	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			try
			{
				// Wait and read messages from a search client. 11/29/2014, Bing Li
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/29/2014, Bing Li
				CoordinatorMessageProducer.SERVER().produceSearchMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
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
