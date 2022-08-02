package org.greatfree.testing.cluster.coordinator.client;

import java.io.IOException;
import java.net.Socket;

import org.greatfree.admin.AdminConfig;
import org.greatfree.client.ClientPoolSingleton;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerIO;
import org.greatfree.testing.cluster.coordinator.CoordinatorMessageProducer;
import org.greatfree.util.ServerStatus;

/*
 * The class is actually an implementation of ServerIO, which serves for the clients which access the coordinator. 11/24/2014, Bing Li
 */

// Created: 11/19/2016, Bing Li
public class ClientIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the client and the coordinator. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/24/2014, Bing Li
	 */
//	public ClientIO(Socket clientSocket, Sync collaborator, int remoteServerPort)
	public ClientIO(Socket clientSocket, Sync collaborator)
	{
//		super(clientSocket, collaborator, remoteServerPort);
		super(clientSocket, collaborator);
	}

	/*
	 * A concurrent method to respond the received messages asynchronously. 11/24/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			// Wait and read messages from a client. 11/24/2014, Bing Li
			try
			{
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/24/2014, Bing Li
				CoordinatorMessageProducer.SERVER().produceClientMessage(new MessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (ClassNotFoundException | IOException e)
			{
				if (!ServerStatus.FREE().isServerDown(AdminConfig.CLIENT_ID))
				{
					try
					{
						// Remove the instance of the shutdown ServerIO. 02/06/2016, Bing Li
						ClientIORegistry.REGISTRY().removeIO(this);
						// Remote the client from the pool. 02/20/2016, Bing Li
						ClientPoolSingleton.SERVER().getPool().removeClient(this.getRemoteServerKey());
					}
					catch (IOException | InterruptedException e1)
					{
						e1.printStackTrace();
					}
				}
				return;
			}
		}
	}
}
