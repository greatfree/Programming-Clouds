package com.greatfree.testing.crawlserver;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.greatfree.concurrency.Collaborator;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.remote.ServerIO;

/*
 * The class receives requests/notifications from the coordinator to accomplish the task of crawling. 11/23/2014, Bing Li
 */

// Created: 11/11/2014, Bing Li
public class CrawlServerIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the coordinator and the crawling server. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/23/2014, Bing Li
	 */
	public CrawlServerIO(Socket clientSocket, Collaborator collaborator)
	{
		super(clientSocket, collaborator);
	}

	/*
	 * Concurrently respond the coordinator's requests/notification. 11/23/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			try
			{
				// Wait and read messages from the coordinator. 11/23/2014, Bing Li
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/23/2014, Bing Li
				CrawlMessageProducer.CRAWL().produceMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
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
