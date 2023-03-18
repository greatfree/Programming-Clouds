package org.greatfree.framework.streaming.unicast.pubsub;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.streaming.StreamConfig;
import org.greatfree.util.TerminateSignal;

// Created: 03/22/2020, Bing Li
class StartPubSub
{

	public static void main(String[] args) throws RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Pub/Sub server starting up ...");
		
		try
		{
			PubSubServer.UNI_STREAM().start(StreamConfig.PUBSUB_SERVER_NAME);
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
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
