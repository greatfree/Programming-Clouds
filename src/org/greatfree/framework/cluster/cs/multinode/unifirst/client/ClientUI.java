package org.greatfree.framework.cluster.cs.multinode.unifirst.client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.chat.ChatOptions;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.cs.multinode.unifirst.message.AddPartnerNotification;
import org.greatfree.framework.cluster.cs.multinode.unifirst.message.ChatPartnerRequest;
import org.greatfree.framework.cluster.cs.multinode.unifirst.message.ChatRegistryRequest;
import org.greatfree.framework.cluster.cs.multinode.wurb.message.ChatPartnerResponse;
import org.greatfree.framework.cluster.cs.multinode.wurb.message.ChatRegistryResponse;
import org.greatfree.framework.cluster.cs.twonode.client.ChatClient;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.Tools;

// Created: 02/15/2019, Bing Li
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
	
	public static ClientUI UNICAST()
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
		System.out.println(ClientMenu.REGISTER_CHATTING + ChatMaintainer.UNIFIRST().getLocalUsername());
		System.out.println(ClientMenu.SEARCH_USER + ChatMaintainer.UNIFIRST().getPartner());
		System.out.println(ClientMenu.ADD_FRIEND + ChatMaintainer.UNIFIRST().getPartner());
		System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.UNIFIRST().getPartner());
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		List<ChatRegistryResponse> crReses;
		List<ChatPartnerResponse> cpReses;
		CollectedClusterResponse response;
		
		switch (option)
		{
			case MenuOptions.REGISTER_CHATTING:
				response = (CollectedClusterResponse)ChatClient.CONTAINER().read(new ChatRegistryRequest(ChatMaintainer.UNIFIRST().getLocalUserKey(), ChatMaintainer.UNIFIRST().getLocalUsername(), ChatMaintainer.UNIFIRST().getLocalUsername() + " is a great & free guy!"));
				crReses = Tools.filter(response.getResponses(), ChatRegistryResponse.class);
				for (ChatRegistryResponse entry : crReses)
				{
					System.out.println("Chatting registry status: " + entry.isSucceeded());
				}
				break;
				
			case MenuOptions.SEARCH_USER:
				response = (CollectedClusterResponse)ChatClient.CONTAINER().read(new ChatPartnerRequest(ChatMaintainer.UNIFIRST().getLocalUserKey(), ChatMaintainer.UNIFIRST().getPartnerKey()));
				cpReses = Tools.filter(response.getResponses(), ChatPartnerResponse.class);
				for (ChatPartnerResponse entry : cpReses)
				{
					System.out.println(entry.getUserName() + ": " + entry.getDescription());
				}
				break;

			case MenuOptions.ADD_FRIEND:
				ChatClient.CONTAINER().syncNotify(new AddPartnerNotification(ChatMaintainer.UNIFIRST().getLocalUserKey(), ChatMaintainer.UNIFIRST().getPartnerKey(), "Hello!"));
				break;

			case MenuOptions.START_CHATTING:
				int chatOption = ChatOptions.NO_OPTION;
				String optionStr;
				while (chatOption != ChatOptions.QUIT_CHAT)
				{
					ChatUI.UNIFIRST().printMenu();
					optionStr = in.nextLine();
					try
					{
						chatOption = Integer.parseInt(optionStr);
						System.out.println("Your choice: " + option);
						ChatUI.UNIFIRST().sent(chatOption);
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
