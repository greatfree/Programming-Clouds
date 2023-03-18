package org.greatfree.framework.cluster.scalable.task.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cluster.scalable.ScalableConfig;
import org.greatfree.util.TerminateSignal;

/*
 * The program defines the child node of the task cluster. 09/06/2020, Bing Li
 */

// Created: 09/06/2020, Bing Li
class TaskChild
{
	private ClusterChildContainer child;
	
	private TaskChild()
	{
	}
	
	private static TaskChild instance = new TaskChild();
	
	public static TaskChild TASK()
	{
		if (instance == null)
		{
			instance = new TaskChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException
	{
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.child.stop(timeout);
	}
	
	public void start(ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(ScalableConfig.TASK_CLUSTER_ROOT_KEY);
	}

}
