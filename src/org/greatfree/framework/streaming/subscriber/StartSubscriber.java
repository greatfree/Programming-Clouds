package org.greatfree.framework.streaming.subscriber;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.streaming.RegisteredStream;
import org.greatfree.framework.streaming.message.StreamResponse;
import org.greatfree.framework.streaming.message.SubscribeStreamResponse;
import org.greatfree.util.UtilConfig;

// Created: 03/19/2020, Bing Li
class StartSubscriber
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Tell me your name as a subscriber: ");
		String subscriberName = in.nextLine();
		
		System.out.println("Subscriber starting up ...");
		try
		{
			SubscriberPeer.CHILD().start(subscriberName);
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}
		catch (IOException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}
		System.out.println("Subscriber started ...");

		String option = "n";
		String publisher = UtilConfig.EMPTY_STRING;
		String topic = UtilConfig.EMPTY_STRING;
		SubscribeStreamResponse response;
		StreamResponse sr;
		do
		{
			if (option.equals("n"))
			{
				if (!publisher.equals(UtilConfig.EMPTY_STRING))
				{
					SubscriberPeer.CHILD().unsubscribe(publisher, topic);
				}
				
				sr = SubscriberPeer.CHILD().getRegisteredStreams();
				System.out.println("Registered Streams");
				System.out.println("--------------------------------------------");
				for (RegisteredStream entry : sr.getStreams())
				{
					System.out.println(entry);
				}
				System.out.println("--------------------------------------------");
				
				System.out.println("Tell me the publisher you are interested in: ");
				publisher = in.nextLine();
				System.out.println("Tell me the topic you are interested in: ");
				topic = in.nextLine();
				response = SubscriberPeer.CHILD().subscribe(publisher, topic);
				if (response.isSucceeded())
				{
					System.out.println("You are subscribed successfully!");
				}
				else
				{
					System.out.println("You are failed to subscribe!");
				}
			}
			System.out.println("Continue the same subscription? (y/n/q)?");
			option = in.nextLine();
		}
		while (!option.equals("q"));

		SubscriberPeer.CHILD().unsubscribe(publisher, topic);
		SubscriberPeer.CHILD().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		in.close();
	}

}
