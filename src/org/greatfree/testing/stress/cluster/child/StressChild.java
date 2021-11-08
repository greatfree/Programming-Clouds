package org.greatfree.testing.stress.cluster.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.stress.cluster.StressConfig;

// Created: 11/07/2021, Bing Li
class StressChild
{
	private ClusterChildContainer child;
	
	private StressChild()
	{
	}
	
	private static StressChild instance = new StressChild();
	
	public static StressChild CHILD()
	{
		if (instance == null)
		{
			instance = new StressChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.child.stop(timeout);
	}

	public void start(String registryIP, int registryPort, ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChildContainer(registryIP, registryPort, task);
		this.child.start(StressConfig.STRESS_ROOT_KEY);
	}
}
