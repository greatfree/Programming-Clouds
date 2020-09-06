package org.greatfree.dsf.cluster.cs.twonode.clusterclient;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.chat.ChatOptions;
import org.greatfree.dsf.cluster.original.cs.twonode.message.AreYouReadyRequest;
import org.greatfree.dsf.cluster.original.cs.twonode.message.AreYouReadyResponse;
import org.greatfree.dsf.cluster.original.cs.twonode.message.ChatRegistryRequest;
import org.greatfree.dsf.cluster.original.cs.twonode.message.ChatRegistryResponse;
import org.greatfree.dsf.cluster.original.cs.twonode.message.ChatRegistryResultNotification;
import org.greatfree.dsf.cs.twonode.client.ChatMaintainer;
import org.greatfree.dsf.cs.twonode.client.ClientMenu;
import org.greatfree.dsf.cs.twonode.client.MenuOptions;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.Rand;
import org.greatfree.util.Tools;

// Created: 01/15/2019, Bing Li
class ClientTasks
{
	public static void register() throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		Response response = (Response)ChatClient.CCC().read(new ChatRegistryRequest(ChatMaintainer.CS().getLocalUserKey(), ChatMaintainer.CS().getLocalUsername(), ChatMaintainer.CS().getLocalUsername() + " is a great & free guy!"));
		List<ChatRegistryResponse> registryResponses = Tools.filter(response.getResponses(), ChatRegistryResponse.class);
		boolean isSucceeded = true;
		for (ChatRegistryResponse entry : registryResponses)
		{
			System.out.println("Chatting registry status: " + entry.isSucceeded());
			if (!entry.isSucceeded())
			{
				isSucceeded = false;
			}
		}
		ChatClient.CCC().asyncBroadcastNotify(new ChatRegistryResultNotification(isSucceeded));
	}
	
	public static void chat(int option, Scanner in) throws DistributedNodeFailedException, IOException
	{
		List<AreYouReadyResponse> readyResponses = Tools.filter(ChatClient.CCC().broadcastRead(new AreYouReadyRequest("rsc" + Rand.getNextLong())), AreYouReadyResponse.class);
		boolean isReady = true;
		for (AreYouReadyResponse entry : readyResponses)
		{
			System.out.println(entry.getResourceKey() + " is ready? " + entry.isReady());
			if (!entry.isReady())
			{
				isReady = false;
			}
		}
		
		if (isReady)
		{
			System.out.println("Client cluster is ready to chat ...");
			int chatOption = ChatOptions.NO_OPTION;
			String optionStr;
			while (chatOption != ChatOptions.QUIT_CHAT)
			{
				ChatUI.CCC().printMenu();
				optionStr = in.nextLine();
				try
				{
					chatOption = Integer.parseInt(optionStr);
					System.out.println("Your choice: " + option);
					ChatUI.CCC().sent(chatOption);
				}
				catch (NumberFormatException e)
				{
					chatOption = MenuOptions.NO_OPTION;
					System.out.println(ClientMenu.WRONG_OPTION);
				}
			}
		}
		else
		{
			System.out.println("Client cluster is NOT ready!");
		}
	}
}
