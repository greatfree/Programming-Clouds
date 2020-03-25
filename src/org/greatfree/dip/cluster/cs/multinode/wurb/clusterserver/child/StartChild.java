package org.greatfree.dip.cluster.cs.multinode.wurb.clusterserver.child;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
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

// Created: 02/04/2019, Bing Li
class StartChild
{

	public static void main(String[] args)
	{
		System.out.println("Chatting child starting up ...");

		try
		{
			ChatChild.CLUSTER_CONTAINER().start(new ChatTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | InterruptedException e)
		{
			e.printStackTrace();
		}

		System.out.println("Chatting child started ...");

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
