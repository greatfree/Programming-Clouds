package org.greatfree.dip.old.multicast.root;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.MenuOptions;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.client.ClientMenu;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.TerminateSignal;

/*
 * The class starts up the cluster and tests it whether data can be multicast from it. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
class StartRoot
{

	public static void main(String[] args)
	{
		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = MenuOptions.NO_OPTION;

		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		System.out.println("Cluster root starting up ...");

		try
		{
			// Start up the cluster root. 06/11/2017, Bing Li
			ClusterRootSingleton.CLUSTER().start();
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | InstantiationException | IllegalAccessException | InterruptedException e)
		{
			e.printStackTrace();
		}

		System.out.println("Cluster root started ...");

		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			MulticastRootUI.CLUSTER().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			// Convert the input string to integer. 09/21/2014, Bing Li
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);

			// Send the option to the cluster. 09/21/2014, Bing Li
			try
			{
				MulticastRootUI.CLUSTER().send(option);
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | RemoteReadException | IOException | InterruptedException | NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		// Set the flag of the server as down. 05/15/2017, Bing Li
		ServerStatus.FREE().setShutdown();
		TerminateSignal.SIGNAL().setTerminated();
		try
		{
			ClusterRootSingleton.CLUSTER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		}
		catch (IOException | InterruptedException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		in.close();

	}

}
