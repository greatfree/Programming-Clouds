package org.greatfree.dsf.container.cps.threenode.front;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.client.StandaloneClient;
import org.greatfree.dsf.container.cps.message.FrontNotification;
import org.greatfree.dsf.container.cps.message.FrontRequest;
import org.greatfree.dsf.container.cps.message.StopCoordinatorNotification;
import org.greatfree.dsf.container.cps.message.StopTerminalNotification;
import org.greatfree.dsf.cps.threetier.front.ClientMenu;
import org.greatfree.dsf.cps.threetier.message.FrontResponse;
import org.greatfree.exceptions.RemoteReadException;

// Created: 12/31/2018, Bing Li
public class ClientUI
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
	
	public static ClientUI CPS()
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
		System.out.println(ClientMenu.NOTIFY);
		System.out.println(ClientMenu.REQUEST);
		System.out.println(ClientMenu.STOP_TERMINAL);
		System.out.println(ClientMenu.STOP_COORDINATOR);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	public void send(int option) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		FrontResponse response;
		switch (option)
		{
			case MenuOptions.NOTIFY:
				StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new FrontNotification("I am learning cloud programming ..."));
				break;
				
			case MenuOptions.REQUEST:
				response = (FrontResponse)StandaloneClient.CS().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new FrontRequest("What is could programming?"));
				System.out.println("The answer is: " + response.getAnswer());
				break;
				
			case MenuOptions.STOP_TERMINAL:
				StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new StopTerminalNotification());
				break;
				
			case MenuOptions.STOP_COORDINATOR:
				StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new StopCoordinatorNotification());
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}
}
