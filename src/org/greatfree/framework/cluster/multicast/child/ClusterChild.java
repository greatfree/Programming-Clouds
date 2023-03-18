package org.greatfree.framework.cluster.multicast.child;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;

/**
 * 
 * @author libing
 * 
 * 03/10/2023
 *
 */
final class ClusterChild
{
	private ClusterChildContainer child;

	private ClusterChild()
	{
	}
	
	private static ClusterChild instance = new ClusterChild();
	
	public static ClusterChild CRY()
	{
		if (instance == null)
		{
			instance = new ClusterChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, NoSuchPaddingException, IOException, InterruptedException, RemoteReadException, DistributedNodeFailedException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.stop(timeout);
	}

	public void start(String registryIP, int registryPort, ChildTask task, String rootKey) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException, DistributedNodeFailedException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException, PeerNameIsNullException
	{
		this.child = new ClusterChildContainer(registryIP, registryPort, task);
		this.child.start(rootKey);
	}
	
	public String getLocalIPKey()
	{
		return this.child.getLocalIPKey();
	}
}
