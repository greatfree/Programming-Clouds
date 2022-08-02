package org.greatfree.framework.container.multicast.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 05/09/2022
 *
 */
final class Child
{
	private ClusterChildContainer child;
	
	private Child()
	{
	}
	
	private static Child instance = new Child();
	
	public static Child MC()
	{
		if (instance == null)
		{
			instance = new Child();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.child.stop(timeout);
	}
	
	public void start(String rootKey, ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(rootKey);
	}
}
