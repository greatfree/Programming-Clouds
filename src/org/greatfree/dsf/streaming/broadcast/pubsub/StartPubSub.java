package org.greatfree.dsf.streaming.broadcast.pubsub;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.streaming.StreamConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 03/18/2020, Bing Li
class StartPubSub
{

	public static void main(String[] args)
	{
		System.out.println("Pub/Sub server starting up ...");
		
		try
		{
			PubSubServer.BROAD_STREAM().start(StreamConfig.PUBSUB_SERVER_NAME);
		}
		catch (IOException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		System.out.println("Pub/Sub server started ...");

		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
