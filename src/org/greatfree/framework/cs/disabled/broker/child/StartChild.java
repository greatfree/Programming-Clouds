package org.greatfree.framework.cs.disabled.broker.child;

import java.io.IOException;

import org.greatfree.cluster.child.container.UnaryChild;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cs.disabled.broker.Config;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/06/2023
 *
 */
final class StartChild
{
	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, IOException, ServerPortConflictedException
	{
		System.out.println("Broker child starting up ...");
		UnaryChild.CLUSTER().start(Config.BROKER_ROOT_KEY, new BrokerChildTask());
		System.out.println("Broker child started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}
}
