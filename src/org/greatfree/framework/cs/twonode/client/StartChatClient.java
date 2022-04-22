package org.greatfree.framework.cs.twonode.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

// Created: 05/13/2018, Bing Li
class StartChatClient
{

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

		// Initialize the status of the local node. 05/26/2017, Bing Li
//		ServerStatus.FREE().init();

		// Initialize the client reader. 05/26/2017, Bing Li
		ChatReader.RR().init();
		
		// Initialize the chatting eventer. 04/28/2017, Bing Li
		ChatEventer.RE().init();

		// The chatting information maintainer. 05/25/2017, Bing Li
		ChatMaintainer.CS().init(username, partner);

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
			catch (NumberFormatException | ClassNotFoundException | RemoteReadException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		// Set the status of the local node as shutdown. 05/26/2017, Bing Li
//		ServerStatus.FREE().setShutdown();
		// Set the terminating flag to true. 09/21/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();

		// Dispose the chatting maintainer. 05/26/2017, Bing Li
		ChatMaintainer.CS().dispose();
		
		// Dispose the chatting eventer. 05/25/2017, Bing Li
		ChatEventer.RE().dispose(RegistryConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);

		// Shutdown the remote reader. 11/23/2014, Bing Li
		ChatReader.RR().shutdown();

		in.close();
	}

}
