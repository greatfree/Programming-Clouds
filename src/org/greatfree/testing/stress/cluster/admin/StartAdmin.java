package org.greatfree.testing.stress.cluster.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.stress.cluster.StressConfig;
import org.greatfree.testing.stress.cluster.message.StopClusterNotification;
import org.greatfree.testing.stress.cluster.message.StopRootNotification;
import org.greatfree.util.Env;
import org.greatfree.util.IPAddress;

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
class StartAdmin
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, RemoteIPNotExistedException
	{
		Scanner in = new Scanner(System.in);

		Env.CONFIG().setupAtRHome(StressConfig.HOME);
		Env.CONFIG().confirmRegistry();

		StandaloneClient.CS().init();

		IPAddress stressClusterIP = StandaloneClient.CS().getIPAddress(Env.CONFIG().getRegistryIP(), Env.CONFIG().getRegistryPort(), StressConfig.STRESS_ROOT_KEY);
		
		System.out.println("Press Enter to stop the cluster children ...");
		in.nextLine();
		
		StandaloneClient.CS().syncNotify(stressClusterIP.getIP(), stressClusterIP.getPort(), new StopClusterNotification());

		System.out.println("Press Enter to stop the cluster root ...");
		in.nextLine();

		StandaloneClient.CS().syncNotify(stressClusterIP.getIP(), stressClusterIP.getPort(), new StopRootNotification());

		StandaloneClient.CS().dispose();
		in.close();
	}

}
