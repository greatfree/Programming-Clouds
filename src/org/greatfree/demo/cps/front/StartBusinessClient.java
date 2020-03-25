package org.greatfree.demo.cps.front;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.app.cps.front.ClientMenu;
import org.greatfree.app.cps.front.MenuOptions;
import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/28/2019, Bing Li
class StartBusinessClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();

		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			ClientUI.CPS_CONTAINER().printMenu();
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.CPS_CONTAINER().send(option);
			}
			catch (NumberFormatException | ClassNotFoundException | RemoteReadException | IOException | InterruptedException e)
			{
				e.printStackTrace();
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}
		
		StandaloneClient.CS().dispose();
		
		in.close();

	}

}
