package org.greatfree.dsf.cluster.cs.twonode.client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.chat.ChatOptions;
import org.greatfree.dsf.cluster.original.cs.twonode.message.ChatRegistryRequest;
import org.greatfree.dsf.cluster.original.cs.twonode.message.ChatRegistryResponse;
import org.greatfree.dsf.cs.twonode.client.ChatMaintainer;
import org.greatfree.dsf.cs.twonode.client.ClientMenu;
import org.greatfree.dsf.cs.twonode.client.MenuOptions;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.Tools;

// Created: 01/13/2019, Bing Li
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
	
	public static ClientUI CONTAINER()
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
		System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.CS().getPartner());
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	/*
	 * Send the users' option to the chatting server. 04/23/2017, Bing Li
	 */
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response response;
		List<ChatRegistryResponse> registryResponses;
		// Check the option to interact with the chatting server. 04/23/2017, Bing Li
		switch (option)
		{
			case MenuOptions.REGISTER_CHATTING:
				response = (Response)ChatClient.CONTAINER().read(new ChatRegistryRequest(ChatMaintainer.CS().getLocalUserKey(), ChatMaintainer.CS().getLocalUsername(), ChatMaintainer.CS().getLocalUsername() + " is a great & free guy!"));
				registryResponses = Tools.filter(response.getResponses(), ChatRegistryResponse.class);
				for (ChatRegistryResponse entry : registryResponses)
				{
					System.out.println("Chatting registry status: " + entry.isSucceeded());
				}
				break;
				
			case MenuOptions.START_CHATTING:
				int chatOption = ChatOptions.NO_OPTION;
				String optionStr;
				while (chatOption != ChatOptions.QUIT_CHAT)
				{
					ChatUI.CONTAINER().printMenu();
					optionStr = in.nextLine();
					try
					{
						chatOption = Integer.parseInt(optionStr);
						System.out.println("Your choice: " + option);
						ChatUI.CONTAINER().sent(chatOption);
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
