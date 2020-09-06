package org.greatfree.dsf.cluster.cs.twonode.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.admin.Menu;
import org.greatfree.dsf.cluster.original.cs.twonode.admin.AdminConfig;
import org.greatfree.dsf.cluster.original.cs.twonode.admin.AdminMenu;
import org.greatfree.dsf.cluster.original.cs.twonode.message.StopChatClusterNotification;
import org.greatfree.dsf.cluster.original.cs.twonode.message.StopOneChildOnClusterNotification;
import org.greatfree.dsf.cluster.original.cs.twonode.message.StopServerOnClusterNotification;
import org.greatfree.dsf.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/13/2019, Bing Li
class Administrator
{
	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		int option = AdminConfig.NO_OPTION;
		// Initialize a scanner to wait for the administrator's commands. 11/27/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		AdminClient.CONTAINER().init();

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

		AdminClient.CONTAINER().dispose();
		in.close();
	}

	/*
	 * Print the console menu for the administrator. 11/27/2014, Bing Li
	 */
	private static void printMenu()
	{
		System.out.println(Menu.MENU_HEAD);
		System.out.println(AdminMenu.STOP_ONE_CHILD_ON_CLUSTER);
		System.out.println(AdminMenu.STOP_CHAT_CLUSTER);
		System.out.println(AdminMenu.STOP_SERVER_ON_CLUSTER);
		System.out.println(AdminMenu.STOP_CHATTING_REGISTRY_SERVER);
		System.out.println(Menu.END);
		System.out.println(Menu.MENU_TAIL);
		System.out.println(Menu.INPUT_PROMPT);
	}

	/*
	 * Notify the chatting server to manage the distributed system. 11/27/2014, Bing Li
	 */
	private static void notifyServer(int option)
	{
		switch (option)
		{
			case AdminConfig.STOP_ONE_CHILD_ON_CLUSTER:
				System.out.println(AdminMenu.STOP_ONE_CHILD_ON_CLUSTER);
				AdminClient.CONTAINER().asyncNotify(new StopOneChildOnClusterNotification());
				break;
				
			case AdminConfig.STOP_CHAT_CLUSTER:
				System.out.println(AdminMenu.STOP_CHAT_CLUSTER);
				AdminClient.CONTAINER().asyncNotify(new StopChatClusterNotification());
				break;
				
			case AdminConfig.STOP_SERVER_ON_CLUSTER:
				System.out.println(AdminMenu.STOP_SERVER_ON_CLUSTER);
				AdminClient.CONTAINER().asyncNotify(new StopServerOnClusterNotification());
				break;
				
			case AdminConfig.STOP_CHAT_REGISTRRY_SERVER:
				System.out.println(AdminMenu.STOP_CHATTING_REGISTRY_SERVER);
				AdminClient.CONTAINER().asyncNotifyRegistry(new ShutdownServerNotification());
				break;
		}
	}
}
