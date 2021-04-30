package edu.chainnet.sc.client.api;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.sc.SCConfig;
import edu.chainnet.sc.client.ClientMenu;
import edu.chainnet.sc.client.MenuOptions;

/*
 * The APIs to access the collaborator tier are tested. 10/24/2020, Bing Li
 */

// Created: 10/24/2020, Bing Li
class CollaboratorClientTester
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);
		String optionStr;
		CollaboratorClient.CHAIN().init(SCConfig.REGISTRY_SERVER_IP, SCConfig.REGISTRY_SERVER_PORT);
		while (option != MenuOptions.QUIT)
		{
			ClientUI.TEST().printMenu();
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				ClientUI.TEST().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}
		
		ClientUI.TEST().dispose();
		CollaboratorClient.CHAIN().dispose();
		in.close();
	}

}
