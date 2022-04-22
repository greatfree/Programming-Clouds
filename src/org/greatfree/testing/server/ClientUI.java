package org.greatfree.testing.server;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatOptions;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.framework.p2p.message.ChatRegistryResponse;
import org.greatfree.framework.p2p.peer.ChatMaintainer;

// Created: 01/12/2019, Bing Li
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
	
	public static ClientUI TEST()
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
		System.out.println(ClientMenu.REGISTER_CHATTING + ChatMaintainer.PEER().getLocalUsername());
		System.out.println(ClientMenu.SEARCH_USER + ChatMaintainer.PEER().getPartner());
		System.out.println(ClientMenu.ADD_FRIEND + ChatMaintainer.PEER().getPartner());
		System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.PEER().getPartner());
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	/*
	 * Send the users' option to the chatting server. 04/23/2017, Bing Li
	 */
	public void send(org.greatfree.testing.server.ChatPeer peer, int option) throws ClassNotFoundException, RemoteReadException, IOException
	{
		ChatRegistryResponse chatRegistryResponse;
		ChatPartnerResponse chatPartnerResponse = null;

		switch (option)
		{
			case MenuOptions.REGISTER_CHATTING:
				chatRegistryResponse = peer.registerChat(ChatMaintainer.PEER().getLocalUserKey(), ChatMaintainer.PEER().getLocalUsername(), ChatMaintainer.PEER().getLocalUsername() + " is a good guy", "Computer science is interesting?");
				System.out.println("Chatting registry status: " + chatRegistryResponse.isSucceeded());
				break;
				
			case MenuOptions.SEARCH_USER:
				chatPartnerResponse = peer.searchUser(ChatMaintainer.PEER().getPartnerKey());
				System.out.println(chatPartnerResponse.getUserName() + ": " + chatPartnerResponse.getDescription());
				System.out.println(chatPartnerResponse.getUserName() + ": " + chatPartnerResponse.getPreference());
				System.out.println(chatPartnerResponse.getUserName() + " is located at " + chatPartnerResponse.getIP() + ":" + chatPartnerResponse.getPort());
				ChatMaintainer.PEER().setPartnerIP(chatPartnerResponse.getIP());
				ChatMaintainer.PEER().setPartnerPort(chatPartnerResponse.getPort());
				
				break;
				
			case MenuOptions.ADD_FRIEND:
				if (ChatMaintainer.PEER().getPartnerIP() != null)
				{
					peer.notifyAddFriend(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), ChatMaintainer.PEER().getLocalUsername(), ChatMaintainer.PEER().getPartner());
				}
				else
				{
					System.out.println("You need to add search your partner, " + ChatMaintainer.PEER().getPartner() + ", first before add her as a friend!");
				}
				break;
				
			case MenuOptions.START_CHATTING:
				int chatOption = ChatOptions.NO_OPTION;
				String optionStr;
				while (chatOption != ChatOptions.QUIT_CHAT)
				{
					ChatUI.TEST().printMenu();
					optionStr = in.nextLine();
					chatOption = Integer.parseInt(optionStr);
					System.out.println("Your choice: " + option);
					ChatUI.TEST().sent(peer, chatOption);
				}
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}
}
