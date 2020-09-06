package org.greatfree.app.search.dip.container.cs.multinode.client.user;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.app.search.dip.container.cluster.message.LocationNotification;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Tools;

// Created: 01/14/2019, Bing Li
class StartUsers
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		System.out.println("Search user starting up ...");

		StandaloneClusterClient.CONTAINER().init(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, Tools.getHash(MulticastConfig.CLUSTER_SERVER_ROOT_NAME));
		
		System.out.println("Search user started ...");

		System.out.println("Please tell me your location: is you located at an international location? (y/n)");
		
		String isInternational = in.nextLine();
		if (isInternational.equals("y"))
		{
			StandaloneClusterClient.CONTAINER().syncNotifyRoot(new LocationNotification("greatfree", true));
		}
		else
		{
			StandaloneClusterClient.CONTAINER().syncNotifyRoot(new LocationNotification("greatfree", false));
		}

		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			SearchUI.CLUSTER().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			// Convert the input string to integer. 09/21/2014, Bing Li
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);

			try
			{
				SearchUI.CLUSTER().send(option);
			}
			catch (ClassNotFoundException | RemoteReadException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}
		
		StandaloneClusterClient.CONTAINER().dispose();
		in.close();
	}

}
