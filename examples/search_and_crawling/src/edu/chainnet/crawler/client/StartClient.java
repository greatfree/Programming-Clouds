package edu.chainnet.crawler.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import edu.chainnet.crawler.CrawlConfig;

/**
 * 
 * @author libing
 * 
 * The client assigns URLs to the crawling cluster and controls the execution of the cluster. 04/23/2021, Bing Li
 *
 */

// Created: 04/22/2021, Bing Li
class StartClient
{
	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		int option = MenuOptions.NO_OPTION;
		
		Scanner in = new Scanner(System.in);

		StandaloneClusterClient.CONTAINER().init(CrawlConfig.REGISTRY_IP, RegistryConfig.PEER_REGISTRY_PORT, CrawlConfig.CRAWLING_COORDINATOR_KEY);

		String optionStr;
		while (option != MenuOptions.QUIT)
		{
			try
			{
				ClientUI.CRAWL().printMenu();
				optionStr = in.nextLine();
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				ClientUI.CRAWL().send(option);
			}
			catch (NumberFormatException | IOException | InterruptedException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}
		
		StandaloneClusterClient.CONTAINER().dispose();
		in.close();
	}
}

