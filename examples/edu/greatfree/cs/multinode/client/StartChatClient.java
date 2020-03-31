package edu.greatfree.cs.multinode.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.data.ClientConfig;
import org.greatfree.util.TerminateSignal;

/*
 * This is a client that interacts with the chatting server through the manner of requesting/responding and notifying. The chatting server can only respond to the client after it requests. 09/21/2014, Bing Li
 */

// Created: 04/23/2017, Bing Li
class StartChatClient
{
	/*
	 * The starting point of the chatting client. 09/21/2014, Bing Li
	 */
	public static void main(String[] args) throws InterruptedException, IOException
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

		// Initialize the client reader. 05/26/2017, Bing Li
		ChatReader.RR().init();
		
		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
		Scheduler.GREATFREE().init(ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the chatting eventer. 04/28/2017, Bing Li
		ChatEventer.RE().init();

		// The chatting information maintainer. 05/25/2017, Bing Li
		ChatMaintainer.CS().init(username, partner);

		// Submit the polling tasks. 05/26/2017, Bing Li
		Scheduler.GREATFREE().submit(new Checker(), ChatConfig.CHAT_POLLING_DELAY	, ChatConfig.CHAT_POLLING_PERIOD);

		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			ClientUI.CS().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.CS().send(option);
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
		ChatMaintainer.CS().dispose();
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
		Scheduler.GREATFREE().shutdown(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);

		ChatEventer.RE().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);

		// Shutdown the remote reader. 11/23/2014, Bing Li
		ChatReader.RR().shutdown();

		in.close();
	}

}
