package org.greatfree.framework.old.multicast.root;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatOptions;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.client.ClientMenu;
import org.greatfree.testing.client.MenuOptions;

/*
 * The class aims to print a menu list on the screen for users to interact with the multicastor and communicate with the distributed nodes in the cluster. The menu is unique in the multicastor such that it is implemented in the pattern of a singleton. 05/02/2017, Bing Li
 */

// Created: 05/10/2017, Bing Li
class MulticastRootUI
{
	private Scanner in = new Scanner(System.in);

	/*
	 * Initialize. 04/23/2017, Bing Li
	 */
	private MulticastRootUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static MulticastRootUI instance = new MulticastRootUI();
	
	public static MulticastRootUI CLUSTER()
	{
		if (instance == null)
		{
			instance = new MulticastRootUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	/*
	 * Print the menu list on the screen. 04/23/2017, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(MulticastRootMenu.MENU_HEAD);
		System.out.println(MulticastRootMenu.BROADCAST_NOTIFICATION);
		System.out.println(MulticastRootMenu.UNICAST_NOTIFICATION);
		System.out.println(MulticastRootMenu.ANYCAST_NOTIFICATION);
		System.out.println(MulticastRootMenu.BROADCAST_REQUEST);
		System.out.println(MulticastRootMenu.UNICAST_REQUEST);
		System.out.println(MulticastRootMenu.ANYCAST_REQUEST);
		System.out.println(MulticastRootMenu.QUIT);
		
		System.out.println(MulticastRootMenu.MENU_TAIL);
	}
	
	/*
	 * Send the users' option to the chatting server. 04/23/2017, Bing Li
	 */
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException, InstantiationException, IllegalAccessException, InterruptedException
	{
		switch (option)
		{
			case MulticastOptions.BROADCAST_NOTIFICATION:
				this.multicast(option);
				break;
				
			case MulticastOptions.UNICAST_NOTIFICATION:
				this.multicast(option);
				break;
				
			case MulticastOptions.ANYCAST_NOTIFICATION:
				this.multicast(option);
				break;
				
			case MulticastOptions.BROADCAST_REQUEST:
				this.multicast(option);
				break;
				
			case MulticastOptions.UNICAST_REQUEST:
				this.multicast(option);
				break;
				
			case MulticastOptions.ANYCAST_REQUEST:
				this.multicast(option);
				break;
				
			case MulticastOptions.QUIT:
				break;
		}
	}
	
	private void multicast(int highOption) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		int notificationOption = ChatOptions.NO_OPTION;
		String optionStr;
		while (notificationOption != ChatOptions.QUIT_CHAT)
		{
			MulticastInputUI.CLUSTER().printMenu();
			optionStr = in.nextLine();
			try
			{
				notificationOption = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + highOption);
				MulticastInputUI.CLUSTER().send(highOption, notificationOption);
			}
			catch (NumberFormatException e)
			{
				notificationOption = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}
	}
}
