package org.greatfree.framework.cluster.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cluster.ClusterConfig;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/10/2023
 *
 */
final class StartRoot
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Cluster root starting up ...");
		ClusterRoot.CRY().start(ClusterConfig.ROOT_PORT, ClusterConfig.ROOT_NAME, ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new CoordinatorTask());

		System.out.println("Cluster root started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
