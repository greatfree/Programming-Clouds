package org.greatfree.framework.cluster.cs.multinode.wurb.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.cs.twonode.client.ChatClient;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

/*

I am implementing the root based solution for the cluster-based CS chatting.

The previous solution that the persistent/writing messages are randomly unicast.

That is not a proper design since one user's information might not reside on a single server.

It causes inconsistency issues.

For example, if one ChatNotification needs to be saved on a randomly selected server, it gets exceptions because the sender's information which is required to keep the notification is not on the server.

Thus, it is required to unicast the persistent/writing messages to the clusters according the nearest approach rather than the random one.

The above descriptions are suitable to chatting at least.

Since my underlying distributed mechanism is open to developers, they can design their own strategies flexibly according to the requirements of specific applications.

So the solution is revised as follows.

1) All the messages are categorized into two classes:

	The persistent/writing ones
	
	The volatile/reading ones
	
2) The persistent/writing messages should be distributed to specific children according to their message keys, e.g., the client keys, NOT randomly, through unicasting.

3) The volatile/reading ones are usually requests or queries, which should be broadcast.

4) For the case of CS chatting, the below messages are persistent,

	ChatRegistryRequest

	AddPartnerNotification
	
	ChatNotification

5) For the case of CS chatting, the below messages are volatile,

	ChatPartnerRequest
	
	PollNewSessionsRequest
	
	PollNewChatsRequest
	
6) The solution spends high-cost on broadcasting.

7) In the solution, no interactions between children of the cluster.

8) I will design another solution that is based on the children interactions.

9) For cluster programmers, when the fundamental functions are available, they are required to design the specific strategies according to their applications.

 */

// Created: 01/28/2019, Bing Li
class StartChatClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = MenuOptions.NO_OPTION;

		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);
		
		// Input the local user name. 05/26/2017, Bing Li
		System.out.println("Tell me your user name: ");
		
		String username = in.nextLine();

		// Input the chatting partner name. 05/26/2017, Bing Li
		System.out.println("Tell me your partner: ");
		
		String partner = in.nextLine();

		ChatClient.CONTAINER().init();

		Scheduler.GREATFREE().init(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		ChatMaintainer.CLUSTER_CONTAINER().init(username, partner);
		
		Scheduler.GREATFREE().submit(new Checker(), ChatConfig.CHAT_POLLING_DELAY	, ChatConfig.CHAT_POLLING_PERIOD);
		
//		ChatMaintainer.CLUSTER_CONTAINER().checkNewSessions();
	
		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			ClientUI.CLUSTER_CONTAINER().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.CLUSTER_CONTAINER().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		// Set the status of the local node as shutdown. 05/26/2017, Bing Li
		// Set the terminating flag to true. 09/21/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();

		// Dispose the chatting maintainer. 05/26/2017, Bing Li
		ChatMaintainer.CLUSTER_CONTAINER().dispose();
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
		Scheduler.GREATFREE().shutdown(RegistryConfig.SCHEDULER_SHUTDOWN_TIMEOUT);

		ChatClient.CONTAINER().dispose();

		in.close();
	}

}
