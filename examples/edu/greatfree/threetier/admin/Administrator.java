package edu.greatfree.threetier.admin;

import java.io.IOException;
import java.util.Scanner;

// Created: 05/06/2019, Bing Li
class Administrator
{

	public static void main(String[] args) throws IOException
	{
		// Initialize the option, which represents the commands. 11/27/2014, Bing Li
		int option = AdminConfig.NO_OPTION;
		// Initialize a scanner to wait for the administrator's commands. 11/27/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		AdminEventer.RE().init();
		
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
			catch (NumberFormatException | IOException | InterruptedException e)
			{
				option = AdminConfig.NO_OPTION;
				System.out.println(AdminMenu.WRONG_OPTION);
			}
		}

		AdminEventer.RE().dispose();
		in.close();
	}

	private static void printMenu()
	{
		System.out.println(AdminMenu.MENU_HEAD);
		System.out.println(AdminMenu.STOP_COORDINATOR);
		System.out.println(AdminMenu.STOP_TERMINAL);
		System.out.println(AdminMenu.END);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}

	private static void notifyServer(int option) throws IOException, InterruptedException
	{
		// Check the option of the administrator. 11/27/2014, Bing Li
		switch (option)
		{
			case AdminConfig.STOP_COORDINATOR:
				System.out.println(AdminMenu.STOP_COORDINATOR);
				AdminEventer.RE().shutdownCoordinator();
				break;
				
			case AdminConfig.STOP_TERMINAL:
				System.out.println(AdminMenu.STOP_TERMINAL);
				AdminEventer.RE().shutdownTerminal();
				break;
		}
	}
	
}
