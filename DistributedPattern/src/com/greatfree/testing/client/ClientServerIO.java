package com.greatfree.testing.client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.greatfree.concurrency.Collaborator;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.remote.ServerIO;

/*
 * This is an implementation of ServerIO to receive and even respond remote ends. In this case of the client, it only receives feedbacks (notifications) from a remote server to set up the local ObjectInputStream. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class ClientServerIO extends ServerIO
{
	// Initialize the server IO. 11/07/2014, Bing Li
	public ClientServerIO(Socket clientSocket, Collaborator collaborator)
	{
		super(clientSocket, collaborator);
	}

	/*
	 * A concurrent running thread to receive and respond the received messages asynchronously. 11/07/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			try
			{
				// Wait and read messages from a client. 11/07/2014, Bing Li
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/07/2014, Bing Li
				ClientServerMessageProducer.CLIENT().produceMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
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
