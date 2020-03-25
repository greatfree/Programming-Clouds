package org.greatfree.testing.cluster.coordinator.dn;

import java.io.IOException;
import java.net.Socket;

import org.greatfree.admin.AdminConfig;
import org.greatfree.client.OutMessageStream;
import org.greatfree.client.ServerIO;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cluster.coordinator.CoordinatorMessageProducer;
import org.greatfree.util.ServerStatus;

/*
 * The class is actually an implementation of ServerIO, which serves for the cluster nodes which access the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class DNIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the cluster server and the coordinator. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/28/2014, Bing Li
	 */
//	public DNIO(Socket clientSocket, Sync collaborator, int remoteServerPort)
	public DNIO(Socket clientSocket, Sync collaborator)
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
				// Wait and read messages from a cluster server. 11/28/2014, Bing Li
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/24/2014, Bing Li
				CoordinatorMessageProducer.SERVER().produceDNMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (ClassNotFoundException | IOException e)
			{
				if (!ServerStatus.FREE().isServerDown(AdminConfig.DN))
				{				
					try
					{
						DNIORegistry.REGISTRY().removeIO(this);
						DNServerClientPool.COORDINATE().getPool().removeClient(this.getRemoteServerKey());
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
