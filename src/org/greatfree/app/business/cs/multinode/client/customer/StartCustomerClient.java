package org.greatfree.app.business.cs.multinode.client.customer;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.app.business.cs.multinode.client.UserID;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

// Created: 12/22/2017, Bing Li
public class StartCustomerClient
{

	public static void main(String[] args)
	{
		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = MenuOptions.NO_OPTION;
		
		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);

		// Input the local user name. 05/26/2017, Bing Li
		System.out.println("Tell me your user name: ");

		// Set the customer's name. 12/22/2017, Bing Li
		UserID.CID().setUserName(in.nextLine());

		// Initialize the customer reader. 12/21/2017, Bing Li
		CustomerReader.CR().init();
		
		// Initialize the customer eventer. 12/21/2017, Bing Li
		CustomerEventer.CE().init();
		
		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to the customer. 09/21/2014, Bing Li
			CustomerUI.CS().printMenu();
			// Input a string that represents the vendor's intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);

				CustomerUI.CS().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}
		
		// Set the terminating flag to true. 09/21/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		
		try
		{
			// Dispose the vendor eventer. 05/25/2017, Bing Li
			CustomerEventer.CE().dispose(RegistryConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		}
		catch (InterruptedException | IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		// Shutdown the vendor reader. 11/23/2014, Bing Li
		CustomerReader.CR().shutdown();
		
		in.close();

	}

}
