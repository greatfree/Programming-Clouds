package org.greatfree.testing.stress.cluster.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
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
class StartRoot
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DistributedNodeFailedException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		Env.CONFIG().setupAtRHome(StressConfig.HOME);
		Env.CONFIG().confirmRegistry();

		StressRoot.ROOT().start(StressConfig.STREE_ROOT_PORT, Env.CONFIG().getRegistryIP(), Env.CONFIG().getRegistryPort(), new CoordinatorTask());
		TerminateSignal.SIGNAL().waitTermination();
	}

}
