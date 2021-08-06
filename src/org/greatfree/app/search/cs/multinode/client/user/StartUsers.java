package org.greatfree.app.search.cs.multinode.client.user;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.app.search.multicast.message.LocationNotification;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.exceptions.RemoteReadException;

// Created: 10/07/2018, Bing Li
class StartUsers
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		System.out.println("Search user starting up ...");
		
		try
		{
			SearchClient.FRONT().init();
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Search user started ...");

		System.out.println("Please tell me your location: is you located at an international location? (y/n)");
		
		String isInternational = in.nextLine();
		if (isInternational.equals("y"))
		{
			SearchClient.FRONT().syncNotify(new LocationNotification("greatfree", true));
		}
		else
		{
			SearchClient.FRONT().syncNotify(new LocationNotification("greatfree", false));
		}
		
		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			SearchUI.CS().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			// Convert the input string to integer. 09/21/2014, Bing Li
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);

			try
			{
				SearchUI.CS().send(option);
			}
			catch (ClassNotFoundException | RemoteReadException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		SearchClient.FRONT().dispose();
		in.close();
	}
}
