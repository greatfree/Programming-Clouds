package edu.greatfree.cs.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.data.ClientConfig;

import edu.greatfree.cs.multinode.ChatConfig;
import edu.greatfree.cs.multinode.ClientMenu;
import edu.greatfree.threetier.admin.AdminConfig;

//Created: 05/13/2018, Bing Li
class Administrator
{

	public static void main(String[] args)
	{
		// Initialize the option, which represents the commands. 11/27/2014, Bing Li
		int option = AdminConfig.NO_OPTION;
		// Initialize a scanner to wait for the administrator's commands. 11/27/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		// Initialize the administrator eventer. 05/27/2017, Bing Li
		ChatAdminEventer.RE().init();
		
		while (option != AdminConfig.END)
		{
			// Print the console menu. 04/17/2017, Bing Li
			printMenu();
			// Wait for the administrator's input from the console. 11/27/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the string to the integer. 11/27/2014, Bing Li
				option = Integer.parseInt(optionStr);
				// Print the option. 11/27/2014, Bing Li
				System.out.println("Your choice is: " + option);
				// Notify the coordinator. 11/27/2014, Bing Li
				notifyServer(option);
			}
			catch (NumberFormatException e)
			{
				option = AdminConfig.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		// Dispose the eventer. 11/27/2014, Bing Li
		try
		{
			ChatAdminEventer.RE().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		}
		catch (InterruptedException | IOException e)
		{
			e.printStackTrace();
		}
		
		in.close();
	}

	/*
	 * Print the console menu for the administrator. 11/27/2014, Bing Li
	 */
	private static void printMenu()
	{
		System.out.println(AdminMenu.MENU_HEAD);
		System.out.println(AdminMenu.STOP_CHATTING_SERVER);
		System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
		System.out.println(AdminMenu.END);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}

	/*
	 * Notify the chatting server to manage the distributed system. 11/27/2014, Bing Li
	 */
	private static void notifyServer(int option)
	{
		// Check the option of the administrator. 11/27/2014, Bing Li
		switch (option)
		{
			// Shutdown the chatting server. 11/27/2014, Bing Li
			case ChatConfig.STOP_CS_CHATTING_SERVER:
				System.out.println(AdminMenu.STOP_CHATTING_SERVER);
				// Notify the CS chatting server to shutdown. 11/27/2014, Bing Li
				ChatAdminEventer.RE().notifyShutdownCSChatServer();
				break;
				
			case ChatConfig.STOP_CHATTING_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
				ChatAdminEventer.RE().notifyShutdownRegistryServer();
				break;
		}
	}
}
