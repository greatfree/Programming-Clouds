package ca.dp.mncs.circle.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;

// Created: 02/26/2020, Bing Li
class StartCircleClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException
	{
		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);

		// Input the local user name. 05/26/2017, Bing Li
		System.out.println("You want to publish? (y/n");
		
		String isAuthor = in.nextLine();

		// Input the chatting partner name. 05/26/2017, Bing Li
		System.out.println("Tell me your partner: ");
		
		CircleReader.RR().init();
		CircleEventer.RE().init();

		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		Scheduler.GREATFREE().submit(new Checker(), ChatConfig.CHAT_POLLING_DELAY	, ChatConfig.CHAT_POLLING_PERIOD);

		if (isAuthor.equals("y"))
		{
			System.out.println("Please type your post ...");
			String post = in.nextLine();
			CircleReader.RR().post(post);
		}
		else
		{
			System.out.println("Please like your friend post ...");
			CircleEventer.RE().like("GreatFree");
		}
		
		System.out.println("Press enter to exit ...");
		in.nextLine();
		in.close();
	}

}
