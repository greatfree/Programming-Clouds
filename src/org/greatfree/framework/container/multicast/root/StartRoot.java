package org.greatfree.framework.container.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.multicast.MultiConfig;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 05/09/2022
 *
 */
final class StartRoot
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DistributedNodeFailedException
	{
		System.out.println("Root starting up ...");
		Root.MC().start(MultiConfig.REGISTRY_SERVER_IP, MultiConfig.REGISTRY_SERVER_PORT, new EntryTask());
		System.out.println("Root started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
