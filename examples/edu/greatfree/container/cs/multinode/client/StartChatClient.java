package edu.greatfree.container.cs.multinode.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cs.multinode.ChatConfig;
import edu.greatfree.cs.multinode.ClientMenu;
import edu.greatfree.cs.multinode.MenuOptions;

// Created: 01/07/2019, Bing Li, in the airplane from Zhuhai to Xi'An
class StartChatClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = MenuOptions.NO_OPTION;
		
		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);

		// Input the local user name. 05/26/2017, Bing Li
		System.out.println("Tell me your user name: ");
		
		String username = in.nextLine();

		// Input the chatting partner name. 05/26/2017, Bing Li
		System.out.println("Tell me your partner: ");
		
		String partner = in.nextLine();

//		ChatClient.CS_FRONT().init();
		StandaloneClient.CS().init();

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
		Scheduler.GREATFREE().init(ChatConfig.SCHEDULER_THREAD_POOL_SIZE, ChatConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		// The chatting information maintainer. 05/25/2017, Bing Li
		ChatMaintainer.CS_CONTAINER().init(username, partner);

		// Submit the polling tasks. 05/26/2017, Bing Li
		Scheduler.GREATFREE().submit(new Checker(), ChatConfig.CHAT_POLLING_DELAY	, ChatConfig.CHAT_POLLING_PERIOD);

		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			ClientUI.CS_CONTAINER().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.CS_CONTAINER().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		// Set the status of the local node as shutdown. 05/26/2017, Bing Li
//		ServerStatus.FREE().setShutdown();
		// Set the terminating flag to true. 09/21/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();

		// Dispose the chatting maintainer. 05/26/2017, Bing Li
		ChatMaintainer.CS_CONTAINER().dispose();
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
		Scheduler.GREATFREE().shutdown(ChatConfig.SCHEDULER_SHUTDOWN_TIMEOUT);

//		ChatClient.CS_FRONT().dispose();
		StandaloneClient.CS().dispose();

		in.close();
	}

}
