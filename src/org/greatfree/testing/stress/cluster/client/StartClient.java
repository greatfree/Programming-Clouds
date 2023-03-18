package org.greatfree.testing.stress.cluster.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.stress.cluster.StressConfig;
import org.greatfree.testing.stress.cluster.message.StressNotification;
import org.greatfree.util.Env;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 *
 * 11/07/2021, Bing Li
 * 
 * I notice that when messages are sent as notifications too frequently, the server-side/cluster-side cannot process responsively. The server might be dead for that.
 * 
 * That is normal. But I need to check whether some improper designs exist in the server/cluster.
 * 
*/

// Created: 11/07/2021, Bing Li
class StartClient
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException, RemoteIPNotExistedException
	{
		Scanner in = new Scanner(System.in);

		Env.CONFIG().setupAtRHome(StressConfig.HOME);
		Env.CONFIG().confirmRegistry();

		StandaloneClusterClient.CONTAINER().init(Env.CONFIG().getRegistryIP(), Env.CONFIG().getRegistryPort(), StressConfig.STRESS_ROOT_KEY);
		
		System.out.println("Press Enter to perform stress testing ...");
		in.nextLine();

		for (long i = 0; i < StressConfig.ITERATION_SIZE; i++)
		{
			StandaloneClusterClient.CONTAINER().syncNotifyRoot(new StressNotification(i, Tools.generateUniqueKey()));
		}

		System.out.println("Stressing performed!");
		in.nextLine();

		StandaloneClusterClient.CONTAINER().dispose();
		in.close();
	}

}
