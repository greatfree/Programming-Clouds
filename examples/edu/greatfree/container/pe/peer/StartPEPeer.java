package edu.greatfree.container.pe.peer;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.UtilConfig;

import edu.greatfree.container.p2p.message.ChatNotification;
import edu.greatfree.container.p2p.message.ChatPartnerRequest;
import edu.greatfree.cs.multinode.ChatConfig;
import edu.greatfree.p2p.message.ChatPartnerResponse;

/*
 * According to users' options, the peer can chat through either eventing or polling. Eventing happens when two nodes are located within the same LAN. Polling happens when two nodes are located in different LANs. 06/05/2020, LB
 */

// Created: 06/05/2020, Bing Li
class StartPEPeer
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		String isEventing = UtilConfig.EMPTY_STRING;
		Scanner in = new Scanner(System.in);
		System.out.println("Polling or Eventing? (p/e)");
		isEventing = in.nextLine();

		System.out.println("Tell me your user name: ");
		String username = in.nextLine();
		System.out.println("Tell me your partner: ");
		String partner = in.nextLine();

		System.out.println("Chatting peer starting up ...");
		ChatPeer.PE().start(username, ChatConfig.CHAT_SERVER_PORT, new ChatTask(), true);
		System.out.println("Chatting peer started ...");

		System.out.println("Are you ready? Press Enter to continue ... ");
		in.nextLine();

		if (!isEventing.equals(ChatConfig.EVENTING))
		{
			Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
			Scheduler.GREATFREE().submit(new Checker(partner), ChatConfig.CHAT_POLLING_DELAY	, ChatConfig.CHAT_POLLING_PERIOD);
		}

		ChatPartnerResponse response = (ChatPartnerResponse)ChatPeer.PE().read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new ChatPartnerRequest(ChatConfig.generateUserKey(partner)));

		String message = UtilConfig.EMPTY_STRING;
		
		if (isEventing.equals(ChatConfig.EVENTING))
		{
			do
			{
				System.out.println("EVENTING: Type your message ('q' to quit) : ...");
				message = in.nextLine();
				if (!message.equals("q"))
				{
					ChatPeer.PE().notify(response.getIP(), response.getPort(), new ChatNotification(message, username));
				}
			}
			while (!message.equals("q"));
		}
		else
		{
			do
			{
				System.out.println("POLLING: Type your message ('q' to quit) : ...");
				message = in.nextLine();
				if (!message.equals("q"))
				{
					ChatPeer.PE().notify(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new ChatNotification(message, username));
				}
			}
			while (!message.equals("q"));
		}

		if (!isEventing.equals(ChatConfig.EVENTING))
		{
			Scheduler.GREATFREE().shutdown(ChatConfig.SERVER_SHUTDOWN_TIMEOUT);
		}
		ChatPeer.PE().stop(ChatConfig.SERVER_SHUTDOWN_TIMEOUT);
		in.close();
	}

}
