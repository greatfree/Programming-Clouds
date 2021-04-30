package edu.chainnet.center.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;

import edu.chainnet.center.CenterConfig;
import edu.chainnet.crawler.CrawlConfig;

// Created: 04/29/2021, Bing Li
class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		System.out.println("Search user starting up ...");

		StandaloneClusterClient.CONTAINER().init(CrawlConfig.REGISTRY_IP, RegistryConfig.PEER_REGISTRY_PORT, CenterConfig.CENTER_COORDINATOR_KEY);
		
		System.out.println("Search user started ...");

		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			SearchUI.CENTER().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			// Convert the input string to integer. 09/21/2014, Bing Li
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);

			try
			{
				SearchUI.CENTER().send(option);
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
