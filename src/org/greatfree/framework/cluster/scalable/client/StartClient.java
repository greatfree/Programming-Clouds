package org.greatfree.framework.cluster.scalable.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.cluster.message.HeavyWorkloadNotification;
import org.greatfree.cluster.message.SuperfluousResourcesNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.scalable.ScalableConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

/*
 * 
 * The client sends the notification of SuperfluousResourcesNotification to the task cluster and then tests whether a new child of the pool cluster leaves the task cluster and joins the pool cluster. 09/06/2020, Bing Li
 * 
 * The client sends the notification of HeavyWorkloadNotification to the pool cluster and then tests whether a new child of the pool cluster leaves the pool cluster and joins the task cluster. 09/06/2020, Bing Li
 */

// Created: 09/06/2020, Bing Li
class StartClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();
		Scanner in = new Scanner(System.in);

		IPAddress poolClusterRootIP = StandaloneClient.CS().getIPAddress(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, ScalableConfig.POOL_CLUSTER_ROOT_KEY);
		System.out.println("The IP of the pool cluster is " + poolClusterRootIP);

		IPAddress taskClusterRootIP = StandaloneClient.CS().getIPAddress(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, ScalableConfig.TASK_CLUSTER_ROOT_KEY);
		System.out.println("The IP of the task cluster is " + taskClusterRootIP);

		String option = UtilConfig.EMPTY_STRING;

		while (!option.equals("q"))
		{
			System.out.println("Press Enter to send HeavyWorkloadNotification to the POOL cluster ...");
			option = in.nextLine();
//			StandaloneClient.CS().syncNotify(poolClusterRootIP.getIP(), poolClusterRootIP.getPort(), new HeavyWorkloadNotification(ScalableConfig.TASK_CLUSTER_ROOT_KEY, taskClusterRootIP, 1));
			StandaloneClient.CS().syncNotify(poolClusterRootIP.getIP(), poolClusterRootIP.getPort(), new HeavyWorkloadNotification(ScalableConfig.TASK_CLUSTER_ROOT_KEY, taskClusterRootIP, 4));
			System.out.println("The notification of HeavyWorkloadNotification is sent to the POOL cluster ...");

			System.out.println("Press Enter to send SuperfluousResourcesNotification to the TASK cluster ...");
			option = in.nextLine();
			StandaloneClient.CS().syncNotify(taskClusterRootIP.getIP(), taskClusterRootIP.getPort(), new SuperfluousResourcesNotification(ScalableConfig.POOL_CLUSTER_ROOT_KEY, poolClusterRootIP));
			System.out.println("The notification of SuperfluousResourcesNotification is sent to the TASK cluster ...");
		}

		System.out.println("Press Enter to quit ...");
		in.nextLine();
		StandaloneClient.CS().dispose();
		in.close();
	}

}
