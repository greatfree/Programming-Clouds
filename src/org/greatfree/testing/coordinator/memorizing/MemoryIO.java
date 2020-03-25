package org.greatfree.testing.coordinator.memorizing;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.greatfree.client.OutMessageStream;
import org.greatfree.client.ServerIO;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.coordinator.CoordinatorMessageProducer;

/*
 * The class is actually an implementation of ServerIO, which serves for the memory nodes which access the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemoryIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the memory server and the coordinator. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/28/2014, Bing Li
	 */
//	public MemoryIO(Socket clientSocket, Sync collaborator, int remoteServerPort)
	public MemoryIO(Socket clientSocket, Sync collaborator)
	{
//		super(clientSocket, collaborator, remoteServerPort);
		super(clientSocket, collaborator);
	}

	/*
	 * A concurrent method to respond the received messages asynchronously. 11/28/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			try
			{
				// Wait and read messages from a memory server. 11/28/2014, Bing Li
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/28/2014, Bing Li
				CoordinatorMessageProducer.SERVER().produceMemoryMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
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
