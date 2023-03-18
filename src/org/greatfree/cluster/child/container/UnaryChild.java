package org.greatfree.cluster.child.container;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
public final class UnaryChild
{
	private ClusterChildContainer child;

	private UnaryChild()
	{
	}
	
	private static UnaryChild instance = new UnaryChild();
	
	public static UnaryChild CLUSTER()
	{
		if (instance == null)
		{
			instance = new UnaryChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.child.stop(timeout);
	}
	
	public void start(String rootKey, ChildTask task) throws ClassNotFoundException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, IOException, ServerPortConflictedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(rootKey);
	}
}
