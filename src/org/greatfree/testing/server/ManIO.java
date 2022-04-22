package org.greatfree.testing.server;

import java.io.IOException;
import java.net.Socket;

import org.greatfree.client.ServerIO;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;

/*
 * The class is actually an implementation of ServerIO, which serves for the administrator which interacts with the server. 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
class ManIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the administrator and the server. The server is shared with other IOs to control the count of ServerIOs instances. 01/20/2016, Bing Li
	 */
//	public ManIO(Socket clientSocket, Sync collaborator, int remoteServerPort)
	public ManIO(Socket clientSocket, Sync collaborator)
	{
//		super(clientSocket, collaborator, remoteServerPort);
		super(clientSocket, collaborator);
	}

	/*
	 * A concurrent method to respond the received messages asynchronously. 01/20/2016, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			// Wait and read messages from a client. 01/20/2016, Bing Li
			try
			{
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 01/20/2016, Bing Li
				ServerMessageProducer.SERVER().produceMessage(new MessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (ClassNotFoundException | IOException e)
			{
				try
				{
					// Remove and dispose the ServerIO. 01/20/2016, Bing Li
					ManIORegistry.REGISTRY().removeIO(this);
				}
				catch (IOException | InterruptedException ex)
				{
				}
				return;
			}
		}
	}
}
