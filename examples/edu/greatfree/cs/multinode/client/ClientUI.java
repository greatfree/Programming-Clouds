package edu.greatfree.cs.multinode.client;

import java.util.Scanner;

import org.greatfree.chat.ChatOptions;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;

import edu.greatfree.cs.multinode.message.ChatPartnerResponse;
import edu.greatfree.cs.multinode.message.ChatRegistryResponse;

/*
 * The class aims to print a menu list on the screen for users to interact with the client and communicate with the chatting server. The menu is unique in the client such that it is implemented in the pattern of a singleton. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
class ClientUI
{
	private Scanner in = new Scanner(System.in);

	/*
	 * Initialize. 04/23/2017, Bing Li
	 */
	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI CS()
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
	}
	
	/*
	 * Print the menu list on the screen. 04/23/2017, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.REGISTER_CHATTING + ChatMaintainer.CS().getLocalUsername());
		System.out.println(ClientMenu.SEARCH_USER + ChatMaintainer.CS().getPartner());
		System.out.println(ClientMenu.ADD_FRIEND + ChatMaintainer.CS().getPartner());
		System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.CS().getPartner());
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}
	
	/*
	 * Send the users' option to the chatting server. 04/23/2017, Bing Li
	 */
	public void send(int option)
	{
		ChatRegistryResponse chatRegistryResponse;
		ChatPartnerResponse chatPartnerResponse;
		
		// Check the option to interact with the chatting server. 04/23/2017, Bing Li
		switch (option)
		{
			case MenuOptions.REGISTER_CHATTING:
				chatRegistryResponse = ChatReader.RR().registerChat(ChatMaintainer.CS().getLocalUserKey(), ChatMaintainer.CS().getLocalUsername(), ChatMaintainer.CS().getLocalUsername() + " is a great & free guy!");
				System.out.println("Chatting registry status: " + chatRegistryResponse.isSucceeded());
				break;
				
			case MenuOptions.SEARCH_USER:
				chatPartnerResponse = ChatReader.RR().searchUser(ChatMaintainer.CS().getPartnerKey());
				System.out.println(chatPartnerResponse.getUserName() + ": " + chatPartnerResponse.getDescription());
				break;
				
			case MenuOptions.ADD_FRIEND:
				ChatEventer.RE().notifyAddFriend();
				break;
				
			case MenuOptions.START_CHATTING:
				int chatOption = ChatOptions.NO_OPTION;
				String optionStr;
				while (chatOption != ChatOptions.QUIT_CHAT)
				{
					ChatUI.CS().printMenu();
					optionStr = in.nextLine();
					try
					{
						chatOption = Integer.parseInt(optionStr);
						System.out.println("Your choice: " + option);
						ChatUI.CS().sent(chatOption);
					}
					catch (NumberFormatException e)
					{
						chatOption = MenuOptions.NO_OPTION;
						System.out.println(ClientMenu.WRONG_OPTION);
					}
				}
				
				break;
				
			case MenuOptions.QUIT:
				break;
		}
		
	}
}
