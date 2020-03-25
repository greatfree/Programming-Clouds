package org.greatfree.dip.cluster.cs.twonode.clusterclient;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.dip.cs.twonode.client.ChatMaintainer;
import org.greatfree.dip.cs.twonode.client.ClientMenu;
import org.greatfree.dip.cs.twonode.client.MenuOptions;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/15/2019, Bing Li
class ClientUI
{
	/*
	 * Initialize. 04/23/2017, Bing Li
	 */
	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI CCC()
	{
		if (instance == null)
		{
			instance = new ClientUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
	}
	
	/*
	 * Print the menu list on the screen. 04/23/2017, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.REGISTER_CHATTING + ChatMaintainer.CS().getLocalUsername());
		System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.CS().getPartner());
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	/*
	 * Send the users' option to the chatting server. 04/23/2017, Bing Li
	 */
	public void send(int option, Scanner in) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		// Check the option to interact with the chatting server. 04/23/2017, Bing Li
		switch (option)
		{
			case MenuOptions.REGISTER_CHATTING:
				ClientTasks.register();
				break;
				
			case MenuOptions.START_CHATTING:
				ClientTasks.chat(option, in);
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}
}
