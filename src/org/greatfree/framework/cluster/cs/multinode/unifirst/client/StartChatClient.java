package org.greatfree.framework.cluster.cs.multinode.unifirst.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.cs.twonode.client.ChatClient;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

/*
 * I need to clarify the solution again as follows.

The message, ChatRegistryRequest/ChatRegistryResponse, has no partners.

Thus, it must be broadcast to all of the children such that other ones can detect the user by unicasting their own children, respectively.

When discussing intercasting initially, I hope the messages can be sent from the source child to the destination child.

However, using the root-based intercasting, it seems that it is not necessary to do so.

The root can send messages for the source user to the destination child using the unicasting.

 */

// Created: 02/15/2019, Bing Li
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

		Scheduler.GREATFREE().init(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		ChatClient.CONTAINER().init();

		ChatMaintainer.UNIFIRST().init(username, partner);
		
		Scheduler.GREATFREE().submit(new Checker(), ChatConfig.CHAT_POLLING_DELAY	, ChatConfig.CHAT_POLLING_PERIOD);

		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			ClientUI.UNICAST().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.UNICAST().send(option);
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
