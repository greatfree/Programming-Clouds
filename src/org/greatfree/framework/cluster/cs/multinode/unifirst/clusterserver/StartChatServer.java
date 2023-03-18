package org.greatfree.framework.cluster.cs.multinode.unifirst.clusterserver;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cluster.cs.twonode.clusterserver.ChatServer;
import org.greatfree.framework.cluster.cs.twonode.clusterserver.ChatServerTask;
import org.greatfree.util.TerminateSignal;

/*

I am considering how to use it to improve the chatting and whether I can use it in other applications.

1) In the previous version of chatting, all writing messages are performed in the way of unicasting and reading messages are performed in the way of broadcasting.

2) The disadvantage for the solution is that reading messages consume high volume of bandwidth.

3) If it is required to save the resource of bandwidth, how to use intercast messages to achieve the goal?

4) To do that, the chatting related notifications, e.g., AddPartnerNotification and ChatNotification, are allowed to transmit in the way of intercasting.

5) Thus, the chatting messages are saved in both the sender and the receiver, which are the children of a cluster.

6) Then, the polling requests, e.g., PollNewSessionsRequest and PollNewChatsRequest, can be performed in the way of unicasting to the receiver's child in the cluster only.

7) The above example is one application case of intercast notifications.

 */

// Created: 02/10/2019, Bing Li
class StartChatServer
{

	public static void main(String[] args) throws RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Chatting server starting up ...");

		try
		{
			ChatServer.CONTAINER().start(ServerConfig.COORDINATOR_PORT, new ChatServerTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}

		System.out.println("Chatting server started ...");

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
