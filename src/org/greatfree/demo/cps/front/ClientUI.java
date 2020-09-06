package org.greatfree.demo.cps.front;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.client.StandaloneClient;
import org.greatfree.data.ServerConfig;
import org.greatfree.demo.cps.message.MerchandiseRequest;
import org.greatfree.demo.cps.message.MerchandiseResponse;
import org.greatfree.demo.cps.message.OrderNotification;
import org.greatfree.dsf.container.cps.message.StopCoordinatorNotification;
import org.greatfree.dsf.container.cps.message.StopTerminalNotification;
import org.greatfree.dsf.container.cps.threenode.front.MenuOptions;
import org.greatfree.dsf.cps.threetier.front.ClientMenu;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/28/2019, Bing Li
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
	
	public static ClientUI CPS_CONTAINER()
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
		System.out.println(ClientMenu.REQUEST);
		System.out.println(ClientMenu.NOTIFY);
		System.out.println(ClientMenu.STOP_TERMINAL);
		System.out.println(ClientMenu.STOP_COORDINATOR);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		MerchandiseResponse response;
		switch (option)
		{
			case MenuOptions.REQUEST:
				response = (MerchandiseResponse)StandaloneClient.CS().read(ChatConfig.CHAT_SERVER_ADDRESS, ServerConfig.COORDINATOR_PORT, new MerchandiseRequest("How much to buy an iPhoneX"));
				System.out.println("The answer is: " + response.getMerchandise() + ", " + response.getPrice());
				break;
		
			case MenuOptions.NOTIFY:
				StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ServerConfig.COORDINATOR_PORT, new OrderNotification("iPhoneX", 2));
				break;
				
			case MenuOptions.STOP_TERMINAL:
				StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ServerConfig.COORDINATOR_PORT, new StopTerminalNotification());
				break;

			case MenuOptions.STOP_COORDINATOR:
				StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ServerConfig.COORDINATOR_PORT, new StopCoordinatorNotification());
				break;

			case MenuOptions.QUIT:
				break;
		}
	}
}
