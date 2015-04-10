package com.greatfree.testing.memory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.greatfree.concurrency.Collaborator;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.remote.ServerIO;

/*
 * The class receives requests/notifications from the coordinator to accomplish the task of data storing and even retrieving. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MemoryIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the coordinator and the memory server. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/27/2014, Bing Li
	 */
	public MemoryIO(Socket clientSocket, Collaborator collaborator)
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
				// Wait and read messages from the coordinator. 11/27/2014, Bing Li
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/27/2014, Bing Li
				MemoryMessageProducer.STORE().produceMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
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
