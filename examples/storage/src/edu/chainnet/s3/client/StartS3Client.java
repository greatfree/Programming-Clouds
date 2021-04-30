package edu.chainnet.s3.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.s3.Setup;

/*
 * The program is the client of the S3. 07/09/2020, Bing Li
 */

// Created: 07/09/2020, Bing Li
class StartS3Client
{
	
	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		if (Setup.setupClient())
		{
			int option = MenuOptions.NO_OPTION;
			Scanner in = new Scanner(System.in);
			String optionStr;

			S3Client.CLIENT().init(Setup.S3_CONFIG_XML, Setup.CLIENT_CONFIG_XML);
			while (option != MenuOptions.QUIT)
			{
				ClientUI.CLIENT().printMenu();
				optionStr = in.nextLine();
				try
				{
					option = Integer.parseInt(optionStr);
					System.out.println("Your choice: " + option);
					ClientUI.CLIENT().execute(option);
				}
				catch (NumberFormatException e)
				{
					option = MenuOptions.NO_OPTION;
					System.out.println(ClientMenu.WRONG_OPTION);
				}
			}

			ClientUI.CLIENT().dispose();
			S3Client.CLIENT().dispose();
			in.close();
		}
		else
		{
			System.out.println("The client is not set up properly!");
		}
	}
}
