package org.greatfree.dsf.cluster.cs.multinode.intercast.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.dsf.cluster.cs.multinode.unifirst.client.ChatMaintainer;
import org.greatfree.dsf.cluster.cs.multinode.unifirst.client.Checker;
import org.greatfree.dsf.cluster.cs.twonode.client.ChatClient;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 03/07/2019, Bing Li
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
		
		String userName = in.nextLine();

		// Input the chatting partner name. 05/26/2017, Bing Li
		System.out.println("Tell me your partner: ");
		
		String partner = in.nextLine();

		Scheduler.GREATFREE().init(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		ChatClient.CONTAINER().init();

		ChatMaintainer.UNIFIRST().init(userName, partner);

		Scheduler.GREATFREE().submit(new Checker(), ChatConfig.CHAT_POLLING_DELAY	, ChatConfig.CHAT_POLLING_PERIOD);

		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			ClientUI.INTERCAST().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.INTERCAST().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		// Set the status of the local node as shutdown. 05/26/2017, Bing Li
		// Set the terminating flag to true. 09/21/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();

		// Dispose the chatting maintainer. 05/26/2017, Bing Li
		ChatMaintainer.UNIFIRST().dispose();
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
		Scheduler.GREATFREE().shutdown(RegistryConfig.SCHEDULER_SHUTDOWN_TIMEOUT);

		ChatClient.CONTAINER().dispose();

		in.close();
	}

}
