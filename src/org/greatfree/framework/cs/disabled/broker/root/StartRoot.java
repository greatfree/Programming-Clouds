package org.greatfree.framework.cs.disabled.broker.root;

import java.io.IOException;

import org.greatfree.cluster.root.container.UnaryRoot;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cs.disabled.broker.Config;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/06/2023
 *
 */
final class StartRoot
{
	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Broker root starting up ...");
		UnaryRoot.CLUSTER().start(Config.BROKER_ROOT, Config.BROKER_PORT, RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new BrokerRootTask(), true);
		System.out.println("Broker root started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}
}

