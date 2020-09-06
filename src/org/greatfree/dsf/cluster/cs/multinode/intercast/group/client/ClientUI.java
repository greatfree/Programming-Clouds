package org.greatfree.dsf.cluster.cs.multinode.intercast.group.client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.chat.ChatOptions;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.GroupMembersRequest;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.GroupMembersResponse;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.GroupRegistryRequest;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.GroupRegistryResponse;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.GroupSearchRequest;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.GroupSearchResponse;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.InviteUserNotification;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.JoinGroupNotification;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.LeaveGroupNotification;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.RemoveUserNotification;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.UserRegistryRequest;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.UserRegistryResponse;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.UserSearchRequest;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.UserSearchResponse;
import org.greatfree.dsf.cluster.cs.twonode.client.ChatClient;
import org.greatfree.dsf.cs.multinode.server.CSAccount;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.Tools;

// Created: 04/06/2019, Bing Li
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
	
	public static ClientUI GROUP()
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
	
	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		if (ChatMaintainer.GROUP().isGroupCreator())
		{
			System.out.println(ClientMenu.REGISTER_USER + ChatMaintainer.GROUP().getLocalUsername());
			System.out.println(ClientMenu.REGISTER_GROUP + ChatMaintainer.GROUP().getGroupName());
			System.out.println(ClientMenu.SEARCH_USER + ChatMaintainer.GROUP().getOneMemberName());
			System.out.println(ClientMenu.INVITE_USER + ChatMaintainer.GROUP().getOneMemberName());
			System.out.println(ClientMenu.REMOVE_USER + ChatMaintainer.GROUP().getOneMemberName());
			System.out.println(ClientMenu.SEARCH_GROUP_MEMBERS + ChatMaintainer.GROUP().getGroupName());
			System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.GROUP().getGroupName());
		}
		else
		{
			System.out.println(ClientMenu.REGISTER_USER + ChatMaintainer.GROUP().getLocalUsername());
			System.out.println(ClientMenu.SEARCH_GROUP + ChatMaintainer.GROUP().getGroupName());
			System.out.println(ClientMenu.JOIN_GROUP + ChatMaintainer.GROUP().getGroupName());
			System.out.println(ClientMenu.LEAVE_GROUP + ChatMaintainer.GROUP().getGroupName());
			System.out.println(ClientMenu.SEARCH_GROUP_MEMBERS + ChatMaintainer.GROUP().getGroupName());
			System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.GROUP().getGroupName());
		}
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}
	
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		List<GroupRegistryResponse> grReses;
		List<UserRegistryResponse> crReses;
		List<GroupSearchResponse> gsReses;
		List<UserSearchResponse> usReses;
		List<GroupMembersResponse> gmReses;
		Response response;
		
		switch (option)
		{
			case MenuOptions.REGISTER_USER:
				response = (Response)ChatClient.CONTAINER().read(new UserRegistryRequest(ChatMaintainer.GROUP().getLocalUserKey(), ChatMaintainer.GROUP().getLocalUsername(), ChatMaintainer.GROUP().getLocalUsername() + " is a great & free guy!", ChatMaintainer.GROUP().getLocalUsername()));
				crReses = Tools.filter(response.getResponses(), UserRegistryResponse.class);
				for (UserRegistryResponse entry : crReses)
				{
					System.out.println("User chatting registry status: " + entry.isSucceeded());
				}
				break;

			case MenuOptions.REGISTER_GROUP:
				response = (Response)ChatClient.CONTAINER().read(new GroupRegistryRequest(ChatMaintainer.GROUP().getLocalUserKey(), ChatMaintainer.GROUP().getGroupKey(), ChatMaintainer.GROUP().getGroupName(), ChatMaintainer.GROUP().getGroupName() + " is a great & free group!", ChatMaintainer.GROUP().getLocalUsername(), ChatMaintainer.GROUP().getLocalUsername() + " is a great & free guy!"));
				grReses = Tools.filter(response.getResponses(), GroupRegistryResponse.class);
				for (GroupRegistryResponse entry : grReses)
				{
					System.out.println("Group chatting registry status: " + entry.isSucceeded());
				}
				break;
				
			case MenuOptions.SEARCH_GROUP:
				response = (Response)ChatClient.CONTAINER().read(new GroupSearchRequest(ChatMaintainer.GROUP().getLocalUserKey(), ChatMaintainer.GROUP().getGroupKey(), ChatMaintainer.GROUP().getLocalUsername()));
				gsReses = Tools.filter(response.getResponses(), GroupSearchResponse.class);
				for (GroupSearchResponse entry : gsReses)
				{
					System.out.println(entry.getGroupName() + ": " + entry.getDescription());
				}
				break;
				
			case MenuOptions.SEARCH_USER:
				response = (Response)ChatClient.CONTAINER().read(new UserSearchRequest(ChatMaintainer.GROUP().getGroupKey(), ChatMaintainer.GROUP().getOneMemberKey(), ChatMaintainer.GROUP().getLocalUsername()));
				usReses = Tools.filter(response.getResponses(), UserSearchResponse.class);
				for (UserSearchResponse entry : usReses)
				{
					System.out.println(entry.getUserName() + ": " + entry.getDescription());
				}
				break;
				
			case MenuOptions.JOIN_GROUP:
				ChatClient.CONTAINER().syncNotify(new JoinGroupNotification(ChatMaintainer.GROUP().getLocalUserKey(), ChatMaintainer.GROUP().getGroupKey(), ChatMaintainer.GROUP().getLocalUsername()));
				break;
				
			case MenuOptions.LEAVE_GROUP:
				ChatClient.CONTAINER().syncNotify(new LeaveGroupNotification(ChatMaintainer.GROUP().getLocalUserKey(), ChatMaintainer.GROUP().getGroupKey(), ChatMaintainer.GROUP().getLocalUsername()));
				break;
				
			case MenuOptions.INVITE_USER:
				ChatClient.CONTAINER().syncNotify(new InviteUserNotification(ChatMaintainer.GROUP().getGroupKey(), ChatMaintainer.GROUP().getOneMemberKey(), ChatMaintainer.GROUP().getLocalUsername()));
				break;
				
			case MenuOptions.REMOVE_USER:
				ChatClient.CONTAINER().syncNotify(new RemoveUserNotification(ChatMaintainer.GROUP().getGroupKey(), ChatMaintainer.GROUP().getOneMemberKey(), ChatMaintainer.GROUP().getLocalUsername()));
				break;
				
			case MenuOptions.SEARCH_GROUP_MEMBERS:
				response = (Response)ChatClient.CONTAINER().read(new GroupMembersRequest(ChatMaintainer.GROUP().getLocalUserKey(), ChatMaintainer.GROUP().getGroupKey(), ChatMaintainer.GROUP().getLocalUsername()));
				gmReses = Tools.filter(response.getResponses(), GroupMembersResponse.class);
				for (GroupMembersResponse entry : gmReses)
				{
					for (CSAccount account : entry.getAccounts())
					{
						System.out.println(account.getUserName() + ": " + account.getDescription());
						ChatMaintainer.GROUP().addMember(account);
					}
				}
				break;
				
			case MenuOptions.START_CHATTING:
				int chatOption = ChatOptions.NO_OPTION;
				String optionStr;
				while (chatOption != ChatOptions.QUIT_CHAT)
				{
					ChatUI.GROUP().printMenu();
					optionStr = in.nextLine();
					try
					{
						chatOption = Integer.parseInt(optionStr);
						System.out.println("Your choice: " + option);
						ChatUI.GROUP().sent(chatOption);
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
