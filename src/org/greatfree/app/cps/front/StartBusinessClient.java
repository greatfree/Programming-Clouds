package org.greatfree.app.cps.front;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;

// Created: 08/14/2018, Bing Li
public class StartBusinessClient
{

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		BusinessReader.RR().init();
		BusinessEventer.RE().init();
		
		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			ClientUI.CPS().printMenu();
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.CPS().send(option);
			}
			catch (NumberFormatException | ClassNotFoundException | RemoteReadException | IOException | InterruptedException e)
			{
				e.printStackTrace();
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		BusinessEventer.RE().dispose(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);

		// Shutdown the remote reader. 11/23/2014, Bing Li
		BusinessReader.RR().shutdown();

		in.close();
	}

}
