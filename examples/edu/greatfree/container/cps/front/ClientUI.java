package edu.greatfree.container.cps.front;

import java.io.IOException;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;

import edu.greatfree.container.cps.CPSConfig;
import edu.greatfree.container.cps.message.FrontNotification;
import edu.greatfree.container.cps.message.FrontRequest;
import edu.greatfree.container.cps.message.StopCoordinatorNotification;
import edu.greatfree.container.cps.message.StopTerminalNotification;
import edu.greatfree.threetier.front.ClientMenu;
import edu.greatfree.threetier.message.FrontResponse;

// Created: 12/31/2018, Bing Li
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
				StandaloneClient.CS().syncNotify(CPSConfig.COORDINATOR_ADDRESS, CPSConfig.COORDINATOR_PORT, new FrontNotification("I am learning cloud programming ..."));
				break;
				
			case MenuOptions.REQUEST:
				response = (FrontResponse)StandaloneClient.CS().read(CPSConfig.COORDINATOR_ADDRESS, CPSConfig.COORDINATOR_PORT, new FrontRequest("What is could programming?"));
				System.out.println("The answer is: " + response.getAnswer());
				break;
				
			case MenuOptions.STOP_TERMINAL:
				StandaloneClient.CS().syncNotify(CPSConfig.COORDINATOR_ADDRESS, CPSConfig.COORDINATOR_PORT, new StopTerminalNotification());
				break;
				
			case MenuOptions.STOP_COORDINATOR:
				StandaloneClient.CS().syncNotify(CPSConfig.COORDINATOR_ADDRESS, CPSConfig.COORDINATOR_PORT, new StopCoordinatorNotification());
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}
}
