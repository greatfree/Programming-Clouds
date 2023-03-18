package org.greatfree.framework.multicast.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatOptions;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.old.multicast.root.MulticastOptions;
import org.greatfree.framework.old.multicast.root.MulticastRootMenu;
import org.greatfree.testing.client.ClientMenu;
import org.greatfree.testing.client.MenuOptions;

// Created: 08/26/2018, Bing Li
final class MulticastClientUI
{
	private Scanner in = new Scanner(System.in);

	/*
	 * Initialize. 04/23/2017, Bing Li
	 */
	private MulticastClientUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static MulticastClientUI instance = new MulticastClientUI();
	
	public static MulticastClientUI FRONT()
	{
		if (instance == null)
		{
			instance = new MulticastClientUI();
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
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException, InstantiationException, IllegalAccessException, InterruptedException, RemoteIPNotExistedException
	{
		switch (option)
		{
			case MulticastOptions.BROADCAST_NOTIFICATION:
				this.communicate(option);
				break;
				
			case MulticastOptions.UNICAST_NOTIFICATION:
				this.communicate(option);
				break;
				
			case MulticastOptions.ANYCAST_NOTIFICATION:
				this.communicate(option);
				break;
				
			case MulticastOptions.BROADCAST_REQUEST:
				this.communicate(option);
				break;
				
			case MulticastOptions.UNICAST_REQUEST:
				this.communicate(option);
				break;
				
			case MulticastOptions.ANYCAST_REQUEST:
				this.communicate(option);
				break;
				
			case MulticastOptions.QUIT:
				break;
		}
	}
	
	private void communicate(int highOption) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		int notificationOption = ChatOptions.NO_OPTION;
		String optionStr;
		while (notificationOption != MulticastOptions.QUIT)
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
