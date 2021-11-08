package org.greatfree.testing.stress.cluster.child;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.stress.cluster.StressConfig;
import org.greatfree.util.Env;
import org.greatfree.util.TerminateSignal;

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
class StartChild
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
	{
		Env.CONFIG().setupAtRHome(StressConfig.HOME);
		Env.CONFIG().confirmRegistry();
		
		StressChild.CHILD().start(Env.CONFIG().getRegistryIP(), Env.CONFIG().getRegistryPort(), new StressChildTask());
		TerminateSignal.SIGNAL().waitTermination();
	}

}
