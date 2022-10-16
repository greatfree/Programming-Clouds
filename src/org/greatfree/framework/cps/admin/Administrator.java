package org.greatfree.framework.cps.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.admin.AdminConfig;
import org.greatfree.data.ServerConfig;

// Created: 07/07/2018, Bing Li
public class Administrator
{

	public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException
	{
		// Initialize the option, which represents the commands. 11/27/2014, Bing Li
		int option = AdminConfig.NO_OPTION;
		// Initialize a scanner to wait for the administrator's commands. 11/27/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		CPSAdminEventer.RE().init();
		
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
				System.out.println(CPSMenu.WRONG_OPTION);
			}
		}

		CPSAdminEventer.RE().dispose(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		in.close();
	}

	/*
	 * Print the console menu for the administrator. 11/27/2014, Bing Li
	 */
	private static void printMenu()
	{
		System.out.println(CPSMenu.MENU_HEAD);
		System.out.println(CPSMenu.STOP_COORDINATOR);
		System.out.println(CPSMenu.STOP_TERMINAL);
		System.out.println(CPSMenu.END);
		System.out.println(CPSMenu.MENU_TAIL);
		System.out.println(CPSMenu.INPUT_PROMPT);
	}

	/*
	 * Notify the chatting server to manage the distributed system. 11/27/2014, Bing Li
	 */
	private static void notifyServer(int option)
	{
		// Check the option of the administrator. 11/27/2014, Bing Li
		switch (option)
		{
			// Shutdown the coordinator server. 11/27/2014, Bing Li
			case CPSConfig.STOP_COORDINATOR:
				System.out.println(CPSMenu.STOP_COORDINATOR);
				// Notify the coordinator server to shutdown. 11/27/2014, Bing Li
				CPSAdminEventer.RE().notifyShutdownCoordinatorServer();
				break;
				
			// Shutdown the terminal server. 11/27/2014, Bing Li
			case CPSConfig.STOP_TERMINAL:
				System.out.println(CPSMenu.STOP_TERMINAL);
				// Notify the terminal server to shutdown. 11/27/2014, Bing Li
				CPSAdminEventer.RE().notifyShutdownTerminalServer();
				break;
		}
	}
	
}
