package edu.greatfree.threetier.front;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;

import edu.greatfree.threetier.message.FrontResponse;

// Created: 07/06/2018, Bing Li
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
				FrontEventer.RE().notify("I am learning cloud programming ...");
				break;
				
			case MenuOptions.REQUEST:
				response = FrontReader.RR().query("What is cloud programming?");
				System.out.println("The answer is: " + response.getAnswer());
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}

}
