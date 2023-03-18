package org.greatfree.framework.container.multicast.child;

import java.io.IOException;

import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.multicast.MultiConfig;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 05/09/2022
 *
 */
final class StartChild
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Child is starting up ...");
		Child.MC().start(MultiConfig.ROOT_KEY, new SubTask());
		System.out.println("Child is started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
