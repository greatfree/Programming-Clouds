package org.greatfree.app.cps.front;

import java.io.IOException;

import org.greatfree.app.cps.message.MerchandiseResponse;
import org.greatfree.exceptions.RemoteReadException;

// Created: 08/14/2018, Bing Li
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
		System.out.println(ClientMenu.REQUEST);
		System.out.println(ClientMenu.NOTIFY);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}
	
	public void send(int option) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		MerchandiseResponse response;
		switch (option)
		{
			case MenuOptions.MERCHANDISE_QUERY:
				response = BusinessReader.RR().query("How much to buy an iPhoneX");
				System.out.println("The answer is: " + response.getMerchandise() + ", " + response.getPrice());
				break;
		
			case MenuOptions.MERCHANDISE_NOTIFICATION:
				BusinessEventer.RE().notify("iPhoneX", 2);
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}
}
