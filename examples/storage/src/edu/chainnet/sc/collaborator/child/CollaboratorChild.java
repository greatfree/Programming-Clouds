package edu.chainnet.sc.collaborator.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.sc.SCConfig;
import edu.chainnet.sc.collaborator.ChildPath;
import edu.chainnet.sc.collaborator.child.db.CollaboratorDB;
import edu.chainnet.sc.message.PathsRequest;
import edu.chainnet.sc.message.PathsResponse;

// Created: 10/17/2020, Bing Li
class CollaboratorChild
{
	private ClusterChildContainer child;
	
	private CollaboratorChild()
	{
	}
	
	private static CollaboratorChild instance = new CollaboratorChild();
	
	public static CollaboratorChild BC()
	{
		if (instance == null)
		{
			instance = new CollaboratorChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		CollaboratorDB.BC().dispose();
		this.child.stop(timeout);
	}
	
	public void start(String registryIP, int registryPort, ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChildContainer(registryIP, registryPort, task);
		this.child.start(SCConfig.COLLABORATOR_ROOT_KEY);
		ChildPath path = ((PathsResponse)this.child.readRoot(new PathsRequest())).getPath();
		CollaboratorDB.BC().init(path);
	}
}
