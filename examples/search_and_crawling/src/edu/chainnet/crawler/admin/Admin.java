package edu.chainnet.crawler.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.admin.Menu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.client.StandaloneClient;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.IPAddress;

import edu.chainnet.center.CenterConfig;
import edu.chainnet.center.message.StopDataCenterChildrenNotification;
import edu.chainnet.center.message.StopDataCenterCoordinatorNotification;
import edu.chainnet.center.message.StopOneDataCenterChildNotification;
import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.message.StopCrawlingChildrenNotification;
import edu.chainnet.crawler.message.StopCrawlingCoordinatorNotification;
import edu.chainnet.crawler.message.StopOneCrawlingChildNotification;

// Created: 04/24/2021, Bing Li
class Admin
{
	private static String registryIP = RegistryConfig.PEER_REGISTRY_ADDRESS;
	private static int registryPort = RegistryConfig.PEER_REGISTRY_PORT;

	private static IPAddress crawlerAddress;
	private static IPAddress dataCenterAddress;
	
	private static Scanner in = new Scanner(System.in);
	
	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();

		crawlerAddress = StandaloneClient.CS().getIPAddress(registryIP, registryPort, CrawlConfig.CRAWLING_COORDINATOR_KEY);
		System.out.println("crawlerAddress = " + crawlerAddress);
		
		dataCenterAddress = StandaloneClient.CS().getIPAddress(registryIP, registryPort, CenterConfig.CENTER_COORDINATOR_KEY);
		System.out.println("dataCenterAddress = " + dataCenterAddress);
		
		int option = MenuOptions.NO_OPTION;
		String optionStr;
		
		while (option != MenuOptions.QUIT)
		{
			printMenu();
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice is: " + option);
				notifyServer(option);
			}
			catch (NumberFormatException e)
			{
				option = Options.NO_OPTION;
				System.out.println(Menu.WRONG_OPTION);
			}
		}
		
		StandaloneClient.CS().dispose();
		in.close();
	
	}
	
	private static void printMenu()
	{
		System.out.println(AdminMenu.MENU_HEAD);
		System.out.println(AdminMenu.STOP_ONE_CRAWLING_CHILD);
		System.out.println(AdminMenu.STOP_CRAWLING_CHILDREN);
		System.out.println(AdminMenu.STOP_CRAWLING_COORDINATOR);
		System.out.println(AdminMenu.STOP_ONE_DATA_CENTER_CHILD);
		System.out.println(AdminMenu.STOP_DATA_CENTER_CHILDREN);
		System.out.println(AdminMenu.STOP_DATA_CENTER_COORDINATOR);
		System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
		System.out.println(AdminMenu.QUIT);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}
	
	private static void notifyServer(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case Options.STOP_ONE_CRAWLING_CHILD:
				System.out.println(AdminMenu.STOP_ONE_CRAWLING_CHILD);
				StandaloneClient.CS().syncNotify(crawlerAddress.getIP(), crawlerAddress.getPort(), new StopOneCrawlingChildNotification());
				break;
				
			case Options.STOP_CRAWLING_CHILDREN:
				System.out.println(AdminMenu.STOP_CRAWLING_CHILDREN);
				StandaloneClient.CS().syncNotify(crawlerAddress.getIP(), crawlerAddress.getPort(), new StopCrawlingChildrenNotification());
				break;
				
			case Options.STOP_CRAWLING_COORDINATOR:
				System.out.println(AdminMenu.STOP_CRAWLING_COORDINATOR);
				StandaloneClient.CS().syncNotify(crawlerAddress.getIP(), crawlerAddress.getPort(), new StopCrawlingCoordinatorNotification());
				break;
				
			case Options.STOP_ONE_DATA_CENTER_CHILD:
				System.out.println(AdminMenu.STOP_ONE_DATA_CENTER_CHILD);
				StandaloneClient.CS().syncNotify(dataCenterAddress.getIP(), dataCenterAddress.getPort(), new StopOneDataCenterChildNotification());
				break;
				
			case Options.STOP_DATA_CENTER_CHILDREN:
				System.out.println(AdminMenu.STOP_DATA_CENTER_CHILDREN);
				StandaloneClient.CS().syncNotify(dataCenterAddress.getIP(), dataCenterAddress.getPort(), new StopDataCenterChildrenNotification());
				break;
				
			case Options.STOP_DATA_CENTER_COORDINATOR:
				System.out.println(AdminMenu.STOP_DATA_CENTER_COORDINATOR);
				StandaloneClient.CS().syncNotify(dataCenterAddress.getIP(), dataCenterAddress.getPort(), new StopDataCenterCoordinatorNotification());
				break;
				
			case Options.STOP_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(registryIP, registryPort, new ShutdownServerNotification());
				break;
		}
	}
}
