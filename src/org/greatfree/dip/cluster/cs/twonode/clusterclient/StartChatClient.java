package org.greatfree.dip.cluster.cs.twonode.clusterclient;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.dip.cs.twonode.client.ChatMaintainer;
import org.greatfree.dip.multicast.MulticastConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/15/2019, Bing Li
class StartChatClient
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DistributedNodeFailedException, InterruptedException
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

		// The chatting information maintainer. 05/25/2017, Bing Li
		ChatMaintainer.CS().init(username, partner);

		System.out.println("Client starting up ...");

		ChatClient.CCC().init(MulticastConfig.CLUSTER_CLIENT_ROOT_NAME);

		System.out.println("Client started ...");

		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			ClientUI.CCC().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.CCC().send(option, in);
			}
			catch (NumberFormatException | ClassNotFoundException | RemoteReadException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		// For testing. 01/16/2019, Bing Li
//		ChatClient.CCC().getPeerClient().read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new UnregisterPeerRequest(MulticastConfig.CLUSTER_CLIENT_ROOT_KEY));
		
//		System.out.println("Press any key to continue ...");

//		optionStr = in.nextLine();

		// Dispose the chatting maintainer. 05/26/2017, Bing Li
		ChatMaintainer.CS().dispose();
		
//		ChatClient.CCC().stopCluster();
		
//		ChatClient.CCC().getPeerClient().read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new UnregisterPeerRequest(MulticastConfig.CLUSTER_CLIENT_ROOT_KEY));

//		System.out.println("Press any key to stop the client root ...");
		
//		optionStr = in.nextLine();

//		ChatClient.CCC().getPeerClient().read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new UnregisterPeerRequest(MulticastConfig.CLUSTER_CLIENT_ROOT_KEY));

		ChatClient.CCC().stop();

//		System.out.println("Press any key to exist ...");

//		optionStr = in.nextLine();

		in.close();
	}
}
