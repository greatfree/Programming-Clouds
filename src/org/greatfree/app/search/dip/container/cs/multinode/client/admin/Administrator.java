package org.greatfree.app.search.dip.container.cs.multinode.client.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.admin.Menu;
import org.greatfree.app.search.dip.container.cluster.message.ShutdownChildrenAdminNotification;
import org.greatfree.app.search.dip.container.cluster.message.ShutdownSearchEntryNotification;
import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.Tools;

// Created: 01/14/2019, Bing Li
class Administrator
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		int option = AdminConfig.NO_OPTION;
		// Initialize a scanner to wait for the administrator's commands. 11/27/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		StandaloneClusterClient.CONTAINER().init(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, Tools.getHash(MulticastConfig.CLUSTER_SERVER_ROOT_NAME));
		
		while (option != AdminConfig.END)
		{
			// Print the console menu. 04/17/2017, Bing Li
			printMenu();
			// Wait for the administrator's input from the console. 11/27/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the string to the integer. 11/27/2014, Bing Li
				option = Integer.parseInt(optionStr);
				// Print the option. 11/27/2014, Bing Li
				System.out.println("Your choice is: " + option);
				// Notify the coordinator. 11/27/2014, Bing Li
				notifyServer(option);
			}
			catch (NumberFormatException e)
			{
				option = AdminConfig.NO_OPTION;
				System.out.println(Menu.WRONG_OPTION);
			}
		}
		
		StandaloneClusterClient.CONTAINER().dispose();
		in.close();
	}

	/*
	 * Print the console menu for the administrator. 11/27/2014, Bing Li
	 */
	private static void printMenu()
	{
		System.out.println(Menu.MENU_HEAD);
		System.out.println(AdminMenu.STOP_SEARCH_CLUSTER);
		System.out.println(AdminMenu.STOP_SEARCH_ENTRY);
		System.out.println(AdminMenu.STOP_CHATTING_REGISTRY_SERVER);
		System.out.println(Menu.END);
		System.out.println(Menu.MENU_TAIL);
		System.out.println(Menu.INPUT_PROMPT);
	}

	/*
	 * Notify the chatting server to manage the distributed system. 11/27/2014, Bing Li
	 */
	private static void notifyServer(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case AdminConfig.STOP_SEARCH_CLUSTER:
				System.out.println(AdminMenu.STOP_SEARCH_CLUSTER);
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new ShutdownChildrenAdminNotification());
				break;

			case AdminConfig.STOP_SEARCH_ENTRY:
				System.out.println(AdminMenu.STOP_SEARCH_ENTRY);
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new ShutdownSearchEntryNotification());
				break;
				
			case AdminConfig.STOP_CHATTING_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_CHATTING_REGISTRY_SERVER);
				StandaloneClusterClient.CONTAINER().syncNotifyRegistry(new ShutdownServerNotification());
				break;
		}
	}
}
