package org.greatfree.framework.cluster.replication.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.replication.ReplicationConfig;
import org.greatfree.framework.cluster.replication.message.ReplicationTaskNotification;
import org.greatfree.framework.cluster.replication.message.ReplicationTaskRequest;
import org.greatfree.framework.cluster.replication.message.ReplicationTaskResponse;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * The client sends replication notifications/requests to the replication cluster. 09/07/2020, Bing Li
 */

// Created: 09//07/2020, Bing Li
class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, RemoteIPNotExistedException
	{
		StandaloneClient.CS().init();
		Scanner in = new Scanner(System.in);

		IPAddress rootIP = StandaloneClient.CS().getIPAddress(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, ReplicationConfig.REPLICATION_ROOT_KEY);
		System.out.println("The IP of the pool cluster is " + rootIP);
		
		String option = UtilConfig.EMPTY_STRING;
//		List<ReplicationTaskResponse> responses;
		CollectedClusterResponse response;
		ReplicationTaskResponse rtr;
		String message = "Hello";
		String msgKey = Tools.getHash(message);
		
		int partitionIndex = 0;
		while (!option.equals("q"))
		{
			System.out.println("partition index = " + partitionIndex);
			System.out.println("Press Enter to send ReplicationTaskNotification to the Replication cluster ...");
			option = in.nextLine();
			StandaloneClient.CS().syncNotify(rootIP.getIP(), rootIP.getPort(), new ReplicationTaskNotification(message, partitionIndex));
			
			/*
			 * The retrieval is performed using normal broadcasting request since the data is replicated in a randomly selected partition. 09/07/2020, Bing Li
			 */
			System.out.println("Press Enter to send ReplicationTaskRequest to the Replication cluster ...");
			option = in.nextLine();
//			response = (Response)StandaloneClient.CS().read(rootIP.getIP(), rootIP.getPort(), new ReplicationTaskRequest(msgKey, message, 0));
			response = (CollectedClusterResponse)StandaloneClient.CS().read(rootIP.getIP(), rootIP.getPort(), new ReplicationTaskRequest(msgKey, message, partitionIndex));
			/*
			responses = Tools.filter(response.getResponses(), ReplicationTaskResponse.class);
			for (ReplicationTaskResponse entry : responses)
			{
				System.out.println("childKey = " + entry.getChildrenKey() + ": message = " + entry.getMessage());
			}
			*/
			rtr = (ReplicationTaskResponse)response.getResponse();
			System.out.println("childKey = " + rtr.getChildrenKey() + ": message = " + rtr.getMessage());
			partitionIndex = ++partitionIndex % ReplicationConfig.REPLICAS;
		}

		System.out.println("Press Enter to quit ...");
		in.nextLine();
		StandaloneClient.CS().dispose();
		in.close();
	}

}
