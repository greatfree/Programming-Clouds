package org.greatfree.framework.multicast.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.MenuOptions;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.client.ClientMenu;

// Created: 08/26/2018, Bing Li
final class StartClient
{

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException
	{
		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = MenuOptions.NO_OPTION;

		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		System.out.println("Multicast client starting up ...");

		try
		{
			MulticastClient.FRONT().init();
		}
		catch (ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Multicast client started ...");

		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			MulticastClientUI.FRONT().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			// Convert the input string to integer. 09/21/2014, Bing Li
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);

			try
			{
				MulticastClientUI.FRONT().send(option);
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | RemoteReadException | IOException | InterruptedException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}
		
		MulticastClient.FRONT().dispose();
		in.close();
	}

}
