package edu.greatfree.container.cs.multinode.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;

import edu.greatfree.container.cs.multinode.message.AddPartnerNotification;
import edu.greatfree.container.cs.multinode.message.ChatPartnerRequest;
import edu.greatfree.container.cs.multinode.message.ChatRegistryRequest;
import edu.greatfree.cs.multinode.ChatConfig;
import edu.greatfree.cs.multinode.ChatOptions;
import edu.greatfree.cs.multinode.ClientMenu;
import edu.greatfree.cs.multinode.MenuOptions;
import edu.greatfree.cs.multinode.message.ChatPartnerResponse;
import edu.greatfree.cs.multinode.message.ChatRegistryResponse;

// Created: 01/07/2019, Bing Li, in the airplane from Zhuhai to Xi'An
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
	
	public static ClientUI CS_CONTAINER()
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
		System.out.println(ClientMenu.REGISTER_CHATTING + ChatMaintainer.CS_CONTAINER().getLocalUsername());
		System.out.println(ClientMenu.SEARCH_USER + ChatMaintainer.CS_CONTAINER().getPartner());
		System.out.println(ClientMenu.ADD_FRIEND + ChatMaintainer.CS_CONTAINER().getPartner());
		System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.CS_CONTAINER().getPartner());
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	/*
	 * Send the users' option to the chatting server. 04/23/2017, Bing Li
	 */
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		ChatRegistryResponse chatRegistryResponse;
		ChatPartnerResponse chatPartnerResponse;
		
		// Check the option to interact with the chatting server. 04/23/2017, Bing Li
		switch (option)
		{
			case MenuOptions.REGISTER_CHATTING:
				chatRegistryResponse = (ChatRegistryResponse)
				StandaloneClient.CS().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatRegistryRequest(ChatMaintainer.CS_CONTAINER().getLocalUserKey(), ChatMaintainer.CS_CONTAINER().getLocalUsername(), ChatMaintainer.CS_CONTAINER().getLocalUsername() + " is a great & free guy!"));
				System.out.println("Chatting registry status: " + chatRegistryResponse.isSucceeded());
				break;
				
			case MenuOptions.SEARCH_USER:
				chatPartnerResponse = (ChatPartnerResponse)
				StandaloneClient.CS().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatPartnerRequest(ChatMaintainer.CS_CONTAINER().getPartnerKey()));
				System.out.println(chatPartnerResponse.getUserName() + ": " + chatPartnerResponse.getDescription());
				break;
				
			case MenuOptions.ADD_FRIEND:
				StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new AddPartnerNotification(ChatMaintainer.CS_CONTAINER().getLocalUserKey(), ChatMaintainer.CS_CONTAINER().getPartnerKey(), "Hello!"));
				break;
				
			case MenuOptions.START_CHATTING:
				int chatOption = ChatOptions.NO_OPTION;
				String optionStr;
				while (chatOption != ChatOptions.QUIT_CHAT)
				{
					ChatUI.CS_CONTAINER().printMenu();
					optionStr = in.nextLine();
					try
					{
						chatOption = Integer.parseInt(optionStr);
						System.out.println("Your choice: " + option);
						ChatUI.CS_CONTAINER().sent(chatOption);
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
