package org.greatfree.testing.cluster.dn;

import java.io.IOException;
import java.net.Socket;

import org.greatfree.admin.AdminConfig;
import org.greatfree.client.ClientPoolSingleton;
import org.greatfree.client.ServerIO;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.util.ServerStatus;

/*
 * The class receives requests/notifications from the coordinator and other DNs to form a cluster. 11/23/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class DNIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the coordinator and the crawling server. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/23/2014, Bing Li
	 */
//	public DNIO(Socket clientSocket, Sync collaborator, int remoteServerPort)
	public DNIO(Socket clientSocket, Sync collaborator)
	{
//		super(clientSocket, collaborator, remoteServerPort);
		super(clientSocket, collaborator);
	}

	/*
	 * Concurrently respond the coordinator's requests/notifications. 11/23/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			// Wait and read messages from the coordinator. 11/23/2014, Bing Li
			try
			{
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/23/2014, Bing Li
				DNMessageProducer.CLUSTER().produceMessage(new MessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (ClassNotFoundException | IOException e)
			{
				if (!ServerStatus.FREE().isServerDown(AdminConfig.COORDINATOR))
				{
					try
					{
						DNIORegistry.REGISTRY().removeIO(this);
						ClientPoolSingleton.SERVER().getPool().removeClient(this.getRemoteServerKey());
					}
					catch (IOException | InterruptedException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
