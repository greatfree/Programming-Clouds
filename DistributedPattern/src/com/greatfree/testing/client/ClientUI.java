package com.greatfree.testing.client;

import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.message.SignUpResponse;

/*
 * The class aims to print a menu list on the screen for users to interact with the client and communicate with the polling server. The menu is unique in the client such that it is implemented in the pattern of a singleton. 09/21/2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class ClientUI
{
	/*
	 * Initialize. 09/21/2014, Bing Li
	 */
	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 09/21/2014, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI FACE()
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

	/*
	 * Print the menu list on the screen. 09/21/2014, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.SIGN_UP);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.END);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	/*
	 * Send the users' option to the polling server. 09/21/2014, Bing Li
	 */
	public void send(int option)
	{
		SignUpResponse signUpResponse;

		// Check the option to interact with the polling server. 09/21/2014, Bing Li
		switch (option)
		{
			// If the sign up option is selected, send the request message to the polling server. 09/21/2014, Bing Li
			case MenuOptions.SIGN_UP:
				signUpResponse = ClientReader.signUp(ClientConfig.USERNAME, ClientConfig.PASSWORD);
				System.out.println(signUpResponse.isSucceeded());
				break;

			// If the quit option is selected, send the notification message to the polling server. 09/21/2014, Bing Li
			case MenuOptions.QUIT:
				break;
		}
	}
}
