package org.greatfree.dsf.cluster.replication.client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.dsf.cluster.replication.ReplicationConfig;
import org.greatfree.dsf.cluster.replication.message.ReplicationTaskNotification;
import org.greatfree.dsf.cluster.replication.message.ReplicationTaskRequest;
import org.greatfree.dsf.cluster.replication.message.ReplicationTaskResponse;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * The client sends replication notifications/requests to the replication cluster. 09/07/2020, Bing Li
 */

// Created: 09//07/2020, Bing Li
class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();
		Scanner in = new Scanner(System.in);

		IPAddress rootIP = StandaloneClient.CS().getIPAddress(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, ReplicationConfig.REPLICATION_ROOT_KEY);
		System.out.println("The IP of the pool cluster is " + rootIP);
		
		String option = UtilConfig.EMPTY_STRING;
		List<ReplicationTaskResponse> responses;
		Response response;
		String message = "Hello";
		String msgKey = Tools.getHash(message);

		while (!option.equals("q"))
		{
			System.out.println("Press Enter to send ReplicationTaskNotification to the Replication cluster ...");
			option = in.nextLine();
			StandaloneClient.CS().syncNotify(rootIP.getIP(), rootIP.getPort(), new ReplicationTaskNotification(message, 0));
			
			/*
			 * The retrieval is performed using normal broadcasting request since the data is replicated in a randomly selected partition. 09/07/2020, Bing Li
			 */
			System.out.println("Press Enter to send ReplicationTaskRequest to the Replication cluster ...");
			option = in.nextLine();
			response = (Response)StandaloneClient.CS().read(rootIP.getIP(), rootIP.getPort(), new ReplicationTaskRequest(msgKey, message, 0));
			responses = Tools.filter(response.getResponses(), ReplicationTaskResponse.class);
			for (ReplicationTaskResponse entry : responses)
			{
				System.out.println("childKey = " + entry.getChildrenKey() + ": message = " + entry.getMessage());
			}
		}

		System.out.println("Press Enter to quit ...");
		in.nextLine();
		StandaloneClient.CS().dispose();
		in.close();
	}

}
