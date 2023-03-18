package org.greatfree.framework.streaming.unicast.client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;

// Created: 03/23/2020, Bing Li
class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, RemoteIPNotExistedException
	{
		ServiceAccessor.UNI().init();
		
		Scanner in = new Scanner(System.in);
		String option = "n";
		String publisher;
		String topic;
		String query;
		
		do
		{
			if (option.equals("n"))
			{
				System.out.println("Tell me your favorite publisher:");
				publisher = in.nextLine();
				System.out.println("Tell me your favorite topic:");
				topic = in.nextLine();
				ServiceAccessor.UNI().setSubscriberAddress(publisher, topic);
			}
			
			System.out.println("Search query:");
			query = in.nextLine();
			
			List<String> results = ServiceAccessor.UNI().search(query);
			System.out.println("Search Results");
			System.out.println("--------------------------------------------");
			for (String entry : results)
			{
				System.out.println(entry);
			}
			System.out.println("--------------------------------------------");
			System.out.println("Continue the same subscription? (y/n/q)?");
			option = in.nextLine();
		}
		while (!option.equals("q"));

		ServiceAccessor.UNI().dispose();
		in.close();
	}

}
