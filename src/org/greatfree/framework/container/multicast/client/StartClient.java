package org.greatfree.framework.container.multicast.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.multicast.MultiConfig;
import org.greatfree.framework.p2p.RegistryConfig;

/**
 * 
 * @author libing
 * 
 * 05/09/2022
 *
 */
final class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		int option = MultiOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);
		
		System.out.println("Starting client ...");

		StandaloneClusterClient.CONTAINER().init(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, MultiConfig.ROOT_KEY);
		
		System.out.println("Client started ...");

		String optionStr;
		while (option != MenuOptions.QUIT)
		{
			ClientUI.MC().printMenu();
			optionStr = in.nextLine();
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);
			
			try
			{
				ClientUI.MC().send(option);
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
