package edu.chainnet.sc.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

// Created: 10/19/2020, Bing Li
class ClientUI
{
	private Scanner in = new Scanner(System.in);

	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI BC()
	{
		if (instance == null)
		{
			instance = new ClientUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
		RegisterUI.BC().dispose();
		RetrieveUI.BC().dispose();
		SynchronizeUI.BC().dispose();
	}

	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		
		System.out.println(ClientMenu.REGISTER);
		System.out.println(ClientMenu.RETRIEVE);
		System.out.println(ClientMenu.UPDATE);
		
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}
	
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		int currentOption = RegisterOptions.NO_OPTION;
		String optionStr;
		switch (option)
		{
			case MenuOptions.REGISTER:
				while (currentOption != RegisterOptions.QUIT_REGISTER)
				{
					RegisterUI.BC().printMenu();
					optionStr = in.nextLine();
					try
					{
						currentOption = Integer.parseInt(optionStr);
						RegisterUI.BC().send(currentOption);
					}
					catch (NumberFormatException e)
					{
						currentOption = RegisterOptions.NO_OPTION;
						System.out.println(RegisterMenu.WRONG_OPTION);
					}
				}
				break;
				
			case MenuOptions.RETRIEVE:
				while (currentOption != RetrieveOptions.QUIT_RETRIEVE)
				{
					RetrieveUI.BC().printMenu();
					optionStr = in.nextLine();
					try
					{
						currentOption = Integer.parseInt(optionStr);
						RetrieveUI.BC().send(currentOption);
					}
					catch (NumberFormatException e)
					{
						currentOption = RetrieveOptions.NO_OPTION;
						System.out.println(RetrieveMenu.WRONG_OPTION);
					}
				}
				break;
				
			case MenuOptions.SYNCHRONIZE:
				while (currentOption != SynchronizeOptions.QUIT_SYNCHRONIZE)
				{
					SynchronizeUI.BC().printMenu();
					optionStr = in.nextLine();
					try
					{
						currentOption = Integer.parseInt(optionStr);
						SynchronizeUI.BC().send(currentOption);
					}
					catch (NumberFormatException e)
					{
						currentOption = SynchronizeOptions.NO_OPTION;
						System.out.println(SynchronizeMenu.WRONG_OPTION);
					}
				}
				break;
		}
	}
}
