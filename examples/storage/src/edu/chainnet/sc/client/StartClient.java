package edu.chainnet.sc.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

/*
 * The program is located at the side where the block-chain resides. Then, the chain interacts with the collaborator to register and retrieve through the client. 10/19/2020, Bing Li
 */

// Created: 10/19/2020, Bing Li
class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);
		String optionStr;
		BCClient.BC().init();
		while (option != MenuOptions.QUIT)
		{
			ClientUI.BC().printMenu();
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				ClientUI.BC().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		ClientUI.BC().dispose();
		BCClient.BC().dispose();
		in.close();
	}

}
