package ca.streaming.news.client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

// Created: 04/05/2020, Bing Lis
class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		ServiceAccessor.UNI().init();

		Scanner in = new Scanner(System.in);
		String option = "n";
		String publisher;
		String category;
		String query;
	
		do
		{
			if (option.equals("n"))
			{
				System.out.println("Tell me your favorite publisher:");
				publisher = in.nextLine();
				System.out.println("Tell me your favorite category:");
				category = in.nextLine();
				ServiceAccessor.UNI().setSubscriberAddress(publisher, category);
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
